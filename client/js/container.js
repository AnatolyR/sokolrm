$.widget('sokol.container', {
    options: {
        mode: "read"
    },
    _create: function () {
        this.createForm();
    },
    _destroy: function() {
        this.header.destroy();
        this.form.destroy();
        this.formButtons.destroy();
        this.attaches.destroy();
        this.element.detach();
    },
    createForm: function() {
        var data = this.options.data;
        var form = this.options.form;
        var fields = form.fields;

        var container = this.element;
        container.empty();
        container.addClass('container');

        this.header = $.sokol.containerHeader({data: data, form: form}, $('<div></div>').appendTo(this.element));

        this.formButtons = $.sokol.formButtons({mode: this.options.mode, dispatcher: this}, $('<div></div>').prependTo(this.element));

        this.form = $.sokol.form({mode: this.options.mode, data: data, form: form, dispatcher: this.options.dispatcher}, $('<div></div>').appendTo(this.element));

        this.attaches = $.sokol.attachesGrid({mode: this.options.mode, documentId: data.id}, $('<div></div>').appendTo(this.element));
    },

    notify: function(message) {
        $.notify({
            message: message
        },{
            type: 'success',
            delay: 1000,
            timer: 1000
        });
    },

    goToMode: function(mode) {
        this.options.mode = mode;

        this.formButtons.setMode(mode);

        this.form.setMode(mode);

        this.attaches.setMode(mode);
    },

    saveForm: function() {
        if (!this.form.validateForm()) {
            return;
        }

        var data = this.form.getData();

        $.post("app/savedocument", JSON.stringify(data), $.proxy(function (id) {
            $.notify({message: 'Сохранено'}, {type: 'success', delay: 1000, timer: 1000});
            this.options.dispatcher.open('document/' + id);
        }, this)).fail(function() {
            $.notify({message: 'Не удалось сохранить документ. Обратитесь к администратору.'},{type: 'danger', delay: 0, timer: 0});
        });
    },

    deleteDocument: function() {
        $.ajax({
            url: 'app/deletedocument?id=' + this.options.data.id,
            type: 'DELETE',
            success: $.proxy(function(result) {
                if (result == "true") {
                    this.element.empty();
                    //$.notify({message: 'Документ удален'}, {type: 'success', delay: 0, timer: 0});
                    $('<div class="alert alert-success" role="alert">Документ удален</div>').appendTo(this.element)
                } else {
                    $.notify({message: 'Не удалось удалить документ. Обратитесь к администратору.'},{type: 'danger', delay: 0, timer: 0});
                }
            }, this),
            error: function() {
                $.notify({message: 'Не удалось удалить документ. Обратитесь к администратору.'},{type: 'danger', delay: 0, timer: 0});
            }
        });
    }
});
