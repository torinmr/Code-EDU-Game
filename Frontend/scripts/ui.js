var ui = {
    makeButton : function(label, func) {
        var but = $("<input type='button' value='" + label + "' />");
        but.bind('click', func);
        return but;
    },
};

// Set up the document
$(document).ready(function() {
    // Make the board
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
    $("#money").html("Money: " + eg.money);
    
    // Make automatic controls
    $("#eval").bind('click', function() {
        eg.execCode($("#codebox").val());
    });
    $("#evaln").bind('click', function() {
        var n = parseInt(prompt('Enter how many games you want to play:', ''));
        for(var i = 0; i < n; i++) {
            eg.execCode($("#codebox").val());
        }
    });
    
    // Make the textarea fancy
    $('#codebox').tabSupport();
    
    // Start the lesson!
    les.nextLesson();
});
