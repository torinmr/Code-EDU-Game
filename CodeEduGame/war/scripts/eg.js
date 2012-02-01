/*
 * eg.js ()
 */
var eg = {
	useRemote: true,
	// Dimensions of the table display
	rows : 2,
	cols : 5,

	// Values that change in game
	money : 500,

	// Values that are for internal use
	deck : null,
	inGame : false,
	turnNum: 0,
	doubled : false,
	playerHand : new Array(),
	dealerHand : new Array(),

	evalLocked : false,
	lockEval : function() {
		eg.evalLocked = true;
	},
	unlockEval : function() {
		eg.evalLocked = false;
	},

	// Game controls
	getBet : function() {
		var bet = parseInt($("#bet").val());
		if (!bet) {
			bet = 0;
		}
		return bet;
	},

	newGame : function() {
		if (eg.inGame) {
			return;
		}
		// Lock the bet
		$("#bet").val(eg.getBet());
		$("#bet").attr('disabled', 'disabled');

		eg.turnNum = 0;

		// Remove leftover messages
		eg.clearBoard();
		
		if (eg.useRemote) {
			rem.rpc('startNextRound', function(s) {
				rem.rpc('bet', function(b) {
					$("#money").html(b.gameObj.money);

					eg.playerHand = new Array();
					eg.dealerHand = new Array();
					
					eg.playerHand.push({
						num : cards.num(b.gameObj.playerValues[0]),
						suit: cards.suit(b.gameObj.playerSuits[0]),
					});
					eg.playerHand.push({
						num : cards.num(b.gameObj.playerValues[1]),
						suit: cards.suit(b.gameObj.playerSuits[1]),
					});
					eg.dealerHand.push({
						num : cards.num(b.gameObj.playerValues[0]),
						suit: cards.suit(b.gameObj.playerSuits[0]),
					});
					eg.dealerHand.push({
						num : cards.num(b.gameObj.playerValues[1]),
						suit: cards.suit(b.gameObj.playerSuits[1]),
					});
					eg.redrawBoard();
					eg.inGame = true;
				}, {amount : eg.getBet()});
			});
			return;
		}
		
		// Unlock other game controls
		eg.ddAble = true;
		eg.inGame = true;
		
		// No blackjacks, Jack
		do {
			eg.playerHand = new Array();
			eg.dealerHand = new Array();
			
			// Make a random deck
			eg.deck = cards.shuffle(cards.makeDeck());
	
			// Deal two cards to each player
			eg.dealerHand.push(eg.deck.pop());
			eg.dealerHand.push(eg.deck.pop());
	
			eg.playerHand.push(eg.deck.pop());
			eg.playerHand.push(eg.deck.pop());
		} while (eg.value(eg.dealerHand) == 21 || eg.value(eg.playerHand) == 21);

		// Display the new game
		eg.redrawBoard();

		// Does anyone have blackjack?
		if (eg.value(eg.dealerHand) == 21 || eg.value(eg.playerHand) == 21) {
			eg.end();
		}
	},
	hit : function() {
		// Check the game lock
		if (!eg.inGame)
			return;
		
		cb.call('hit');
		
		if (eg.useRemote) {
			eg.inGame = false;
			rem.rpc('hit', function(b) {
				eg.inGame = true;
				eg.playerHand.push({
					num : cards.num(b.gameObj.playerValues.pop()),
					suit: cards.suit(b.gameObj.playerSuits.pop()),
				});
				eg.redrawBoard();
				
				if (b.gameObj.roundIsOver) {
					if (b.gameObj.playerIsWinner) {
						$("#gameOut").html("You win.");
					} else {
						$("#gameOut").html("You lose.");
					}
				}
			});
			return;
		}

		eg.turnNum++;
		
		// Add a card and redraw
		eg.playerHand.push(eg.deck.pop());
		eg.redrawBoard();		

		// Check for bust
		if (eg.value(eg.playerHand) > 21) {
			eg.end();
		}
	},
	stand : function() {
		// Check the game lock
		if (!eg.inGame)
			return;
		
		cb.call('stand');
		
		if (eg.useRemote) {
			eg.inGame = false;
			rem.rpc('stand', function(b) {
				eg.inGame = true;
				eg.redrawBoard();
			});
			return;
		}
		
		eg.turnNum++;

		eg.ai();

		// The user can't do anything else
		eg.end();
	},
	ai : function() {
		// Some crappy AI
		while (eg.value(eg.dealerHand) < 15) {
			eg.dealerHand.push(eg.deck.pop());
		}
		eg.redrawBoard();
	},
	// The game has ended, for whatever reason
	end : function() {
		// Check the game lock
		if (!eg.inGame)
			return;

		// Lock the game controls
		eg.inGame = false;

		// Who won? Adjust money accordingly
		if (eg.value(eg.playerHand) > 21) {
			eg.lose("Bust!");
		} else if (eg.value(eg.dealerHand) > 21) {
			eg.win("Dealer bust!");
		} else if (eg.value(eg.playerHand) > eg.value(eg.dealerHand)) {
			eg.win("You win!");
		} else {
			eg.lose("You lose!");
		}
		if (eg.doubled) {
			$("#bet").val(eg.getBet() / 2);
			eg.doubled = false;
		}
		// Reveal hidden card
		$("#display00").html(
				'<img src="'
						+ cards.cardImg(eg.dealerHand[0].suit,
								eg.dealerHand[0].num) + '" />');

		// Update money
		$("#money").html("Money: " + eg.money);
		
		cb.call('end');
	},
	win : function(msg) {
		$("#gameOut").html(msg);
		eg.money += eg.getBet();
		cb.call('won');
	},
	lose : function(msg) {
		$("#gameOut").html(msg);
		eg.money -= eg.getBet();
	},
	// Double down
	doubleDown : function() {
		if (!eg.ddAble) {
			return;
		}
		eg.ddAble = false;
		eg.doubled = true;
		$("#bet").val(2 * eg.getBet());
		eg.hit();
		eg.stand();
	},
	clearBoard: function() {
		$("#gameOut").html('');
		for ( var r = 0; r < eg.rows; r++) {
			for ( var c = 0; c < eg.cols; c++) {
				$("#display" + r + c).html('');
			}
		}
	},
	// Convert game data to HTML
	redrawBoard : function() {
		// Replace "Player" with playerName
		if (name) {
			$("#playerName").html(name);
		}
		if (!eg.inGame)
			return;

		// Draw the dealer's hidden card
		$("#display00").html('<img src="' + cards.backImg() + '" />');

		// Draw the rest of the dealer's hand
		for ( var p = 1; p < eg.dealerHand.length; p++) {
			$("#display0" + p).html(
					'<img src="'
							+ cards.cardImg(eg.dealerHand[p].suit,
									eg.dealerHand[p].num) + '" />');
		}

		// Draw the player's hand
		for ( var p = 0; p < eg.playerHand.length; p++) {
			$("#display1" + p).html(
					'<img src="'
							+ cards.cardImg(eg.playerHand[p].suit,
									eg.playerHand[p].num) + '" />');
		}
	},
	// Calculate the value of a hand
	value : function(hand) {
		var numAces = 0;
		var val = 0;
		for ( var i = 0; i < hand.length; i++) {
			if (hand[i].num == 14) {
				numAces++;
				val += 11;
			} else if (hand[i].num > 10) {
				val += 10;
			} else {
				val += hand[i].num;
			}
		}
		while (numAces > 0 && val > 21) {
			val -= 10;
			numAces--;
		}
		return val;
	},
	// Execute user code
	execCode : function(code) {
		if (eg.evalLocked) {
			return;
		}
		
		// Kludgy, but whatever...
		var sub = {
			'handValue' : '(handValue())',
		};
		for (raw in sub) {
			code = code.replace(new RegExp(raw, 'g'), sub[raw]);
		}

		var turns = 0;
		eg.newGame();
		while (turns < 5 && eg.inGame) {
			try {
				jQuery.globalEval(code);
			} catch (E) {
				$("#debug").html(E.toString());
				cb.call('error', E.toString());
				return;
			}
			turns++;
		}
		ui.minIns();
		eg.redrawBoard();
		eg.ai();
		eg.end();
		
		cb.call('exec');
	}
}