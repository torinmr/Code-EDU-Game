package com.appspot.codeedugame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.appspot.codeedugame.json.JSONException;
import com.appspot.codeedugame.json.JSONObject;
import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class UserProgress {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	
	@Persistent(serialized = "true")
	private HashMap<String, Integer> lessonProgress;
	
	private UserProgress () {}
	public static UserProgress make() {
		UserProgress up = new UserProgress();
		up.lessonProgress = new HashMap<String, Integer>();
		return up;
	}
	
	public void setLevelInProgress(String level) {
		lessonProgress.put(level, 1);
	}
	
	public void setLevelDone(String level) {
		lessonProgress.put(level, 2);
	}

	public JSONObject getProgress() {
		JSONObject progressObj = new JSONObject();
        try {
        	ArrayList<Map.Entry<String, Integer>> progressArray =
        			new ArrayList<Map.Entry<String, Integer>>(lessonProgress.entrySet());
            
            for (Map.Entry<String, Integer> e : progressArray) {
            	String value;
            	int v = e.getValue();
            	if (v == 1) {
            		value = "in progress";
            	} else if (v == 2) {
            		value = "completed";
            	} else {
            		value = "undefined";
            	}
                progressObj.put(e.getKey(), value);
            }
            return progressObj;
        } catch (JSONException e) {
            throw new RuntimeException(e.getMessage());
        }
	}
}
	

