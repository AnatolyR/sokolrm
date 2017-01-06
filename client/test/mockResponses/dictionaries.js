var configResponses = configResponses || [];
(function() {
    var dictionariesData = {
        "items": [
            {
                "id": "users",
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

    configResponses['navigation/dictionaries'] = function(params, callback) {
        callback(dictionariesData);
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

    configResponses['dictionaries/documentKind'] = function(params, callback) {
        callback(simpleDictionaryData);
    };

})();