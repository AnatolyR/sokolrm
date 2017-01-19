var mockResponses = mockResponses || [];
(function() {
    var attachesResponse1 = {
        "data" : [ {
            "id" : "c082c2bc-4cc1-4de9-8c5f-968a6c22992b",
            "title" : "ZFt7CsGffvM.jpg",
            "size" : "67 KB"
        }, {
            "id" : "ecff1ca5-edce-4d86-b3e5-5baf10ca85ce",
            "title" : "l9gi0kody5U.jpg",
            "size" : "73 KB"
        }, {
            "id" : "e6789e58-4d94-42d3-8df8-1d7b1911a5a9",
            "title" : "Badoo-2.jpg",
            "size" : "72 KB"
        } ]
    };

    var attachesResponse2 = {
        "data" : [ {
            "id" : "c082c2bc-4cc1-4de9-8c5f-968a6c22992b",
            "title" : "ZFt7CsGffvM.jpg",
            "size" : "67 KB",
            "creationDate": "23.11.2016 22:23",
            "creator": "Петров П. П."
        }, {
            "id" : "ecff1ca5-edce-4d86-b3e5-5baf10ca85ce",
            "title" : "l9gi0kody5U.jpg",
            "size" : "73 KB",
            "creationDate": "23.11.2016 22:23",
            "creator": "Петров П. П."
        }, {
            "id" : "e6789e58-4d94-42d3-8df8-1d7b1911a5a9",
            "title" : "Badoo-2.jpg",
            "size" : "72 KB",
            "creationDate": "23.11.2016 22:23",
            "creator": "Петров П. П."
        } ]
    };

    mockResponses['app/attaches'] = function(params, callback) {
        if (params.id == 'testAttaches') {
            callback(attachesResponse2.data);
        } else {
            callback(attachesResponse1.data);
        }
    };

    mockResponses['app/deleteAttach'] = function(params, callback) {
        var newAttachesList = [];
        for (var i = 0; i < attachesResponse2.data.length; i++) {
            var file = attachesResponse2.data[i];
            if (file.id != params.id) {
                newAttachesList.push(file);
            }
        }
        attachesResponse2.data = newAttachesList;
        callback();
    };
})();