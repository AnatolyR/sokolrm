$.widget('sokol.form', {
    options: {
        mode: 'read',
        objectType: '',
        usePanel: true
    },

    _create: function () {
        this.fieldsInfo = [];
        this.fieldsInfoMap = [];
        this.createForm();
    },

    _destroy: function() {
        this.element.detach();
    },

    createForm: function() {
        this.formId = this.options.form.id;
        var data = this.options.data;
        var form = this.options.form;

        var container = this.element;
        container.empty();

        this.createMainBlock(container, form, data, this.options.mode == "edit");
    },

    createBlock: function(container, title) {
        var panel = $('<div class="panel panel-default"></div>');
        if (this.options.class) {
            panel.addClass(this.options.class);
        }
        panel.appendTo(container);
        var panelHeader = $('<div class="panel-heading"><div class="panel-title">' + title + '</div></div>');
        panelHeader.appendTo(panel);
        var panelBody = $('<div class="panel-body"></div>');
        panelBody.appendTo(panel);
        this.body = panelBody;
        return panelBody;
    },

    createButton: function(formNode, field, value, edit) {
        var button = $('<button type="button" class="btn">' + field.title + '</button>');
        if (field.class) {
            button.addClass(field.class);
        } else {
            button.addClass('btn-default');
        }
        button.click($.proxy(function() {
            if (this[field.method]) {
                this[field.method]();
            } else if (this.options.dispatcher[field.method]) {
                this.options.dispatcher[field.method]();
            }
        }, this));
        button.appendTo(formNode);
    },

    resetPassword: function() {
        $.post('app/resetPassword', {id: this.options.data.id}, function(data) {
            $.sokol.smodal({
                title: 'Новый пароль',
                body: data
            });
        });
    },

    createFieldPassword: function(formNode, field, value, edit) {
        $(formNode).append('' +
            '<div class="form-group' + (field.mandatory && edit ? ' formGroupRequired' : '') + '" style="' + (field.type == 'smallstring' ? 'width: 50%;' : '') + '">' +
            '<label class="control-label">' + field.title + ':</label>' +
            (edit ? ('<input name="' + field.id + '" class="form-control" type="password" value="' + value + '"' + (field.disabled ? ' disabled' : '') + '>') :
                ('<div>' + value + '</div>')) +
            '</div>');
    },

    createFieldString: function(formNode, field, value, edit) {
        $(formNode).append('' +
            '<div class="form-group' + (field.mandatory && edit ? ' formGroupRequired' : '') + '" style="' + (field.type == 'smallstring' ? 'width: 50%;' : '') + '">' +
            '<label class="control-label">' + field.title + ':</label>' +
            (edit ? ('<input name="' + field.id + '" class="form-control" type="text" value="' + value + '"' + (field.disabled ? ' disabled' : '') + '>') :
                ('<div>' + value + '</div>')) +
            '</div>');
    },

    createFieldText: function(formNode, field, value, edit) {
        if (!value && field.hideIfEmpty) {
            return;
        }

        $(formNode).append('' +
            '<div class="form-group' + (field.mandatory && edit ? ' formGroupRequired' : '') + '" style="' + (field.type == 'smallstring' ? 'width: 50%;' : '') + '">' +
            '<label class="control-label">' + field.title + ':</label>' +
            (edit ? ('<textarea rows="3" name="' + field.id + '" class="form-control"' + (field.disabled ? ' disabled' : '') + '>' + value + '</textarea>') :
                ('<div>' + value + '</div>')) +
            '</div>');
    },

    createFieldDate: function (formNode, field, value, edit) {
        value = value ? moment(value, 'DD.MM.YYYY HH:mm').format("L LT") : '';

        if (!value && field.hideIfEmpty) {
            return;
        }

        if (!edit || field.ar == 'read') {
            $(formNode).append('' +
                '<div class="form-group' + (field.mandatory && edit? ' formGroupRequired' : '') + '" style="' + (field.width ? 'width: ' + field.width + ';' : '') + '">' +
                '<label class="control-label">' + field.title + ':</label>' +
                '<div>' + value + '</div>' +
                '</div>' +
                '');
            return;
        }
        var dateNode = $('' +
            '<div class="form-group' + (field.mandatory && edit ? ' formGroupRequired' : '') + '" style="width: 200px;">' +
            '<label class="control-label">' + field.title + ':</label>' +
            '<div class="input-group date">' +
            '<input name="' + field.id + '" type="text" class="form-control" value="' + value + '"/>' +
            '<span class="input-group-addon">' +
            '<span class="glyphicon glyphicon-calendar"></span>' +
            '</span>' +
            '</div>' +
            '</div>');
        dateNode.appendTo(formNode);
        dateNode.find(".date").datetimepicker({
            //format: 'L',
            locale: 'ru'
        });

    },
    createFieldSelect: function(formNode, field, value, edit) {
        if (!value && field.hideIfEmpty) {
            return;
        }

        if (!edit) {
            if (field.options && field.valueField) {
                var vo =field.options.find(function (o) {
                    return o[field.valueField] == value;
                });
                if (vo) {
                    value = vo.title;
                }
            }
            $(formNode).append('' +
                '<div class="form-group' + (field.mandatory && edit ? ' formGroupRequired' : '') + '" style="' + (field.width ? 'width: ' + field.width + ';' : '') + '">' +
                '<label class="control-label">' + field.title + ':</label>' +
                '<div>' + value + '</div>' +
                '</div>' +
                '');
            return;
        }
        var option = value ? ('<option value="' + value + '">' + value + '</option>') : '';
        $(formNode).append('' +
            '<div class="form-group single' + (field.mandatory && edit? ' formGroupRequired' : '') + '" style="' + (field.width ? 'width: ' + field.width + ';' : '') + '">' +
            '<label class="control-label">' + field.title + ':</label>' +
            '<select name="' + field.id + '" class="demo-default">' + option + '</select>' +
            '</div>' +
            '');
        this.element.find('[name=' + field.id + ']').selectize({
            maxItems: 1,
            //plugins: ['remove_button'],
            valueField: field.valueField ? field.valueField : 'title',
            labelField: 'title',
            searchField: 'title',
            preload: true,
            options: field.options ? field.options : [],
            load: field.options ? null : function(query, callback) {
                $.getJSON('app/simpledictionary', {
                                id: field.dictionary
                            }, callback).fail(function() {
                    $.notify({message: 'Не удалось загрузить данные для справочника. Обратитесь к администратору.'},{type: 'danger', delay: 0, timer: 0});
                });
            },
            create: false
        });
    },
    createFieldDictionary: function(formNode, field, value, valueTitle, edit) {
        if( Object.prototype.toString.call(value) !== '[object Array]' ) {
            value = value ? [value] : [];
            valueTitle = valueTitle ? [valueTitle] : [];
        }

        var options = [];
        var initialValues = [];
        var titles = [];

        for (var i = 0; i < value.length; i++) {
            options.push({
                id: value[i],
                title: valueTitle[i]
            });
            initialValues.push(value[i]);
            titles.push(valueTitle[i]);
        }

        if (!edit) {
            $(formNode).append('' +
                '<div class="form-group' + (field.mandatory && edit ? ' formGroupRequired' : '') + '" style="' + (field.width ? 'width: ' + field.width + ';' : '') + '">' +
                '<label class="control-label">' + field.title + ':</label>' +
                '<div>' + titles.join(", ") + '</div>' +
                '</div>' +
                '');
            return;
        }

        var selector = $('<div class="form-group no-dropdown' + (field.mandatory ? ' formGroupRequired' : '') + '" style="' + (field.width ? 'width: ' + field.width + ';' : '') + '">' +
            '<label class="control-label">' + field.title + ':</label>' +
            '<select name="' + field.id + '" class="demo-default" id="selector_' + field.id + '">' + '</select>' +
            '' +
            '</div>' +
            '');
        $(formNode).append(selector);

        selector.find('select').selectize({
            maxItems: field.multiple ? 1000 : 1,
            plugins: ['restore_on_backspace', 'remove_button'],
            valueField: 'id',
            labelField: 'title',
            searchField: 'title',
            preload: field.preload ? true : false,
            closeAfterSelect: true,
            options: options,
            load: function(query, callback) {
                $.getJSON('app/dictionary', {
                    id: field.dictionary,
                    query: query
                }, callback).fail(function() {
                    $.notify({message: 'Не удалось загрузить данные для справочника. Обратитесь к администратору.'},{type: 'danger', delay: 0, timer: 0});
                });
            },
            create: false
        });
        selector.find('select')[0].selectize.setValue(initialValues);
    },

    createFieldGroup: function(formNode, field, value, valueTitle, edit) {
        if( Object.prototype.toString.call(value) !== '[object Array]' ) {
            value = value ? [value] : [];
            valueTitle = valueTitle ? [valueTitle] : [];
        }

        var options = [];
        var initialValues = [];
        var titles = [];

        for (var i = 0; i < value.length; i++) {
            options.push({
                id: value[i],
                title: valueTitle[i]
            });
            initialValues.push(value[i]);
            titles.push(valueTitle[i]);
        }

        if (!edit) {
            $(formNode).append('' +
                '<div class="form-group' + (field.mandatory && edit ? ' formGroupRequired' : '') + '" style="' + (field.width ? 'width: ' + field.width + ';' : '') + '">' +
                '<label class="control-label">' + field.title + ':</label>' +
                '<div>' + titles.join(", ") + '</div>' +
                '</div>' +
                '');
            return;
        }

        var selector = $('<div class="form-group no-dropdown' + (field.mandatory ? ' formGroupRequired' : '') + '" style="' + (field.width ? 'width: ' + field.width + ';' : '') + '">' +
            '<label class="control-label">' + field.title + ':</label>' +
            '<select name="' + field.id + '" class="demo-default" id="selector_' + field.id + '">' + '</select>' +
            '' +
            '</div>' +
            '');
        $(formNode).append(selector);

        selector.find('select').selectize({
            maxItems: 100,
            plugins: ['restore_on_backspace', 'remove_button'],
            valueField: 'id',
            labelField: 'title',
            searchField: 'title',
            preload: true,
            closeAfterSelect: false,
            options: options,
            load: function(query, callback) {
                $.getJSON('app/groups', {
                    size: 100,
                    conditions: JSON.stringify([{'condition':'','column':'title','operation':'LIKE','value':query}])
                }, function(response) {
                    callback(response.data);
                }).fail(function() {
                    $.notify({message: 'Не удалось загрузить данные для справочника. Обратитесь к администратору.'},{type: 'danger', delay: 0, timer: 0});
                });
            },
            create: false
        });
        selector.find('select')[0].selectize.setValue(initialValues);
    },

    createFieldUser: function(formNode, field, value, valueTitle, edit) {
        if( Object.prototype.toString.call(value) !== '[object Array]' ) {
            value = value ? [value] : [];
            valueTitle = valueTitle ? [valueTitle] : [];
        }

        if (value.length == 0 && field.hideIfEmpty) {
            return;
        }

        var options = [];
        var initialValues = [];
        var titles = [];

        for (var i = 0; i < value.length; i++) {
            options.push({
                id: value[i],
                title: valueTitle[i]
            });
            initialValues.push(value[i]);
            titles.push(valueTitle[i]);
        }

        if (!edit || field.ar == 'read') {
            $(formNode).append('' +
                '<div class="form-group' + (field.mandatory && edit ? ' formGroupRequired' : '') + '" style="' + (field.width ? 'width: ' + field.width + ';' : '') + '">' +
                '<label class="control-label">' + field.title + ':</label>' +
                '<div>' + titles.join(", ") + '</div>' +
                '</div>' +
                '');
            return;
        }

        var selector = $('<div class="form-group no-dropdown' + (field.mandatory ? ' formGroupRequired' : '') + '" style="' + (field.width ? 'width: ' + field.width + ';' : '') + '">' +
            '<label class="control-label">' + field.title + ':</label>' +
            '<select name="' + field.id + '" class="demo-default" id="selector_' + field.id + '">' + '</select>' +
            '' +
            '</div>' +
            '');
        $(formNode).append(selector);

        selector.find('select').selectize({
            maxItems: field.multiple ? 1000 : 1,
            plugins: ['restore_on_backspace', 'remove_button'],
            valueField: 'id',
            labelField: 'title',
            searchField: 'title',
            preload: true,
            closeAfterSelect: false,
            options: options,
            load: function(query, callback) {
                $.getJSON('app/users', {
                    size: 1000,
                    conditions: JSON.stringify([{'condition':'','column':'title','operation':'LIKE','value':query}])
                }, function(response) {
                    callback(response.data);
                }).fail(function() {
                    $.notify({message: 'Не удалось загрузить данные. Обратитесь к администратору.'},{type: 'danger', delay: 0, timer: 0});
                });
            },
            create: false
        });
        selector.find('select')[0].selectize.setValue(initialValues);
    },

    createFieldNumber: function(formNode, field, value, edit) {
        if (!edit) {
            $(formNode).append('' +
                '<div class="form-group' + (field.mandatory ? ' formGroupRequired' : '') + '" style="' + (field.width ? 'width: ' + field.width + ';' : '') + '">' +
                '<label class="control-label">' + field.title + ':</label>' +
                '<div>' + value + '</div>' +
                '</div>' +
                '');
            return;
        }
        $(formNode).append('' +
            '<div class="form-group' + (field.mandatory ? ' formGroupRequired' : '') + '" style="width: 200px;">' +
            '<label class="control-label">' + field.title + '</label>' +
            '<input name="' + field.id + '" class="form-control" type="text" value="' + value + '" ' + (field.disabled ? ' disabled' : '') + '>' +
            '</div>');
    },

    createFieldNumber00: function(formNode, field, value, edit) {
        if (!edit) {
            $(formNode).append('' +
                '<div class="form-group' + (field.mandatory ? ' formGroupRequired' : '') + '" style="' + (field.width ? 'width: ' + field.width + ';' : '') + '">' +
                '<label class="control-label">' + field.title + ':</label>' +
                '<div>' + value + '</div>' +
                '</div>' +
                '');
            return;
        }
        $(formNode).append('' +
            '<div class="form-group' + (field.mandatory ? ' formGroupRequired' : '') + '" style="width: 200px;">' +
            '<label class="control-label">' + field.title + '</label>' +
            '<input name="' + field.id + '" class="form-control" type="text" value="' + value + '"' + (field.disabled ? ' disabled' : '') + '>' +
            '</div>');
    },
    
    createMainBlock: function(container, form, data, edit) {
        var formNode;
        if (this.options.usePanel) {
            formNode = this.element.find('[name="mainForm"]');
            if (formNode.length == 0) {
                formNode = $('<form name="mainForm"></form>');
                var blockTitle = form.title;
                var blockNode = this.createBlock(container, blockTitle);
                formNode.appendTo(blockNode);
            } else {
                formNode.children().remove();
            }
        } else {
            container.empty();
            formNode = $('<form name="mainForm"></form>').appendTo(container);
        }
        for (var i = 0; i < form.fields.length; i++) {
            var field = form.fields[i];
            this.createField(formNode, field, data, edit);
        }
    },

    createField: function(formNode, field, data, edit) {
        if (field.delimeter) {
            $('<div class="">&nbsp;</div>').appendTo(formNode);
            return;
        }
        if (field.items) {
            var row = $('<div class="row" style="width: 100%; display1: table-row;"></div>');
            row.appendTo(formNode);
            var colsNum =  field.cols ? field.cols : field.items.length;
            var getColNumClass = function(cols) {
                var n = 12 / colsNum;
                return "col-md-" + (n * cols);
            };

            for (var i = 0; i < field.items.length; i++) {
                var subfield = field.items[i];
                if (subfield.type == "smallstring") {
                    subfield.type = "string";
                }
                var td = $('<div class="' + getColNumClass(subfield.cols ? subfield.cols : 1) + '"></div>');
                td.appendTo(row);
                this.createField(td, subfield, data, edit);
            }
            var ctd = $('<div style="clear: both" />');
            ctd.appendTo(row);
            return;
        }

        var id = field.id;
        var value = data[id];
        var valueTitle = data[id + "Title"];
        var type = field.type;

        this.fieldsInfo.push(field);
        this.fieldsInfoMap[id] = field;

        if (!value && value !== 0) {
            value = "";
        }
        edit =  edit && !(field.ar == 'read');
        
        if (type == "string" || type == "smallstring") {
            this.createFieldString(formNode, field, value, edit);
        } else if (type == "text") {
            this.createFieldText(formNode, field, value, edit);
        } else if (type == "password") {
            this.createFieldPassword(formNode, field, value, edit);
        } else if (type == "button") {
            this.createButton(formNode, field, value, edit);
        } else if (type == "date") {
            this.createFieldDate(formNode, field, value, edit);
        } else if (type == "select") {
            this.createFieldSelect(formNode, field, value, edit);
        } else if (type == "dictionary") {
            this.createFieldDictionary(formNode, field, value, valueTitle, edit);
        } else if (type == "group") {
            this.createFieldGroup(formNode, field, value, valueTitle, edit);
        } else if (type == "users") {
            this.createFieldUser(formNode, field, value, valueTitle, edit);
        } else if (type == "number") {
            this.createFieldNumber(formNode, field, value, edit);
        } else if (type == "number00") {
            this.createFieldNumber00(formNode, field, value, edit);
        }
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

    setMode: function(mode) {
        this.options.mode = mode;

        this.createMainBlock(this.element, this.options.form, this.options.data, mode == "edit");
    },

    validateForm: function() {
        var mainForm = this.element.find('[name="mainForm"]');
        var valuesList = mainForm.serializeArray();
        var values = [];
        for (var j = 0; j < valuesList.length; j++) {
            var val = valuesList[j];
            values[val.name] = val;
        }

        var valid = true;
        var numberRegex = /^-?\d+$/
        var decimalRegex = /^[0-9]+(\.[0-9][0-9]?)?$/

        for (var i = 0; i < this.fieldsInfo.length; i++) {
            var field = this.fieldsInfo[i];
            var val = values[field.id];

            var fieldDiv = mainForm.find("[name=" + field.id + "]").parent();
            if (fieldDiv.hasClass('date')) {
                fieldDiv = fieldDiv.parent();
            }
            if (field.mandatory) {
                if (val && val.value) {
                    fieldDiv.removeClass("has-error");
                    var helpBlock = fieldDiv.find(".help-block");
                    helpBlock.remove();
                } else {
                    if (!fieldDiv.hasClass('has-error')) {
                        fieldDiv.append($('<span class="help-block">Поле не может быть пустое</span>'));
                    }
                    fieldDiv.addClass("has-error");
                    valid = false;
                }

            } else if(field.type == 'number') {
                if (val.value && !val.value.match(numberRegex)) {
                    fieldDiv.addClass("has-error");
                    valid = false;
                } else {
                    fieldDiv.removeClass("has-error");
                }
            } else if(field.type == 'number00') {
                if (val.value && !val.value.match(decimalRegex)) {
                    fieldDiv.addClass("has-error");
                    valid = false;
                } else {
                    fieldDiv.removeClass("has-error");
                }
            } else if(field.validation) {
                if (val.value.search(field.validation) < 0) {
                    fieldDiv.addClass("has-error");
                    valid = false;
                } else {
                    fieldDiv.removeClass("has-error");
                }
            }
        }
        return valid;
    },

    getData: function() {
        var valuesList = this.element.find('[name="mainForm"]').serializeArray();
        var fields = {};
        for (var i = 0; i < valuesList.length; i++) {
            var value = valuesList[i];
            var fieldInfo = this.fieldsInfoMap[value.name];
            if (fieldInfo.multiple) {
                if (fields[value.name]) {
                    fields[value.name].push(value.value);
                } else {
                    fields[value.name] = [value.value];
                }
            } else {
                fields[value.name] = value.value;
            }
        }

        var data = {
            id: this.options.data.id,
            fields: fields,
            type: this.options.data.type
        };
        if (this.options.isNew) {
            data.isNew = this.options.isNew;
            data.type = this.options.data.type;
        }
        return data;
    },

    //todo remove
    saveForm: function() {
        console.log(">>>> form ", this.options.form);
        console.log(">>>> fieldsInfo ", this.options.fieldsInfo);
        //if (!this.validateForm()) {
        //    return;
        //}
        //console.log("Data: ", this.getData());
    }
});
