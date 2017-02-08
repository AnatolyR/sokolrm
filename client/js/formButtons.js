$.widget('sokol.formButtons', {
    options: {
        mode: "read",
        actions: []
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

        if (this.options.id) {
            var cancelButton = $('<button type="button" name="cancel" style="margin-right: 5px; display: none;" class="btn btn-default">Отменить</button>');
            cancelButton.click($.proxy(function () {
                this.options.dispatcher.goToMode("read");
            }, this));
            cancelButton.appendTo(buttons);
        }

        var deleteButton = $('<button type="button" name="delete" style="margin-right: 5px; display: none;" class="btn btn-danger">Удалить</button>');
        deleteButton.click($.proxy(function() {
            this.options.dispatcher.deleteDocument();
        }, this));
        deleteButton.appendTo(buttons);

        if (this.options.actions.indexOf('doresolution') >= 0) {
            var resolutionButton = $('<button type="button" name="doresolution" style="margin-right: 5px; display: none;" class="btn btn-default">Резолюция</button>');
            resolutionButton.click($.proxy(function() {

            }, this));
            resolutionButton.appendTo(buttons);
        }

        this.manageButtons();
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
                if ($c.attr('name').indexOf('do') == 0) {
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