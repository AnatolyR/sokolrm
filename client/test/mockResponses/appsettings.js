var mockResponses = mockResponses || [];
(function() {
    var appSettings = {
        userName: "Test User",
        leftMenu: [
            {
                title: "Главная",
                link: ""
            },
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

    mockResponses['app/appsettings'] = function(params, callback) {
        callback(appSettings);
    };
})();