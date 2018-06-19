$.mockjax({
    url: "app/list",
    responseText: [ {
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
    } ]
});

var sdic = [ {
    "id" : "580f62b3-7b96-4109-a321-dc7d24109a1a",
    "title" : "Поляков И. В."
}, {
    "id" : "722b151c-f9d7-4222-b541-cfc554695510",
    "title" : "Ивашов В. Н."
}, {
    "id" : "dc175f6e-b18d-495f-aca9-58c956e48a42",
    "title" : "Беломестов Г. В."
}, {
    "id" : "e879c49c-4fdf-43a1-8507-7091f2dea03d",
    "title" : "Волков Б. П."
}, {
    "id" : "3379db0f-8221-43fd-8d46-e05edcef9686",
    "title" : "Ивашов Н. В."
}, {
    "id" : "a1109397-b265-4cf4-8d6e-986c0fa0ce54",
    "title" : "Былинкин Б. Н."
}, {
    "id" : "a2cfff23-4070-4063-9e94-c3956e824122",
    "title" : "Захаров Н. В."
}, {
    "id" : "fb2f4ebc-34c6-4fb5-b73a-7ed1b9249a68",
    "title" : "Агапов Г. В."
}, {
    "id" : "0cde912f-55e6-48c9-97f8-464fbe74cbb5",
    "title" : "Добрынин Л. В."
} ];

$.mockjax({
    url: "app/dictionary",
    data: function (params) {
        return params.id == "organizationPersons";
    },
    response: function (settings) {
        this.responseText = sdic;
    }
    
});