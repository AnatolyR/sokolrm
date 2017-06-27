var configResponses = configResponses || [];
var mockResponses = mockResponses || [];
(function() {
    var dictionariesData = {
        "items": [
            {
                "id": "organizationPersons",
                "title": "Сотрудники"
            },
            {
                "id": "contragents",
                "title": "Контрагенты"
            },
            {
                "id": "documentKind",
                "title": "Виды документов"
            },
            {
                "id": "titles",
                "title": "Заголовки документов"
            }
        ]
    };

    var users = [];

    var rs = {};

    rs.user = function(i) {
        return {
            id: "id" + i,
            lastName: "Фамилия " + i,
            firstName: "Имя " + i,
            middleName: "Отчество " + i,
            title: "Фамилияю И. О. " + i
        };
    };

    var total = 55;
    for (var i = 1; i <= total; i++) {
        users["id" + i] = rs.user(i);
    }

    rs.list = function(id, size, offset) {
        var r = [];
        if (!offset) {
            offset = 0;
        }

        if (!size) {
            size = 50;
        }

        for (var i = offset + 1; i < size + offset + 1 && i <= total; i++) {
            r.push(users["id" + i]);
        }

        return {
            data: r,
            total: total,
            offset: offset
        };
    };

    configResponses['navigation/dictionaries'] = function(params, callback) {
        callback(dictionariesData);
    };

    mockResponses['app/users'] = function(params, callback) {
        callback(rs.list(params.id, parseInt(params.size), parseInt(params.offset)));
    };

    mockResponses['app/dictionary'] = function(params, callback) {
        callback(rs.list(params.id, parseInt(params.size), parseInt(params.offset)).data);
    };

    var simpleDictionaryData = {
        "title": "Вид документа",
        "gridConfig": {
            "title": "Вид документа",
            "columnsVisible": [
                "value"
            ],
            "columns": [
                {
                    "id": "value",
                    "title": "Значение"
                }
            ]
        }
    };

    var documentKindData = {
        "title": "Вид документа",
        "addable": true,
        "deletable": true,
        "selectable": true,
        "gridConfig": {"title": "Вид документа", "columns": [{"id": "title", "title": "Знчение", "editor": "text"}]},
        "data": [{
            "id": "5bd27ea3-80a5-4cb8-a289-14c2c0f9a4f9",
            "title": "Акт"
        }, {
            "id": "14d15543-7cac-4927-a63a-9653e10f6bfe",
            "title": "Указ"
        }, {
            "id": "99efc746-abc6-46ea-806a-b4541906536a",
            "title": "Ответ на запрос"
        }, {
            "id": "02054adb-7be4-47c1-baee-8515f41d5bb4",
            "title": "Жалоба"
        }, {
            "id": "333f7dda-9c9b-4a06-9346-d00ca7c698b2",
            "title": "Запрос"
        }, {
            "id": "3da3b499-91c9-4637-80d7-e445391b8a37",
            "title": "Заявка"
        }, {
            "id": "981e0fe3-1538-403c-b117-db2f93cc52b7",
            "title": "Письмо"
        }, {
            "id": "2d74d432-a430-4917-858b-366a5b603622",
            "title": "Приказ"
        }, {
            "id": "da13ae00-1e25-4f93-b170-f41132f8353d",
            "title": "Решение"
        }, {
            "id": "59f6524e-52af-4697-8385-ba056a1e1e80",
            "title": "Справка"
        }, {
            "id": "921712b3-7340-4413-9948-aac05932bdc4",
            "title": "Исковое заявление"
        }, {
            "id": "251e563b-c350-479f-896b-383312a374ca",
            "title": "Повестка"
        }, {
            "id": "098aa80a-ba54-4c0f-8bfd-10c44bbe7bb1",
            "title": "Протокол"
        }, {
            "id": "b38de36d-50b0-4411-8c35-f3f05087afe3",
            "title": "Приговор суда"
        }, {
            "id": "f37d57eb-5c3e-490f-b5f7-462607b9a982",
            "title": "Судебная повестка"
        }, {
            "id": "c926f49a-fb4c-4527-98bf-32836215cbd5",
            "title": "Указание Минтранса"
        }, {
            "id": "111beda3-3f83-4b94-a771-658e5b55dbc0",
            "title": "Извещение"
        }, {
            "id": "e2d3be91-bd48-4631-bede-a6e2ac86d4f9",
            "title": "Поручение"
        }, {
            "id": "b9a8d2d0-5c73-4b4a-ab8c-cb744342a21f",
            "title": "Претензия"
        }, {
            "id": "8b9e5e98-02d0-4c8b-93ab-d42399e94cc1",
            "title": "Телеграмма"
        }, {
            "id": "f4bb5ef6-995c-46fe-80c3-216c2fa37cb4",
            "title": "Требование"
        }, {
            "id": "95de1418-4b97-4d39-b60b-f3ba1935006d",
            "title": "Определение"
        }, {
            "id": "025cca21-92e8-4941-9569-8afd13e37524",
            "title": "Предписание"
        }, {
            "id": "a95815a0-d33c-4bbd-8d02-db76c36f64c7",
            "title": "Приглашение"
        }, {
            "id": "1e3e3392-8c78-49c0-a560-d68199942f7a",
            "title": "Уведомление"
        }, {
            "id": "75f5e60d-5005-4841-ac8c-818a18d1e29b",
            "title": "Распоряжение"
        }, {
            "id": "e435c2c2-2eee-41c4-b322-4fb6324eaa50",
            "title": "Кассационная жалоба"
        }, {
            "id": "808aecf7-b03a-4673-8114-4c74c985a05d",
            "title": "Представление"
        }, {
            "id": "6fbc32d2-0485-4b6c-a021-1844c845c2bd",
            "title": "Апелляционная жалоба"
        }, {
            "id": "395fb6b3-ed0d-4a72-8380-947211112ac2",
            "title": "Предупреждение"
        }, {
            "id": "2c66768d-3975-498b-8afc-d178dd290567",
            "title": "Телефонограмма"
        }, {"id": "7b3c0298-9411-44d5-8f8e-1e7bfd52b708", "title": "Исполнительный лист"}]
    };

    var organizationPersonsData = {
        "addable": "link",
        "deletable": true,
        "selectable": true,
        "gridConfig": {
            "title": "Сотрудники организации",
            "columnsVisible": ["lastName", "firstName", "middleName", "title"],
            "columns": [{"id": "title", "title": "Сокращенное имя"}, {
                "id": "lastName",
                "title": "Фамилия"
            }, {"id": "firstName", "title": "Имя"}, {"id": "middleName", "title": "Отчество"}],
            "id": "organizationPersons",
            "url": "app/users"
        }
    };

    mockResponses['app/dictionaryinfo'] = function(params, callback) {
        callback(documentKindData);
    };

    configResponses['dictionaries/organizationPersons'] = function(params, callback) {
        callback(organizationPersonsData);
    };

})();