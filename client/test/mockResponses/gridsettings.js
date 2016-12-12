var mockResponses = mockResponses || [];
(function() {
    var gridSettings = {
        title: 'Test',
        url: "app/grid",
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

    mockResponses['app/config'] = function(params, callback) {
        callback(gridSettings);
    };
})();