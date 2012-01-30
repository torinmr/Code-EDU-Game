package com.appspot.codeedugame;

import com.appspot.codeedugame.deck.PokerDeck;
import com.google.appengine.api.datastore.Key;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class Blackjack {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

	@Persistent
	PokerDeck deck;
	
	@Persistent
	PokerDeck playerCards;
	
	@Persistent
	PokerDeck dealerCards;
	
	@Persistent
	int playerMoney;
	
	@Persistent
	int bid;

	public Blackjack(int playerMoney) {
		this.playerMoney = playerMoney;
		this.bid = 0;
		// insert code for initializing deck.
	}
	
	// Accessors.
	public Key getKey() {
		return key;
	}
	
	public int deckSize() {
		// insert code.
	}
	
	public Deck getPlayerCards() {
		// insert
	}
	
	public Deck getDealerCards() {
		// insert
	}
	
	public int getPlayerMoney() {
		return playerMoney;
	}
	
	public int getBid() {
		return bid;
	}

	// real methods
	
	// updates the game state to reflect a move of "hit"
	// returns true if successful, returns false in case of error.
	public boolean hit() {
		
	}
	
	// updates the game state to reflect a move of "stand"
	// returns true if successful, false in case of error.
	public boolean doubleDown() {
		if (bid > playerMoney) {
			return false;
		}
		playerMoney -= bid;
		bid *= 2;
		
		return hit();
	}
	
	public boolean makeBid() {
		
	}
}
