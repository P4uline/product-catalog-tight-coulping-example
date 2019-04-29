package controllers;

import com.google.common.io.Files;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import io.ebean.Finder;
import models.Event;
import models.ProductEntity;
import models.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import play.api.Play;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static controllers.Users.getCurrentUser;
import static models.Event.EventType.*;
import static models.Event.newEvent;

public class Products extends Controller {

    private final FormFactory playForm;

    private static Finder<Long, ProductEntity> ebeanProductFinder = new Finder<>(ProductEntity.class);

    private static Finder<Long, Event> ebeanEventFinder = new Finder<>(Event.class);
    
    // TODO faire un petit formulaire d'authentification et exemple de code découplé
    
    private User currentUser = getCurrentUser();

    @Inject
    public Products(FormFactory formFactory) {
        this.playForm = formFactory;
    }

    public Result findAllProducts() {
        newEvent(CONSULT_ALL_PRODUCTS, "no ean", getCurrentUser().getName()).save();
        return ok(views.html.list.render(ebeanProductFinder.all()));
    }

    public Result newProduct() {
        return ok(views.html.newProduct.render(playForm.form(ProductEntity.class)));
    }

    public Result detail(String ean) {
        
        newEvent(Event.EventType.CONSULT_PRODUCT, ean, getCurrentUser().getName()).save();
        List<Event> events = null;
        if (getCurrentUser().getRole().equals(User.Role.SUPER_GESTIONNAIRE)) {
            events = ebeanEventFinder.query().where().ne("type", Event.EventType.CHANGE_USER_ACCESS).findList();
        } else if (getCurrentUser().getRole().equals(User.Role.GESTIONAIRE)) {
            events = ebeanEventFinder.query().where()
                    .ne("type", Event.EventType.CHANGE_USER_ACCESS)
                    .eq("owner", getCurrentUser().getName())
                    .findList();
        } else if (getCurrentUser().getRole().equals(User.Role.ADMIN)) {
            events = ebeanEventFinder.query().where().findList();
        }

        List<Event> finalEvents = events;
        return ebeanProductFinder.query().where().eq("ean", ean).findOneOrEmpty() //
                .map( product -> ok(views.html.detail.render(product, finalEvents, getCurrentUser()))) //
                .orElseGet(() -> notFound("Le produit '" + ean + "' n'a pas été trouvé")); //
    }

   
   
   

    @NotNull
    private Optional<ProductEntity> getProduct(String ean) {
        return ebeanProductFinder.query().where().eq("ean", ean).findOneOrEmpty();
    }

    public Result editProduct(String ean) {
        newEvent(EDIT_PRODUCT, ean, getCurrentUser().getName()).save();

        return ebeanProductFinder.query().where().eq("ean", ean).findOneOrEmpty() //
                .map( product -> ok(views.html.editProduct.render(playForm.form(ProductEntity.class).fill(product)))) //
                .orElseGet(() -> notFound("Le produit '" + ean + "' n'a pas été trouvé")); //
    }

    public Result submitEditProduct(String ean) {
        newEvent(UPDATE_PRODUCT, ean, getCurrentUser().getName()).save();
        
        Form<ProductEntity> productForm = playForm.form(ProductEntity.class).bindFromRequest();
        if (productForm.hasErrors()) {
            return badRequest(views.html.editProduct.render(productForm));
        }
        ProductEntity product = productForm.get();
        return modifyProduct(p -> p.update(ean), product);
    }

    public Result submitNewProduct() {
        
        Form<ProductEntity> productForm = playForm.form(ProductEntity.class).bindFromRequest();
        if (productForm.hasErrors()) {
            return badRequest(views.html.newProduct.render(productForm));
        }
        ProductEntity product = productForm.get();
        newEvent(CREATE_PRODUCT, product.ean, getCurrentUser().getName()).save();
        return modifyProduct(p -> p.save(), product);
    }

    private Result modifyProduct(Consumer<ProductEntity> modifyAction, ProductEntity toModify) {
        Http.MultipartFormData.FilePart<File> picture = request().body().<File>asMultipartFormData().getFile("picture");
        if (picture == null) {
            flash("error", "Missing file");
            return badRequest();
        }
        toModify.picture = getBytesFromFile(picture);
        modifyAction.accept(toModify);
        flash("success", "New product created");
        return ok(views.html.list.render(ProductEntity.findAll()));
    }

    private byte[] getBytesFromFile(Http.MultipartFormData.FilePart<File> picture) {
        File file = picture.getFile();
        byte[] arrays = new byte[0];
        try {
            arrays = Files.toByteArray(file);
        } catch (IOException e) {
            return null;
        }
        if (arrays.length == 0) {
            return null;
        }
        return arrays;
    }

    public Result picture(String ean) {
        return ProductEntity.findByEan(ean) //
                .map(product -> ok(product.picture)) //
                .orElseGet(() -> ok(Play.current().getFile("public/images/favicon.png"))); //
    }

    public Result delete(String ean) {
        newEvent(CREATE_PRODUCT, ean, getCurrentUser().getName()).save();
        ProductEntity.findByEan(ean).ifPresent(product -> product.delete());
        flash("success", "Product deleted");
        return redirect(routes.Products.findAllProducts());
    }

    public Result prepareLoadSamples() {
        return ok(views.html.prepareLoadSamples.render());
    }

    public Result loadSamples() {
        ProductEntity.flushAll();
        newEvent(FLUSH_DATABASE, "no ean", getCurrentUser().getName()).save();

        CsvParserSettings settings = new CsvParserSettings();
        settings.getFormat().setLineSeparator("\n");
        CsvParser parser = new CsvParser(settings);

        // call beginParsing to read records one by one, iterator-style.
        parser.beginParsing(new java.io.File("public/hikea-names.csv"));

        String[] row;
        while ((row = parser.parseNext()) != null) {
            final String ean = RandomStringUtils.randomAlphanumeric(10);
            final String name = row[0];
            final String description = row[1];
            final ProductEntity p = new ProductEntity(ean, name, description);
            p.save();
        }
        newEvent(POPULATE_DATABASE_FROM_DATAFILE, "no ean", getCurrentUser().getName()).save();
        
        parser.stopParsing();
        flash("success", "Loaded a CSV file");
        return redirect(routes.Products.findAllProducts());
    }

}