
var modules;
modules = modules || {};

modules["app"] = (function() {

    var testComponent = null;

    var testAll = function(completeCallback) {

        var steps = [];

        doTest({destroy: function() { testComponent.destroy() }}, steps, completeCallback);
    };


    return {
        before: function() {

        },
        testAll: {
            title: "Тестировать все",
            action: function(completeCallback) {
                testAll(completeCallback);
            }
        },
        showApp: {
            title: "Отобразить",
            action: function() {
                    $.sokol.app({}, $('body'));
            }
        }
    }
}());

