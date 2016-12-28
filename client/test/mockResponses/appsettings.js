var configResponses = configResponses || [];

(function() {
    var appSettings = {
        userName: "Test User",
        leftMenu: [
            {
                title: "Создать",
                submenu: [
                    {
                        title: "Входящий",
                        link: "#new/incomingDocument"
                    },
                    {
                        title: "Исходящий",
                        link: "#new/outgoingDocument"
                    },
                    {
                        title: "Внутренний",
                        link: "#new/internalDocument"
                    }
                ]
            },
            {
                title: "Отчеты",
                link: "#reports"
            },
            {
                title: "Архив",
                link: "#archive"
            },
            {
                "title": "Справочники",
                "link": "#dictionaries"
            },
            {
                "title": "Поиск",
                "link": "#search"
            }
        ],
        rightMenu: [
            {
                title: "Администрирование",
                link: "#admin"
            },
            {
                title: "$userName",
                submenu: [
                    {
                        title: "Настройки",
                        link: "#settings"
                    },
                    {
                        title: "Помощь",
                        link: "#help"
                    },
                    {
                        separator: true
                    },
                    {
                        title: "Выход",
                        link: "#logout"
                    }
                ]
            }
        ]
    };

    configResponses['appSettings'] = function(params, callback) {
        callback(appSettings);
    };

})();