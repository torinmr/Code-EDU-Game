package com.appspot.codeedugame.deck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

/**
 * @author aschild
 *
 */
@PersistenceCapable
public class PokerDeck {
    private static final int SUITS = 4;
    private static final int MIN_RANK = 2;
    private static final int MAX_RANK = 14;

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
    
    @Persistent
    private Map<String, Integer> quantityMap;
    
    @Persistent
    private List<PokerCard> deckList;
    
    @Persistent
    private int size;

    public PokerDeck() {
        this.quantityMap = new HashMap<String, Integer>();
        this.deckList = new ArrayList<PokerCard>();
        this.size = 0;
    }
    
    public void constructStandardDeck() {
        for (int rank = MIN_RANK; rank < MAX_RANK + 1; rank++) {
            for (int suit = 0; suit < SUITS; suit++) {
                this.discard(new PokerCard(rank, suit));
            }
        }
    }
    
    public void discard(PokerCard card) {
        int count;
        if(!quantityMap.containsKey(card.toString()))
            count = 0;
        else
            count = quantityMap.remove(card.toString());
        count++;
        quantityMap.put(card.toString(), count);
        deckList.add(card);
        this.size++;
    }
    
    public PokerCard draw() {
        if (size == 0) {
            return null;
        }
        PokerCard topCard = deckList.remove(0);
        this.size--;
        int count = this.quantityMap.remove(topCard.toString());
        count--;
        if(count > 0)
            this.quantityMap.put(topCard.toString(), count);
        return topCard;
    }
    
    public boolean draw(PokerDeck card) {
        int quantity = (quantityMap.get(card.toString()) == null) ? 0 : quantityMap.get(card.toString()).intValue();
        if (quantity < 1) {
            return false;
        }
        quantity--;
        quantityMap.put(card.toString(), quantity);
        deckList.remove(card.toString());
        size--;
        return true;
    }
    
    public void shuffle() {
        Collections.shuffle(deckList);
    }
    
    public int getFromQuantityMap(PokerCard card) {
        return (this.quantityMap.get(card.toString()) == null)
                ? 0 : this.quantityMap.get(card.toString());
    }
    
    public int getSize() {
        return this.size;
    }
    
    public boolean contains(PokerDeck that) {
        PokerCard card;
        int counter = 0;
        while(counter < that.getSize()) {
            card = that.draw();
            counter++;
            that.discard(card);
            if(this.getFromQuantityMap(card) < that.getFromQuantityMap(card)) {
                return false;
            }
        }
        return true;
    }
    
    public boolean unorderedEquals(PokerDeck that) {
        PokerCard card;
        int counter = 0;
        while(counter < that.getSize()) {
            card = that.draw();
            counter++;
            that.discard(card);
            if(this.getFromQuantityMap(card) != that.getFromQuantityMap(card)) {
                return false;
            }
        }
        return true;
    }    
    
    public List<PokerCard> getOrdering() {
        return new ArrayList<PokerCard>(deckList);
    }

    public Iterator<PokerCard> iterator() {
        return deckList.iterator();
    }
}