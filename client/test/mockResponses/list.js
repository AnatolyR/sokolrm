var mockResponses = mockResponses || [];
var configResponses = configResponses || [];
(function() {
    var sidebarData = {
        "items": [
            {
                "id": "documents",
                "title": "Документы",
                "type": "header"
            },
            {
                "id": "incomingDocuments",
                "title": "Входящие"
            },
            {
                "id": "outgoingDocuments",
                "title": "Исходящие"
            },
            {
                "id": "internalDocuments",
                "title": "Внутренние"
            },
            {
                "id": "myDocuments",
                "title": "Мои документы"
            },
            {
                "id": "favoriteDocuments",
                "title": "Избранное"
            },
            {
                "id": "incomingTasks",
                "title": "Входящие задачи",
                "type": "header"
            },
            {
                "id": "",
                "title": "На рассмотрение"
            },
            {
                "id": "",
                "title": "На подписание"
            },
            {
                "id": "",
                "title": "На согласование"
            },
            {
                "id": "",
                "title": "На ознакомление"
            },
            {
                "id": "",
                "title": "На исполнение"
            },{
                "id": "",
                "title": "Исходящие задачи",
                "type": "header"
            },
            {
                "id": "",
                "title": "Исполнение"
            },
            {
                "id": "",
                "title": "Утвердить"
            },
            {
                "id": "",
                "title": "Перенесение срока"
            }
        ]
    };

    var gridSettings = {
        title: 'Test',
        url: "app/documents",
        columnsVisible: [
            "id",
            "documentNumber",
            "type",
            "status",
            "title",
            "registrationDate"
        ],
        "columns": [
            {
                "id": "id",
                "type": "hidden"
            },
            {
                "id": "documentNumber",
                "title": "Номер"
            },
            {
                "id": "type",
                "title": "Тип",
                "render": "doctype"
            },
            {
                "id": "status",
                "title": "Статус"
            },
            {
                "id": "title",
                "title": "Заголовок"
            },
            {
                "id": "registrationDate",
                "title": "Дата регистрации",
                "render": "datetime"
            }
        ]
    };

    configResponses['lists/documentsList'] = function(params, callback) {
        callback(gridSettings);
    };
    configResponses['lists/incomingDocumentsList'] = function(params, callback) {
        callback(gridSettings);
    };
    configResponses['navigation/main'] = function(params, callback) {
        callback(sidebarData);
    };

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
    mockResponses['app/documents'] = function(params, callback) {
        callback(rs.list(params.id, parseInt(params.size), parseInt(params.offset)));
    };
})();