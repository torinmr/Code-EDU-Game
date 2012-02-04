var les = {
	// Store lesson information
	currLesson : 'lesson0',
	lessonText : '',
	objComplete : false,
	objectives : {},
	flags : {},
	hintUnlocked: false,

	// Invalidate code with errors
	invalidated : false,
	invalidate : function() {
		les.invalidated = true;
	},

	// References to the next lesson
	lessonList : {
		'lesson0' : {
			action : function() {
				// Make manual controls
				$("#buttons").fadeIn();
				$("#buttons").append(ui.makeButton('New Game', function() {
					eg.newGame();
					eg.bet();
				}));
				$("#buttons").append(ui.makeButton('Hit', eg.hit));
				$("#buttons").append(ui.makeButton('Stand', eg.stand));
				// $("#buttons").append(ui.makeButton('Double', eg.doubleDown));
				$("#previousButton").hide();
				$("#instructions").css({height:'340px'});
				$("#betbox")
						.html(
								'Bet:<input type="text" id="bet" maxlength="6" value="10" />');
				$("#bet").css({
					width : '3em'
				});

				$("#codebox").attr('disabled', 'disabled');
				$("#eval").attr('disabled', 'disabled');
				cb.add('end', les.checkObjectives);
				ui.minIns();
			},
			objectives : [ {
				text : "Play a round of Blackjack",
				check : function() {
					return true;
				},
			} ],
			prev : '',
			next : 'lesson1',
		},
		'lesson1' : {
			action : function() {
				cb.clear();
				ui.maxIns();
				/*
				 * $("#buttons").fadeOut().queue(function() { $(this).html('');
				 * });
				 */
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
			noBet : true,
			prev : 'lesson0',
			next : 'lesson2',
		},
		'lesson2' : {
			action : function() {
				cb.clear();
				ui.maxIns();
				cb.add('hit', function() {
					les.lessonList.lesson2.complete = true;
				});
				cb.add('exec', les.checkObjectives);
			},
			complete : false,
			objectives : [ {
				text : "Hit every turn",
				check : function() {
					if (les.lessonList.lesson2.complete) {
						les.lessonList.lesson2.complete = false;
						return true;
					}
					return false;
				},
			} ],
			noBet : true,
			prev : 'lesson1',
			next : 'lesson3.1',
		},
		'lesson3.1' : {
			action : function() {
				cb.clear();
				ui.maxIns();
				cb.add('error', function(E) {
					var hitMatch = ui.getUserCode().match(/hit/g);
					var standMatch = ui.getUserCode().match(/stand/g);
					if (E.indexOf("somethingtrue") != -1
							&& ui.getUserCode().indexOf("if") != -1
							&& hitMatch.length === 1
							&& standMatch.length === 1) {
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
			noBet : true,
			prev : 'lesson2',
			next : 'lesson3.2',
		},
		'lesson3.2' : {
			action : function() {
				cb.clear();
				ui.maxIns();
				cb.add('exec', les.checkObjectives);
			},
			objectives : [
					{
						text : "Evaluate an if statement with a boolean",
						check : function() {
							var code = ui.getUserCode();
							return ((code.indexOf('true') != -1 || code.indexOf('false') != -1)
									&& code.indexOf('if') != -1 
									&& code.indexOf('else') != -1);
						},
					},
					{
						text : "Evaluate an if statement with a numerical boolean expression",
						check : function() {
							var code = ui.getUserCode();
							return (code.match(/\d+\s*(===|==|>=|<=|>|<)\s*\d+/)
									&& code.indexOf('if') != -1 
									&& code.indexOf('else') != -1);
						},
					},
					{
						text : "Evaluate an if statement with a boolean expression using variables",
						check : function() {
							var code = ui.getUserCode();
							return ((code.match(/name\s*===?\s*"/) || code.match(/"\s*===?\s*name/))
									&& code.indexOf('if') != -1 
									&& code.indexOf('else') != -1);	
						},
					} ],
			noBet : true,
			prev : 'lesson3.1',
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
							&& code.indexOf('<') != -1 && code.indexOf('>') != -1 
							&& code.indexOf('if') != -1 && code.indexOf('else') != -1);
				},
			} ],
			noBet : true,
			prev : 'lesson3.2',
			next : 'lesson4.1',
		},
		'lesson4.1' : {
			action : function() {
				cb.clear();
				ui.maxIns();
				cb.add('error', function(E) {
					var code = ui.getUserCode();
					if (E.indexOf("totalValue") != -1
						&& (code.match(/totalValue()\s*<=?\s*\d/) || code.match(/\d\s*>=?\s*totalValue()/))
						&& code.indexOf('if') != -1 && code.indexOf('else') != -1) {
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
			noBet : true,
			prev : 'lesson3.3',
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
					var code = ui.getUserCode();
					var funcName = code.match(/function ([a-zA-Z0-9]+)\(\)/);
					if (!funcName) { return false; }
					if (typeof window[funcName[1]] === 'function' && typeof window[funcName[1]]() === 'number'
						&& (code.match(new RegExp(funcName[1]+'\\(\\)\\s*<=?\\s*\\d')) 
								|| code.match(new RegExp('\\d\\s*>=?\\s*' + funcName[1] + '\\(\\)')))
								) {
						return true;
					} else {
						return false;
					}
				},
			}, {
				text : "Evaluate a mathematical expression",
				check : function() {
					var code = ui.getUserCode();
					var funcName = code.match(/function ([a-zA-Z0-9]+)\(\)/);
					if (!funcName) { return false; }
					if (code.match(/(\+|-|\*|\/|%)/)
						&& typeof window[funcName[1]] === 'function' && typeof window[funcName[1]]() === 'number'
								&& (code.match(new RegExp(funcName[1]+'\\(\\)\\s*<=?\\s*\\d')) 
										|| code.match(new RegExp('\\d\\s*>=?\\s*' + funcName[1] + '\\(\\)')))) {
						return true;
					} else {
						return false;
					}
				},
			} ],
			noBet : true,
			prev : 'lesson4.1',
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
					var code = ui.getUserCode();
					var funcName = code.match(/function ([a-zA-Z0-9]+)\(\)/);
					if (!funcName) { return false; }
					if (typeof window[funcName[1]] === 'function' 
						&& window[funcName[1]]() == handValues[0] + handValues[1]) {
						return true;
					} else {
						return false;
					}
				},
			} ],
			noBet : true,
			prev : 'lesson4.2',
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
					var handValues = handValue();
					var code = ui.getUserCode();
					var funcName = code.match(/function ([a-zA-Z0-9]+)\(\)/);
					if (!funcName) { return false; }
					if (typeof window[funcName[1]] === 'function' 
						&& window[funcName[1]]() == handValues[0] + handValues[1]
						&& (code.match(/\/\*.*\*\//) || code.indexOf("//") != -1)) {
						return true;
					} else {
						return false;
					}
				},
			} ],
			noBet : true,
			prev : 'lesson4.3',
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
			noBet : true,
			prev : 'lesson5',
			next : 'lesson6.2',
		},
		'lesson6.2' : {
			action : function() {
				cb.clear();
				ui.maxIns();
				cb.add('exec', les.checkObjectives);
			},
			objectives : [ {
				text : "Make a functional loop and return the total value",
				check : function() {
					var handValues = handValue();
					var sum = 0;
					for ( var i = 0; i < handValues.length; i++) {
						sum += handValues[i];
					}
					if (typeof totalValue == 'function' && totalValue() == sum
							&& ui.getUserCode().indexOf('while') != -1) {
						return true;
					} else {
						return false;
					}
				},
			} ],
			noBet : true,
			prev : 'lesson6.1',
			next : 'lesson7.1',
		},
		'lesson7.1' : {
			action : function() {
				cb.clear();
				ui.maxIns();
				cb.add('exec', les.checkObjectives);
			},
			objectives : [ {
				text : "Simplify incrementation",
				check : function() {
					var handValues = handValue();
					var sum = 0;
					for ( var i = 0; i < handValues.length; i++) {
						sum += handValues[i];
					}
					if (typeof totalValue == 'function' && totalValue() == sum
							&& ui.getUserCode().indexOf('while') != -1
							&& ui.getUserCode().indexOf('++') != -1) {
						return true;
					} else {
						return false;
					}
				},
			} ],
			noBet : true,
			prev : 'lesson6.2',
			next : 'lesson7.2',
		},
		'lesson7.2' : {
			action : function() {
				cb.clear();
				ui.maxIns();
				cb.add('exec', les.checkObjectives);
			},
			objectives : [ {
				text : "Turn a while loop into a for loop",
				check : function() {
					var handValues = handValue();
					var sum = 0;
					for ( var i = 0; i < handValues.length; i++) {
						sum += handValues[i];
					}
					if (typeof totalValue == 'function' && totalValue() == sum
							&& ui.getUserCode().indexOf('for') != -1) {
						return true;
					} else {
						return false;
					}
				},
			} ],
			noBet : true,
			prev : 'lesson7.1',
			next : 'lesson8.1',
		},
		'lesson8.1' : {
			action : function() {
				cb.clear();
				ui.maxIns();
				cb.add('bet', function() {
					les.lessonList['lesson8.1'].complete = true;
				});
				cb.add('exec', les.checkObjectives);
			},
			objectives : [ {
				text : "Bet, at the beginning of the hand only",
				check : function() {
					return les.lessonList['lesson8.1'].complete;
				},
			} ],
			complete: false,
			prev : 'lesson7.2',
			next : 'lesson8.2',
		},
		'lesson8.2' : {
			action : function() {
				cb.clear();
				ui.maxIns();
				les.lessonList['lesson8.2'].prevMoney = eg.money;
				cb.add('exec', les.checkObjectives);
			},
			objectives : [ {
				text : "Ensure you won't bet more than you can afford",
				check : function() {
					var allIn = (eg.money === 0 || eg.money === 2*les.lessonList['lesson8.2'].prevMoney);
					les.lessonList['lesson8.2'].prevMoney = eg.money;
					return allIn;
				},
			} ],
			prevMoney: 0,
			prev : 'lesson8.1',
			next : 'lesson9.1',
		},
		'lesson9.1' : {
			action : function() {
				cb.clear();
				ui.maxIns();
				les.checkObjectives();
			},
			objectives : [ {
				text : "Add a conditional double down command",
				check : function() {
					return true;
				},
			} ],
			riggedDeck: [],
			prev : 'lesson8.2',
			next : 'lesson9.2',
		},
		'lesson9.2' : {
			action : function() {
				cb.clear();
				ui.maxIns();
				les.checkObjectives();
			},
			objectives : [ {
				text : "Prevent errors and increase efficiency with else if",
				check : function() {
					return true;
				},
			} ],
			prev : 'lesson9.1',
			next : 'lesson9.3',
		},
		'lesson9.3' : {
			action : function() {
				cb.clear();
				ui.maxIns();
				les.checkObjectives();
			},
			objectives : [ {
				text : "Use an \"or\" statement",
				check : function() {
					return true;
				},
			}, {
				text : "Use a combined \"and-or\" statement",
				check : function() {
					return true;
				},
			} ],
			prev : 'lesson9.2',
			next : 'lesson10.1',
		},
		'lesson10.1' : {
			action : function() {
				cb.clear();
				ui.maxIns();
				les.checkObjectives();
			},
			objectives : [ {
				text : "Factor in the dealer's up card",
				check : function() {
					return true;
				},
			} ],
			prev : 'lesson9.3',
			next : 'lesson10.2',
		},
		'lesson10.2' : {
			action : function() {
				cb.clear();
				ui.maxIns();
				les.checkObjectives();
			},
			objectives : [ {
				text : "Add the second condition",
				check : function() {
					return true;
				},
			} ],
			prev : 'lesson10.1',
			next : '',
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
					$("#lessonNumberForThisNotification").html(
							les.currLesson.substr(6));
					$("#objectiveThatIsComplete").html(les.objectives[i].text);
					$("#notification").show().delay(1500).fadeOut();
				} else {
					allDone = false;
				}
			}
		}
		if (allDone) {
			if (ui.isLoggedIn) {
				rem.acc('setLevelDone', function(n) {

				}, {
					level : les.currLesson
				});
			}
			// $("#instructions").append($(ui.nextButton));
			$("#continueButton").show();
			$("#instructions").css({height:'310px'});
			les.objComplete = true;
		}
	},

	showHint : function(e) {
		if (!les.hintUnlocked) {
			alert('Please try the lesson yourself before looking at our hints!');
			return;
		}
		$(e).toggle(100).queue(function() {
			// Why does jQuery not work...

			// $('#instructions').attr('scrollTop',
			// $('#instructions').attr('scrollHeight'));
			var instructions = document.getElementById("instructions");
			instructions.scrollTop = instructions.scrollHeight;
			$(this).dequeue();
		});
	},

	loadLesson : function(lesson) {
		eg.lockEval();
		les.currLesson = lesson;
		les.hintUnlocked = false;
		if (ui.isLoggedIn) {
			rem.acc('setLevelInProgress', function(n) {

			}, {
				level : les.currLesson
			});
		}
		$("#betbox").html('');
		$("#buttons").html('');
		$("#codebox").removeAttr('disabled');
		$("#eval").removeAttr('disabled');
		$("#previousButton").show();
		$("#continueButton").hide();
		$("#instructions").css({height:'310px'});
		les.objectives = les.lessonList[les.currLesson].objectives;
		if (les.lessonList[les.currLesson].noBet) {
			$("#resetMoneyBox").css({
				display : 'none'
			});
		} else {
			$("#resetMoneyBox").css({
				display : 'inline'
			});
		}
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
			data = data.replace(/show="([^"]*)"/g,
					'href="javascript:void(les.showHint(\'#$1\'));"');

			les.lessonText = data;

			$("#instructions").fadeOut().queue(
					function() {
						$("#instructions").html(les.lessonText);

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

						if (!ui.sideMaxed) {
							$(".objectives").css({
								width : '150px',
								float : 'none',
								margin : '10px auto 10px auto'
							});
						}

						if (les.lessonList[les.currLesson].action) {
							les.lessonList[les.currLesson].action();
						}
						$(this).dequeue();
					}).fadeIn().queue(function() {
				eg.unlockEval();
				document.getElementById("instructions").scrollTop = 0;
				$(this).dequeue();
			});
		});
	},
	prevLesson : function() {
		if (!les.lessonList[les.currLesson]) {

		} else {
			location.hash = les.lessonList[les.currLesson].prev;
		}
	},
	nextLesson : function() {
		if (!les.lessonList[les.currLesson]) {

		} else {
			location.hash = les.lessonList[les.currLesson].next;
		}
	},
};
