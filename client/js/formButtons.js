$.widget('sokol.formButtons', {
    options: {
        mode: "read",
        actions: []
    },

    _create: function () {
        var buttons = this.element;
        buttons.addClass('formMainPanel');

        var saveButton = $('<button type="button" name="save" style="display: none;" class="btn btn-success controlElementLeftMargin">Сохранить</button>');
        saveButton.click($.proxy(function() {
            this.options.dispatcher.saveForm();
        }, this));
        saveButton.appendTo(buttons);

        var editButton = $('<button type="button" name="edit" style="display: none;" class="btn btn-default controlElementLeftMargin">Редактировать</button>');
        editButton.click($.proxy(function() {
            this.options.dispatcher.goToMode("edit");
        }, this));
        editButton.appendTo(buttons);

        if (this.options.id) {
            var cancelButton = $('<button type="button" name="cancel" style="display: none;" class="btn btn-default controlElementLeftMargin">Отменить</button>');
            cancelButton.click($.proxy(function () {
                this.options.dispatcher.goToMode("read");
            }, this));
            cancelButton.appendTo(buttons);
        }

        var deleteButton = $('<button type="button" name="delete" style="display: none;" class="btn btn-danger controlElementLeftMargin">Удалить</button>');
        deleteButton.click($.proxy(function() {
            this.options.dispatcher.deleteDocument();
        }, this));
        deleteButton.appendTo(buttons);

        var actions = this.options.actions;

        $.each(actions, $.proxy(function(i, a) {
            var actionButton = $('<button data-type="action" type="button" name="' + a.id + '" style="display: none;" class="btn btn-default controlElementLeftMargin">' + a.title + '</button>');
            actionButton.click($.proxy(function() {
                if (a.form) {
                    if (a.form == 'resolution') {
                        this.options.dispatcher.execution('resolution');
                    } else if (a.form == 'approval') {
                        this.options.dispatcher.execution('approval');
                    }
                } else {
                    this.doAction(a);
                }
            }, this));
            actionButton.appendTo(buttons);
        }, this));

        this.manageButtons();
    },

    doAction: function(action) {
        $.post('app/doAction', JSON.stringify({documentId: this.options.id, action: action.id}), $.proxy(function(result) {
            if (result == 'true') {
                this.options.dispatcher.reopen();
            }
        }, this)).fail($.proxy(function() {
            $.notify({message: 'Не удалось выполнить действие.'},{type: 'danger', delay: 0, timer: 0});
        }, this));
    },

    setMode: function(mode) {
        this.options.mode = mode;
        this.manageButtons();
    },

    manageButtons: function() {
        var buttons = this.element;
        buttons.children().hide();
        if (this.options.mode == 'read') {
            buttons.children().each(function(i, c) {
                var $c = $(c);
                if ($c.attr('data-type') == 'action') {
                    $c.show();
                }
            });
            buttons.children('[name="edit"]').show();
            buttons.children('[name="delete"]').show();
        } else if (this.options.mode == "edit") {
            if (!this.options.isNew) {
                buttons.children('[name="cancel"]').show();
            }
            buttons.children('[name="save"]').show();
        }
    },

    _destroy: function() {
        this.element.detach();
    }
});