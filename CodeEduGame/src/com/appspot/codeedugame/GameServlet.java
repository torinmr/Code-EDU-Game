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

@SuppressWarnings("serial")
public class GameServlet extends HttpServlet {
    private final int STARTING_MONEY = 100;
    
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String rpcName = req.getParameter("rpcName");
        if (rpcName == null) {
            sendError("You need an rpcName field.", resp, null);
            return;
        }
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            Blackjack game = null;
            if (rpcName.equals("startGame")) {
                String id = getNewGameId(UserUtilities.getUser(), pm, resp);
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
                game = getGame(UserUtilities.getUser(), pm, resp);
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
                            + " is an invalid move.", resp, game);
                }
            }
        } finally {
            pm.close();
        }
    }

    private void deleteGame(Blackjack game, PersistenceManager pm, HttpServletResponse resp) {
        pm.deletePersistent(game);
        UserAndGame uag = pm.getObjectById(UserAndGame.class, UserUtilities.getUser().getUserId());
        pm.deletePersistent(uag);
        
        JSONObject respObj = new JSONObject();
        try {
            respObj.put("isSuccess", true);
            respObj.put("msg", "You successfully deleted a game for user "
                    + UserUtilities.getUser().getNickname() + ".");
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
            Blackjack game = pm.getObjectById(Blackjack.class, uag.getGameId());
            sendError("User " + user.getNickname() + " already is playing a game.", resp, game);
            return null;
        } catch (JDOObjectNotFoundException e) {
            //this is what we want to happen
            uag = UserAndGame.make(user);
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
            sendError("No user object found for user " + user.getNickname() + ".", resp, null);
            return null;
        }
        Key k = KeyFactory.createKey(Blackjack.class.getSimpleName(),
                uag.getGameId());
        try {
            return pm.getObjectById(Blackjack.class, k);
        } catch (JDOObjectNotFoundException e) {
            sendError("No game for user " + user.getNickname() + " exists.", resp, null);
            return null;
        }
    }

    private void attemptStartNextRound(Blackjack game, HttpServletRequest req,
            HttpServletResponse resp) {
        if (game.startNextRound()) {
            sendSuccess(game, resp, req);
        } else if (!game.roundIsOver()) {
            sendError("You may not start a round that isn't over.", resp, game);
        } else {
            sendError("UNKNOWN ROUND STARTING ERROR!!!!!!!", resp, game);
        }
    }

    private void attemptDoubleDown(Blackjack game, HttpServletRequest req,
            HttpServletResponse resp) {
        if (game.doubleDown()) {
            sendSuccess(game, resp, req);
        } else if (game.roundIsOver()) {
            sendError("You may not double down because the round is over.", resp, game);
        } else if (game.getPlayerCards().size() > 2) {
            sendError("You tried to double down after hitting.", resp, game);
        } else if (game.getBid() > game.getPlayerMoney()) {
            sendError("You bid " + game.getBid() + " but you have "
                    + game.getPlayerMoney() + ", so you may not double down.", resp, game);
        } else {
            sendError("UNKNOWN DOUBLE DOWN ERROR!!!!!!!", resp, game);
        }
    }

    private void attemptStand(Blackjack game, HttpServletRequest req,
            HttpServletResponse resp) {
        if (game.stand()) {
            sendSuccess(game, resp, req);
        } else if (game.roundIsOver()) {
            sendError("You may not stand because the round is over.", resp, game);
        } else {
            sendError("UNKNOWN STANDING ERROR!!!!!!!", resp, game);
        }
    }

    private void attemptHit(Blackjack game, HttpServletRequest req,
            HttpServletResponse resp) {
        if (game.hit()) {
            sendSuccess(game, resp, req);
        } else if (game.roundIsOver()) {
            sendError("You may not hit because the round is over.", resp, game);
        } else {
            sendError("UNKNOWN HITTING ERROR!!!!!!!", resp, game);
        }
    }

    private void attemptBid(Blackjack game, HttpServletRequest req,
            HttpServletResponse resp) {
        String amt = req.getParameter("amount");
        if (amt == null) {
            sendError("You forgot to specify a bidding amount.", resp, game);
        } else {
            int amount = Integer.parseInt(amt);
            boolean success = game.makeBid(amount);
            if (success) {
                sendSuccess(game, resp, req);
            } else if (game.roundIsOver()) {
                sendError("You may not bid because the round is over.", resp, game);
            } else if (!game.getPlayerCards().isEmpty()) {
                sendError("You are trying to bid during a turn.", resp, game);
            } else if (amount < 0) {
                sendError("You specified a negative bid of " + amount + ".", resp, game);
            } else if (amount > game.getPlayerMoney()) {
                sendError(
                    "You bid " + amount + " > " + game.getPlayerMoney() + ", your stash.", resp, game);
            } else if (amount == 0) {
                sendError("You bid nothing at the wrong time.", resp, game);
            } else {
                sendError("UNKNOWN BIDDING ERROR!!!!!!!", resp, game);
            }
        }
    }

    private void sendError(String error, HttpServletResponse resp, Blackjack game) {
        JSONObject respObj = new JSONObject();
        try {
            respObj.put("msg", error);
            respObj.put("isSuccess", false);
            if (game != null) {
                respObj.put("gameObj", assembleGameObj(game));
            }
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
            	
            if (game.roundIsOver()) {
	            for (PokerCard c : game.getDealerCards()) {
	                dealerSuits.put(c.getSuit());
	                dealerValues.put(c.getRank());
	            }
            } else {
            	if (game.getDealerCards().size() > 0) {
            		for (PokerCard c : game.getDealerCards().subList(1, game.getDealerCards().size())) {
            			dealerSuits.put(c.getSuit());
            			dealerValues.put(c.getRank());
 	            	}
            	}	
            }
            gameObj.put("playerSuits", playerSuits);
            gameObj.put("playerValues", playerValues);
            gameObj.put("dealerSuits", dealerSuits);
            gameObj.put("dealerValues", dealerValues);
            
            gameObj.put("handIsOver", game.roundIsOver());
            gameObj.put("isDeck", game.deckSize() != 0);
            gameObj.put("hasJustReshuffled", game.getHasReshuffled());
            gameObj.put("lastRoundResult", game.getLastRoundResult());
            return gameObj;
        } catch (JSONException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
