var modules = modules || {};

modules["execution"] = (function() {
    var options = {

    };

    var testComponent = null;

    var testAll = function(completeCallback) {

        var steps = [];

        steps.push({
            step: function renderModal() {
                testComponent = $.sokol.modal(options, $("<div></div>"));
            },
            wait: 500
        });
        steps.push({
            step: function checkModal() {
                assertEquals('', testComponent.element.text());
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
                $.sokol.executionForm(options, $("<div></div>").appendTo("body"));
            }
        }
    }
}());

