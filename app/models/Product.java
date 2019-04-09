package models;

import io.ebean.Finder;
import io.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Optional;

@Entity
public class Product extends Model {

    @Id
    @Constraints.Required
    public String ean;

    @Constraints.Required
    public String name;

    public byte[] getPicture() {
        return picture;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public String getDescription() {
    
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
    
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPictureUrl() {
    
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getEan() {
    
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public String description;

    private static Finder<Long, Product> find = new Finder<>(Product.class);

    @Size(max = 883647)
    public byte[] picture;
    public String pictureUrl;

    public Product() {
    }

    public Product(String ean, String name, String description) {
        this.ean = ean;
        this.name = name;
        this.description = description;
    }

    public static List<Product> findAll() {
        return find.all();
    }

    public static Optional<Product> findByEan(String ean) {
        return find.query().where().eq("ean", ean).findOneOrEmpty();
    }

    public static List<Product> findByName(String name) {
        return find.query().where().eq("name", name).findList();
    }

    public static void flushAll() {
        findAll().forEach(product -> product.delete());
    }
    
}