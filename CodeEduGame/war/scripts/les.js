var les = {
	// Store lesson information
	currLesson : 'welcome',
	lessonText : '',
	objComplete : false,
	objectives : {},
	flags : {},

	// Invalidate code with errors
	invalidated : false,
	invalidate : function() {
		les.invalidated = true;
	},

	// References to the next lesson
	lessonList : {
		'welcome' : {
			action : function() {
				// Make manual controls
				$("#buttons").fadeIn();
				$("#buttons").append(ui.makeButton('New Game', eg.newGame));
				$("#buttons").append(ui.makeButton('Hit', eg.hit));
				$("#buttons").append(ui.makeButton('Stand', eg.stand));

				$("#codebox").attr('disabled', 'disabled');
				$("#eval").attr('disabled', 'disabled');
				$("#evaln").attr('disabled', 'disabled');
				cb.add('end', les.checkObjectives);
				ui.minIns();
			},
			objectives : [ {
				text : "Play a round of Blackjack",
				check : function() {
					return true;
				},
			} ],
			next : 'lesson1',
		},
		'lesson1' : {
			action : function() {
				cb.clear();
				ui.maxIns();
				$("#codebox").removeAttr('disabled');
				$("#eval").removeAttr('disabled');
				/*$("#buttons").fadeOut().queue(function() {
					$(this).html('');
				});*/
				$("#buttons").html('');
				cb.add('exec', les.checkObjectives);
			},
			objectives : [ {
				text : "Set your name",
				check : function() {
					if (typeof name == "string" && name.length > 0) {
						return true;
					}
					return false;
				},
			} ],
			next : 'lesson2',
		},
		'lesson2' : {
			action : function() {
				cb.clear();
				ui.maxIns();
				cb.add('hit', les.checkObjectives);
			},
			objectives : [ {
				text : "Hit every turn",
				check : function() {
					return true;
				},
			} ],
			next : 'lesson3.1',
		},
		'lesson3.1' : {
			action : function() {
				cb.clear();
				ui.maxIns();
				cb.add('error', function(E) {
					if (E.indexOf("somethingtrue") != -1
							&& ui.getUserCode().indexOf("if") != -1) {
						les.checkObjectives();
					}
				});
			},
			objectives : [ {
				text : "Learn structure of if-else statement",
				check : function() {
					return true;
				},
			} ],
			next : 'lesson3.2',
		},
		'lesson3.2' : {
			action : function() {
				cb.clear();
				cb.add('exec', les.checkObjectives);
			},
			objectives : [
					{
						text : "Evaluate an if statement with a boolean",
						check : function() {
							var code = ui.getUserCode();
							return (code.indexOf('true') != -1
									&& code.indexOf('if') != -1 && code
									.indexOf('else') != -1);
						},
					},
					{
						text : "Evaluate an if statement with a numerical boolean expression",
						check : function() {
							var code = ui.getUserCode();
							return (code.indexOf('==') != -1
									&& code.indexOf('if') != -1 && code
									.indexOf('else') != -1);
						},
					},
					{
						text : "Evaluate an if statement with a boolean expression using variables",
						check : function() {
							var code = ui.getUserCode();
							return (code.indexOf('name') != -1
									&& code.indexOf('==') != -1
									&& code.indexOf('if') != -1 && code
									.indexOf('else') != -1);
						},
					} ],
			next : 'lesson3.3',
		},
		'lesson3.3' : {
			action : function() {
				cb.clear();
				ui.maxIns();
				cb.add('exec', les.checkObjectives);
			},
			objectives : [ {
				text : "Implement a boolean expression that evaluates your cards",
				check : function() {
					var code = ui.getUserCode();
					return (code.indexOf('secondDealtCardVal') != -1
							&& code.indexOf('if') != -1 && code.indexOf('else') != -1);
				},
			} ],
			next : 'lesson4.1',
		},
		'lesson4.1' : {
			action : function() {
				cb.clear();
				ui.maxIns();
				cb.add('error', function(E) {
					if (E.indexOf("totalValue") != -1
							&& ui.getUserCode().indexOf("if") != -1) {
						les.checkObjectives();
					}
				});
			},
			objectives : [ {
				text : "Create a better boolean expression",
				check : function() {
					return true;
				},
			} ],
			next : 'lesson4.2',
		},
		'lesson4.2' : {
			action : function() {
				cb.clear();
				ui.maxIns();
				cb.add('exec', les.checkObjectives);
			},
			objectives : [ {
				text : "Write a function that returns a value",
				check : function() {
					if (typeof totalValue == 'function' && totalValue() == 3) {
						return true;
					} else {
						return false;
					}
				},
			}, {
				text : "Evaluate a mathematical expression",
				check : function() {
					if (typeof totalValue == 'function' && totalValue() == 21) {
						return true;
					} else {
						return false;
					}
				},
			} ],
			next : 'lesson4.3',
		},
		'lesson4.3' : {
			action : function() {
				cb.clear();
				ui.maxIns();
				cb.add('exec', les.checkObjectives);
			},
			objectives : [ {
				text : "Write a function that returns the value of your first two cards",
				check : function() {
					var handValues = handValue();
					if (typeof totalValue == 'function'
							&& totalValue() == handValues[0] + handValues[1]) {
						return true;
					} else {
						return false;
					}
				},
			} ],
			next : 'lesson5',
		},
		'lesson5' : {
			action : function() {
				cb.clear();
				ui.maxIns();
				cb.add('exec', les.checkObjectives);
			},
			objectives : [ {
				text : "Comment your code",
				check : function() {
					var code = ui.getUserCode();
					return ((code.indexOf('/*') != -1 && code.indexOf('*/') != -1) || code
							.indexOf('//') != -1);
				},
			} ],
			next : 'lesson6.1',
		},
		'lesson6.1' : {
			action : function() {
				cb.clear();
				ui.maxIns();
				les.checkObjectives();
			},
			objectives : [ {
				text : "DON'T PRESS SUBMIT!",
				check : function() {
					return true;
				},
			} ],
			next : 'lesson6.2',
		},
		'lesson6.2' : {
			action : function() {
				cb.clear();
				ui.maxIns();
				les.checkObjectives();
			},
			objectives : [ {
				text : "Make a functional loop",
				check : function() {
					return true;
				},
			}, {
				text : "Return the total value",
				check : function() {
					return true;
				},
			} ],
			next : 'lesson7',
		},
		'lesson7' : {
			action : function() {
				cb.clear();
				ui.maxIns();
				les.checkObjectives();
			},
			objectives : [ {
				text : "DON'T PRESS SUBMIT!",
				check : function() {
					return true;
				},
			} ],
			next : 'lesson7',
		},
		'lesson8' : {
			action : function() {
				cb.clear();
				ui.maxIns();
				les.checkObjectives();
			},
			objectives : [ {
				text : "DON'T PRESS SUBMIT!",
				check : function() {
					return true;
				},
			} ],
			next : 'lesson7',
		},
		'lesson9' : {
			action : function() {
				cb.clear();
				ui.maxIns();
				les.checkObjectives();
			},
			objectives : [ {
				text : "DON'T PRESS SUBMIT!",
				check : function() {
					return true;
				},
			} ],
			next : 'lesson7',
		},
	},

	checkObjectives : function() {
		if (les.objComplete) {
			return;
		}
		var allDone = true;
		for ( var i = 0; i < les.objectives.length; i++) {
			if (typeof les.objectives[i].complete == 'undefined'
					|| !les.objectives[i].complete) {
				if (les.objectives[i].check()) {
					les.objectives[i].complete = true;
					$(".objective" + i).append(
							$("<img src='./img/check.png' alt='O' />"));
				} else {
					allDone = false;
				}
			}
		}
		if (allDone) {
			$("#instructions").append($(ui.nextButton));
			les.objComplete = true;
		}
	},

	loadLesson : function(lesson) {
		eg.lockEval();
		les.currLesson = lesson;
		les.objectives = les.lessonList[les.currLesson].objectives;
		for ( var i = 0; i < les.objectives.length; i++) {
			les.objectives[i].complete = false;
		}
		les.objComplete = false, les.flags = {},

		$.get('./lessons/' + lesson + '.htm', function(data) {
			if (name) {
				data = data.replace(/\[NAME\]/g, name);
			} else {
				data = data.replace(/\[NAME\]/g, 'friend');
			}
			data = data.replace(/show="([^"]*)"/g, 'href="javascript:void($(\'#$1\').toggle(300));"');

			les.lessonText = data;
			$("#instructions").fadeOut().queue(
					function() {
						$("#instructions").html(les.lessonText);
						var toggle = $("<a href='javascript:void(0);'></a>");
						toggle.bind('click', function() {
							if (ui.sideMaxed) {
								ui.minIns();
								$(this).html('&gt;');
							} else {
								ui.maxIns();
								$(this).html('&lt;');
							}
						});
						if (les.objectives.length > 0) {
							var objList = $("<ol></ol>");
							for ( var i = 0; i < les.objectives.length; i++) {
								objList.append($("<li></li>").addClass(
										"objective" + i).html(
										les.objectives[i].text));
							}
							var objBox = $("<div></div>")
									.addClass("objectives").append(
											$("<h4>Objectives</h4>")).append(
											objList);
							objBox.insertAfter($("#instructions h3"));
						}

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

						if (les.lessonList[les.currLesson].action) {
							les.lessonList[les.currLesson].action();
						}
						$(this).dequeue();
					}).fadeIn().queue(function() {
				eg.unlockEval();
				$(this).dequeue();
			});
		});
	},
	nextLesson : function() {
		if (!les.lessonList[les.currLesson]) {
			// fail gracefully
		} else {
			//les.loadLesson(les.lessonList[les.currLesson].next);
			location.hash = les.lessonList[les.currLesson].next;
		}
	},
};
