var page = require('webpage').create();
var i = 1;

page.viewportSize = {
    width: 1024,
    height: 760
};

page.onConsoleMessage = function(msg) {
    console.log('CONSOLE: ' + msg);
    if (msg == 'TESTS COMPLETES') {
        page.render('result.png');
        phantom.exit();
    }
    //page.render('page' + i++ + '.png');
};

page.onError = function(msg, trace) {
    var msgStack = ['PHANTOM ERROR: ' + msg];
    if (trace && trace.length) {
        msgStack.push('TRACE:');
        trace.forEach(function(t) {
            msgStack.push(' -> ' + (t.file || t.sourceURL) + ': ' + t.line + (t.function ? ' (in function ' + t.function +')' : ''));
        });
    }
    console.error(msgStack.join('\n'));
    page.render('result.png');
    phantom.exit(1);
};

page.open('/Users/anatolii/Documents/sokolsed/client/test/testAll.html', function(status) {
    console.log("Status: " + status);
    //if(status === "success") {
    //    page.render('example.png');
    //}
    //phantom.exit();
});