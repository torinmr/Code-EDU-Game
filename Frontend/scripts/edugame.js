var eg = {
    cbImg : "img/cards/back-blue-75-3.png",
    rows : 2,
    cols : 5,
    money : 500,
    playerName : '',
    card : function(s, n) {
        return "img/cards/" + eg.suit(s) + "-" + eg.num(n) + "-75.png";
    },
    deck : null,
    inGame : false,
    playerHand : new Array(),
    dealerHand : new Array(),

    newGame : function() {
        eg.inGame = true;
        eg.deck = eg.shuffle(eg.makeDeck());

        eg.playerHand = new Array();
        eg.dealerHand = new Array();

        $("#debug").html('');

        for(var r = 0; r < eg.rows; r++) {
            for(var c = 0; c < eg.cols; c++) {
                $("#display" + r + c).html('');
            }
        }
        $("#display00").html('<img src="' + eg.cbImg + '" />');

        eg.dealerHand.push(eg.deck.pop());
        eg.dealerHand.push(eg.deck.pop());

        eg.playerHand.push(eg.deck.pop());
        eg.playerHand.push(eg.deck.pop());
        
        eg.drawGame();
        
        if(eg.value(eg.dealerHand) == 21 || eg.value(eg.playerHand) == 21) {
            eg.end();
        }
    },
    drawGame : function() {
        if(eg.playerName) {
            $("#playerName").html(eg.playerName);
        }
        $("#display01").html('<img src="' + eg.cbImg + '" />');
        for(var p = 1; p < eg.dealerHand.length; p++) {
            $("#display0" + (p + 1)).html('<img src="' + eg.card(eg.dealerHand[p].suit, eg.dealerHand[p].num) + '" />');
        }
        for(var p = 0; p < eg.playerHand.length; p++) {
            $("#display1" + p).html('<img src="' + eg.card(eg.playerHand[p].suit, eg.playerHand[p].num) + '" />');
        }
    },
    hit : function() {
        if(!eg.inGame)
            return;
        eg.playerHand.push(eg.deck.pop());
        eg.drawGame();
        if(eg.value(eg.playerHand) > 21) {
            eg.end();
        }
    },
    stand : function() {
        if(!eg.inGame)
            return;
        while(eg.value(eg.dealerHand) < 15) {
            eg.dealerHand.push(eg.deck.pop());
        }
        eg.drawGame();
        eg.end();
    },
    end : function() {
        if(!eg.inGame)
            return;
        eg.inGame = false;
        if(eg.value(eg.playerHand) > 21) {
            $("#debug").html("Bust!");
            eg.money--;
        } else if(eg.value(eg.dealerHand) > 21) {
            $("#debug").html("Dealer bust!");
            eg.money++;
        } else if(eg.value(eg.playerHand) > eg.value(eg.dealerHand)) {
            $("#debug").html("You win!");
            eg.money++;
        } else {
            $("#debug").html("You lose!");
            eg.money--;
        }
        $("#display01").html('<img src="' + eg.card(eg.dealerHand[0].suit, eg.dealerHand[0].num) + '" />');
        $("#money").html("Money: " + eg.money);
    },
    // Calculate the value of a hand
    value : function(hand) {
        var numAces = 0;
        var val = 0;
        for(var i = 0; i < hand.length; i++) {
            if(hand[i].num == 14) {
                numAces++;
                val += 11;
            } else if(hand[i].num > 10) {
                val += 10;
            } else {
                val += hand[i].num;
            }
        }
        while(numAces > 0 && val > 21) {
            val -= 10;
            numAces--;
        }
        return val;
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
    execCode : function(code) {
        var sub = {
            'hit' : 'eg.hit',
            'stand' : 'eg.stand',
            'value' : 'eg.value(eg.playerHand)',
            'name' : 'eg.playerName',
        };
        for(raw in sub) {
            code = code.replace(new RegExp(raw, 'g'), sub[raw]);
        }
        var turns = 0;
        eg.newGame();
        while(turns < 20 && eg.inGame) {
            eval(code);
            turns++;
        }
        eg.drawGame();
        eg.end();
    }
}
$(document).ready(function() {
    for(var r = 0; r < eg.rows; r++) {
        var tr = document.createElement("tr");
        for(var c = 0; c < eg.cols; c++) {
            var td = document.createElement("td");
            td.style.width = "80px";
            td.setAttribute("id", "display" + r + c);
            tr.appendChild(td);
        }
        $("#display").append(tr);
    }
    $("#display00").html('<img src="' + eg.cbImg + '" />');
    $("#money").html("Money: " + eg.money);

    var ngButton = $("<input type='button' value='New Game' />");
    ngButton.bind('click', eg.newGame);
    $("#buttons").append(ngButton);

    var hitButton = $("<input type='button' value='Hit' />");
    hitButton.bind('click', eg.hit);
    $("#buttons").append(hitButton);

    var standButton = $("<input type='button' value='Stand' />");
    standButton.bind('click', eg.stand);
    $("#buttons").append(standButton);

    $("#eval").bind('click', function() {
        eg.execCode($("#codebox").val());
    });

    $("#evaln").bind('click', function() {
        var n = parseInt(prompt('Enter how many games you want to play:', ''));
        for(var i = 0; i < n; i++) {
            eg.execCode($("#codebox").val());
        }
    });

    $('#codebox').tabSupport();
});
