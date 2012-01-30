package com.appspot.codeedugame;

import java.io.IOException;

import javax.jdo.PersistenceManager;
import javax.servlet.http.*;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.repackaged.org.json.JSONObject;

@SuppressWarnings("serial")
public class CodeEduGameServlet extends HttpServlet {
    private final String ID = "THE SCHIZ";
    private final int STARTING_MONEY = 100;
    
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String rpcName = req.getParameter("rpcName");
        Blackjack game = getGame(ID);
        
        if (rpcName.equals("bid")) {
            attemptBid(game, req, resp);
        } else if (rpcName.equals("hit")) {
            attemptHit(game, req, resp);
        } else if (rpcName.equals("stand")) {
            attemptStand(game, req, resp);
        } else if (rpcName.equals("doubleDown")) {
            attemptDoubleDown(game, req, resp);
        } else {
            sendError(resp, req);
        }
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            pm.makePersistent(game);
        } finally {
            pm.close();
        }
    }

    private void attemptDoubleDown(Blackjack game, HttpServletRequest req,
            HttpServletResponse resp) {
        boolean success;
        
    }

    private void attemptStand(Blackjack game, HttpServletRequest req,
            HttpServletResponse resp) {
        // TODO Auto-generated method stub
        
    }

    private void attemptHit(Blackjack game, HttpServletRequest req,
            HttpServletResponse resp) {
        // TODO Auto-generated method stub
        
    }

    private void attemptBid(Blackjack game, HttpServletRequest req,
            HttpServletResponse resp) {
        // TODO Auto-generated method stub
        
    }

    private Blackjack getGame(String id) {
        Key k = KeyFactory.createKey(Blackjack.class.getSimpleName(), id);
        Blackjack game = PMF.get().getPersistenceManager().getObjectById(Blackjack.class, k);
        if (game != null) {
            return game;
        } else {
            return new Blackjack(STARTING_MONEY);
        }
    }

    private void sendError(HttpServletResponse resp, HttpServletRequest req) {
        
    }
    
    private void sendSuccess(
            Blackjack game, HttpServletResponse resp, HttpServletRequest req) {
        
    }
}
