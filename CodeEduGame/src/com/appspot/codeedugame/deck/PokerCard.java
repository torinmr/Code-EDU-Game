package com.appspot.codeedugame.deck;

import java.io.Serializable;

import javax.jdo.annotations.PersistenceCapable;

@PersistenceCapable
public class PokerCard implements Comparable<PokerCard>, Serializable {
    private static final long serialVersionUID = 1L;
    private static final String SUIT_NAMES[] = {"C", "D", "H", "S"};
	private static final String RANK_NAMES[] = {"","","2","3","4","5",
												"6","7","8","9","10",
												"J","Q","K","A"};
    private int rank;// 2 through 14, 2 is 2, 3 is 3, ..., 14 is Ace   
    private int suit;//0 through 3, clubs diamonds hearts spades
    
    public PokerCard(int rank, int suit) {
        this.rank = rank;
        this.suit = suit;
    }
    
    public int getRank() {
        return rank;
    }
    
    public int getSuit() {
        return suit;
    }
    
    @Override
    public int compareTo(PokerCard o) {
        return o.rank - this.rank;
    }
    
    @Override
    public boolean equals(Object o) {
        if(o == null) {
            return false;
        } if (!(o instanceof PokerCard)) {
            return false;
        }
        
        PokerCard that = (PokerCard) o;
        if (this.rank != that.rank) {
            return false;
        } if (this.suit != that.suit) {
            return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        return 31 * suit + rank;
    }
    
    @Override
    public String toString() {
        String result = SUIT_NAMES[this.suit] + RANK_NAMES[this.rank];
        return result;
    }
}
