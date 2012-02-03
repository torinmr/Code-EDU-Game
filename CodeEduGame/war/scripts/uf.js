/*
 * uf.js (User Functions)
 * Provides variables and functions for the student to use
 */
var uf = {
		
}

var hit = eg.hit;
var stand = eg.stand;
var value = function() {
	return eg.value(eg.playerHand);
}
var handValue = function() {
	var hand = new Array();
	for ( var i = 0; i < eg.playerHand.length; i++) {
		if (eg.playerHand[i].num == 14) {
			hand.push(11);
		} else if (eg.playerHand[i].num > 10) {
			hand.push(10);
		} else {
			hand.push(eg.playerHand[i].num);
		}
	}
	return hand;
}
var secondDealtCardVal = function() {
	return (handValue())[1];
}

var bet = function(b) {
	b = parseInt(b);
	if (isNaN(b)) {
		throw "Error: bet() must be called with an integer.";
		return;
	} else if (eg.turnNum != 0) {
		throw "Error: bet() cannot be called after other actions.";
		return;
	} else {
		eg.betValue = b;
	}
}

var handStart = function() {
	return eg.turnNum == 0;
}

var totalMoney = function() {
	return eg.money;
}

var name = '';