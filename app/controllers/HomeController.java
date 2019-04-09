package controllers;

import play.i18n.Messages;
import play.mvc.*;

import views.html.*;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index() {
        // SI on voulait faire les choses bien, on ferai une nouvelle route
        // pour le changement de langue, pour gagner du temps on le fait là
        ctx().changeLang("en");

        Messages messages = Http.Context.current().messages();
        String ready = messages.at("welcome.ready");
        return ok(index.render(ready));
    }

}
