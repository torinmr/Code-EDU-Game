package com.appspot.codeedugame.deck;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class PokerCard implements Comparable<PokerCard> {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
    
    @Persistent
    private int rank;// 2 through 14, 2 is 2, 3 is 3, ..., 14 is Ace
    
    @Persistent
    private int suit;//0 through 3, clubs diamonds hearts spades
    
    private PokerCard() {}
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
        String result = "Suit: " + this.suit + "\nRank: " + this.rank;
        return result;
    }
}
