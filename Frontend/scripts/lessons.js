var les = {
    // Store lesson information
    currLesson : 'beginning',
    // References to the next lesson
    lessonList : {
        'beginning' : {
            next : 'lesson1',
        },
        'lesson1' : {
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

                cb.add('won', les.nextLesson);
            },
            next : 'lesson2',
        },
        'lesson2' : {
            action : function() {
                $('#sidebar').animate({
                    width : '800px'
                });
                $("#codebox").removeAttr('disabled');
                $("#eval").removeAttr('disabled');
                $("#buttons").html('');
                cb.add('exec', function() {
                    if(eg.playerName != '') {
                        les.nextLesson();
                    }
                });
            },
            next : 'lesson3',
        },
        'lesson3' : {
            action : function() {
                $('#sidebar').animate({
                    width : '200px'
                });
            },
        },
    },

    loadLesson : function(lesson) {
        les.currLesson = lesson;
        $.get('./lessons/' + lesson + '.htm', function(data) {
            if(eg.playerName) {
                data = data.replace(/\{NAME\}/g, eg.playerName);
            }
            $("#instructions").html(data);
        });
        if(les.lessonList[les.currLesson].action) {
            les.lessonList[les.currLesson].action();
        }
    },
    nextLesson : function() {
        if(!les.lessonList[les.currLesson]) {
            // fail gracefully
        } else {
            les.loadLesson(les.lessonList[les.currLesson].next);
        }
    },
};
