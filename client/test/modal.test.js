var modules = modules || {};

modules["modal"] = (function() {
    var deleteAction = function(data) {
        alert(data);
    };
    var options = {
        title: 'Test modal title',
        body: 'Test modal body',
        confirmButtonTitle: 'Test confirm',
        confirmAction: deleteAction,
        data: '123'
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
                $.sokol.smodal(options);
            }
        }
    }
}());

