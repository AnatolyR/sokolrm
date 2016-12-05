
var modules;
modules = modules || {};


modules["attachesGrid"] = (function() {

    var options = {
        documentId: 'testAttaches'
    };

    var testComponent = null;

    var testAll = function(completeCallback) {

        var steps = [];

        steps.push({
            step: function renderAttaches() {
                testComponent = $.sokol.attachesGrid(options, $("<div></div>").appendTo("body"));
            },
            wait: 500
        });
        steps.push({
            step: function checkAttaches() {
                var rows = testComponent.element.find('tbody > tr');
                assertEquals(3, rows.length, "Wrong rows count");
                assertEquals('ZFt7CsGffvM.jpg67 KB23.11.2016 22:23Петров П. П.', rows.first().text(), 'Wrong first attach info');
                assertEquals('l9gi0kody5U.jpg73 KB23.11.2016 22:23Петров П. П.', rows.eq(1).text(), 'Wrong second attach info');
                assertEquals('Badoo-2.jpg72 KB23.11.2016 22:23Петров П. П.', rows.last().text(), 'Wrong last attach info');
            },
            wait: 500
        });
        steps.push({
            step: function setEditMode() {
                testComponent.setMode('edit');
            },
            wait: 500
        });
        steps.push({
            step: function deleteAttach() {
                var rows = testComponent.element.find('tbody > tr');
                var secondRow = rows.eq(1);
                secondRow.find('button').first().trigger("click");
            },
            wait: 500
        });
        steps.push({
            step: function checkAfterDeleteAttach() {
                var rows = testComponent.element.find('tbody > tr');
                assertEquals(2, rows.length, "Wrong rows count");
                assertEquals('ZFt7CsGffvM.jpg67 KB23.11.2016 22:23Петров П. П.', rows.first().text(), 'Wrong first attach info');
                assertEquals('Badoo-2.jpg72 KB23.11.2016 22:23Петров П. П.', rows.last().text(), 'Wrong last attach info');
                if (!completeCallback) {
                    $('<div class="alert alert-success">TEST PASSED</div>').prependTo('body');
                }
            },
            wait: 500
        });

        if (completeCallback) {
            steps.push({
                step: function () {
                    testComponent.destroy();
                },
                wait: 500
            });
            steps.push({
                step: function () {
                    completeCallback();
                },
                wait: 500
            });
        }

        doAction(0, steps);
    };

    return {
        testAll: {
            title: "Тестировать все",
            action: function(completeCallback) {
                testAll(completeCallback);
            }
        },
        showGrid: {
            title: "Отобразить",
            action: function() {
                var modes = $('<div style="padding-bottom: 10px;"><a name="editLink" href="">Edit mode</a> <a name="readLink" href="">Read mode</a></div>');
                modes.appendTo('body');
                var attaches = $.sokol.attachesGrid(options, $("<div></div>").appendTo("body"));
                modes.find('[name="editLink"]').click(function(e) {e.preventDefault(); attaches.setMode('edit')});
                modes.find('[name="readLink"]').click(function(e) {e.preventDefault(); attaches.setMode('read')});
            }
        }
    }
}());

