
var modules;
modules = modules || {};

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

mockResponses['app/simpledic'] = function(params, callback) {
    if (params.id == 'documentKind') {
        callback(documentKindsResponse);
    }
};

modules["form"] = (function() {
    var dispatcher = {

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
            "registrationDate" : "30.04.2016 13:44",
            "correspondent" : "65208064-e1ef-4bf5-be7b-39a3839668d3",
            "correspondentTitle" : "КВАЛИТЕТ, строительная экспертиза",
            "externalSigner" : "Васильев (23847623785)",
            "externalExecutor" : "Романцев",
            "externalNumber" : "455-67",
            "externalDate" : "30.04.2016 13:44",
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

    var testAll = function(completeCallback) {

        var steps = [];

        steps.push({
            step: function renderForm() {
                testComponent = $.sokol.form(options, $("<div></div>").appendTo("body"));
            },
            wait: 500
        });
        steps.push({
            step: function checkForm() {
                assertEquals('Основные реквизитыЗаголовок:Test document 1rtyr235577788891011Вид документа:ПисьмоАдресаты:Середин Т. И., Власов Л. И., ' +
                    'Козлов Я. В. Корреспондент:КВАЛИТЕТ, строительная экспертизаКем подписано:Васильев (23847623785)Исполнитель:РоманцевИсходящий ' +
                    'номер:455-67Исходящая дата:30.04.2016 13:44 Номер документа:Б12Дата регистрации:30.04.2016 13:44Количество листов:2', testComponent.element.text());
                assertEquals(0, testComponent.element.find('input').length, 'Wrong input elements count');
            },
            wait: 500
        });
        steps.push({
            step: function setEditMode() {
                testComponent.setMode('edit');
            },
            wait: 500
        });
        steps.push({
            step: function checkEditMode() {
                assertEquals(11, testComponent.element.find('input').length, 'Wrong input elements count');
            },
            wait: 500
        });

        doTest({destroy: function() { testComponent.destroy() }}, steps, completeCallback);
    };

    return {
        testAll: {
            title: "Тестировать все",
            action: function(completeCallback) {
                testAll(completeCallback);
            }
        },
        showForm: {
            title: "Отобразить",
            action: function() {
                var modes = $('<div style="padding-bottom: 10px;"><a name="editLink" href="">Edit mode</a> <a name="readLink" href="">Read mode</a> ' +
                    '<a name="saveLink" href="">Save form</a></div>');
                modes.appendTo('body');
                var form = $.sokol.form(options, $("<div></div>").appendTo("body"));
                modes.find('[name="editLink"]').click(function(e) {e.preventDefault(); form.setMode('edit')});
                modes.find('[name="readLink"]').click(function(e) {e.preventDefault(); form.setMode('read')});
                modes.find('[name="saveLink"]').click(function(e) {e.preventDefault(); form.saveForm()});
            }
        }
    }
}());

