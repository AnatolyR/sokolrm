var mockResponses = mockResponses || [];
(function() {
    var document = {
        "id" : "d0e38e2e-18bd-48fd-91ec-5e102519cd06",
        "type" : "incomingDocument",
        "title" : "Test document 1rtyr235577788891011",
        "documentKind" : "Письмо",
        "registrationDate" : 1462013086426,
        "correspondent" : "65208064-e1ef-4bf5-be7b-39a3839668d3",
        "correspondentTitle" : "КВАЛИТЕТ, строительная экспертиза",
        "externalSigner" : "Васильев (23847623785)",
        "externalExecutor" : "Романцев",
        "externalNumber" : "455-67",
        "externalDate" : 1462013086417,
        "documentNumber" : "Б12",
        "pagesQuantity" : "2",
        "addressee" : [ "bbb3bfbf-66af-41b8-8a6d-24f6e527386a", "be547579-8664-4221-8dde-029ef242b517", "137e29ed-acb2-4f06-b6f6-74ed1b332caa" ],
        "addresseeTitle" : [ "Середин Т. И.", "Власов Л. И.", "Козлов Я. В." ],
        "status" : "Зарегистрирован",
        "author" : "test"
    };

    mockResponses['app/card'] = function(params, callback) {
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
            "data" : document
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
                document.documentKind = field.value;
            }
        }
        callback(data.id);
    };
})();