/*
 * ui.js
 * Provides functions dealing with the user interface of the lessons.
 * Sets up said interface.
 */
var ui = {
	nextButton : '<p style="text-align:center"><input type="button" value="Continue" onclick="les.nextLesson()" /></p>',
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
		$(".objectives").css({
			width : '200px',
			float : 'right'
		});
		$('#sidebar').animate({
			width : '800px'
		});
	},
	minIns : function() {
		if (!ui.sideMaxed) {
			return;
		}
		ui.sideMaxed = false;
		$(".objectives").css({
			width : '150px',
			float : 'none',
			margin : '10px auto 10px auto'
		});
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
	$("#submit").bind('click', function() {
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
	$('#code').click(function() {
		$('#codebox').focus();
	});

	if (eg.useRemote) {
		rem.rpc('getLogin', function(l) {
			if (l.login) {
				location.href = l.login;
			} else {
				rem.rpc('startGame', function(s) {
				});
			}
		}, {
			returnURL : document.URL
		});
	}

	// Check for bookmarks
	if (location.hash) {
		var lesson = location.hash.substr(1);
		if (les.lessonList[lesson]) {
			les.currLesson = lesson;
		}
	}

	$(window).hashchange(function() {
		var lesson = location.hash.substr(1);
		if (les.lessonList[lesson]) {
			les.currLesson = lesson;
		} else {
			les.currLesson = "welcome";
		}
		les.loadLesson(les.currLesson);
	});

	// Start the lesson!
	les.loadLesson(les.currLesson);
});
