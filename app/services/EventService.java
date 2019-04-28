package services;

import controllers.Users;
import io.ebean.Finder;
import models.Event;
import models.User;

import java.util.List;
import java.util.stream.Collectors;

public class EventService {
    
    public List<Event> findEvents() {
        User currentUser = Users.getCurrentUser();
        
        // FIXME : le finder ne peut pas être mocké, pas d'injection de dependences.
        return new Finder<>(Event.class).all().stream().filter(e -> {
            if (!currentUser.getRole().equals(User.Role.ADMIN)) {
                if (currentUser.getRole().equals(User.Role.GESTIONAIRE)) {
                    return !e.type.equals(Event.EventType.CHANGE_USER_ACCESS) && e.owner.equals(currentUser.getName());  
                } else if (currentUser.getRole().equals(User.Role.SUPER_GESTIONNAIRE)) {
                    return !e.type.equals(Event.EventType.CHANGE_USER_ACCESS);
                }
            }
            return true;
        }).collect(Collectors.toList());
    }
}
