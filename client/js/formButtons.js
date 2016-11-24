$.widget('sokol.formButtons', {
    options: {
        mode: "read"
    },

    _create: function () {
        var buttons = this.element;
        buttons.addClass('formMainPanel');

        var saveButton = $('<button type="button" name="save" style="margin-right: 5px; display: none;" class="btn btn-success">Сохранить</button>');
        saveButton.click($.proxy(function() {
            this.options.dispatcher.saveForm();
        }, this));
        saveButton.appendTo(buttons);

        var editButton = $('<button type="button" name="edit" style="margin-right: 5px; display: none;" class="btn btn-default">Редактировать</button>');
        editButton.click($.proxy(function() {
            this.options.dispatcher.goToMode("edit");
        }, this));
        editButton.appendTo(buttons);

        var cancelButton = $('<button type="button" name="cancel" style="margin-right: 5px; display: none;" class="btn btn-default">Отменить</button>');
        cancelButton.click($.proxy(function() {
            this.options.dispatcher.goToMode("read");
        }, this));
        cancelButton.appendTo(buttons);

        this.manageButtons();
    },

    setMode: function(mode) {
        this.options.mode = mode;
        this.manageButtons();
    },

    manageButtons: function() {
        var buttons = this.element;
        buttons.children().hide();
        if (this.options.mode == "read") {
            buttons.children('[name="edit"]').show();
        } else if (this.options.mode == "edit") {
            if (!this.options.isNew) {
                buttons.children('[name="cancel"]').show();
            }
            buttons.children('[name="save"]').show();
        }
    },

    _destroy: function() {
        this.element.remove();
    }
});