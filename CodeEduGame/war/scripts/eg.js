/*
 * eg.js ()
 */
function dump(arr,level) {
	var dumped_text = "";
	if(!level) level = 0;
	
	//The padding given at the beginning of the line.
	var level_padding = "";
	for(var j=0;j<level+1;j++) level_padding += "    ";
	
	if(typeof(arr) == 'object') { //Array/Hashes/Objects 
		for(var item in arr) {
			var value = arr[item];
			
			if(typeof(value) == 'object') { //If it is an array,
				dumped_text += level_padding + "'" + item + "' ...\n";
				dumped_text += dump(value,level+1);
			} else {
				dumped_text += level_padding + "'" + item + "' => \"" + value + "\"\n";
			}
		}
	} else { //Stings/Chars/Numbers etc.
		dumped_text = "===>"+arr+"<===("+typeof(arr)+")";
	}
	return dumped_text;
}
var eg = {
	useRemote: false,
	// Dimensions of the table display
	rows : 2,
	cols : 5,

	// Cashmonies
	defaultMoney : 500,
	money : 500,
	
	betValue: 0,
	
	turnActions : new Array(),

	// Values that are for internal use
	deck : null,
	inGame : false,
	turnNum: 0,
	doubled : false,
	playerHand : new Array(),
	dealerHand : new Array(),
	
	// For exec()
	turns: -1,

	evalLocked : false,
	lockEval : function() {
		eg.evalLocked = true;
	},
	unlockEval : function() {
		eg.evalLocked = false;
	},

	newGame : function() {
		if (eg.inGame) {
			return;
		}

		eg.turnNum = 0;

		// Remove leftover messages
		eg.clearBoard();
		
		eg.turnActions = new Array();
		
		if (eg.useRemote) {
			rem.rpc('startNextRound', function(s) {
				rem.rpc('bid', function(b) {
					if (!b.isSuccess) {
						alert(dump(b))
					}
					$("#money").html(b.gameObj.money);

					eg.playerHand = new Array();
					eg.dealerHand = new Array();
					eg.playerHand.push({
						num : b.gameObj.playerValues[0],
						suit: b.gameObj.playerSuits[0],
					});
					eg.playerHand.push({
						num : b.gameObj.playerValues[1],
						suit: b.gameObj.playerSuits[1],
					});
					eg.dealerHand.push({
						num : -1,
						suit: -1,
					});
					eg.dealerHand.push({
						num : b.gameObj.dealerValues[0],
						suit: b.gameObj.dealerSuits[0],
					});
					eg.inGame = true;
					eg.redrawBoard();
				}, {amount : eg.betValue});
			});
			return;
		}
		
		// Unlock other game controls
		eg.inGame = true;
		
		if (les.lessonList[les.currLesson].noBet) {
			eg.bet();
		}
	},
	bet : function() {
		if (!eg.inGame) {
			return;
		}
		// Lock the bet
		var uiBet = parseInt($("#bet").val());
		if (!isNaN(uiBet)) {
			eg.betValue = uiBet;
		}
		
		if (eg.turns > -1 && !les.lessonList[les.currLesson].noBet) {
			eg.turnActions[eg.turns] = true;
		}

		eg.turnNum++;
		
		$("#bet").val(eg.betValue);
		$("#bet").attr('disabled', 'disabled');
		
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
		
		cb.call('bet');

		// Does anyone have blackjack?
		if (eg.value(eg.dealerHand) == 21 || eg.value(eg.playerHand) == 21) {
			eg.end();
		}
	},
	hit : function() {
		// Check the game lock
		if (!eg.inGame)
			return;
		
		if (eg.turns > -1 && eg.turnActions[eg.turns]) {
			throw "Error: Can't perform more than one action per round";
			return;
		}
		if (eg.turns > -1) {
			eg.turnActions[eg.turns] = true;
		}
		
		cb.call('hit');
		
		if (eg.useRemote) {
			eg.inGame = false;
			rem.rpc('hit', function(b) {
				eg.inGame = true;
				eg.playerHand.push({
					num : b.gameObj.playerValues.pop(),
					suit: b.gameObj.playerSuits.pop(),
				});
				eg.redrawBoard();
				if (b.gameObj.handIsOver) {
					if (b.gameObj.lastRoundResult == "win") {
						$("#gameOut").html("You win.");
					} else {
						$("#gameOut").html("You lose.");
					}
					eg.inGame = false;
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
		
		if (eg.turns > -1 && eg.turnActions[eg.turns]) {
			throw "Error: Can't perform more than one action per round";
			return;
		}
		if (eg.turns > -1) {
			eg.turnActions[eg.turns] = true;
		}
		
		cb.call('stand');
		eg.turnNum++;
		
		if (eg.useRemote) {
			eg.inGame = false;
			rem.rpc('stand', function(b) {
				eg.inGame = true;
				eg.redrawBoard();
				if (b.gameObj.handIsOver) {
					$('#codebox').val(dump(b.gameObj));
					eg.dealerHand = new Array();
					for (var i = 0; i < b.gameObj.dealerValues.length; i++) {
						eg.dealerHand.push({
							num : b.gameObj.dealerValues[i],
							suit: b.gameObj.dealerSuits[i],
						});
					}
					eg.redrawBoard();
					$("#display00").html(
							'<img src="'
									+ cards.cardImg(eg.dealerHand[0].suit,
											eg.dealerHand[0].num) + '" />');
					if (b.gameObj.lastRoundResult == "win") {
						$("#gameOut").html("You win.");
					} else {
						$("#gameOut").html("You lose.");
					}
					eg.inGame = false;
				}
			});
			return;
		}
		
		eg.turnNum++;

		eg.ai();

		// The user can't do anything else
		eg.end();
	},
	ai : function() {
		if (eg.playerHand.length == 0) {
			throw "Error: you must bet first.";
		}
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
		$("#bet").removeAttr('disabled');
		// Reveal hidden card
		$("#display00").html(cards.cardImg(eg.dealerHand[0].suit, eg.dealerHand[0].num));

		// Update money
		$("#money").html("Money: " + eg.money);
		
		cb.call('end');
	},
	win : function(msg) {
		$("#gameOut").html(msg);
		eg.money += eg.betValue;
		cb.call('won');
	},
	lose : function(msg) {
		$("#gameOut").html(msg);
		eg.money -= eg.betValue;
	},
	// Double down
	doubleDown : function() {
		if (eg.turnNum > 1) {
			return;
		}
		eg.doubled = true;
		eg.betValue *= 2;
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
		$("#display00").html(cards.backImg());

		// Draw the rest of the dealer's hand
		for ( var p = 1; p < eg.dealerHand.length; p++) {
			$("#display0" + p).html(cards.cardImg(eg.dealerHand[p].suit, eg.dealerHand[p].num));
		}

		// Draw the player's hand
		for ( var p = 0; p < eg.playerHand.length; p++) {
			$("#display1" + p).html(cards.cardImg(eg.playerHand[p].suit, eg.playerHand[p].num));
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
		
		$("#debug").html('');
		eg.betValue = 0;
		
		les.hintUnlocked = true;
		
		// Kludgy, but whatever...
		var sub = {
			'handValue' : '(handValue())',
			'handStart' : '(handStart())',
		};
		for (raw in sub) {
			code = code.replace(new RegExp(raw, 'g'), sub[raw]);
		}

		eg.turns = 0;
		eg.newGame();
		ui.minIns();
		
		// A-NI-MA-TION!!!!!!!!
		
		var animation = window.setInterval(function() {
			// Figure out the turn cap
			var turncap;
			if (code.indexOf('hit()') == -1 && code.indexOf('stand()') == -1) {
				turncap = 1;
			} else {
				turncap = 10;
			}
			try {
				if (eg.turns > turncap || !eg.inGame) {
					if (eg.playerHand.length == 0) {
						throw "Error: you must bet first.";
					}
					eg.ai();
					eg.end();
					cb.call('exec');
					if (typeof name !== 'undefined' && name.match(/david/i)) {
						$("#holycow").show();
					} else {
						$("#holycow").hide();
					}
					window.clearInterval(animation);
					eg.turns = -1;
					return;
				}
			
				jQuery.globalEval(code);
			} catch (E) {
				var errorMsg = E.toString();
				errorMsg = errorMsg.replace(/\w*Error:/, "<span style='color:red'>$&</span>")
				$("#debug").html(errorMsg);
				cb.call('error', E.toString());
				window.clearInterval(animation);
				eg.turns = -1;
				eg.inGame = false;
				return;
			}
			eg.redrawBoard();
			eg.turns++;
		}, 500);
		
		/*while (turns < 10 && eg.inGame) {
			try {
				jQuery.globalEval(code);
			} catch (E) {
				var errorMsg = E.toString();
				errorMsg = errorMsg.replace(/\w*Error:/, "<span style='color:red'>$&</span>")
				$("#debug").html(errorMsg);
				cb.call('error', E.toString());
				return;
			}
			turns++;
		}
		eg.redrawBoard();
		eg.ai();
		eg.end();
		
		cb.call('exec');*/
	}
}