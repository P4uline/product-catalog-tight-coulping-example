package controllers;

import models.User;

public class Users {

    private static User admin = new User("Pierre-Yves Eradeve", User.Role.ADMIN);
    private static User gestionaire = new User("Edgard Chitecture", User.Role.GESTIONAIRE);
    private static User superGestionnaire = new User("Alex Agonal", User.Role.SUPER_GESTIONNAIRE);

    public static User getCurrentUser() {
        return gestionaire;
    }
}
