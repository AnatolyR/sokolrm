var modules = modules || {};

modules["dictionaries"] = (function() {
    var options = {

    };

    var testComponent = null;

    var testAll = function(completeCallback) {

        var steps = [];

        steps.push({
            step: function renderDictionaries() {
                testComponent = $.sokol.dictionaries(options, $("<div></div>").appendTo("body"));
            },
            wait: 500
        });
        steps.push({
            step: function checkDictionaries() {
                assertEquals('СотрудникиКонтрагентыВиды документовЗаголовки документов', testComponent.element.text());
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
                $.sokol.dictionaries(options, $("<div></div>").appendTo("body"));
            }
        }
    }
}());

