var configResponses = configResponses || [];
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

    configResponses['lists/documentsList'] = function(params, callback) {
        callback(gridSettings);
    };
    configResponses['lists/incomingDocumentsList'] = function(params, callback) {
        callback(gridSettings);
    };
})();