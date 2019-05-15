package services;

import io.ebean.Finder;
import models.CredentialUserInformation;
import models.User;


public class AuthenticatorService {

    private static User admin = new User("Pierre-Yves Eradeve", User.Role.ADMIN);
    private static User gestionnaire = new User("Edgard Chitecture", User.Role.GESTIONNAIRE);
    private static User superGestionnaire = new User("Alex Agonal", User.Role.SUPER_GESTIONNAIRE);

    private static Finder<Long, User> ebeanUserFinder = new Finder<>(User.class);
    
    public void authenticate(CredentialUserInformation userInformation) {
        if (getCurrentUser() == null && userInformation.authenticate()) {
            gestionnaire.save();
            // TODO : add event CONNECTION_USER
            //Event.newEvent(Event.EventType.CONNECTION_USER, "no ean", getCurrentUser().getName()).save();
        }
    }

    public User getCurrentUser() {
        return ebeanUserFinder.query().where().eq("name", gestionnaire.getName()).findOne();
    }
    
    
}
