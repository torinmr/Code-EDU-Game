package com.appspot.codeedugame;

import java.util.Iterator;
import java.util.List;
import java.io.*;

import com.appspot.codeedugame.deck.PokerCard;


public class TestBlackjack {
	public static void main(String[] args) {
		Blackjack game = new Blackjack(100, "BLDSFKO:JF:LDJF");
		InputStreamReader istream = new InputStreamReader(System.in);
		BufferedReader bufRead = new BufferedReader(istream);
		
		System.out.println("Welcome to Blackjack!");
		while (true) {
			
			System.out.printf("Funds: %d Bid: %d\n", game.getPlayerMoney(),
					game.getBid());
	
			System.out.printf("Cards in deck: %d\n", game.deckSize());
			
			System.out.print("Dealer cards: ");
			List<PokerCard> dealerCards = game.getDealerCards();
			Iterator<PokerCard>dealerIt = dealerCards.iterator();
			while (dealerIt.hasNext()) {
				System.out.print((PokerCard) dealerIt.next());
				System.out.print(" ");
			}
			System.out.println();
			
			System.out.print("Player cards: ");
			List<PokerCard> playerCards = game.getPlayerCards();
			Iterator<PokerCard>playerIt = playerCards.iterator();
			while (playerIt.hasNext()) {
				System.out.print((PokerCard) playerIt.next());
				System.out.print(" ");
			}
			System.out.println();
			
			if (!game.roundIsOver()) {
				System.out.println("Make a move! (type 1 for bid, 2 for stand," +
						"3 for hit, or 4 for double.");
		 
				int commandNumber = -1;
				try {
					String command = bufRead.readLine();
					commandNumber = Integer.parseInt(command);
				}
				catch (IOException err) {
					System.out.println("Error reading line");
				}
				catch(NumberFormatException err) {
					System.out.println("Error Converting Number");
				} 
				
				if (commandNumber == 1) {
					System.out.println("How much do you want to bid?");
					int bidNum = 0;
					try {
						String bidString = bufRead.readLine();
						bidNum = Integer.parseInt(bidString);
					}
					catch (IOException err) {
						System.out.println("Error reading line");
					}
					catch(NumberFormatException err) {
						System.out.println("Error Converting Number");
					} 
					if (!game.makeBid(bidNum)) {
						System.out.println("Error.");
					}
				} else if (commandNumber == 2) {
					if (!game.stand()) {
						System.out.println("Error.");
					}
				} else if (commandNumber == 3) {
					if (!game.hit()) {
						System.out.println("Error.");
					}
				} else if (commandNumber == 4) {
					if (!game.doubleDown()) {
						System.out.println("Error.");
					}
				}
			} else {
				while (true) {
					System.out.println("Type 1 to start a game!");
					int commandNumber = 0;
					try {
						String commandString = bufRead.readLine();
						commandNumber = Integer.parseInt(commandString);
					}
					catch (IOException err) {
						System.out.println("Error reading line");
					}
					catch(NumberFormatException err) {
						System.out.println("Error Converting Number");
					} 
					if (commandNumber == 1) {
						game.startNextRound();
						break;
					}
				}
			}
		}
	}
}
