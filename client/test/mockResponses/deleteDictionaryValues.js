var mockResponses = mockResponses || [];
(function() {
    mockResponses['app/deleteDictionaryValues'] = function(data, callback) {
        var ids = data.ids;
        console.log('Deleting ', ids);
        if (ids.indexOf('20a8fac1-cebd-455a-8eb6-ce34e59c3631') >= 0) {
            callback('false');
        } else {
            callback('true');
        }
    };
})();