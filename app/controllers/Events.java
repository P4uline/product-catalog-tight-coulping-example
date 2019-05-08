package controllers;

import io.ebean.Finder;
import models.Event;
import models.User;
import play.mvc.Controller;
import play.mvc.Result;
import services.AuthenticatorService;
import services.EventService;

import javax.inject.Inject;

public class Events extends Controller {

    private static Finder<Long, Event> ebeanEventFinder = new Finder<>(Event.class);
    
    @Inject
    private EventService eventService;
    
    @Inject
    private AuthenticatorService authenticatorService;

    public Result listEvents() {
        if (authenticatorService.getCurrentUser().getRole().equals(User.Role.SUPER_GESTIONNAIRE)) {
            return ok(views.html.listEvent.render(ebeanEventFinder.query().where().ne("type", Event.EventType.CHANGE_USER_ACCESS).findList()));
        }
        
        return ok(views.html.listEvent.render(eventService.findEvents()));
    }
}
