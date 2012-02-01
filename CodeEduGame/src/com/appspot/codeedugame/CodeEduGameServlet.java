package com.appspot.codeedugame;

import java.io.IOException;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.servlet.http.*;

import com.appspot.codeedugame.json.JSONArray;
import com.appspot.codeedugame.json.JSONException;
import com.appspot.codeedugame.json.JSONObject;
import com.appspot.codeedugame.deck.PokerCard;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.users.UserService;

@SuppressWarnings("serial")
public class CodeEduGameServlet extends HttpServlet {
    private final int STARTING_MONEY = 100;
    
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String rpcName = req.getParameter("rpcName");
        if (rpcName == null) {
            sendError("You need an rpcName field.", resp);
            return;
        }
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            if (rpcName.equals("getLogin")) {
                sendURL(resp, req);
                return;
            } else if (rpcName.equals("getName")) {
                sendName(resp);
                return;
            }
            if (getUser() == null) {
                sendError("You are not logged in.", resp);
                return;
            }
            Blackjack game = null;
            if (rpcName.equals("startGame")) {
                String id = getNewGameId(getUser(), pm, resp);
                if (id != null) {
                    JSONObject respObj = new JSONObject();
                    try {
                        respObj.put("isSuccess", true);
                        respObj.put("msg", "You have started a new game.");
                        resp.getWriter().print(respObj);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                game = getGame(getUser(), pm, resp);
            }
          
            if (game != null) {
                if (rpcName.equals("bid")) {
                    attemptBid(game, req, resp);
                } else if (rpcName.equals("hit")) {
                    attemptHit(game, req, resp);
                } else if (rpcName.equals("stand")) {
                    attemptStand(game, req, resp);
                } else if (rpcName.equals("doubleDown")) {
                    attemptDoubleDown(game, req, resp);
                } else if (rpcName.equals("startNextRound")) {
                    attemptStartNextRound(game, req, resp);
                } else if (rpcName.equals("deleteGame")) {
                    deleteGame(game, pm, resp);
                } else if (rpcName.equals("startGame")) {
                    //don't do anything
                } else {
                    sendError(req.getParameter("rpcName")
                            + " is an invalid move.", resp);
                }
            }
        } finally {
            pm.close();
        }
    }

    private void deleteGame(Blackjack game, PersistenceManager pm, HttpServletResponse resp) {
        pm.deletePersistent(game);
        UserAndGame uag = pm.getObjectById(UserAndGame.class, getUser().getUserId());
        pm.deletePersistent(uag);
        
        JSONObject respObj = new JSONObject();
        try {
            respObj.put("isSuccess", true);
            respObj.put("msg", "You successfully deleted a game for user "
                    + getUser().getNickname() + ".");
            resp.getWriter().print(respObj);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getNewGameId(User user, PersistenceManager pm, HttpServletResponse resp) {
        UserAndGame uag = null;
        try {
            uag = pm.getObjectById(UserAndGame.class, user.getUserId());
            sendError("User " + user.getNickname() + " already is playing a game.", resp);
            return null;
        } catch (JDOObjectNotFoundException e) {
            //this is what we want to happen
            uag = UserAndGame.make(user.getUserId());
            Blackjack game = new Blackjack(STARTING_MONEY, uag.getGameId());
            pm.makePersistent(uag);
            pm.makePersistent(game);
            return uag.getGameId();
        }
    }
    
    private Blackjack getGame(User user, PersistenceManager pm, HttpServletResponse resp) {
        UserAndGame uag = null;
        try {
            uag = pm.getObjectById(UserAndGame.class, user.getUserId());
        } catch (JDOObjectNotFoundException e) {
            sendError("No user object found for user " + user.getNickname() + ".", resp);
            return null;
        }
        Key k = KeyFactory.createKey(Blackjack.class.getSimpleName(),
                uag.getGameId());
        try {
            return pm.getObjectById(Blackjack.class, k);
        } catch (JDOObjectNotFoundException e) {
            sendError("No game for user " + user.getNickname() + " exists.", resp);
            return null;
        }
    }

    private void attemptStartNextRound(Blackjack game, HttpServletRequest req,
            HttpServletResponse resp) {
        if (game.startNextRound()) {
            sendSuccess(game, resp, req);
        } else if (!game.roundIsOver()) {
            sendError("You may not start a round that isn't over.", resp);
        } else {
            sendError("UNKNOWN ROUND STARTING ERROR!!!!!!!", resp);
        }
    }

    private void attemptDoubleDown(Blackjack game, HttpServletRequest req,
            HttpServletResponse resp) {
        if (game.doubleDown()) {
            sendSuccess(game, resp, req);
        } else if (game.roundIsOver()) {
            sendError("You may not double down because the round is over.", resp);
        } else if (game.getPlayerCards().size() > 2) {
            sendError("You tried to double down after hitting.", resp);
        } else if (game.getBid() > game.getPlayerMoney()) {
            sendError("You bid " + game.getBid() + " but you have "
                    + game.getPlayerMoney() + ", so you may not double down.", resp);
        } else {
            sendError("UNKNOWN DOUBLE DOWN ERROR!!!!!!!", resp);
        }
    }

    private void attemptStand(Blackjack game, HttpServletRequest req,
            HttpServletResponse resp) {
        if (game.stand()) {
            sendSuccess(game, resp, req);
        } else if (game.roundIsOver()) {
            sendError("You may not stand because the round is over.", resp);
        } else {
            sendError("UNKNOWN STANDING ERROR!!!!!!!", resp);
        }
    }

    private void attemptHit(Blackjack game, HttpServletRequest req,
            HttpServletResponse resp) {
        if (game.hit()) {
            sendSuccess(game, resp, req);
        } else if (game.roundIsOver()) {
            sendError("You may not hit because the round is over.", resp);
        } else {
            sendError("UNKNOWN HITTING ERROR!!!!!!!", resp);
        }
    }

    private void attemptBid(Blackjack game, HttpServletRequest req,
            HttpServletResponse resp) {
        String amt = req.getParameter("amount");
        if (amt == null) {
            sendError("You forgot to specify a bidding amount.", resp);
        } else {
            int amount = Integer.parseInt(amt);
            boolean success = game.makeBid(amount);
            if (success) {
                sendSuccess(game, resp, req);
            } else if (game.roundIsOver()) {
                sendError("You may not bid because the round is over.", resp);
            } else if (!game.getPlayerCards().isEmpty()) {
                sendError("You are trying to bid during a turn.", resp);
            } else if (amount < 0) {
                sendError("You specified a negative bid of " + amount + ".", resp);
            } else if (amount > game.getPlayerMoney()) {
                sendError(
                    "You bid " + amount + " > " + game.getPlayerMoney() + ", your stash.", resp);
            } else if (amount == 0) {
                sendError("You bid nothing at the wrong time.", resp);
            } else {
                sendError("UNKNOWN BIDDING ERROR!!!!!!!", resp);
            }
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
    
    private void sendSuccess(
            Blackjack game, HttpServletResponse resp, HttpServletRequest req) {
        JSONObject respObj = new JSONObject();
        try {
            respObj.put("gameObj", assembleGameObj(game));
            respObj.put("isSuccess", true);
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
        }
    	try {
	        if (req.getUserPrincipal() != null) {
	            respObj.put("logout", userService.createLogoutURL(returnURL));
	        } else {
	            respObj.put("login", userService.createLoginURL(returnURL));
	        }
	        resp.getWriter().print(respObj.toString());
    	} catch (JSONException e) {
            throw new RuntimeException(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    // sends the current user's nickname if logged in, sends "Anonymous" otherwise.
    private void sendName(HttpServletResponse resp) {
    	JSONObject respObj = new JSONObject();
    	User user = getUser();
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
    
    // returns the current user if logged in, otherwise returns null.
    private User getUser() {
    	UserService userService = UserServiceFactory.getUserService();
    	return userService.getCurrentUser();
    }
    
    private JSONObject assembleGameObj(Blackjack game) {
        JSONObject gameObj = new JSONObject();
        try {
            gameObj.put("money", game.getPlayerMoney());
            gameObj.put("inPot", game.getBid());
            
            JSONArray playerSuits = new JSONArray();
            JSONArray playerValues = new JSONArray();
            for (PokerCard c : game.getPlayerCards()) {
                playerSuits.put(c.getSuit());
                playerValues.put(c.getRank());
            }
            JSONArray dealerSuits = new JSONArray();
            JSONArray dealerValues = new JSONArray();
            for (PokerCard c : game.getDealerCards()) {
                dealerSuits.put(c.getSuit());
                dealerValues.put(c.getRank());
            }
            gameObj.put("playerSuits", playerSuits);
            gameObj.put("playerValues", playerValues);
            gameObj.put("dealerSuits", dealerSuits);
            gameObj.put("dealerValues", dealerValues);
            
            gameObj.put("roundIsOver", game.roundIsOver());
            gameObj.put("isDeck", game.deckSize() != 0);
            gameObj.put("hasJustReshuffled", game.getHasReshuffled());
            gameObj.put("playerIsWinner", game.playerIsWinner());
            return gameObj;
        } catch (JSONException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
