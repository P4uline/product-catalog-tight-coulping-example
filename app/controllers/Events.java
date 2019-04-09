package controllers;

import models.Event;
import play.mvc.Controller;
import play.mvc.Result;

public class Events extends Controller {

    public Result listEvents() {
        return ok(views.html.listEvent.render(Event.findAll()));
    }
}
