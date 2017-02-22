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
        var panelTitle = $('<div class="panel-title">Резолюция на исполнение</div>').appendTo(panelHeader);
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
                        "id": "executors",
                        "title": "Исполнители",
                        "type": "dictionary",
                        "multiple": true,
                        "mandatory": true,
                        "dictionary": "organizationPersons"
                    },
                    {
                        "id": "description",
                        "title": "Резолюция",
                        "type": "text",
                        "mandatory": true
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
        var panelHeader = $('<div data-name="reportHeader" class="panel-heading panel-footer" style="border-radius: 0;"></div>');
        var panelTitle = $('<div>Отчет об исполнении</div>');
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
                                "mandatory": true
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
                        "id": "comment",
                        "title": "Отчет",
                        "type": "text",
                        "mandatory": true
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

    saveReportForm: function() {
        if (!this.reportForm.validateForm()) {
            return;
        }
        var data = this.reportForm.getData();
        data.id = this.options.data.id;

        var saveUrl = 'app/saveTaskReport';
        var message = 'Не удалось сохранить отчет. Обратитесь к администратору.';

        $.post(saveUrl, JSON.stringify(data), $.proxy(function (response) {
            $.notify({message: 'Сохранено'}, {type: 'success', delay: 1000, timer: 1000});
            this.options.mode = 'complete';

            this.reportForm.options.data.comment = response.comment;
            this.reportForm.options.data.result = response.result;

            this.reportForm.setMode('read');
            var buttons = $(this.element).find('[name="buttons"]');
            buttons.remove();
        }, this)).fail($.proxy(function() {
            $.notify({message: message},{type: 'danger', delay: 0, timer: 0});
        }, this));
    },

    createButtons: function() {
        if (this.options.mode == 'complete') {
            return;
        }
        var buttons = $(this.element).find('[name="buttons"]');
        if (buttons.length == 0) {
            buttons = $('<div name="buttons" class="panel-body execution-report-form-buttons"></div>').appendTo(this.element);
        }
        buttons.empty();

        var saveButton = $('<button type="button" name="save" style="display: none;" class="btn btn-success controlElementLeftMargin">Сохранить отчет</button>');
        saveButton.click($.proxy(function() {
            this.saveReportForm();
        }, this));
        saveButton.appendTo(buttons);

        var executionButton = $('<button type="button" name="executionButton" style="display: none;" class="btn btn-info controlElementLeftMargin">Создать резолюцию</button>');
        executionButton.click($.proxy(function() {

        }, this));
        executionButton.appendTo(buttons);

        var cancelButton = $('<button type="button" name="cancel" style="display: none;" class="btn btn-default controlElementLeftMargin">Отмена</button>');
        cancelButton.click($.proxy(function() {
            this.options.mode = 'read';
            this.removeReportForm();
        }, this));
        cancelButton.appendTo(buttons);

        var reportButton = $('<button type="button" name="reportButton" style="display: none;" class="btn btn-success controlElementLeftMargin">Создать отчет</button>');
        reportButton.click($.proxy(function() {
            this.options.mode = 'edit';
            this.renderReportForm();
        }, this));
        reportButton.appendTo(buttons);

        this.manageButtons();
    },

    manageButtons: function() {
        var buttons = $(this.element).find('[name="buttons"]');
        buttons.children().hide();
        var mode = this.options.mode;

        if (mode == 'read') {
            buttons.children('[name="executionButton"]').show();
            buttons.children('[name="reportButton"]').show();
        } else if (mode == 'edit') {
            buttons.children('[name="save"]').show();
            buttons.children('[name="cancel"]').show();
        }
    },

    _destroy: function () {
        this.element.detach();
    }
});