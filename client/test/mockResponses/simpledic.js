var mockResponses = mockResponses || [];
(function() {
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

    mockResponses['app/config'] = function(params, callback) {
        if (params.id == 'dictionaries/documentKind') {
            callback(documentKindsResponse);
        }
    };
})();