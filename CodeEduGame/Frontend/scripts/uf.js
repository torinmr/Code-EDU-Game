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

var name = '';