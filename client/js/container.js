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
    createForm: function(isNew) {
        var data = this.options.data;
        var form = this.options.form;
        var fields = form.fields;
        if (isNew) {
            this.mode = "edit";
        } else {
            this.mode = "read";
        }
        var container = this.element;
        container.empty();
        container.addClass('container');

        this.header = $.sokol.containerHeader({data: data, form: form}, $('<div></div>').appendTo(this.element));

        this.formButtons = $.sokol.formButtons({dispatcher: this}, $('<div></div>').prependTo(this.element));

        this.form = $.sokol.form({data: data, form: form}, $('<div></div>').appendTo(this.element));

        this.attaches = $.sokol.attachesGrid(this.options, $('<div></div>').appendTo(this.element));
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
        this.form.saveForm();
    }
});