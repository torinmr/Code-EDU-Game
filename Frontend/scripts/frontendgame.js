var eg = {
    // Dimensions of the table display
    rows : 2,
    cols : 5,

    // Values that change in game
    money : 500,
    playerName : '',

    // Values that are for internal use
    deck : null,
    inGame : false,
    playerHand : new Array(),
    dealerHand : new Array(),

    // Game controls
    newGame : function() {
        // Unlock other game controls
        eg.inGame = true;

        // Make a random deck
        eg.deck = cards.shuffle(cards.makeDeck());

        eg.playerHand = new Array();
        eg.dealerHand = new Array();

        // Remove leftover messages
        $("#debug").html('');
        for(var r = 0; r < eg.rows; r++) {
            for(var c = 0; c < eg.cols; c++) {
                $("#display" + r + c).html('');
            }
        }
        $("#display00").html('<img src="' + cards.backImg() + '" />');

        // Deal two cards to each player
        eg.dealerHand.push(eg.deck.pop());
        eg.dealerHand.push(eg.deck.pop());

        eg.playerHand.push(eg.deck.pop());
        eg.playerHand.push(eg.deck.pop());

        // Display the new game
        eg.drawGame();

        // Does anyone have blackjack?
        if(eg.value(eg.dealerHand) == 21 || eg.value(eg.playerHand) == 21) {
            eg.end();
        }
    },
    hit : function() {
        // Check the game lock
        if(!eg.inGame)
            return;

        // Add a card and redraw
        eg.playerHand.push(eg.deck.pop());
        eg.drawGame();

        // Check for bust
        if(eg.value(eg.playerHand) > 21) {
            eg.end();
        }
    },
    stand : function() {
        // Check the game lock
        if(!eg.inGame)
            return;

        // Some crappy AI
        while(eg.value(eg.dealerHand) < 15) {
            eg.dealerHand.push(eg.deck.pop());
        }
        eg.drawGame();

        // The user can't do anything else
        eg.end();
    },
    // The game has ended, for whatever reason
    end : function() {
        // Check the game lock
        if(!eg.inGame)
            return;

        // Lock the game controls
        eg.inGame = false;

        // Who won? Adjust money accordingly
        if(eg.value(eg.playerHand) > 21) {
            $("#debug").html("Bust!");
            eg.money--;
        } else if(eg.value(eg.dealerHand) > 21) {
            $("#debug").html("Dealer bust!");
            eg.money++;
            cb.call('won');
        } else if(eg.value(eg.playerHand) > eg.value(eg.dealerHand)) {
            $("#debug").html("You win!");
            eg.money++;
            cb.call('won');
        } else {
            $("#debug").html("You lose!");
            eg.money--;
        }

        // Reveal hidden card
        $("#display00").html('<img src="' + cards.cardImg(eg.dealerHand[0].suit, eg.dealerHand[0].num) + '" />');

        // Update money
        $("#money").html("Money: " + eg.money);
    },
    // Convert game data to HTML
    drawGame : function() {
        // Replace "Player" with playerName
        if(eg.playerName) {
            $("#playerName").html(eg.playerName);
        }

        // Draw the dealer's hidden card
        $("#display00").html('<img src="' + cards.backImg() + '" />');

        // Draw the rest of the dealer's hand
        for(var p = 1; p < eg.dealerHand.length; p++) {
            $("#display0" + p).html('<img src="' + cards.cardImg(eg.dealerHand[p].suit, eg.dealerHand[p].num) + '" />');
        }

        // Draw the player's hand
        for(var p = 0; p < eg.playerHand.length; p++) {
            $("#display1" + p).html('<img src="' + cards.cardImg(eg.playerHand[p].suit, eg.playerHand[p].num) + '" />');
        }
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
    // Execute user code
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
        cb.call('exec');
    }
}