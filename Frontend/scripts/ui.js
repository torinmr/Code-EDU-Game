var ui = {
	sideMaxed : true,
	makeButton : function(label, func) {
		var but = $("<input type='button' value='" + label + "' />");
		but.bind('click', func);
		return but;
	},
	maxIns : function() {
		if (ui.sideMaxed) {
			return;
		}
		ui.sideMaxed = true;
		$('#sidebar').animate({
			width : '800px'
		});
	},
	minIns : function() {
		if (!ui.sideMaxed) {
			return;
		}
		ui.sideMaxed = false;
		$('#sidebar').animate({
			width : '200px'
		});
	},
	toggleIns : function() {
		if (ui.sideMaxed) {
			ui.minIns();
		} else {
			ui.maxIns();
		}
	},
	disableEval : function() {
		$("#eval").attr('disabled', 'disabled');
	},
	enableEval : function() {
		$("#eval").removeAttr('disabled');
	},
	getUserCode : function() {
		return $("#codebox").val();
	},
};

// Set up the document
$(document).ready(function() {
	// Make the board
	for ( var r = 0; r < eg.rows; r++) {
		var tr = document.createElement("tr");
		for ( var c = 0; c < eg.cols; c++) {
			var td = document.createElement("td");
			td.style.width = "80px";
			td.setAttribute("id", "display" + r + c);
			tr.appendChild(td);
		}
		$("#display").append(tr);
	}
	$("#bet").removeAttr('disabled');
	$("#money").html("Money: " + eg.money);

	// Make automatic controls
	$("#eval").bind('click', function() {
		eg.execCode($("#codebox").val());
	});
	$("#evaln").bind('click', function() {
		var n = parseInt(prompt('Enter how many games you want to play:', ''));
		for ( var i = 0; i < n; i++) {
			eg.execCode(ui.getUserCode());
		}
	});

	// Make the textarea fancy
	$('#codebox').tabSupport();

	// Start the lesson!
	les.nextLesson();
});
