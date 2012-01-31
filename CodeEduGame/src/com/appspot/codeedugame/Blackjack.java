package com.appspot.codeedugame;

import java.util.Iterator;
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

	@Persistent(dependent = "true")
	private PokerDeck deck;
	
	@Persistent(dependent = "true")
	private PokerDeck discardPile;
	
	@Persistent(dependent = "true")
	private PokerDeck playerCards;
	
	@Persistent(dependent = "true")
	private PokerDeck dealerCards;
	
	@Persistent
	private int playerMoney;
	
	@Persistent
	private int bid;
	
	@Persistent
	private boolean roundOver;
	
	@Persistent
	private boolean hasReshuffled;
	
	public Blackjack(int playerMoney, String id) {
		this.playerMoney = playerMoney;
		this.bid = 0;
		this.key = KeyFactory.createKey(Blackjack.class.getSimpleName(), id);
		
		this.deck = new PokerDeck();
		this.deck.constructStandardDeck();
		this.deck.shuffle();
		
		this.playerCards = new PokerDeck();
		this.dealerCards = new PokerDeck();
		this.discardPile = new PokerDeck();
		this.roundOver = true;
		this.hasReshuffled = true;
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
		discardHand(playerCards);
		discardHand(dealerCards);
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
		if (playerCards.getSize() == 0) {
			makeBid(0);
		}
		dealCard(playerCards);
		if (handValue(playerCards) > 21) {
			playerLose();
		}
		if (playerCards.getSize() >= 5) {
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
		if (playerCards.getSize() == 0) {
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
		if (roundOver) {
			return false;
		}
		if (playerCards.getSize() > 0) {
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

		dealCard(playerCards);
		dealCard(dealerCards);
		dealCard(playerCards);
		dealCard(dealerCards);

		int dealerValue = handValue(dealerCards);
		int playerValue = handValue(playerCards);
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
		dummy = discardPile;
		discardPile = deck;
		deck = dummy;
		deck.shuffle();
		hasReshuffled = true;
	}

	private void dealCard(PokerDeck hand) {
		PokerCard card = deck.draw();
		if (card == null) {
			shuffleDeck();
			card = deck.draw();
		}
		hand.discard(card);
		return;
	}
	
	private int handValue(PokerDeck hand) {
		Iterator<PokerCard> it = hand.iterator();
		int value = 0;
		int numAce = 0;
		while (it.hasNext()) {
			PokerCard card = it.next();
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
			discardPile.discard(card);
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
		while (handValue(dealerCards) < 17) {
			dealCard(dealerCards);
		}
		if (handValue(dealerCards) > 21) {
			playerWin();
			return;
		}
		if (handValue(dealerCards) >= handValue(playerCards)) {
			playerLose();
		} else {
			playerWin();
		}
	}
}
