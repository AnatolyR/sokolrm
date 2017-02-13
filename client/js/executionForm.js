$.widget('sokol.executionForm', {
    options: {},

    _create: function () {
        this.createBlock()
    },

    createBlock: function () {

        this.element.addClass('panel panel-default');
        this.element.attr('name', 'attachmentsPanel');

        var panelHeader = $('<div class="panel-heading"></div>').appendTo(this.element);
        var panelTitle = $('<div class="panel-title">Резолюция</div>').appendTo(panelHeader);
        var panelBody = $('<div class="panel-body"></div>');
        panelBody.appendTo(this.element);

        this.form = $.sokol.form({
            mode: 'edit',
            data: {},
            form: {
                "id": "userSystem",
                "title": "Системные свойства",
                "fields": [
                    {
                        "items": [
                            {
                                "id": "author",
                                "title": "Автор",
                                "type": "users",
                                "mandatory": true,
                                "multiple": false
                            },
                            {
                                "id": "date",
                                "title": "Дата",
                                "type": "date",
                                "mandatory": true
                            }
                        ]
                    },
                    {
                        "id": "comment",
                        "title": "Резолюция",
                        "type": "text",
                        "mandatory": true
                    }
                ]
            },
            usePanel: false,
            dispatcher: this.options.dispatcher,
            containerType: this.options.containerType
        }, $('<div></div>').appendTo(panelBody));

        var response = {
            "gridConfig": {
                "title": "Исполнители",
                "sortable": true,
                "mode": "edit",
                "columnsVisible": [
                    "executorTitle",
                    "date",
                    "executed"
                ],
                "columns": [
                    {
                        "id": "executor",
                        "title": "Исполнитель (ИД)",
                        "render": "link",
                        "linkType": "user"
                    },
                    {
                        "id": "executorTitle",
                        "idColumn": "executor",
                        "title": "Исполнитель",
                        "render": "link",
                        "linkType": "user",
                        "editor": "user",
                        "width": "500px"
                    },
                    {
                        "id": "date",
                        "title": "Срок",
                        render: 'datetime',
                        "editor": "date"
                    },
                    {
                        "id": "executed",
                        "title": "Выполнено",
                        render: 'datetime'
                    }
                ],
                "id": "tasks",
                "filterable": false
            }
        };
        var data = [];
        var options = response.gridConfig;
        options.data = data;
        options.usePanel = false;
        this.grid = $.sokol.grid(options, this.element);

        this.createButtons();
    },

    saveExecution: function() {
        if (!this.form.validateForm()) {
            return;
        }

        var data = this.form.getData();

        var saveUrl = 'app/saveExecutionList';
        var message = 'Не удалось сохранить список исполнителей. Обратитесь к администратору.';

        var rows = this.grid.element.find('tr');
        var rowsData = [];
        rows.each(function(i, row) {
            var rowData = {};
            var inputs = $(row).find('input');
            for (var j = 0; j < inputs.length; j++) {
                var input = $(inputs[j]);
                var name = input.attr('name');
                var value = input.attr('value');
                if (name && value) {
                    rowData[name] = value;
                }
            }
            var selects = $(row).find('select');
            for (var j = 0; j < selects.length; j++) {
                var select = $(selects[j]);
                var name = select.attr('name');
                var value = select.val();
                if (name && value) {
                    if (name == 'executorTitle') {
                        name = 'executorId';
                    }
                    rowData[name] = value;
                }
            }
            if (!$.isEmptyObject(rowData)) {
                rowsData.push(rowData);
            }
        });

        data.rowsData = rowsData;
        data.type = 'resolution';

        //alert(JSON.stringify(data));

        $.post(saveUrl, JSON.stringify(data), $.proxy(function (id) {
            $.notify({message: 'Сохранено'}, {type: 'success', delay: 1000, timer: 1000});

            //todo reload

        }, this)).fail($.proxy(function() {
            $.notify({message: message},{type: 'danger', delay: 0, timer: 0});
        }, this));
    },

    createButtons: function() {
        var buttons = $('<div class="panel-body"></div>').appendTo(this.element);

        var saveButton = $('<button type="button" name="add" style="" class="btn btn-success controlElementLeftMargin">Сохранить</button>');
        saveButton.click($.proxy(function() {
            this.saveExecution();
        }, this));
        saveButton.appendTo(buttons);

        var cancelButton = $('<button type="button" name="add" style="" class="btn btn-default controlElementLeftMargin">Отмена</button>');
        cancelButton.click($.proxy(function() {
            $.sokol.smodal({
                title: 'Подтверждение отмены',
                body: 'Отменить создание резолюции?',
                confirmButtonTitle: 'Подтвердить',
                confirmAction: $.proxy(this.options.dispatcher.cancelExecution, this.options.dispatcher)
            });
        }, this));
        cancelButton.appendTo(buttons);
    },

    _destroy: function () {
        this.element.detach();
    }
});