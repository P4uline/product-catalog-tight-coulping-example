package services;

import io.ebean.Finder;
import models.Event;
import models.User;

import javax.inject.Inject;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class EventService {
    
    @Inject
    private AuthenticatorService authenticatorService;
    private Finder<Object, Event> eventFinder = new Finder<>(Event.class);

    public List<Event> findEvents() {
        User currentUser = authenticatorService.getCurrentUser();
        
        return getEventFinder().all().stream().filter(e -> {
            if (!currentUser.getRole().equals(User.Role.ADMIN)) {
                if (currentUser.getRole().equals(User.Role.GESTIONNAIRE)) {
                    return !e.type.equals(Event.EventType.CHANGE_USER_ACCESS) && e.owner.equals(currentUser.getName());  
                } else if (currentUser.getRole().equals(User.Role.SUPER_GESTIONNAIRE)) {
                    return !e.type.equals(Event.EventType.CHANGE_USER_ACCESS);
                }
            }
            return true;
        }).collect(toList());
    }
    
    public List<Event> findProuctEvents(String ean) {
        return findEvents().stream().filter(e -> e.ean.equals(ean)).collect(toList());
    }

    protected Finder<Object, Event> getEventFinder() {
        return eventFinder;
    }
}
