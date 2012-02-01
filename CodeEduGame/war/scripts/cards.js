/*
 * cards.js
 * Provide functions that allow for card management.
 */
var cards = {
	// Get card images
	backImg : function() {
		return $("<div></div>").css({
			width : "75px",
			height : "108px",
			backgroundImage: "url('./img/cardsprite.png')",
			backgroundPosition: "-0px -428px",
			backgroundRepeat: "no-repeat",
			backgroundColor: "transparent",
		});
		// return "img/cards/back-blue-75-3.png";
	},
	cardImg : function(s, n) {
		return $("<div></div>").css({
			width : "75px",
			height : "108px",
			backgroundImage: "url('./img/cardsprite.png')",
			backgroundPosition: "-" + ((n-2)*75) + "px -" + (s*107) + "px",
			backgroundRepeat: "no-repeat",
			backgroundColor: "transparent",
		});
		// return "img/cards/" + cards.suit(s) + "-" + cards.num(n) + "-75.png";
	},

	// New deck
	makeDeck : function() {
		var deck = new Array();
		for ( var n = 2; n <= 14; n++) {
			for ( var s = 0; s <= 3; s++) {
				deck.push({
					num : n,
					suit : s,
				});
			}
		}
		return deck;
	},

	// Shuffle an array
	shuffle : function(array) {
		var tmp, current, top = array.length;
		if (top)
			while (--top) {
				current = Math.floor(Math.random() * (top + 1));
				tmp = array[current];
				array[current] = array[top];
				array[top] = tmp;
			}
		return array;
	},

	// Convert number to card number
	num : function(num) {
		num = parseInt(num);
		if (num <= 10) {
			return "" + num;
		} else {
			return (new Array(11)).concat([ "j", "q", "k", "a" ])[num];
		}
	},

	// Convert number to suit
	suit : function(suitNum) {
		suitNum = parseInt(suitNum);
		return [ "clubs", "diamonds", "hearts", "spades" ][suitNum];
	},
};
