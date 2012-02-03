package com.appspot.codeedugame;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.appspot.codeedugame.json.JSONArray;
import com.appspot.codeedugame.json.JSONException;
import com.appspot.codeedugame.json.JSONObject;

public class UserProgress implements Serializable {
    static final long serialVersionUID = 2;
    
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

	public JSONObject getJSONObject() {
    	final Comparator<Map.Entry<String, Integer>> KEY_ORDER =
    			new Comparator<Map.Entry<String, Integer>>() {
    		public int compare(Map.Entry<String, Integer> m1,
    				Map.Entry<String, Integer> m2) {
    			return m1.getKey().compareTo(m2.getKey());
    		}
    	};
    			
    	ArrayList<Map.Entry<String, Integer>> progressArray =
    			new ArrayList<Map.Entry<String, Integer>>(lessonProgress.entrySet());
        
    	Collections.sort(progressArray, KEY_ORDER);
    
		JSONObject progressObj = new JSONObject();
        JSONArray levels =  new JSONArray();
		
		try {	
            for (Map.Entry<String, Integer> e : progressArray) {
            	JSONArray level = new JSONArray();
            	
            	String value;
            	int v = e.getValue();
            	if (v == 1) {
            		value = "in progress";
            	} else if (v == 2) {
            		value = "completed";
            	} else {
            		value = "undefined";
            	}
            	level.put(e.getKey());
            	level.put(value);
            	levels.put(level);
            }
            progressObj.put("levels", levels);
            return progressObj;
        } catch (JSONException e) {
            throw new RuntimeException(e.getMessage());
        }
	}
}
	

