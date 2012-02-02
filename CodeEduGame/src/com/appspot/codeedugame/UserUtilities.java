package com.appspot.codeedugame;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class UserUtilities {
	// returns the current user if logged in, otherwise returns null.
    public static User getUser() {
    	UserService userService = UserServiceFactory.getUserService();
    	return userService.getCurrentUser();
    }
}
