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
	lessonList : lessonList,

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
