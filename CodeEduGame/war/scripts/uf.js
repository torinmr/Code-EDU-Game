/*
 * uf.js (User Functions)
 * Provides variables and functions for the student to use
 */
var uf = {
		
}

var hit = function() {
	if (eg.playerHand.length == 0) {
		throw "Error: you must bet first.";
	}
	eg.hit();
}
var stand = function() {
	if (eg.playerHand.length == 0) {
		throw "Error: you must bet first.";
	}
	eg.stand();
}
var value = function() {
	return eg.value(eg.playerHand);
};
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
};
var secondDealtCardVal = function() {
	return (handValue())[1];
};

var bet = function(b) {
	b = parseInt(b);
	if (isNaN(b)) {
		throw "Error: bet() must be called with an integer.";
	} else if (eg.turnNum != 0) {
		throw "Error: bet() cannot be called after other actions.";
	} else if (b <= 0) {
		throw "Error: You need to bet() a positive amount.";
	} else {
		eg.betValue = b;
		eg.bet();
	}
};

var doubleDown = function() {
	if (eg.turnNum > 1) {
		throw "Error: doubleDown() must be called on the first turn.";
	}
	eg.doubleDown();
};

var dealerUpCard = function() {
	if (eg.dealerHand[1].num == 14) {
		return 11;
	} else if (eg.dealerHand[1].num > 10) {
		return 10;
	} else {
		return eg.dealerHand[1].num;
	}
}

var handStart = function() {
	return eg.turnNum == 0;
};

var totalMoney = function() {
	return eg.money;
};

var name = '';