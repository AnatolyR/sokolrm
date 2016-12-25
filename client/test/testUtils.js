var assertEquals = function(expected, real, message) {
    var splice = function(inp, idx, rem, str) {
        return inp.slice(0, idx) + str + inp.slice(idx + Math.abs(rem));
    };

    if (expected !== real) {
        message = message || '';

        var len = Math.min(expected.length, real.length);
        var maxDiffLen = 20;
        var diffLen = 0;
        var diffExp = "";
        var diffR = "";
        var diff = false;
        var startPosition = 0;
        for (var i = 0; i < len; i++) {
            if (expected.charAt(i) != real.charAt(i)) {
                diff = true;
                startPosition = i;
                break;
            }
            //if (diff) {
            //    diffExp += expected.charAt(i);
            //    diffR += real.charAt(i);
            //    diffLen++;
            //    if (diffLen >= maxDiffLen) {
            //        break;
            //    }
            //}
        }

        var e = new Error(message + " : \nExpected: [" + expected + "]\n  Actual: [" + real + "]");
        e.ignoreDoActionTryCatch = true;

        expected = splice(expected, startPosition, 0, "<span style='color: white; background-color: red;'>") + "</span>";
        real = splice(real, startPosition, 0, "<span style='color: white; background-color: red;'>") + "</span>";

        var error = message +
            "<div style='font-family: \"Courier New\", Courier, monospace;'><span style='color: black; font-weight: bold;'>Expected:</span> [" + expected +"]</div>" +
            "<div style='font-family: \"Courier New\", Courier, monospace;'><span style='color: black; font-weight: bold;'>&nbsp;&nbsp;Actual:</span> [" + real + "]</div>";
        $('<div class="alert alert-danger" style="z-index: 2000; position: relative;">' + error + '</div>').prependTo("body");

        throw e;
    }
};

var doAction = function(i, steps) {
    if (i < steps.length) {
        if (steps[i].disable) {
            doAction(i + 1, steps);
        } else {
            setTimeout(function executeStep() {
                var stepName = steps[i].step.name && steps[i].step.name != 'step' ? steps[i].step.name : '';
                if (stepName) {
                    console.info("[" + stepName + "]");
                }
                try {
                    steps[i].step();
                } catch(e) {
                    if (!e.ignoreDoActionTryCatch) {
                        var error = 'Exception in step "' + stepName + '" ' +
                            "<div style='font-family: \"Courier New\", Courier, monospace;'><span style='color: black; font-weight: bold;'>Exception:</span> [" + e + "]</div>";
                        $('<div class="alert alert-danger" style="z-index: 2000; position: relative;">' + error + '</div>').prependTo("body");
                    }
                    throw e;
                }
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
                $('<div class="alert alert-success" style="z-index: 2000; position: relative;">TEST PASSED</div>').prependTo('body');
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