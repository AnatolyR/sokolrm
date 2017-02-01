$.widget('sokol.container', {
    options: {
        mode: "read"
    },
    _create: function () {
        this.childs = [];
        this.createForm();
    },
    _destroy: function() {
        if (this.header) {
            this.header.destroy();
        }
        if (this.form) {
            this.form.destroy();
        }
        if (this.formButtons) {
            this.formButtons.destroy();
        }
        if (this.attaches) {
            this.attaches.destroy();
        }
        for (var i = 0; i < this.childs.length; i++) {
            var child = this.childs[i];
            if (child.destroy) {
                child.destroy();
            }
        }
        this.element.detach();
    },
    createForm: function() {
        var data = this.options.data;
        var form = this.options.form;
        var fields = form.fields;

        var container = this.element;
        container.empty();
        container.addClass('container');

        if (this.options.containerType == 'user') {
            this.header = $.sokol.userHeader({data: data, form: form}, $('<div></div>').appendTo(this.element));
        } else if (this.options.containerType == 'contragent') {
            this.header = $.sokol.titleHeader({
                title: data.title,
                subtitle: data.fullName
            }, $('<div></div>').appendTo(this.element));
        } else if (this.options.containerType == 'document') {
            this.header = $.sokol.containerHeader({data: data, form: form}, $('<div></div>').appendTo(this.element));
        } else {
            this.header = $.sokol.titleHeader({
                title: data.title
            }, $('<div></div>').appendTo(this.element));
        }

        this.formButtons = $.sokol.formButtons({
            mode: this.options.mode,
            dispatcher: this,
            containerType: this.options.containerType,
            id: this.options.id
        }, $('<div></div>').prependTo(this.element));

        this.form = $.sokol.form({
            mode: this.options.mode,
            data: data,
            form: form,
            dispatcher: this.options.dispatcher,
            containerType: this.options.containerType
        }, $('<div></div>').appendTo(this.element));

        if (this.options.subforms) {
            for (var i = 0; i < this.options.subforms.length; i++) {
                var subform = this.options.subforms[i];
                this.createSubform(subform);
            }
        }

        if (this.options.id) {
            this.attaches = $.sokol.attachesGrid({mode: this.options.mode, id: data.id}, $('<div></div>').appendTo(this.element));
        }
    },

    createSubform: function(subform) {
        if (subform.form.id == 'accessRightsGrid') {
            var arGrid = $.sokol.accessRightsGrid({
                groupId: this.options.id
            }, $('<div></div>').appendTo(this.element));
            this.childs.push(arGrid);
            return;
        }
        var form = $.sokol.form({
            mode: "read",
            data: subform.data ? subform.data : this.options.data,
            form: subform.form,
            parent: this,
            dispatcher: this.options.dispatcher,
            containerType: this.options.containerType
        }, $('<div></div>').appendTo(this.element));
        this.childs.push(form);
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

        for (var i = 0; i < this.childs.length; i++) {
            var child = this.childs[i];
            if (child.setMode) {
                child.setMode(mode);
            }
        }
    },

    saveForm: function() {
        if (!this.form.validateForm()) {
            return;
        }

        var data = this.form.getData();

        for (var i = 0; i < this.childs.length; i++) {
            var child = this.childs[i];
            if (child.getData) {
                data[child.formId] = child.getData();
            }
        }

        var saveUrl;
        var message;
        var openType;
        if (this.options.containerType == 'user') {
            saveUrl = 'app/saveuser';
            openType = 'user';
            message = 'Не удалось сохранить карточку пользователя. Обратитесь к администратору.';
        } else if (this.options.containerType == 'contragent') {
            saveUrl = 'app/savecontragent';
            openType = 'contragent';
            message = 'Не удалось сохранить карточку контрагента. Обратитесь к администратору.';
        } else {
            saveUrl = 'app/savedocument';
            openType = 'document';
            message = 'Не удалось сохранить документ. Обратитесь к администратору.';
        }

        $.post(saveUrl, JSON.stringify(data), $.proxy(function (id) {
            $.notify({message: 'Сохранено'}, {type: 'success', delay: 1000, timer: 1000});
            this.options.dispatcher.open(openType + '/' + id);
        }, this)).fail($.proxy(function() {
            $.notify({message: message},{type: 'danger', delay: 0, timer: 0});
        }, this));
    },

    deleteDocument: function() {
        $.sokol.smodal({
            title: 'Подтверждение удаления',
            body: 'Удалить "' + this.options.data.title + '" ?',
            confirmButtonTitle: 'Удалить',
            confirmAction: $.proxy(this.doDeleteDocument, this)
        });
    },

    doDeleteDocument: function() {
        var deleteUrl;
        var errorMessage;
        var message;
        if (this.options.containerType == 'user') {
            deleteUrl = 'app/deleteuser';
            errorMessage = 'Не удалось удалить карточку пользователя. Обратитесь к администратору.';
            message = 'Карточка пользователя удалена';
        } else if (this.options.containerType == 'contragent') {
            deleteUrl = 'app/deletecontragent';
            errorMessage = 'Не удалось удалить карточку контрагента. Обратитесь к администратору.';
            message = 'Карточка контрагента удалена';
        } else {
            deleteUrl = 'app/deletedocument';
            errorMessage = 'Не удалось удалить документ. Обратитесь к администратору.';
            message = 'Документ удален';
        }
        $.ajax({
            url: deleteUrl + '?id=' + this.options.id,
            type: 'DELETE',
            success: $.proxy(function(result) {
                if (result == "true") {
                    this.element.empty();
                    $('<div class="alert alert-success" role="alert">' + message + '</div>').appendTo(this.element)
                } else {
                    $.notify({message: errorMessage},{type: 'danger', delay: 0, timer: 0});
                }
            }, this),
            error: function() {
                $.notify({message: errorMessage},{type: 'danger', delay: 0, timer: 0});
            }
        });
    }
});
