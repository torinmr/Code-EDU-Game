var somethingtrue = false;
var les = {
	// Store lesson information
	currLesson : 'beginning',
	lessonText : '',
	objComplete : false,
	// References to the next lesson
	lessonList : {
		'beginning' : {
			next : 'welcome',
		},
		'welcome' : {
			action : function() {
				// Make manual controls
				$("#buttons").append(ui.makeButton('New Game', eg.newGame));
				$("#buttons").append(ui.makeButton('Hit', eg.hit));
				$("#buttons").append(ui.makeButton('Stand', eg.stand));
				$("#buttons").append(ui.makeButton('Chuck Norris', function() {
					cb.call('won');
				}));

				$("#codebox").attr('disabled', 'disabled');
				$("#eval").attr('disabled', 'disabled');
				$("#evaln").attr('disabled', 'disabled');

				cb.add('won', function() {
					$(document).delay(1000).queue(les.nextLesson);
				});
			},
			next : 'lesson1',
		},
		'lesson1' : {
			action : function() {
				cb.clear();
				ui.maxIns();
				$("#codebox").removeAttr('disabled');
				$("#eval").removeAttr('disabled');
				$("#buttons").html('');
				cb.add('exec', function() {
					if (typeof name == "string") {
						les.nextLesson();
					}
				});
			},
			next : 'lesson2',
		},
		'lesson2' : {
			action : function() {
				cb.clear();
				ui.maxIns();
				cb.add('hit', function() {
					les.objComplete = true;
				});
				cb.add('exec', function() {
					if (les.objComplete) {
						les.objComplete = false;
						les.nextLesson();
					}
				});
			},
			next : 'lesson3.1',
		},
		'lesson3.1' : {
			action : function() {
				cb.clear();
				ui.maxIns();
				cb.add('stand', function() {
					if (ui.getUserCode().indexOf("somethingtrue") != -1) {
						les.objComplete = true;
					}
				});
				cb.add('exec', function() {
					if (les.objComplete) {
						les.objComplete = false;
						les.nextLesson();
					}
				});
			},
			next : 'lesson3.2',
		},
		'lesson3.2' : {
			action : function() {
				cb.clear();
				cb.add('hit', function() {
					if (ui.getUserCode().indexOf("true") != -1) {
						les.objComplete = true;
					}
				});
				cb.add('exec', function() {
					if (les.objComplete) {
						les.objComplete = false;
						les.nextLesson();
					}
				});
			},
			next : 'lesson3.3',
		},
		'lesson3.3' : {
			action : function() {
				cb.clear();
				ui.maxIns();
			},
			next : 'lesson3.4',
		},
		'lesson3.4' : {
			action : function() {
				cb.clear();
				ui.maxIns();
			},
			next : 'lesson4',
		},
		'lesson4' : {
			action : function() {
				cb.clear();
				ui.maxIns();
			},
			next : 'lesson5',
		},
		'lesson5' : {
			action : function() {
				cb.clear();
				ui.maxIns();
			},
			next : 'lesson6',
		},
		'lesson6' : {
			action : function() {
				cb.clear();
				ui.maxIns();
			},
			next : 'lesson7',
		},
	},

	loadLesson : function(lesson) {
		eg.lockEval();
		les.currLesson = lesson;
		$.get('./lessons/' + lesson + '.htm', function(data) {
			if (name) {
				data = data.replace(/\{NAME\}/g, name);
			} else {
				data = data.replace(/\{NAME\}/g, 'friend');
			}
			
			data = data.replace(/\{NEXT\}/g, '<p style="text-align:center"><input type="button" value="Continue" onclick="les.nextLesson()" /></p>');

			les.lessonText = data;
			$("#instructions").fadeOut().queue(function() {
				$("#instructions").html(les.lessonText);
				var toggle = $("<a href='#'></a>");
				toggle.bind('click', function() {
					if (ui.sideMaxed) {
						ui.minIns();
						$(this).html('&gt;');
					} else {
						ui.maxIns();
						$(this).html('&lt;');
					}
				});
				toggle.css({
					float : 'right',
					color : 'black',
					textDecoration : 'none'
				});
				if (ui.sideMaxed) {
					toggle.html('&lt;');
				} else {
					toggle.html('&gt;');
				}
				$("#instructions").prepend(toggle);
				$(this).dequeue();
			}).fadeIn().queue(function() {
				eg.unlockEval();
				$(this).dequeue();
			});
		});
		if (les.lessonList[les.currLesson].action) {
			les.lessonList[les.currLesson].action();
		}
	},
	nextLesson : function() {
		if (!les.lessonList[les.currLesson]) {
			// fail gracefully
		} else {
			les.loadLesson(les.lessonList[les.currLesson].next);
		}
	},
};
