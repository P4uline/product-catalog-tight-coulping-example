package services;

import io.ebean.Finder;
import models.Event;
import models.User;
import play.data.validation.Constraints;

import javax.inject.Inject;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static models.Event.EventType.CHANGE_USER_ACCESS;
import static models.User.Role.*;

public class EventService {
    
    @Inject
    private AuthenticatorService authenticatorService;
    private Finder<Object, Event> eventFinder = new Finder<>(Event.class);

    public List<Event> findEvents() {
        User currentUser = authenticatorService.getCurrentUser();
        
        return getEventFinder().all().stream().filter(e -> {
            User.Role userRole = currentUser.getRole();
            Event.@Constraints.Required EventType eventType = e.type;
            @Constraints.Required String eventOwner = e.owner;
            
            if (userRole.equals(GESTIONNAIRE)) {
                return !eventType.equals(CHANGE_USER_ACCESS) && eventOwner.equals(currentUser.getName());  
            } else if (userRole.equals(SUPER_GESTIONNAIRE)) {
                return !eventType.equals(CHANGE_USER_ACCESS);
            }
            return true;
        }).collect(toList());
    }
    
    public List<Event> findProductEvents(String ean) {
        return findEvents().stream().filter(e -> e.ean.equals(ean)).collect(toList());
    }

    protected Finder<Object, Event> getEventFinder() {
        return eventFinder;
    }
}
