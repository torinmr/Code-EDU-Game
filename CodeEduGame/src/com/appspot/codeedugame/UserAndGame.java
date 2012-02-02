package com.appspot.codeedugame;

import java.io.Serializable;

import java.util.UUID;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.appspot.codeedugame.json.JSONObject;
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
    
    @Persistent(serialized = "true")
    private UserProgressWrapper progress;
    
    public static class UserProgressWrapper implements Serializable {
		private static final long serialVersionUID = 1L;
		private final UserProgress progress;

		public UserProgressWrapper(UserProgress progress) {
			this.progress = progress;
		}
		public UserProgress get() {
			return this.progress;
		}
	} 
    
    private UserAndGame() {}
    public static UserAndGame make(User user) {
        UserAndGame uag = new UserAndGame();
        uag.token = user.getUserId();
        uag.username = user.getNickname();
        uag.gameId = "EMPTY";
        uag.progress = new UserProgressWrapper(UserProgress.make());
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
    
    public void setLevelInProgress(String level) {
		progress.get().setLevelInProgress(level);
		progress = new UserProgressWrapper(progress.get());
	}
    
    public void setLevelDone(String level) {
		progress.get().setLevelDone(level);
		progress = new UserProgressWrapper(progress.get());
	}

	public JSONObject getProgressJSONObject() {
		return progress.get().getJSONObject();
	}
}
