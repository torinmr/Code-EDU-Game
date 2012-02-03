package com.appspot.codeedugame;

import java.io.IOException;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appspot.codeedugame.json.JSONException;
import com.appspot.codeedugame.json.JSONObject;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class AccountServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String rpcName = req.getParameter("rpcName");
        if (rpcName == null) {
            sendError("You need an rpcName field.", resp);
            return;
        }
        
        PersistenceManager pm = PMF.get().getPersistenceManager();
        User user = UserUtilities.getUser(pm);
        
        try {
            if (rpcName.equals("getLogin")) {
                sendURL(resp, req);
                return;
            } else if (rpcName.equals("getName")) {
                sendName(user, resp);
                return;
            } else if (rpcName.equals("getProgress")) {
	            if (user == null) {
	                sendError("You are not logged in.", resp);
	                return;
	            }
	            sendProgress(user, pm, resp);
            } else if (rpcName.equals("setLevelDone")) {
            	if (user == null) {
	                sendError("You are not logged in.", resp);
	                return;
	            }
            	setLevelDone(user, pm, resp, req);
            } else if (rpcName.equals("setLevelInProgress")) {
            	if (user == null) {
	                sendError("You are not logged in.", resp);
	                return;
	            }
            	setLevelInProgress(user, pm, resp, req);
            } else {
            	sendError("Unknown rpc name.", resp);
            }
        } finally {
            pm.close();
        }
    }
    
    private void sendError(String error, HttpServletResponse resp) {
        JSONObject respObj = new JSONObject();
        try {
            respObj.put("msg", error);
            respObj.put("isSuccess", false);
            resp.getWriter().print(respObj.toString());
        } catch (JSONException e) {
            throw new RuntimeException(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    private void sendURL(HttpServletResponse resp, HttpServletRequest req) {
        JSONObject respObj = new JSONObject();
        UserService userService = UserServiceFactory.getUserService();
        
        String returnURL = req.getParameter("returnURL");
        if (returnURL == null) {
            sendError("You forgot to specify a return URL.", resp);
            return;
        }
        
        String resultURL;
        if (req.getUserPrincipal() != null) {
            resultURL = userService.createLogoutURL(returnURL);
        } else {
            resultURL = userService.createLoginURL(returnURL);
        }

        try {
        	respObj.put("isLoggedIn", req.getUserPrincipal() != null);
            respObj.put("URL", resultURL);
            resp.getWriter().print(respObj.toString());
        } catch (JSONException e) {
            throw new RuntimeException(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    // sends the current user's nickname if logged in, sends "Anonymous" otherwise.
    private void sendName(User user, HttpServletResponse resp) {
        JSONObject respObj = new JSONObject();
        try {
            if (user != null) {
                respObj.put("name", user.getNickname());
            } else {
                respObj.put("name", "anonymous");
            }
            resp.getWriter().print(respObj.toString());
        } catch (JSONException e) {
            throw new RuntimeException(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }   
    }
    
    private void sendProgress(User user, PersistenceManager pm, HttpServletResponse resp) {
    	try {
            UserAndGame uag = pm.getObjectById(UserAndGame.class, user.getUserId());
            JSONObject respObj = uag.getProgressJSONObject();
            resp.getWriter().print(respObj.toString());
        } catch (JDOObjectNotFoundException e) {
            sendError("No user object found for user " + user.getNickname() + ".", resp);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } 
	}
    
    private void setLevelDone(User user, PersistenceManager pm, HttpServletResponse resp, HttpServletRequest req) {
    	String level = req.getParameter("level");
        if (level == null) {
            sendError("You forgot to specify a level.", resp);
            return;
        }
    	try {
            UserAndGame uag = pm.getObjectById(UserAndGame.class, user.getUserId());
            uag.setLevelDone(level);
            
            JSONObject respObj = new JSONObject();
            respObj.put("isSuccess", true);
            resp.getWriter().print(respObj.toString());
        } catch (JDOObjectNotFoundException e) {
            sendError("No user object found for user " + user.getNickname() + ".", resp);
        } catch (JSONException e) {
            throw new RuntimeException(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } 
    }
    
    private void setLevelInProgress(User user, PersistenceManager pm, HttpServletResponse resp, HttpServletRequest req) {
    	String level = req.getParameter("level");
        if (level == null) {
            sendError("You forgot to specify a level.", resp);
            return;
        }
    	try {
            UserAndGame uag = pm.getObjectById(UserAndGame.class, user.getUserId());
            uag.setLevelInProgress(level);
            
            JSONObject respObj = new JSONObject();
            respObj.put("isSuccess", true);
            resp.getWriter().print(respObj.toString());
        } catch (JDOObjectNotFoundException e) {
            sendError("No user object found for user " + user.getNickname() + ".", resp);
        } catch (JSONException e) {
            throw new RuntimeException(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } 
    }
}
