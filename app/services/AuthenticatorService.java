package services;

import io.ebean.Finder;
import models.CredentialUserInformation;
import models.Event;
import models.User;

import static models.Event.EventType.CONNECTION_USER;
import static models.Event.newEvent;

public class AuthenticatorService {

    private static User admin = new User("Pierre-Yves Eradeve", User.Role.ADMIN);
    private static User gestionaire = new User("Edgard Chitecture", User.Role.GESTIONAIRE);
    private static User superGestionnaire = new User("Alex Agonal", User.Role.SUPER_GESTIONNAIRE);

    private static Finder<Long, User> ebeanUserFinder = new Finder<>(User.class);
    
    public void authenticate(CredentialUserInformation userInformation) {
        if (getCurrentUser() == null && userInformation.authenticate()) {
            gestionaire.save();
            // TODO : add event CONNECTION_USER
            //newEvent(CONNECTION_USER, "no ean", getCurrentUser().getName()).save();
        }
    }

    public User getCurrentUser() {
        return ebeanUserFinder.query().where().eq("name", gestionaire.getName()).findOne();
    }
    
    
}
