package com.appspot.codeedugame;

import java.util.Iterator;
import java.util.List;

import com.appspot.codeedugame.deck.PokerDeck;
import com.appspot.codeedugame.deck.PokerCard;
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
		
		this.deck = new PokerDeck();
		this.deck.constructStandardDeck();
		this.deck.shuffle();
		
		this.playerCards = new PokerDeck();
		this.dealerCards = new PokerDeck();
		this.discardPile = new PokerDeck();
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
		if (playerCards.getSize() == 0) {
			makeBid(0);
		}
		
		dealPlayerCard();
		if (isBust(playerCards)) {
			playerLose();
		}
	}
	
	// updates the game state to reflect a move of "stand"
	// returns false in case of illegal play, true otherwise.
	public boolean stand() {
		if (roundOver) {
			return false;
		}
		if (playerCards.getSize() == 0) {
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

// private methods
	private void shuffleDeck() {
		PokerDeck dummy;
		dummy = discardPile;
		discardPile = deck;
		deck = dummy;
		deck.shuffle();
	}

	private void dealPlayerCard() {
		PokerCard card = deck.draw();
		if (card == null) {
			shuffleDeck();
			card = deck.draw();
		}
		playerCards.discard(card);
		return;
	}
	
	private void dealDealerCard() {
		PokerCard card = deck.draw();
		if (card == null) {
			shuffleDeck();
			card = deck.draw();
		}
		dealerCards.discard(card);
		return;
	}
	
	private int handvalue(PokerDeck hand) {
		Iterator<PokerCard> it = hand.iterator();
		int value = 0;
		int numAce = 0;
		while (it.hasNext()) {
			PokerCard card = it.next();
			value += getCardValue(card);
			if (card.getRank() == 14) {
				
			}
		}
		
		return value;
	}
	
	private int getCardValue(PokerCard card) {
		if
	}
}	