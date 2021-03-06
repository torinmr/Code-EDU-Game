package com.appspot.codeedugame.deck;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @author aschild
 *
 */
public class PokerDeck implements Serializable {
    private static final int SUITS = 4;
    private static final int MIN_RANK = 2;
    private static final int MAX_RANK = 14;
    static final long serialVersionUID = 1;

    private HashMap<String, Integer> quantityMap;
    
    private ArrayList<PokerCard> deckList;
    
    private int size;

    private PokerDeck() {} 
    public static PokerDeck make() {
        PokerDeck deck = new PokerDeck();
        deck.quantityMap = new HashMap<String, Integer>();
        deck.deckList = new ArrayList<PokerCard>();
        deck.size = 0;
        return deck;
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
    
    public boolean draw(PokerCard card) {
        int quantity = (quantityMap.get(card.toString()) == null) ? 0 : quantityMap.get(card).intValue();
        if (quantity < 1) {
            return false;
        }
        quantity--;
        quantityMap.put(card.toString(), quantity);
        deckList.remove(card);
        size--;
        return true;
    }
    
    public void shuffle() {
        Collections.shuffle(deckList);
    }
    
    public int getFromQuantityMap(PokerCard card) {
        return (this.quantityMap.get(card.toString()) == null)
                ? 0 : this.quantityMap.get(card);
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

    /*
    @Override
    public Iterator<PokerCard> iterator() {
        return deckList.iterator();
    }*/
}
