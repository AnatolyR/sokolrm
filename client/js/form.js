$.widget('sokol.form', {
    options: {
        mode: "read"
    },

    _create: function () {
        this.createForm();
    },

    _destroy: function() {
        this.element.detach();
    },

    createForm: function(isNew) {
        var data = this.options.data;
        var form = this.options.form;

        var container = this.element;
        container.empty();

        this.createMainBlock(container, form, data, this.options.mode == "edit");
    },

    createBlock: function(container, title) {
        var panel = $('<div class="panel panel-default"></div>');
        panel.appendTo(container);
        var panelHeader = $('<div class="panel-heading">' + title + '</div>');
        panelHeader.appendTo(panel);
        var panelBody = $('<div class="panel-body"></div>');
        panelBody.appendTo(panel);
        return panelBody;
    },

    createFieldString: function(formNode, field, value, edit) {
        $(formNode).append('' +
            '<div class="form-group' + (field.mandatory && edit ? ' formGroupRequired' : '') + '" style="' + (field.type == 'smallstring' ? 'width: 50%;' : '') + '">' +
            '<label class="control-label">' + field.title + ':</label>' +
            (edit ? ('<input name="' + field.id + '" class="form-control" type="text" value="' + value + '">') :
                ('<div>' + value + '</div>')) +
            '</div>');
    },
    createFieldDate: function (formNode, field, value, edit) {
        value = moment(value).format("L LT");
        if (!edit) {
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
            locale: 'ru'
        });

    },
    createFieldSelect: function(formNode, field, value, edit) {
        if (!edit) {
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
            '<select name="' + field.id + '" class="demo-default" id="selector_' + field.id + '">' + option + '</select>' +
            '</div>' +
            '');
        $('#selector_' + field.id).selectize({
            maxItems: 1,
            //plugins: ['remove_button'],
            valueField: 'title',
            labelField: 'title',
            searchField: 'title',
            preload: true,
            options: [],
            load: function(query, callback) {
                //$.ajax({
                //    url: 'app/simpledic',
                //    type: 'GET',
                //    dataType: 'json',
                //    data: {
                //        id: field.dictionary
                //    },
                //    error: function() {
                //        callback();
                //    },
                //    success: function(res) {
                //        callback(res);
                //    }
                //});
                $.getJSON('app/simpledic', {
                                id: field.dictionary
                            }, callback);
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
            preload: false,
            options: options,
            load: function(query, callback) {
                $.ajax({
                    url: 'app/dic',
                    type: 'GET',
                    dataType: 'json',
                    data: {
                        id: field.dictionary,
                        query: query
                    },
                    error: function() {
                        callback();
                    },
                    success: function(res) {
                        callback(res);
                    }
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
            '<input name="' + field.id + '" class="form-control" type="text" value="' + value + '">' +
            '</div>');
    },
    createMainBlock: function(container, form, data, edit) {
        var formNode = $('#mainForm');
        if (formNode.length == 0) {
            formNode = $('<form id="mainForm"></form>');
            var blockNode = this.createBlock(container, "Основные реквизиты");
            formNode.appendTo(blockNode);
        } else {
            formNode.children().remove();
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

        if (!value) {
            value = "";
        }
        if (type == "string" || type == "smallstring") {
            this.createFieldString(formNode, field, value, edit);
        } else if (type == "date") {
            this.createFieldDate(formNode, field, value, edit);
        } else if (type == "select") {
            this.createFieldSelect(formNode, field, value, edit);
        } else if (type == "dictionary") {
            this.createFieldDictionary(formNode, field, value, valueTitle, edit);
        } else if (type == "number") {
            this.createFieldNumber(formNode, field, value, edit);
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

    validateForm: function(form, values) {
        var valid = true;
        for (var i = 0; i < form.fields.length; i++) {
            var field = form.fields[i];
            for (var j = 0; j < values.length; j++) {
                var val = values[j];
                if (val.name == field.id || j + 1 == values.length) {
                    var fieldDiv = $("#mainForm").find("[name=" + field.id + "]").parent();
                    if (field.mandatory) {
                        if (val && val.value) {
                            fieldDiv.removeClass("has-error");
                            var helpBlock = fieldDiv.find(".help-block");
                            helpBlock.remove();
                            break;
                        } else {
                            fieldDiv.addClass("has-error");
                            fieldDiv.append($('<span class="help-block">Поле не может быть пустое</span>'));
                            valid = false;
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
            }
        }
        return valid;
    },

    saveForm: function() {
        var formFields = $("#mainForm").serializeArray();
        if (!this.validateForm(this.options.form, formFields)) {
            return;
        }

        var data = {
            id: this.options.data.id,
            fields: formFields
        };
        if (this.options.isNew) {
            data.isNew = this.options.isNew;
            data.type = this.options.data.type;
        }
        $.post("app/save", JSON.stringify(data), $.proxy(function (data) {
            this.options.dispatcher.open(data.id);
            this.notify("Сохранено");
        }, this)).fail(function() {
            $.notify({message: 'Не удалось сохранить форму, проблемы с сетевым соединением'},{type: 'danger', delay: 1000, timer: 1000});
        });
    }
});