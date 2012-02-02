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
    
    @Persistent
    private UserProgress progress;
    
    private UserAndGame() {}
    public static UserAndGame make(User user) {
        UserAndGame uag = new UserAndGame();
        uag.token = user.getUserId();
        uag.username = user.getNickname();
        uag.gameId = "EMPTY";
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
    
    public void deleteGameId() {
        if (!gameId.equals("EMPTY")) {
            this.gameId = "EMPTY";
        } else {
            throw new IllegalStateException("Should not be deleting an empty gameId.");
        }
    }
    
    public void createGameId() {
        if (gameId.equals("EMPTY")) {
            this.gameId = UUID.randomUUID().toString();
        } else {
            throw new IllegalStateException("Should not be reseting a nonempty gameId.");
        }
    }
    
    public UserProgress getProgress() {
    	return progress;
    }
}
