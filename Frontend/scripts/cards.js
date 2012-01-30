var cards = {
    // Get card images
    backImg : function() {
        return "img/cards/back-blue-75-3.png";
    },
    cardImg : function(s, n) {
        return "img/cards/" + cards.suit(s) + "-" + cards.num(n) + "-75.png";
    },
    // New deck
    makeDeck : function() {
        var deck = new Array();
        for(var n = 2; n <= 14; n++) {
            for(var s = 0; s <= 3; s++) {
                deck.push({
                    num : n,
                    suit : s,
                });
            }
        }
        return deck;
    },
    // Convert number to card number
    num : function(num) {
        num = parseInt(num);
        if(num <= 10) {
            return "" + num;
        } else {
            return (new Array(11)).concat(["j","q","k","a"])[num];
        }
    },
    // Shuffle an array
    shuffle : function(array) {
        var tmp, current, top = array.length;
        if(top)
            while(--top) {
                current = Math.floor(Math.random() * (top + 1));
                tmp = array[current];
                array[current] = array[top];
                array[top] = tmp;
            }
        return array;
    },
    // Convert number to suit
    suit : function(suitNum) {
        suitNum = parseInt(suitNum);
        return ["clubs", "diamonds", "hearts", "spades"][suitNum];
    },
};
