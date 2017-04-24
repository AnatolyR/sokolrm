
var modules;
modules = modules || {};

modules["grid"] = (function() {

    var rs = {};

    rs.list = function(id, size, offset) {
        var r = [];

        var total = 55;

        for (var i = offset + 1; i < size + offset + 1 && i <= total; i++) {
            r.push({
                id: "id" + i,
                documentNumber: i.toString(),
                type: "Test",
                status: "Draft",
                title: "Document " + i,
                //registrationDate: this.dateFormat(new Date (1479723927756), "%Y.%m.%d %H:%M", false)
                registrationDate: moment(new Date (1479723927756)).format('L LT')
            });
        }

        return {
            data: r,
            total: total,
            offset: offset
        };
    };

    rs.dateFormat = function(date, fstr, utc) {
        utc = utc ? 'getUTC' : 'get';
        return fstr.replace (/%[YmdHMS]/g, function (m) {
            switch (m) {
                case '%Y': return date[utc + 'FullYear'] (); // no leading zeros required
                case '%m': m = 1 + date[utc + 'Month'] (); break;
                case '%d': m = date[utc + 'Date'] (); break;
                case '%H': m = date[utc + 'Hours'] (); break;
                case '%M': m = date[utc + 'Minutes'] (); break;
                case '%S': m = date[utc + 'Seconds'] (); break;
                default: return m.slice (1); // unknown code, remove %
            }
            // add leading zero if required
            return ('0' + m).slice (-2);
        });
    };

    mockResponses['app/gridtest'] = function(params, callback) {
        callback(rs.list(params.id, parseInt(params.size), parseInt(params.offset)))
    };

    var options = {
        url: "app/gridtest",
        title: "Grid test",
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
                "title": "Заголовок",
                "editor": "text"
            },
            {
                "id": "registrationDate",
                "title": "Дата регистрации",
                "render": "datetime"
            }
        ]
    };

    var optionsC = {
        columnsVisible: [
            "id",
            "documentNumber",
            "type",
            "status",
            "title",
            "registrationDate"
        ],
        title: "Columns",
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
                "title": "Заголовок",
                "editor": "text"
            },
            {
                "id": "registrationDate",
                "title": "Дата регистрации",
                "render": "datetime"
            }
        ],
        "data" : [ {
            "id" : "b0cf186d-ae9c-44e0-93db-d4d2a72fc88b",
            "documentNumber" : 1,
            "type" : "test",
            "status" : "Зарегистрирован",
            "title" : "Test document 2",
            "registrationDate" : "09.04.2016 15:43"
        }, {
            "id" : "04b77f61-f92c-4e44-9559-c76261ce3d1e",
            "documentNumber" : 2,
            "type" : "Входящий",
            "status" : "Зарегистрирован",
            "title" : "Тестовый документ 12",
            "registrationDate" : "13.04.2016 22:44"
        }, {
            "id" : "7bb0fc4f-efbe-4845-81a7-59cbafd303bf",
            "documentNumber" : "",
            "type" : "Входящий",
            "status" : "Зарегистрирован",
            "title" : "43545656-1111",
            "registrationDate" : "03.05.2016 13:40"
        }, {
            "id" : "e38b0d70-fc25-4e88-b072-e20611e76d73",
            "documentNumber" : "",
            "type" : "Исходящий",
            "status" : "Зарегистрирован",
            "title" : "1111",
            "registrationDate" : "03.05.2016 17:58"
        }, {
            "id" : "70c17fa8-f7b2-47a5-a57b-02220253fb8e",
            "documentNumber" : "",
            "type" : "Внутренний",
            "status" : "Зарегистрирован",
            "title" : "Документ документ",
            "registrationDate" : "03.05.2016 17:26"
        }, {
            "id" : "10984d8e-8e18-4384-95bd-8743d1f98676",
            "documentNumber" : null,
            "type" : "Входящий",
            "status" : "Зарегистрирован",
            "title" : "",
            "registrationDate" : "13.04.2016 22:03"
        }, {
            "id" : "57b5cfa7-9dfb-40a4-8044-8f3fb6a71a83",
            "documentNumber" : null,
            "type" : "Входящий",
            "status" : "Зарегистрирован",
            "title" : "Еще один документ",
            "registrationDate" : "13.04.2016 22:45"
        }, {
            "id" : "20a8fac1-cebd-455a-8eb6-ce34e59c3631",
            "documentNumber" : null,
            "type" : "Входящий",
            "status" : "Зарегистрирован",
            "title" : "1235",
            "registrationDate" : "23.04.2016 00:03"
        }, {
            "id" : "d0e38e2e-18bd-48fd-91ec-5e102519cd06",
            "documentNumber" : "Б12",
            "type" : "Входящий",
            "status" : "Зарегистрирован",
            "title" : "Test document 123",
            "registrationDate" : "30.04.2016 13:44"
        }, {
            "id" : "ec8b9829-6fcb-4b7f-9356-fc26b38dff10",
            "documentNumber" : "АА123",
            "type" : "Входящий",
            "status" : "Зарегистрирован",
            "title" : "Входящий ec8b9829",
            "registrationDate" : "04.05.2016 22:52"
        } ]
    };

    var testComponent = null;

    var testAll = function(completeCallback) {

        var assertTableStateCorrect = function(count, rowCount, page, pageCount, firstDoc, secondDoc, lastDoc) {

            if (count != undefined) {
                var countElements = testComponent.element.find('[name="gridItemsCount"]');
                assertEquals(count, parseInt(countElements.first().text()), "Wrong first total count");
                assertEquals(count, parseInt(countElements.last().text()), "Wrong last total count");
            }

            var rows = testComponent.element.find('[name="tableBody"] > tr');
            if (rowCount != undefined) {
                assertEquals(rowCount, rows.length, "Wrong rows count");
            }

            var pageInfo = $(".selectpicker[name='pageSelector']").first().parent().find("button:first").attr("title").split(/[ /]+/g);

            if (page != undefined) {
                assertEquals(page, parseInt(pageInfo[0]), "Wrong page");
            }

            if (pageCount != undefined) {
                assertEquals(pageCount, parseInt(pageInfo[1]), "Wrong page count");
            }

            if (firstDoc != undefined) {
                assertEquals(firstDoc, rows.first().text());
            }

            if (secondDoc != undefined) {
                assertEquals(secondDoc, rows.eq(1).text());
            }

            if (lastDoc != undefined) {
                assertEquals(lastDoc, rows.last().text());
            }

        };

        var steps = [];

        steps.push({
            step: function renderTable() {
                options.title = "Test All";
                testComponent = $.sokol.grid(options, $("<div class='container'></div>").appendTo("body"));
            },
            wait: 500
        });
        steps.push({
            step: function checkTable() {
                assertTableStateCorrect(55, 20, 1, 3, "1TestDraftDocument 121.11.2016 13:25", "2TestDraftDocument 221.11.2016 13:25", "20TestDraftDocument 2021.11.2016 13:25");
            },
            wait: 500
        });
        steps.push({
            step: function openPageDialog() {
                $(".selectpicker[name='pageSize']").first().parent().find("button:first").trigger("click");
            },
            wait: 500
        });
        steps.push({
            step: function changePageSize() {
                $(".selectpicker[name='pageSize']").first().parent().find("ul li:nth-child(3) a").trigger("click");
            },
            wait: 500
        });
        steps.push({
            step: function checkPageSize() {
                assertTableStateCorrect(55, 50);
            },
            wait: 500
        });
        steps.push({
            step: function goNextPage() {
                $("button[name='next']").first().trigger("click");
            },
            wait: 500
        });
        steps.push({
            step: function checkNextPage() {
                assertTableStateCorrect(55, 5, 2, 2, "51TestDraftDocument 5121.11.2016 13:25", "52TestDraftDocument 5221.11.2016 13:25", "55TestDraftDocument 5521.11.2016 13:25");
            },
            wait: 500
        });
        if (completeCallback) {
            steps.push({
                step: function() {
                    testComponent.destroy();
                },
                wait: 500
            });
            steps.push({
                step: function() {
                    completeCallback();
                },
                wait: 500
            });
        }

        doAction(0, steps);
    };

    return {
        testAll: {
            title: "Тестировать все",
            action: function(completeCallback) {
                testAll(completeCallback);
            }
        },
        showDocuments: {
            title: "Загрузить выборку \"Документы\"",
            action: function() {
                $.sokol.grid(options, $("<div></div>").appendTo("body"));
            }
        },
        showColumns: {
            title: "Видимость колонок",
            action: function() {
                $.sokol.grid(optionsC, $("<div></div>").appendTo("body"));
            }
        },
        showAllColumns: {
            title: "Видимость всех колонок без настройки",
            action: function() {
                var optionsD = {
                    columnsVisible: null,
                    title: "All columns",
                    "columns": optionsC.columns,
                    "data" : optionsC.data
                };
                $.sokol.grid(optionsD, $("<div></div>").appendTo("body"));
            }
        },
        showCheckboxes: {
            title: "С возможностью выбора",
            action: function () {
                var deleteAction = function(data) {
                    var ids = data.map(function(e) {return e.id});
                    //alert();
                    $.post('app/deleteDictionaryValues',
                        {ids: ids},
                        $.proxy(function(response){
                            if (response === 'true') {
                                $.notify({
                                    message: 'Элементы удалены'
                                },{
                                    type: 'success',
                                    delay: 1000,
                                    timer: 1000
                                });
                            } else {
                                $.notify({message: 'Не удалось удалить эелементы'},{type: 'danger', delay: 0, timer: 0});
                            }
                        }, this)
                    );
                };
                var deleteMethod = function(grid, objects) {
                    var titles = objects.map(function(e) {return e.title});

                    $.sokol.smodal({
                        title: 'Подтверждение удаления',
                        body: titles.join(),
                        confirmButtonTitle: 'Удалить',
                        confirmAction: deleteAction,
                        data: objects
                    });
                };
                var optionsD = {
                    columnsVisible: null,
                    title: "All columns",
                    "columns": optionsC.columns,
                    "data": optionsC.data,
                    selectable: true,
                    deletable: true,
                    deleteMethod: deleteMethod
                };
                $.sokol.grid(optionsD, $("<div></div>").appendTo("body"));
            }
        },
        showAddable: {
            title: "С возможностью добавления",
            action: function () {
                var addAction = function(data, callback) {
                    console.log("data >>>> ", data);

                    var obj = {};

                    data.forEach(function(e) {
                        obj[e.id] = e.value;
                    });

                    obj.id = "test";

                    callback(obj);
                    //$.post('app/addDictionaryValues',
                    //    {value: data},
                    //    $.proxy(function(response){
                    //        if (response === 'true') {
                    //            $.notify({
                    //                message: 'Элемент добавлен'
                    //            },{
                    //                type: 'success',
                    //                delay: 1000,
                    //                timer: 1000
                    //            });
                    //        } else {
                    //            $.notify({message: 'Не удалось добавить эелемент'},{type: 'danger', delay: 0, timer: 0});
                    //        }
                    //    }, this)
                    //);
                };
                var optionsD = {
                    columnsVisible: null,
                    title: "All columns",
                    "columns": optionsC.columns,
                    "data": optionsC.data,
                    selectable: true,
                    addable: true,
                    addMethod: addAction
                };
                $.sokol.grid(optionsD, $("<div></div>").appendTo("body"));
            }
        },
        showWithFilter: {
            title: "Таблица с фильтром",
            action: function() {
                var optionsD = {
                    columnsVisible: null,
                    title: "Filterable",
                    "columns": optionsC.columns,
                    "data" : optionsC.data,
                    filterable: true
                };
                $.sokol.grid(optionsD, $("<div></div>").appendTo("body"));
            }
        }
    }
}());

