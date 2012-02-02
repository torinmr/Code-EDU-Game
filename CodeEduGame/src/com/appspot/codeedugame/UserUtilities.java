package com.appspot.codeedugame;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class UserUtilities {
	// returns the current user if logged in, otherwise returns null.
    public static User getUser(PersistenceManager pm) {
    	UserService userService = UserServiceFactory.getUserService();
    	User user = userService.getCurrentUser();
    	
    	if (user == null) {
    		return null;
    	}
    	
    	UserAndGame uag;
    	try {
    		uag = pm.getObjectById(UserAndGame.class, user.getUserId());
    	} catch (JDOObjectNotFoundException e) {
            uag = UserAndGame.make(user);
            pm.makePersistent(uag);
        }
    	return user;
    }
}
