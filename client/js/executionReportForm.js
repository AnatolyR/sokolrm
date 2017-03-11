$.widget('sokol.executionReportForm', {
    options: {
        mode: 'read'
    },
    _create: function () {
        this.options.mode = this.options.data.status == "complete" ? "complete" : "read";

        this.createHeader();
        this.createBlock();

        if (this.options.mode == "complete") {
            this.renderReportForm();
        }
    },

    createHeader: function() {
        this.element.addClass('panel panel-default');
        this.element.attr('name', 'attachmentsPanel');

        var panelHeader = $('<div class="panel-heading"></div>').appendTo(this.element);

        var title;
        var type = this.options.data.type;
        if (type == 'execution') {
            title = 'Резолюция на исполнение';
        } else if (type = 'approval') {
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

        var executorsTitle;
        var commentTitle;
        var type = this.options.data.type;
        if (type == 'execution') {
            executorsTitle = 'Исполнители';
            commentTitle = 'Резолюция';
        } else if (type = 'approval') {
            executorsTitle = 'Согласующие';
            commentTitle = 'Комментарий';
        } else if (type == 'acquaintance') {
            executorsTitle = 'Ознакамливающиеся';
            commentTitle = 'Комментария';
        }

        this.form = $.sokol.form({
            mode: (this.options.mode == 'create' || this.options.mode == 'edit') ? 'edit' : 'read',
            data: this.options.data ? this.options.data : {},
            form: {
                "id": "task",
                "fields": [
                    {
                        "items": [
                            {
                                "id": "author",
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
                        'id': 'executors',
                        'title': executorsTitle,
                        'type': 'dictionary',
                        'multiple': true,
                        'mandatory': true,
                        'dictionary': 'organizationPersons'
                    },
                    {
                        'id': 'description',
                        'title': commentTitle,
                        'type': 'text',
                        'mandatory': true
                    }
                ]
            },
            usePanel: false,
            dispatcher: this.options.dispatcher,
            containerType: 'execution'
        }, $('<div></div>').appendTo(this.panelBody));

        this.createButtons();
    },

    renderReportForm: function() {
        var commentTitle;
        var reportTitle;
        var type = this.options.data.type;
        var options = [];
        if (type == 'execution') {
            commentTitle = 'Отчет';
            reportTitle = 'Отчет об исполнении';
            options = [
                {id: 'done', title: 'Исполнено'},
                {id: 'not_done', title: 'Не исполнено'}
            ];
        } else if (type = 'approval') {
            commentTitle = 'Замечание';
            reportTitle = this.options.mode == "complete" ? 'Отчет о согласовании' :'Согласование с замечанием';
            options = [
                {id: 'agreed', title: 'Согласовано'},
                {id: 'agreed_with_note', title: 'Согласовано с замечаниями'},
                {id: 'not_agreed', title: 'Не согласовано'}
            ];
        } else if (type == 'acquaintance') {
            commentTitle = 'Коментарий';
            reportTitle = 'Ознакомление с комментарием';
        }

        var panelHeader = $('<div data-name="reportHeader" class="panel-heading panel-footer" style="border-radius: 0;"></div>');
        var panelTitle = $('<div>' + reportTitle + '</div>');
        panelTitle.appendTo(panelHeader);
        panelHeader.insertAfter($(this.form.element).parent());

        var reportBody = $('<div data-name="reportBody" class="panel-body"></div>').insertAfter(panelHeader);

        this.reportForm = $.sokol.form({
            mode: this.options.mode == "complete" ? "read" : 'edit',
            data: this.options.data ? this.options.data : {},
            form: {
                "id": "task",
                "fields": [
                    {
                        "items": [
                            {
                                "id": "result",
                                "title": "Статус",
                                "type": "select",
                                "dictionary": "executionReportStatus",
                                "width": "200px",
                                "mandatory": type == 'execution',
                                "hideIfEmpty": type != 'execution',
                                'options' : options,
                                'valueField': 'id'
                            },
                            {
                                "id": "executedDate",
                                "title": "Дата",
                                "type": "date",
                                "ar": "read",
                                "hideIfEmpty": true
                            }
                        ]
                    },
                    {
                        'id': 'comment',
                        'title': commentTitle,
                        'type': 'text',
                        'mandatory': true,
                        'hideIfEmpty': this.options.mode == "complete"
                    }
                ]
            },
            usePanel: false,
            dispatcher: this.options.dispatcher,
            containerType: 'report'
        }, $('<div></div>').appendTo(reportBody));
        this.manageButtons();
    },

    removeReportForm: function() {
        if (this.reportForm) {
            this.reportForm.destroy();
        }
        this.element.find("[data-name='reportHeader']").remove();
        this.element.find("[data-name='reportBody']").remove();
        this.manageButtons();
    },

    saveReportForm: function(result) {
        if (!result && !this.reportForm.validateForm()) {
            return;
        }
        var data = result ? {fields: {}} : this.reportForm.getData();
        data.id = this.options.data.id;

        var type = this.options.data.type;
        if (type == 'approval') {
            data.fields['result'] = result ? result : 'agreed_with_note';
        }

        var saveUrl = 'app/saveTaskReport';
        var message = 'Не удалось сохранить отчет. Обратитесь к администратору.';

        $.post(saveUrl, JSON.stringify(data), $.proxy(function (response) {
            $.notify({message: 'Сохранено'}, {type: 'success', delay: 1000, timer: 1000});
            this.options.dispatcher.reopen(this.options.data.id);
            //this.options.mode = 'complete';
            //
            //if (this.reportForm) {
            //    this.reportForm.options.data.comment = response.comment;
            //    this.reportForm.options.data.result = response.result;
            //    this.reportForm.setMode('read');
            //}
            //
            //var buttons = $(this.element).find('[name="buttons"]');
            //buttons.remove();
        }, this)).fail($.proxy(function() {
            $.notify({message: message},{type: 'danger', delay: 0, timer: 0});
        }, this));
    },

    createButtons: function() {
        if (this.options.mode == 'complete') {
            return;
        }

        var executionsTitle;
        var reportTitle;
        var saveTitle;
        var type = this.options.data.type;
        if (type == 'execution') {
            executionsTitle = 'Создать резолюцию';
            reportTitle = 'Создать отчет';
            saveTitle = 'Сохранить отчет';
        } else if (type = 'approval') {
            executionsTitle = 'Создать согласование';
            reportTitle = 'Согласовать с замечанием';
            saveTitle = 'Согласовать';
        } else if (type == 'acquaintance') {
            executionsTitle = 'Создать ознакомление';
            reportTitle = 'Ознакомиться с комментарием';
            saveTitle = 'Ознакомиться';
        }

        var buttons = $(this.element).find('[name="buttons"]');
        if (buttons.length == 0) {
            buttons = $('<div name="buttons" class="panel-body execution-report-form-buttons"></div>').appendTo(this.element);
        }
        buttons.empty();

        var saveButton = $('<button type="button" name="save" style="display: none;" class="btn btn-success controlElementLeftMargin">' + saveTitle + '</button>');
        saveButton.click($.proxy(function() {
            this.saveReportForm();
        }, this));
        saveButton.appendTo(buttons);

        var executionButton = $('<button type="button" name="executionButton" style="display: none;" class="btn btn-info controlElementLeftMargin">' + executionsTitle + '</button>');
        executionButton.click($.proxy(function() {

        }, this));
        executionButton.appendTo(buttons);

        var cancelButton = $('<button type="button" name="cancel" style="display: none;" class="btn btn-default controlElementLeftMargin">Отмена</button>');
        cancelButton.click($.proxy(function() {
            this.options.mode = 'read';
            this.removeReportForm();
        }, this));
        cancelButton.appendTo(buttons);

        var approveButton = $('<button type="button" name="approveButton" style="display: none;" class="btn btn-success controlElementLeftMargin">Согласовать</button>');
        approveButton.click($.proxy(function() {
            this.saveReportForm("agreed");
        }, this));
        approveButton.appendTo(buttons);

        var reportButton = $('<button type="button" name="reportButton" style="display: none;" class="btn controlElementLeftMargin">' + reportTitle + '</button>');
        if (type == 'approval') {
            reportButton.addClass('btn-default');
        } else {
            reportButton.addClass('btn-success');
        }
        reportButton.click($.proxy(function() {
            this.options.mode = 'edit';
            this.renderReportForm();
        }, this));
        reportButton.appendTo(buttons);

        var notApproveButton = $('<button type="button" name="notApproveButton" style="display: none;" class="btn btn-default controlElementLeftMargin">Не согласовывать</button>');
        notApproveButton.click($.proxy(function() {
            this.saveReportForm("not_agreed");
        }, this));
        notApproveButton.appendTo(buttons);

        this.manageButtons();
    },

    manageButtons: function() {
        var buttons = $(this.element).find('[name="buttons"]');
        buttons.children().hide();
        var mode = this.options.mode;

        if (mode == 'read') {
            buttons.children('[name="executionButton"]').show();
            buttons.children('[name="reportButton"]').show();
            if (this.options.data.type == 'approval') {
                buttons.children('[name="approveButton"]').show();
                buttons.children('[name="notApproveButton"]').show();
            }
        } else if (mode == 'edit') {
            buttons.children('[name="save"]').show();
            buttons.children('[name="cancel"]').show();
        }
    },

    _destroy: function () {
        this.element.detach();
    }
});