var configResponses = configResponses || [];
(function() {
    var adminData = {
        "items": [
            {
                "id": "users",
                "title": "Сотрудники"
            },
            {
                "id": "groups",
                "title": "Группы"
            }
        ]
    };

    configResponses['navigation/admin'] = function(params, callback) {
        callback(adminData);
    };

})();