/*
 * ui.js
 * Provides functions dealing with the user interface of the lessons.
 * Sets up said interface.
 */

// Preload images
(function() {
	var preloads = [ "./img/cardsprite.png", "./img/objectivecomplete.png" ];
	var imgCache = new Array();
	for (var i = 0; i < preloads.length; i++) {
		imgCache[i] = new Image();
		imgCache[i].src = preloads[i];
	}
})();

var ui = {
	sideMaxed : true,
	isLoggedIn : false,
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
		$("#toggleIns").attr('src', './img/larr.png');
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
		$("#toggleIns").attr('src', './img/rarr.png');
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

	// Make the textarea fancy
	$('#codebox').tabSupport();
	$('#code').click(function() {
		$('#codebox').focus();
	});

	if ($.cookie("c")) {
		$("#codebox").val($.cookie("c"));
	}

	$("#codebox").change(function() {
		$.cookie("c", $("#codebox").val(), {
			expires : 7
		});
	});

	$("#previousButton").click(function() {
		les.prevLesson();
	});

	$("#continueButton").click(function() {
		les.nextLesson();
	});

	$("#resetMoney").click(function() {
		eg.money = eg.defaultMoney;
		$("#money").html("Money: " + eg.money);
	});
	/*
	 * rem.acc('getLogin', function(l) { if (!l.isLoggedIn) {
	 * $("#log").attr('href', l.URL); $("#log").html('Login'); } else {
	 * ui.isLoggedIn = true; $("#log").html('Logout'); $("#log").attr('href',
	 * l.URL); rem.acc('getName', function(n) { $("#accName").html(n.name); }); }
	 * },{returnURL : document.URL});
	 */

	var toggle = $("#toggleIns");
	toggle.bind('click', ui.toggleIns);

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
			les.currLesson = "lesson0";
		}
		les.loadLesson(les.currLesson);
	});

	// Start the lesson!
	les.loadLesson(les.currLesson);
});
