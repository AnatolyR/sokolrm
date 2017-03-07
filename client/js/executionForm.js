$.widget('sokol.executionForm', {
    options: {
        mode: 'read',
        type: null
    },

    _create: function () {
        if (!this.options.type) {
            new Error('Execution list type is not defined');
        }
        this.createHeader();
        this.createBlock();
    },

    createHeader: function() {
        this.element.addClass('panel panel-default');
        this.element.attr('name', 'attachmentsPanel');

        var panelHeader = $('<div class="panel-heading"></div>').appendTo(this.element);

        var title;
        var type = this.options.type;
        if (type == 'execution') {
            title = 'Резолюция';
        } else if (type == 'approval') {
            title = 'Согласование';
        } else if (type == 'acquaintance') {
            title = 'Ознакомление';
        }
        var panelTitle = $('<div class="panel-title">' + title + '</div>').appendTo(panelHeader);

        var panelBody = $('<div class="panel-body"></div>');
        panelBody.appendTo(this.element);
        this.panelBody = panelBody;
    },

    createBlock: function () {
        if (this.form) {
            this.form.destroy();
        }
        this.form = $.sokol.form({
            mode: (this.options.mode == 'create' || this.options.mode == 'edit') ? 'edit' : 'read',
            data: this.options.data ? this.options.data : {},
            form: {
                "id": "userSystem",
                "fields": [
                    {
                        "items": [
                            {
                                "id": "userId",
                                "title": "Автор",
                                "type": "users",
                                "multiple": false,
                                ar: 'read',
                                hideIfEmpty: true
                            },
                            {
                                "id": "created",
                                "title": "Дата",
                                "type": "date",
                                ar: 'read',
                                hideIfEmpty: true
                            }
                        ]
                    },
                    {
                        "id": "comment",
                        "title": this.options.type == 'resolution' ? 'Резолюция' : 'Комментарий',
                        "type": "text",
                        "mandatory": true
                    }
                ]
            },
            usePanel: false,
            dispatcher: this.options.dispatcher,
            containerType: 'execution'
        }, $('<div></div>').appendTo(this.panelBody));

        var executorsListTitle;
        var executorTitle;
        var type = this.options.type;
        if (type == 'execution') {
            executorsListTitle = 'Исполнители';
            executorTitle = 'Исполнитель';
        } else if (type = 'approval') {
            executorsListTitle = 'Согласующие';
            executorTitle = 'Согласующий';
        } else if (type == 'acquaintance') {
            executorsListTitle = 'Ознакамливающиеся';
            executorTitle = 'Ознакамливающийся';
        }
        var options = {
                'title': executorsListTitle,
                'sortable': true,
                mode: (this.options.mode == 'create' || this.options.mode == 'edit') ? 'edit' : 'read',
                'columnsVisible': [
                    'userTitle',
                    'dueDate',
                    'executedDate',
                    'status',
                    'result'
                ],
                'columns': [
                    {
                        'id': 'userId',
                        'title': executorTitle + ' (ИД)',
                        'render': 'link',
                        'linkType': 'user'
                    },
                    {
                        'id': 'userTitle',
                        'idColumn': 'userId',
                        'title': executorTitle,
                        'render': 'link',
                        'linkType': 'user',
                        'editor': 'user',
                        'width': '500px'
                    },
                    {
                        'id': 'dueDate',
                        'title': 'Срок',
                        'render': 'datetime',
                        'editor': 'date'
                    },
                    {
                        'id': 'executedDate',
                        'title': 'Завершено',
                        'render': 'datetime'
                    },
                    {
                        'id': 'result',
                        'title': 'Статус',
                        'render': 'expand',
                        'dataColumn': 'comment'
                    }
                ],
                'id': 'tasks',
                'filterable': false,
                'deleteMethod': $.proxy(this.doDelete, this)
        };
        options.data = (this.options.data && this.options.data.tasks) ? this.options.data.tasks : [];
        options.usePanel = false;

        if (this.grid) {
            this.grid.destroy();
        }

        this.grid = $.sokol.grid(options, $('<div class="panel panel-default" style="border-width: 0 0 1px 0; border-radius: 0;"></div>').insertAfter(this.panelBody));

        this.createButtons();
    },

    doDelete: function(grid, objects) {
        var ids = objects.map(function(e) {return e.id});
        var updatedData = [];
        $.each(grid.options.data, function(i, o) {
           if (ids.indexOf(o.id) < 0) {
               updatedData.push(o);
           }
        });
        grid.options.data = updatedData;
        grid.refresh();
    },

    saveExecution: function() {
        if (!this.form.validateForm()) {
            return;
        }

        var data = this.form.getData();

        var saveUrl = 'app/saveExecutionList';
        var type = this.options.type;
        var title;
        if (type == 'execution') {
            title = 'исполнителей';
        } else if (type = 'approval') {
            title = 'согласующих';
        } else if (type == 'acquaintance') {
            title = 'ознакамливающихся';
        }
        var message = 'Не удалось сохранить список ' + title + '. Обратитесь к администратору.';

        var rows = this.grid.element.find('tr');
        var rowsData = [];
        rows.each(function(i, row) {
            var rowData = {};
            var inputs = $(row).find('input');
            for (var j = 0; j < inputs.length; j++) {
                var input = $(inputs[j]);
                var name = input.attr('name');
                var value = input.val();
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
                    if (name == 'userTitle') {
                        name = 'userId';
                    }
                    rowData[name] = value;
                }
            }
            if (!$.isEmptyObject(rowData)) {
                rowsData.push(rowData);
            }
        });

        data.executors = rowsData;
        data.type = this.options.type;
        data.documentId = this.options.documentId;

        $.post(saveUrl, JSON.stringify(data), $.proxy(function (id) {
            //this.options.dispatcher.refreshExecutionList('resolution');
            this.options.dispatcher.reopen();
            $.notify({message: 'Сохранено'}, {type: 'success', delay: 1000, timer: 1000});
        }, this)).fail($.proxy(function() {
            $.notify({message: message},{type: 'danger', delay: 0, timer: 0});
        }, this));
    },

    createButtons: function() {
        var buttons = $(this.element).find('[name="buttons"]');
        if (buttons.length == 0) {
            buttons = $('<div name="buttons" class="panel-body"></div>').appendTo(this.element);
        }
        buttons.empty();

        var saveButton = $('<button type="button" name="save" style="display: none;" class="btn btn-success controlElementLeftMargin">Сохранить</button>');
        saveButton.click($.proxy(function() {
            this.saveExecution();
        }, this));
        saveButton.appendTo(buttons);

        var editButton = $('<button type="button" name="edit" style="display: none;" class="btn btn-success controlElementLeftMargin">Редактировать</button>');
        editButton.click($.proxy(function() {
            this.options.mode = 'edit';
            this.createBlock();
        }, this));
        editButton.appendTo(buttons);

        var cancelButton = $('<button type="button" name="cancel" style="display: none;" class="btn btn-default controlElementLeftMargin">Отмена</button>');
        cancelButton.click($.proxy(function() {
            this.options.mode = 'read';
            this.createBlock();
        }, this));
        cancelButton.appendTo(buttons);

        var title;
        var type = this.options.type;
        if (type == 'execution') {
            title = 'резолюции';
        } else if (type = 'approval') {
            title = 'согласования';
        } else if (type == 'acquaintance') {
            title = 'ознакомления';
        }
        var cancelCreateButton = $('<button type="button" name="cancelCreate" style="display: none;" class="btn btn-default controlElementLeftMargin">Отмена</button>');
        cancelCreateButton.click($.proxy(function() {
            $.sokol.smodal({
                title: 'Подтверждение отмены',
                body: 'Отменить создание ' + title + '?',
                confirmButtonTitle: 'Подтвердить',
                confirmAction: $.proxy(this.options.dispatcher.cancelExecution, this.options.dispatcher)
            });
        }, this));
        cancelCreateButton.appendTo(buttons);

        this.manageButtons();
    },

    manageButtons: function() {
        var buttons = $(this.element).find('[name="buttons"]');
        buttons.children().hide();
        var mode = this.options.mode;
        if (mode == 'create') {
            buttons.children('[name="save"]').show();
            buttons.children('[name="cancelCreate"]').show();
        } else if (mode == 'edit') {
            buttons.children('[name="save"]').show();
            buttons.children('[name="cancel"]').show();
        } else {
            buttons.children('[name="edit"]').show();
        }
    },

    _destroy: function () {
        this.element.detach();
    }
});