
var modules;
modules = modules || {};

modules["container"] = (function() {
    var dispatcher = {

    };

    var documentKindsResponse = [ {
        "id" : 1,
        "title" : "Отчет"
    }, {
        "id" : 2,
        "title" : "Письмо"
    }, {
        "id" : 3,
        "title" : "Представление"
    }, {
        "id" : 4,
        "title" : "Предписание"
    }, {
        "id" : 5,
        "title" : "Претензия"
    }, {
        "id" : 6,
        "title" : "Приглашение"
    }, {
        "id" : 7,
        "title" : "Справка"
    }, {
        "id" : 8,
        "title" : "Решение"
    } ];

    var attachesResponse = {
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

    var options = {
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
        "data" : {
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
        },
        dispatcher: dispatcher
    };


    var testComponent = null;

    dispatcher.open = function(id) {
        testComponent.destroy();
        testComponent = $.sokol.container(options, $("<div class='container'></div>").appendTo("body"));
    };

    var testAll = function(completeCallback) {

        var steps = [];

        steps.push({
            step: function renderContainer() {
                testComponent = $.sokol.container(options, $("<div></div>").appendTo("body"));
            },
            wait: 500
        });
        steps.push({
            step: function checkForm() {
                assertEquals('Входящий № Б12 от 30.04.2016 (Письмо)Test document 1rtyr235577788891011Статус: Зарегистрирован', testComponent.header.element.text());
                assertEquals('Редактировать', testComponent.formButtons.element.children(":visible").text());
                assertEquals('Основные реквизитыЗаголовок:Test document 1rtyr235577788891011Вид документа:ПисьмоАдресаты:Середин Т. И., Власов Л. И., ' +
                    'Козлов Я. В. Корреспондент:КВАЛИТЕТ, строительная экспертизаКем подписано:Васильев (23847623785)' +
                    'Исполнитель:РоманцевИсходящий номер:455-67Исходящая дата:30.04.2016 13:44 Номер документа:Б12' +
                    'Дата регистрации:30.04.2016 13:44Количество листов:2', testComponent.form.element.text());
                assertEquals('НазваниеРазмерДата добавленияКто добавилZFt7CsGffvM.jpg67 KBl9gi0kody5U.jpg73 KBBadoo-2.jpg72 KBДобавить вложение',
                    testComponent.attaches.element.children().eq(1).text() + testComponent.attaches.element.children().last().find('button:visible').text());
            },
            wait: 500
        });

        doTest({destroy: function() { testComponent.destroy() }}, steps, completeCallback);
    };

    return {
        before: function() {
            mockResponses['app/simpledic'] = function(params, callback) {
                if (params.id == 'documentKind') {
                    callback(documentKindsResponse);
                }
            };

            mockResponses['app/attaches'] = function(params, callback) {
                callback(attachesResponse);
            };
        },
        testAll: {
            title: "Тестировать все",
            action: function(completeCallback) {
                testAll(completeCallback);
            }
        },
        showContainer: {
            title: "Отобразить",
            action: function() {
                $.sokol.container(options, $('<div></div>').appendTo("body"));
            }
        }
    }
}());
