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
                "id": "documentkinds",
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

})();