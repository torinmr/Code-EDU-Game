package com.appspot.codeedugame;

<<<<<<< HEAD
import com.appspot.codeedugame.deck.PokerDeck;
=======
import java.util.List;

import com.appspot.codeedugame.deck.PokerDeck;
import com.appspot.codeedugame.deck.PokerCard;
>>>>>>> branch 'master' of https://github.com/torinmr/Code-EDU-Game.git
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
	PokerDeck discardPile;
	
	@Persistent
	PokerDeck playerCards;
	
	@Persistent
	PokerDeck dealerCards;
	
	@Persistent
	int playerMoney;
	
	@Persistent
	int bid;
	
	@Persistent
	boolean roundOver;
	
	public Blackjack(int playerMoney) {
		this.playerMoney = playerMoney;
		this.bid = 0;
		
		this.deck = PokerDeck();
		this.deck.constructStandardDeck();
		this.deck.shuffle();
		
		this.playerCards = PokerDeck();
		this.dealerCards = PokerDeck();
		this.discardPile = PokerDeck();
		this.roundOver = false;
	}
	
	// Accessors.
	public Key getKey() {
		return key;
	}
	
	public int deckSize() {
		return deck.getSize();
	}
	
	public List<PokerCard> getPlayerCards() {
		return playerCards.getOrdering();
	}
	
	public List<PokerCard> getDealerCards() {
		return dealerCards.getOrdering();
	}
	
	public int getPlayerMoney() {
		return playerMoney;
	}
	
	public int getBid() {
		return bid;
	}
	
	public boolean isOver() {
		return roundOver;
	}

	// real methods
	
	// updates the game state to reflect a move of "hit"
	// returns true if successful, returns false in case of illegal play.
	public boolean hit() {
		if (roundOver) {
			return false;
		}
		if (playerCards.getSize == 0) {
			makeBid(0);
		}
		
		dealPlayerCard();
		if (isBust()) {
			playerLose();
		}
	}
	
	// updates the game state to reflect a move of "stand"
	// returns false in case of illegal play, true otherwise.
	public boolean stand() {
		if (roundOver) {
			return false;
		}
		if (playerCards.getSize == 0) {
			makeBid(0);
		}
		
		dealerFinish();
		return true;
	}
	
	// updates the game state to reflect a move of "double down"
	// returns false in case of illegal play, true otherwise.
	public boolean doubleDown() {
		if (roundOver) {
			return false;
		}
		
		if (playerCards.getSize() > 2) {
			return false;
		}
		
		if (playerCards.getSize() == 0) {
			makeBid(0);
		}
		
		if (bid > playerMoney) {
			return false;
		}
		playerMoney -= bid;
		bid *= 2;
		
		hit();
		stand();
		return true;
	}
	
	// sets the bid amount to the given value. Returns false in case of
	// illegal play, true otherwise.
	public boolean makeBid(int bidAmount) {
		if (playerCards.getSize() > 0) {
			return false;
		}
		if (bidAmount > playerMoney) {
			return false;
		}
		
		bid = bidAmount;
		playerMoney -= bidAmount;

		dealPlayerCard();
		dealDealerCard();
		dealPlayerCard();
		dealDealerCard();
		return true;
	}
}

// private methods
private void dealPlayerCard() {
	if (deck.draw(card) == null) {
		
	}
	return;
}
