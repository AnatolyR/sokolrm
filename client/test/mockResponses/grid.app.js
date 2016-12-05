var mockResponses = mockResponses || [];
(function() {
    var documents = [];

    var rs = {};

    rs.dateFormat = function(date, fstr, utc) {
        utc = utc ? 'getUTC' : 'get';
        return fstr.replace (/%[YmdHMS]/g, function (m) {
            switch (m) {
                case '%Y': return date[utc + 'FullYear'] (); // no leading zeros required
                case '%m': m = 1 + date[utc + 'Month'] (); break;
                case '%d': m = date[utc + 'Date'] (); break;
                case '%H': m = date[utc + 'Hours'] (); break;
                case '%M': m = date[utc + 'Minutes'] (); break;
                case '%S': m = date[utc + 'Seconds'] (); break;
                default: return m.slice (1); // unknown code, remove %
            }
            // add leading zero if required
            return ('0' + m).slice (-2);
        });
    };

    rs.document = function(i) {
        return {
            id: "id" + i,
            documentNumber: i.toString(),
            type: "Test",
            status: "Draft",
            title: "Document " + i,
            registrationDate: new Date (1479723927756).toISOString(),
            "correspondent" : "65208064-e1ef-4bf5-be7b-39a3839668d3",
            "correspondentTitle" : "КВАЛИТЕТ, строительная экспертиза",
            "addressee" : [ "bbb3bfbf-66af-41b8-8a6d-24f6e527386a", "be547579-8664-4221-8dde-029ef242b517", "137e29ed-acb2-4f06-b6f6-74ed1b332caa" ],
            "addresseeTitle" : [ "Середин Т. И.", "Власов Л. И.", "Козлов Я. В." ]
        };
    };

    var total = 55;
    for (var i = 1; i <= total; i++) {
        documents["id" + i] = rs.document(i);
    }

    rs.list = function(id, size, offset) {
        var r = [];

        for (var i = offset + 1; i < size + offset + 1 && i <= total; i++) {
            r.push(documents["id" + i]);
        }

        return {
            data: r,
            total: total,
            offset: offset
        };
    };

    mockResponses['app/card'] = function(params, callback) {
        //var id = params.id.substring(2);
        var id = params.id;
        var response = {
            "type" : "form",
            "form" : {
                "fields" : [ {
                    "id" : "title",
                    "title" : "Заголовок",
                    "type" : "string",
                    "mandatory" : true
                }, {
                    "id" : "documentKind",
                    "width" : "300px",
                    "title" : "Вид документа",
                    "type" : "select",
                    "dictionary" : "documentKind"
                }, {
                    "id" : "addressee",
                    "title" : "Адресаты",
                    "type" : "dictionary",
                    "multiple" : true,
                    "mandatory" : true,
                    "dictionary" : "organizationPersons"
                }, {
                    "delimeter" : true
                }, {
                    "id" : "correspondent",
                    "title" : "Корреспондент",
                    "type" : "dictionary",
                    "dictionary" : "correspondent"
                }, {
                    "items" : [ {
                        "id" : "externalSigner",
                        "title" : "Кем подписано",
                        "type" : "smallstring"
                    }, {
                        "id" : "externalExecutor",
                        "title" : "Исполнитель",
                        "type" : "smallstring"
                    } ]
                }, {
                    "items" : [ {
                        "id" : "externalNumber",
                        "title" : "Исходящий номер",
                        "type" : "smallstring"
                    }, {
                        "id" : "externalDate",
                        "title" : "Исходящая дата",
                        "type" : "date"
                    } ]
                }, {
                    "delimeter" : true
                }, {
                    "cols" : 4,
                    "items" : [ {
                        "id" : "documentNumber",
                        "cols" : 2,
                        "title" : "Номер документа",
                        "type" : "smallstring"
                    }, {
                        "id" : "registrationDate",
                        "title" : "Дата регистрации",
                        "type" : "date"
                    }, {
                        "id" : "pagesQuantity",
                        "title" : "Количество листов",
                        "type" : "number"
                    } ]
                } ],
                "typeTitle" : "Входящий"
            },
            "data" : documents[id]
        };
        callback(response);
    };

    mockResponses['app/grid'] = function(params, callback) {
        callback(rs.list(params.id, parseInt(params.size), parseInt(params.offset)));
    };

    mockResponses['app/save'] = function(data, callback) {
        var data = JSON.parse(data);
        var fields = data.fields;
        var id = data.id;
        for (var i = 0; i < fields.length; i++) {
            var field = fields[i];
            if (field.name == 'documentKind') {
                documents[id].documentKind = field.value;
            }
        }
        callback(data.id);
    };
})();