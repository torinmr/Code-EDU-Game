package com.appspot.codeedugame;

import java.util.UUID;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.users.User;

@PersistenceCapable
public class UserAndGame {

    @PrimaryKey
    @Persistent
    private String token;
    
    @Persistent
    private String username;
    
    @Persistent
    private String gameId;
    
    private UserAndGame() {}
    public static UserAndGame make(User user) {
        UserAndGame uag = new UserAndGame();
        uag.token = user.getUserId();
        uag.username = user.getNickname();
        uag.gameId = UUID.randomUUID().toString();
        return uag;
    }
    
    public String getToken() {
        return token;
    }
    
    public String getGameId() {
        return gameId;
    }
    
    public String getUsername() {
        return username;
    }
}
