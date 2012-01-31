package com.appspot.codeedugame;

import java.util.ArrayList;
import java.util.List;

import com.appspot.codeedugame.deck.PokerDeck;
import com.appspot.codeedugame.deck.PokerCard;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class Blackjack {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

	@Persistent(serialized = "true")
	private ArrayList<PokerDeck> decks;
	// 0 = deck, 1 = discard pile, 2 = dealer hand, 3 = player hand.
	
	@Persistent
	private int playerMoney;
	
	@Persistent
	private int bid;
	
	@Persistent
	private boolean roundOver;
	
	@Persistent
	private boolean hasReshuffled;
	
	public Blackjack(int playerMoney, String id) {
		System.out.println("Creating new game.");
		this.playerMoney = playerMoney;
		this.bid = 0;
		this.key = KeyFactory.createKey(Blackjack.class.getSimpleName(), id);
		
		this.decks = new ArrayList<PokerDeck>(4);
		this.decks.add(PokerDeck.make());
		this.decks.add(PokerDeck.make());
		this.decks.add(PokerDeck.make());
		this.decks.add(PokerDeck.make());
		
		this.decks.get(0).constructStandardDeck();
		this.decks.get(0).shuffle();
		
		this.roundOver = true;
		this.hasReshuffled = true;
	}
	
	// Accessors.
	public Key getKey() {
		return key;
	}
	
	public int deckSize() {
		return decks.get(0).getSize();
	}
	
	public List<PokerCard> getPlayerCards() {
		return decks.get(3).getOrdering();
	}
	
	public List<PokerCard> getDealerCards() {
		return decks.get(2).getOrdering();
	}
	
	public int getPlayerMoney() {
		return playerMoney;
	}
	
	public int getBid() {
		return bid;
	}
	
	public boolean roundIsOver() {
		return roundOver;
	}
	
	public boolean getHasReshuffled() {
		return hasReshuffled;
	}

	// real methods
	
	// starts a new round. Returns false if this is illegal (i.e. if
	// in the middle of a round.), returns true otherwise.
	public boolean startNextRound() {
		if (roundOver == false) {
			return false;
		}
		
		bid = 0;
		discardHand(decks.get(2));
		discardHand(decks.get(3));
		roundOver = false;
		return true;
	}
	
	// updates the game state to reflect a move of "hit"
	// returns true if successful, returns false in case of illegal play.
	// if player hits without bidding, will automatically bid 0 for them.
	public boolean hit() {	
		if (roundOver) {
			return false;
		}
		if (decks.get(3).getSize() == 0) {
			makeBid(0);
		}
		dealCard(decks.get(3));
		if (handValue(decks.get(3)) > 21) {
			playerLose();
		}
		if (decks.get(3).getSize() >= 5) {
			playerWin();
		}
	
		return true;
	}
	
	// updates the game state to reflect a move of "stand"
	// returns false in case of illegal play, true otherwise.
	// if player hits without bidding, will automatically bid 0 for them.
	public boolean stand() {
		if (roundOver) {
			return false;
		}
		if (decks.get(3).getSize() == 0) {
			makeBid(0);
		}
		
		dealerFinish();
		return true;
	}
	
	// updates the game state to reflect a move of "double down"
	// returns false in case of illegal play, true otherwise.
	// if player hits without bidding, will automatically bid 0 for them.
	public boolean doubleDown() {
		if (roundOver) {
			return false;
		}
		if (decks.get(3).getSize() > 2) {
			return false;
		}
		if (decks.get(3).getSize() == 0) {
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
		if (roundOver) {
			return false;
		}
		if (decks.get(3).getSize() > 0) {
			return false;
		}
		if (bidAmount < 0) {
			return false;
		}
		if (bidAmount > playerMoney) {
			return false;
		}
		
		bid = bidAmount;
		playerMoney -= bidAmount;

		dealCard(decks.get(3));
		dealCard(decks.get(2));
		dealCard(decks.get(3));
		dealCard(decks.get(2));

		int dealerValue = handValue(decks.get(2));
		int playerValue = handValue(decks.get(3));
		if (dealerValue == 21 && playerValue == 21) {
			tie();		
		} else if (dealerValue == 21) {
			playerLose();
		} else if (playerValue == 21) {
			playerBlackjack();
		}
		
		hasReshuffled = false;
		return true;
	}

// private methods
	private void shuffleDeck() {
		PokerDeck dummy;
		dummy = decks.get(1);
		decks.set(1, decks.get(0));
		decks.set(0, dummy);
		decks.get(0).shuffle();
		hasReshuffled = true;
	}
	//TODO(torinmr): WHAT IS THIS FUNCTION DOING??? NULL POINTERS HERE!
	private void dealCard(PokerDeck hand) {
		PokerCard card = decks.get(0).draw();
		if (card == null) {
			shuffleDeck();
			card = decks.get(0).draw();
		}
		hand.discard(card);
		return;
	}
	
	private int handValue(PokerDeck hand) {
		int value = 0;
		int numAce = 0;
		for (PokerCard card : hand.getOrdering()) {
			value += getCardValue(card);
			if (card.getRank() == 14) {
				numAce++;
			}
		}
		while (value > 21 && numAce > 0) {
			value -= 10;
			numAce -= 1;
		}
		return value;
	}
	
	private void discardHand(PokerDeck hand) {
		PokerCard card = hand.draw();
		while (card != null) {
			decks.get(1).discard(card);
			card = hand.draw();
		}
	}
	
	private int getCardValue(PokerCard card) {
		int rank = card.getRank();
		if (rank >= 2 && rank <= 10) {
			return rank;
		}
		if (rank >= 11 && rank <= 13) {
			return 10;
		}
		if (rank == 14) {
			return 11;
		}
		return 0;
	}
	
	private void playerLose() {
		roundOver = true;
	}
	
	private void playerWin() {
		playerMoney += 2*bid;
		roundOver = true;
	}
	
	private void playerBlackjack() {
		playerMoney += 2.5*bid;
		roundOver = true;
	}
	
	private void tie() {
		playerMoney += bid;
		roundOver = true;
	}
	
	private void dealerFinish() {
		while (handValue(decks.get(2)) < 17) {
			dealCard(decks.get(2));
		}
		if (handValue(decks.get(2)) > 21) {
			playerWin();
			return;
		}
		if (handValue(decks.get(2)) >= handValue(decks.get(3))) {
			playerLose();
		} else {
			playerWin();
		}
	}
}
