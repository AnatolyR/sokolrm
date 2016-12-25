var modules;
modules = modules || {};

modules["list"] = (function() {
    var options = {
        id: 'incomingDocuments'
    };

    var testComponent = null;

    var testAll = function(completeCallback) {

        var steps = [];

        steps.push({
            step: function renderList() {
                testComponent = $.sokol.list(options, $("<div></div>").appendTo("body"));
            },
            wait: 500
        });
        steps.push({
            step: function checkListCategory() {
                assertEquals('Входящие', testComponent.element.find('li.active > a').text());

            },
            wait: 500
        });
        steps.push({
            step: function checkListGrid() {
                assertEquals('1TestDraftDocument 121.11.2016 13:25', testComponent.element.find('tr').eq(1).text());

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
        showList: {
            title: "Отобразить",
            action: function() {
                $.sokol.list(options, $("<div></div>").appendTo("body"));
            }
        }
    }
}());

