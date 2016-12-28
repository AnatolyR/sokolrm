var modules = modules || {};

modules["admin"] = (function() {
    var options = {

    };

    var testComponent = null;

    var testAll = function(completeCallback) {

        var steps = [];

        steps.push({
            step: function renderAdmin() {
                testComponent = $.sokol.admin(options, $("<div></div>").appendTo("body"));
            },
            wait: 500
        });
        steps.push({
            step: function checkAdmin() {
                assertEquals('СотрудникиГруппы', testComponent.element.text());
            },
            wait: 500
        });

        doTest({destroy: function() { testComponent.destroy() }}, steps, completeCallback);
    };

    return {
        testAll: {
            title: "Тестировать все",
            action: function(completeCallback) {
                testAll(completeCallback);
            }
        },
        show: {
            title: "Отобразить",
            action: function() {
                $.sokol.admin(options, $("<div></div>").appendTo("body"));
            }
        }
    }
}());

