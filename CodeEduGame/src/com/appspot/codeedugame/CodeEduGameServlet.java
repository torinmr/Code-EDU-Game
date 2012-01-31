package com.appspot.codeedugame;

import java.io.IOException;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.servlet.http.*;

import com.appspot.codeedugame.deck.PokerCard;
import com.google.appengine.repackaged.org.json.JSONArray;
import com.google.appengine.repackaged.org.json.JSONException;
import com.google.appengine.repackaged.org.json.JSONObject;

@SuppressWarnings("serial")
public class CodeEduGameServlet extends HttpServlet {
    private final String ID = "THE SCHIZ";
    private final int STARTING_MONEY = 100;
    
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String rpcName = req.getParameter("rpcName");
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            Blackjack game = getGame(ID, pm);
            pm.deletePersistent(game);
            
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
            	pm.deletePersistent(game);
            } else {
                sendError(req.getParameter("rpcName")
                        + " is an invalid move.", resp);
            }
        } finally {
            pm.close();
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

    private Blackjack getGame(String id, PersistenceManager pm) {
        try {
            return pm.getObjectById(Blackjack.class, id);
        } catch (JDOObjectNotFoundException e) {
            Blackjack game = new Blackjack(STARTING_MONEY, id);
            pm.makePersistent(game);
            return game;
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
            return gameObj;
        } catch (JSONException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
