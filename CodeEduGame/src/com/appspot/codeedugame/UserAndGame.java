package com.appspot.codeedugame;

import java.util.UUID;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class UserAndGame {
    
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

    @Persistent
    private String token;
    
    @Persistent
    private String gameId;
    
    private UserAndGame() {}
    public static UserAndGame make(String token) {
        UserAndGame uag = new UserAndGame();
        uag.token = token;
        uag.gameId = UUID.randomUUID().toString();
        return uag;
    }
    
    public String getToken() {
        return token;
    }
    
    public String getGameId() {
        return gameId;
    }
}
