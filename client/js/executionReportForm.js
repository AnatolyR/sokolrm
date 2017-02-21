$.widget('sokol.executionReportForm', {
    options: {

    },
    _create: function () {
        this.createHeader();
        this.createBlock();
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

    createButtons: function() {
        var buttons = $(this.element).find('[name="buttons"]');
        if (buttons.length == 0) {
            buttons = $('<div name="buttons" class="panel-body execution-report-form-buttons"></div>').appendTo(this.element);
        }
        buttons.empty();

        var executionButton = $('<button type="button" name="executionButton" style="display: none;" class="btn btn-info controlElementLeftMargin">Создать резолюцию</button>');
        executionButton.click($.proxy(function() {

        }, this));
        executionButton.appendTo(buttons);

        var reportButton = $('<button type="button" name="reportButton" style="display: none;" class="btn btn-success controlElementLeftMargin">Создать отчет</button>');
        reportButton.click($.proxy(function() {

        }, this));
        reportButton.appendTo(buttons);

        this.manageButtons();
    },

    manageButtons: function() {
        var buttons = $(this.element).find('[name="buttons"]');
        buttons.children().hide();
        var mode = this.options.mode;
        buttons.children('[name="executionButton"]').show();
        buttons.children('[name="reportButton"]').show();
    },

    _destroy: function () {
        this.element.detach();
    }
});