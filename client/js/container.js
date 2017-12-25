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

        if (this.history) {
            this.history.destroy();
        }
        if (this.linkeddocs) {
            this.linkeddocs.destroy();
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
        } else if (this.options.containerType == 'document' || this.options.containerType == 'task') {
            this.header = $.sokol.containerHeader({data: data, form: form}, $('<div></div>').appendTo(this.element));
        } else {
            this.header = $.sokol.titleHeader({
                title: data.title,
                subtitle: this.options.form.subtitle
            }, $('<div></div>').appendTo(this.element));
        }

        if (this.options.containerType != 'task') {
            this.formButtons = $.sokol.formButtons({
                mode: this.options.mode,
                dispatcher: this,
                containerType: this.options.containerType,
                actions: this.options.form.actions,
                deleteAction: this.options.form.deleteAction,
                editAction: this.options.form.editAction,
                id: this.options.id
            }, $('<div></div>').prependTo(this.element));
        }

        this.form = $.sokol.form({
            mode: this.options.mode,
            data: data,
            form: form,
            class: 'sokolMainAttributesPanel',
            dispatcher: this.options.dispatcher,
            containerType: this.options.containerType
        }, $('<div></div>').appendTo(this.element));

        if (this.options.containerType == 'document') {
            this.createExecutionListIfExist('approval');
            this.createExecutionListIfExist('execution');

            if (this.options.mode == 'read') {
                this.history = $.sokol.history({id: data.id}, $('<div></div>').appendTo(this.element));

                this.linkeddocs = $.sokol.linkeddocs({id: data.id}, $('<div></div>').appendTo(this.element));
            }
        }

        if (this.options.subforms) {
            for (var i = 0; i < this.options.subforms.length; i++) {
                var subform = this.options.subforms[i];
                this.createSubform(subform);
            }
        }

        if (this.options.id && !(this.options.form.showAttaches === false)) {
            this.attaches = $.sokol.attachesGrid({
                    mode: this.options.mode, 
                    id: data.id,
                    objectType: this.options.containerType
                }, 
                $('<div></div>').appendTo(this.element));
        }
    },

    execution: function(type, taskId) {
        if (!this[type + "Form"]) {
            this[type + "Form"] = $.sokol.executionForm({
                dispatcher: this,
                documentId: this.options.id,
                mode: 'create',
                type: type,
                taskId: taskId
            }, $("<div></div>").insertAfter(this.header.element));
        } else {
            $('html, body').animate({
                scrollTop: $(this[type + "Form"].element).offset().top - 50
            }, 2000);
        }
    },

    refreshExecutionList: function(type) {
        if (this[type + "Form"]) {
            this[type + "Form"].destroy();
        }
        this.createExecutionListIfExist(type);
    },

    createExecutionListIfExist: function(type, taskId) {
        $.getJSON('app/getExecutionList', {
            documentId: this.options.id,
            type: type,
            taskId: taskId
        }, $.proxy(function(data) {
            if (!$.isEmptyObject(data)) {
                this[type + "Form"] = $.sokol.executionForm({
                    dispatcher: this,
                    data: data,
                    documentId: this.options.id,
                    mode: 'read',
                    type: type,
                    taskId: taskId
                }, $("<div></div>").insertAfter(this.executionReportForm ? this.executionReportForm.element : this.form.element));
            }
        }, this));
    },

    createSubform: function(subform) {
        if (subform.form.id == 'accessRightsGrid') {
            var arGrid = $.sokol.accessRightsGrid({
                groupId: this.options.id
            }, $('<div></div>').appendTo(this.element));
            this.childs.push(arGrid);
            return;
        }
        if (subform.form.id == 'task') {
            var executionReportForm = $.sokol.executionReportForm({
                data: subform.data,
                dispatcher: this
            }, $('<div></div>').insertAfter(this.header.element));
            this.childs.push(executionReportForm);
            var taskId = subform.data.id;
            this.options.taskId = taskId;
            this.executionReportForm = executionReportForm;
            this.createExecutionListIfExist(subform.data.type, taskId);
            return;
        }
        if (subform.form.id == 'configFileEditor') {
            var arGrid = $.sokol.configFileEditor({
                configFileId: this.options.id
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

        if (this.formButtons && this.formButtons.setMode) {
            this.formButtons.setMode(mode);
        }

        if (this.form && this.form.setMode) {
            this.form.setMode(mode);
        }

        if (this.attaches && this.attaches.setMode) {
            this.attaches.setMode(mode);
        }

        for (var i = 0; i < this.childs.length; i++) {
            var child = this.childs[i];
            if (child.setMode) {
                child.setMode(mode);
            }
        }
    },

    updateCardFromTemplate: function(templateDocumentId) {
        $.getJSON('app/card', {id: templateDocumentId},
            $.proxy(function (data) {
                data.data.id = this.options.data.id;
                this.options.data = data.data;
                this.form.options.data = data.data;
                this.form.setMode('edit');
            }, this)
        ).fail($.proxy(function(e) {
                this.error = $('<div class="alert alert-danger" role="alert">Не удалось загрузить шаблон "' + id + '". Обратитесь к администратору.</div>').appendTo(this.element);
            }, this));
    },

    saveAsTemplate: function() {
        this.options.template = true;
        this.saveForm();
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

        if (this.options.template) {
            data.template = true;
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
        } else if (this.options.containerType == 'registrationlist') {
            saveUrl = 'app/saveRegistrationList';
            openType = 'registrationlist';
            message = 'Не удалось сохранить журнала регистрации. Обратитесь к администратору.';
        } else if (this.options.containerType == 'group') {
            saveUrl = 'app/saveGroup';
            openType = 'group';
            message = 'Не удалось сохранить карточку группы. Обратитесь к администратору.';
        } else if (this.options.containerType == 'document') {
            saveUrl = 'app/savedocument';
            openType = 'document';
            message = 'Не удалось сохранить документ. Обратитесь к администратору.';
        } else {
            saveUrl = 'app/saveEntity';
            data.saveType = this.options.containerType;
            openType = this.options.containerType;
            message = 'Не удалось сохранить сущность. Обратитесь к администратору.';
        }

        $.post(saveUrl, JSON.stringify(data), $.proxy(function (id) {
            $.notify({message: 'Сохранено'}, {type: 'success', delay: 1000, timer: 1000});
            this.options.dispatcher.open(openType + '/' + id);
        }, this)).fail($.proxy(function(e) {
            var detail = e.responseJSON.error;
            $.notify({message: message + " " + detail},{type: 'danger', delay: 0, timer: 0});
        }, this));
    },

    reopen: function(id) {
        this.options.dispatcher.open(this.options.containerType + '/' + (id ? id : (this.options.taskId ? this.options.taskId : this.options.id)));
    },

    deleteDocument: function() {
        $.sokol.smodal({
            title: 'Подтверждение удаления',
            body: 'Удалить "' + this.options.data.title + '" ?',
            confirmButtonTitle: 'Удалить',
            confirmAction: $.proxy(this.doDeleteDocument, this)
        });
    },

    cancelExecution: function() {
        if (this.executionForm) {
            this.executionForm.destroy();
            this.executionForm = null;
        }
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
        } else if (this.options.containerType == 'registrationlist') {
            deleteUrl = 'app/deleteRegistrationList';
            errorMessage = 'Не удалось удалить карточку журнала регистрации. Обратитесь к администратору.';
            message = 'Карточка журнала регистрации удалена';
        } else if (this.options.containerType == 'group') {
            deleteUrl = 'app/deleteGroup';
            errorMessage = 'Не удалось удалить карточку группы. Обратитесь к администратору.';
            message = 'Карточка группы удалена';
        } else if (this.options.containerType == 'document') {
            deleteUrl = 'app/deletedocument';
            errorMessage = 'Не удалось удалить документ. Обратитесь к администратору.';
            message = 'Документ удален';
        } else {
            deleteUrl = 'app/deleteEntity';
            errorMessage = 'Не удалось удалить сущность. Обратитесь к администратору.';
            message = 'Удалено';
        }
        $.ajax({
            url: deleteUrl + '?id=' + this.options.id + "&type=" + this.options.containerType,
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
