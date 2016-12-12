var assertEquals = function(expected, real, message) {
    if (expected !== real) {
        message = message || 'Not equal';

        var e = new Error(message + " : [" + expected + "] != [" + real + "]");
        $('<div class="alert alert-danger">' + e + '</div>').prependTo("body");

        throw e;
    }
};

var doAction = function(i, steps) {
    if (i < steps.length) {
        if (steps[i].disable) {
            doAction(i + 1, steps);
        } else {
            setTimeout(function executeStep() {
                if (steps[i].step.name && steps[i].step.name != 'step') {
                    console.info("[" + steps[i].step.name + "]");
                }
                steps[i].step();
                doAction(i + 1, steps);
            }, steps[i].wait);
        }
    }
};

var doTest = function(testComponent, steps, completeCallback) {
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
    } else {
        steps.push({
            step: function () {
                $('<div class="alert alert-success">TEST PASSED</div>').prependTo('body');
            },
            wait: 500
        });
    }

    doAction(0, steps);
};

var mockResponses = mockResponses || [];
$.getJSON = function(url, params, callback) {
    setTimeout(function() {
        if (mockResponses[url]) {
            mockResponses[url](params, callback);
        } else {
            throw new Error('Not response for "' + url + '"');
        }
    }, 0);
    return {
        fail: function() {

        }
    }
};

$.post = function(url, data, callback) {
    setTimeout(function() {
        if (mockResponses[url]) {
            mockResponses[url](data, callback);
        } else {
            throw new Error('Not response for "' + url + '"');
        }
    }, 0);
    return {
        fail: function() {

        }
    }
};

var modules = [];
var initTestPage = function(moduleName) {
    $(window).bind('hashchange', $.proxy(function() {
        var id = location.hash.slice(1);

        var testModule = modules[moduleName];

        var body = $("body");
        body.empty();
        if (id) {
            setTimeout(function() {
                if (testModule.before) {
                    testModule.before();
                }
                testModule[id].action();
            }, 0);
        } else {
            for (var name in testModule) {
                if (name != 'before') {
                    body.append($("<div><a href='#" + name+ "'>" + testModule[name].title + " (" + name + ")</a></div>"));
                }
            }
        }

    }, this));
    $(window).trigger('hashchange');
};