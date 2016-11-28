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
var mockResponses = [];
$.getJSON = function(url, params, callback) {
    setTimeout(function() {
        if (mockResponses[url]) {
            mockResponses[url](params, callback);
        } else {
            throw new Error('Not response for "' + url + '"');
        }
    }, 0);
};