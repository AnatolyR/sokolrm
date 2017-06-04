$.widget('sokol.profile', {
    options: {

    },
    _create: function () {
        this.createHeader();
        this.createBlock();
    },
    _destroy: function() {
        this.element.detach();
    },

    createHeader: function() {
        this.element.addClass('panel panel-default');
        this.element.attr('name', 'attachmentsPanel');

        var panelHeader = $('<div class="panel-heading"></div>').appendTo(this.element);

        var panelTitle = $('<div class="panel-title">Профиль пользователя</div>').appendTo(panelHeader);

        var panelBody = $('<div class="panel-body"></div>');
        panelBody.appendTo(this.element);
        this.panelBody = panelBody;
    },

    createBlock: function () {
        if (this.form) {
            this.form.destroy();
        }
        this.form = $.sokol.form({
            mode: "edit",
            data: this.options.data ? this.options.data : {},
            form: {
                "id": "userSystem",
                "fields": [
                    {
                        "id": "oldPassword",
                        "title": "Старый пароль",
                        "type": "password",
                        "mandatory": true
                    },
                    {
                        "id": "newPassword",
                        "title": "Новый пароль",
                        "type": "password",
                        "mandatory": true
                    },
                    {
                        "id": "newPasswordConfirm",
                        "title": "Новый пароль подтверждение",
                        "type": "password",
                        "mandatory": true
                    },
                    {
                        "id": "newPasswordButton",
                        "title": "Сохранить",
                        "type": "button",
                        "method": "saveProfile",
                        "class": "btn-danger"
                    }
                ]
            },
            usePanel: false,
            dispatcher: this,
            containerType: 'profile'
        }, $('<div></div>').appendTo(this.panelBody));

        //this.createButtons();
    },

    saveProfile: function() {
        if (!this.form.validateForm()) {
            return;
        }

        var data = this.form.getData();
        var saveUrl = 'app/saveProfile';
        var message = 'Не удалось сохранить профиль пользователя.';

        $.post(saveUrl, JSON.stringify(data), $.proxy(function (id) {
            $.notify({message: 'Сохранено'}, {type: 'success', delay: 1000, timer: 1000});
            this.options.dispatcher.open('profile');
        }, this)).fail($.proxy(function() {
            $.notify({message: message},{type: 'danger', delay: 0, timer: 0});
        }, this));
    }
});
