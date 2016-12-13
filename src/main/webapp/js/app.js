$.widget('sokol.app', {
    options: {

    },
    _create: function () {
        console.debug("app.init");
        moment.locale('ru');
        this.showHeader();

        $(window).bind('hashchange', $.proxy(function() {
            var id = location.hash.slice(1);
            if (id) {
                this.open(id);
            } else {
                this.open("lists/documentsList");
            }

        }, this));
        $(window).trigger('hashchange');
    },
    _destroy: function() {
        this.header.destroy();
        this.grid.destroy();
        this.container.destroy();
    },

    showHeader: function() {
        $.getJSON("app/appsettings", {},
            $.proxy(function (data) {
                var options = data;
                this.headerObject = $.sokol.header(options, $("<nav></nav>").prependTo("body"));
            }, this)
        );
    },

    open:function(id) {
        this.updateHash(id);

        if (this.container) {
            this.container.destroy();
        }
        if (this.grid) {
            this.grid.destroy();
        }

        if (id.startsWith('lists/')) {
            this.createListWithNavigation(id)
        } else if (id.startsWith('document/')) {
            this.createDocumentForm(id.substring(9));
        }

    },

    createDocumentForm: function(id) {
        $.getJSON('app/card', {id: id},
            $.proxy(function (data) {
                var options = {
                    id: data.id,
                    data: data.data,
                    form: data.form
                };
                options.dispatcher = this;
                this.container = $.sokol.container(options, $('<div></div>').appendTo("body"));
            }, this)
        ).fail($.proxy(function(e) {
                $('<div class="alert alert-danger" role="alert">Не удалось загрузить документ "' + id + '". Обратитесь к администратору.</div>').appendTo(this.element);
            }, this));
    },

    createListWithNavigation: function(id) {
        //$("#main").removeClass("container").addClass("container-fluid");
        //$('<div class="row">' +
        //    '<div class="col-md-2" id="navigation" style="padding-right: 0;"></div>' +
        //    '<div class="col-md-10" id="central"></div>' +
        //    '</div>').appendTo("#main");
        //
        //this.createNavigation();
        //this.createList(data);

        $.getJSON('app/config', {id: id},
            $.proxy(function (data) {
                var options = {
                    title: data.title,
                    columnsVisible: data.columnsVisible,
                    columns: data.columns,
                    url: 'app/documents'
                };
                this.grid = $.sokol.grid(options, $("<div></div>").appendTo("body"));
            }, this)
        );
    },

    createNavigation: function() {
        $.getJSON("app/navigation",
            {
                id: "main"
            },
            $.proxy(function (data) {
                this.renderNavigation(data);
            }, this)
        );
    },

    renderNavigation: function(data) {
        var nav = $('<ul class="nav nav-pills nav-stacked"></ul>');
        nav.appendTo("#navigation");
        var that = this;
        for (var i = 0; i < data.length; i++) {
            var item = data[i];
            var li = $('<li role="presentation"></li>');
            if (item.type == "header") {
                li.addClass("nav-header");
            }
            var a = $('<a href="#">' + item.title + '</a>');
            a.appendTo(li);
            a.click(function (sli, snav, sitem) {
                return function (e) {
                    nav.children("li").removeClass("active");
                    if (!sli.hasClass("nav-header")) {
                        sli.addClass('active');
                    }
                    that.openList(sitem.id);
                    return false;
                }
            }(li, nav, item));
            li.appendTo(nav);

        }
    },

    updateHash: function(hash) {
        if (location.hash == "#" + hash) {
            return;
        }
        if(history.pushState) {
            history.pushState(null, null, "#" + hash);
        } else {
            location.hash = "#" + hash;
        }
    }
});

$.widget('sokol.attachesGrid', {
    options: {

    },

    _create: function () {
        this.createAttachmentBlock()
    },

    _destroy: function() {
        this.element.detach();
    },

    setMode: function(mode) {
        this.options.mode = mode;
        this.getAttachesList();
    },

    getAttachesList: function() {
        var documentId = this.options.documentId;
        $.getJSON("app/attaches",{documentId: documentId}, $.proxy(function (listData) {
            this.createAttachesList(listData, documentId);
        }, this));
    },

    createAttachesList: function(listData, objectId) {
        var edit = this.options.mode == "edit";
        var table = $("<table class='attachesList table'></table>");
        var thead = $('<thead></thead>').appendTo(table);
        var tbody = $('<tbody></tbody>').appendTo(table);

        this.element.children('.attachesList').remove();
        this.element.children("div:first-child").after(table);
        var columns = [
            {
                id: "title",
                title: "Название"
            },
            {
                id: "size",
                title: "Размер"
            },
            {
                id: "creationDate",
                title: "Дата добавления"
            },
            {
                id: "creator",
                title: "Кто добавил"
            }
        ];

        var header = $("<tr></tr>");
        header.appendTo(thead);
        for (var i = 0; i < columns.length; i++) {
            var col = columns[i];
            var colType = columns[i].type;
            if (colType != "hidden") {
                var th = $("<th>" + col.title + "</th>");
                th.appendTo(header);
            }
        }
        if (edit) {
            $("<th></th>").appendTo(header);
        }
        var rowsData = listData.data;
        for (var j = 0; j < rowsData.length; j++) {
            var row = $("<tr></tr>");
            var rowObj = rowsData[j];
            for (var k = 0; k < columns.length; k++) {
                var colId = columns[k].id;
                var colType = columns[k].type;
                if (colType != "hidden") {
                    if (colId == "title") {
                        var td = $('<td><a href="download?id=' + rowObj.id + '" target="_blank">' + rowObj[colId] + '</a></td>');
                        td.appendTo(row);
                    } else {
                        var val = rowObj[colId];
                        var td = $("<td>" + (val ? val : '') + "</td>");
                        td.appendTo(row);
                    }
                }
            }
            if (edit) {
                this.createDeleteAttachButoon(rowObj, row);
            }
            row.appendTo(tbody);
        }
    },

    createDeleteAttachButoon: function(rowObj, row) {
        var delButton = $('<button type="button" class="btn btn-danger btn-sm" aria-label="Left Align">' +
            '<span class="glyphicon glyphicon-trash" aria-hidden="true"></span>' +
            '</button>');
        delButton.click($.proxy(function() {
            $.getJSON('app/deleteAttach',
                {id: rowObj.id},
                $.proxy(function(response){
                    $.notify({
                        message: 'Вложение удалено'
                    },{
                        type: 'success',
                        delay: 1000,
                        timer: 1000
                    });
                    $("#attachField").filestyle('clear');
                    this.getAttachesList();
                }, this)
            );
        }, this));
        var delTd = $('<td></td>');
        delButton.appendTo(delTd);
        delTd.appendTo(row);
    },

    createAttachmentBlock: function() {
        this.element.addClass('panel panel-default');
        this.element.attr('name', 'attachmentsPanel');

        var panelHeader = $('<div class="panel-heading">Вложения</div>');
        panelHeader.appendTo(this.element);
        var panelBody = $('<div class="panel-body"></div>');
        panelBody.appendTo(this.element);
        var data = this.options.data;

        this.getAttachesList();
        var attachDiv = $('<div style="width: 50%; float: left;"><input type="file" id="attachField" class="filestyle"></div>');
        attachDiv.hide();
        attachDiv.appendTo(panelBody);
        $(":file").filestyle({buttonText: "Выбрать вложение"});
        var saveAttachButton = $('<button type="button" style="margin-right: 5px;" class="btn btn-primary">Добавить</button>');
        saveAttachButton.click($.proxy(function() {
            var file_data = $('#attachField').prop('files')[0];
            var form_data = new FormData();
            form_data.append('file', file_data);
            $.ajax({
                url: 'upload?objectId=' + data.id, // point to server-side PHP script
                dataType: 'text',  // what to expect back from the PHP script, if anything
                cache: false,
                contentType: false,
                processData: false,
                data: form_data,
                type: 'post',
                success: $.proxy(function(response){
                    this.notify("Вложение сохранено");
                    $("#attachField").filestyle('clear');
                    this.getAttachesList(panel, data.id);
                }, this)
            });
        }, this));
        saveAttachButton.hide();
        saveAttachButton.appendTo(panelBody);
        var addAttachButton = $("<button type='button' class='btn btn-default'>Добавить вложение</button>");
        var cancelAttachButton = $("<button type='button' class='btn btn-default'>Отменить</button>");
        addAttachButton.click($.proxy(function(e) {
            attachDiv.show();
            saveAttachButton.show();
            cancelAttachButton.show();
            $(e.target).hide();
        }, this));
        addAttachButton.appendTo(panelBody);
        cancelAttachButton.click($.proxy(function(e) {
            attachDiv.hide();
            saveAttachButton.hide();
            $(e.target).hide();
            addAttachButton.show();
        }, this));
        cancelAttachButton.hide();
        cancelAttachButton.appendTo(panelBody);
        return panelBody;
    }
});
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

        this.form = $.sokol.form({data: data, form: form, dispatcher: this.options.dispatcher}, $('<div></div>').appendTo(this.element));

        this.attaches = $.sokol.attachesGrid({documentId: data.id}, $('<div></div>').appendTo(this.element));
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
    }
});

$.widget('sokol.containerHeader', {
    options: {
        form: null,
        data: null
    },
    _create: function () {
        var form = this.options.form;
        var data = this.options.data;
        var date = data.registrationDate ? moment(data.registrationDate, 'DD.MM.YYYY HH:mm').format("L") : '-';
        this.element.addClass('panel-body');
        $('<h3>' + (form.typeTitle ? form.typeTitle : '-') +
            ' № ' + (data.documentNumber ? data.documentNumber : '-') +
            ' от ' + date +
            (data.documentKind ? (' (' + data.documentKind + ')') : '') +
            '</h3>' +
            (data.title ? ('<h4 class="">' + data.title + '</h4>') : '')+
            '<div>Статус: ' + (data.status ? data.status : '') + '</div>'
            ).appendTo(this.element);
    },
    _destroy: function() {
        this.element.detach();
    }
});
$.widget('sokol.form', {
    options: {
        mode: 'read',
        objectType: '',
        fieldsInfo: [],
        fieldsInfoMap: []
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
        value = value ? moment(value, 'DD.MM.YYYY HH:mm').format("L LT") : '';
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
            '<select name="' + field.id + '" class="demo-default">' + option + '</select>' +
            '</div>' +
            '');
        this.element.find('[name=' + field.id + ']').selectize({
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
                $.getJSON('app/config', {
                                id: "dictionaries/" + field.dictionary
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
        var formNode = this.element.find('[name="mainForm"]');
        if (formNode.length == 0) {
            formNode = $('<form name="mainForm"></form>');
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

        this.options.fieldsInfo.push(field);
        this.options.fieldsInfoMap[id] = field;

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

    validateForm: function() {
        var mainForm = this.element.find('[name="mainForm"]');
        var valuesList = mainForm.serializeArray();
        var values = [];
        for (var j = 0; j < valuesList.length; j++) {
            var val = valuesList[j];
            values[val.name] = val;
        }

        var valid = true;

        for (var i = 0; i < this.options.fieldsInfo.length; i++) {
            var field = this.options.fieldsInfo[i];
            var val = values[field.id];

            var fieldDiv = mainForm.find("[name=" + field.id + "]").parent();
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
            var fieldInfo = this.options.fieldsInfoMap[value.name];
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
        this.element.detach();
    }
});
$.widget("sokol.grid", {
    options: {
        url: null,
        id: "",
        pageSize: 20,
        offset: 0,
        pageCount: 0,
        currentPage: 1,
        total: 0,
        title: "",
        columnsVisible: [],
        objectType: 'document'
    },

    _create: function () {
        this.createList();
    },

    _destroy: function() {
        this.element.detach();
    },

    _setOptions: function (options) {
        this._super(options);
        this.reload();
    },

    createList: function() {
        var central = this.element;
        central.empty();
        central.addClass('container');

        var pagination = this.createPagination(central);
        this.createColumnsSelector(pagination);

        this.renderTablePanel();
        this.reload();

        this.createPagination(central);
    },

    setPage: function(page) {
        if (page > 0 && page < this.options.pageCount + 1) {
            this.options.currentPage = page;
            this.options.offset = this.options.pageSize * (page - 1);
            this.reload();
        }
    },

    nextPage: function() {
        this.setPage(this.options.currentPage + 1)
    },

    previewPage: function() {
        this.setPage(this.options.currentPage - 1);
    },

    refresh: function () {
        this.renderTableHeader();
        this.renderRows();
        this.updatePagination();
    },

    reload: function() {
        if (this.options.url) {
            $.getJSON(this.options.url, {
                id: this.options.id,
                size: this.options.pageSize,
                offset: this.options.offset
            }, $.proxy(function (data) {
                this.options.data = data.data;
                this.options.total = data.total;
                this.refresh();

            }, this)).fail(function() {
                $.notify({message: 'Не удалось загрузить данные. Обратитесь к администратору.'}, {type: 'danger', delay: 0, timer: 0});
            });
        } else {
            this.options.total = this.options.data.length;
            this.refresh();
        }
    },

    createPagination: function(container) {
        var div = $('<div style="text-align1: center; margin-bottom: 10px;"></div>').appendTo(container);
        $(
            '<span style="margin-right: 10px;">Найдено: <span name="gridItemsCount">0</span></span>' +
            '<button name="preview" class="btn btn-default" href = "#" disabled1="disable">' +
            '<span class="glyphicon glyphicon-triangle-left" aria-hidden="true"></span> Предыдущая</button>&nbsp;' +
            '<select name="pageSelector" class="selectpicker"></select>&nbsp;' +
            '<button name="next" class="btn btn-default" href = "#">Следующая ' +
            '<span class="glyphicon glyphicon-triangle-right" aria-hidden="true"></span></button>&nbsp;' +
            '<span style="margin-left: 10px;">Отображать ' +
            '<select name="pageSize" class="selectpicker"><option>20</option><option>50</option><option>100</option></select>&nbsp;' +
            '</span>&nbsp;'
        ).appendTo(div);

        $(div).find('.selectpicker').selectpicker({
            noneSelectedText: '0 / 0',
            width: 'fit'
        });
        $(div).find('.selectpicker[name="pageSelector"]').on('change', {grid: this}, function(event){
            var selected = $(this).find("option:selected").val();
            var val = selected.split(" / ")[0];
            event.data.grid.setPage(val);
        });
        $(div).find('.selectpicker[name="pageSize"]').on('change', {grid: this}, function(event){
            var size = $(this).find("option:selected").val();
            var grid = event.data.grid;
            grid.options.pageSize = size;
            grid.reload();

        });
        $(div).find("[name='preview']").on('click', $.proxy(this.previewPage, this));
        $(div).find("[name='next']").on('click', $.proxy(this.nextPage, this));

        return div;
    },
    
    updatePagination: function() {
        this.element.find("[name='gridItemsCount']").text(this.options.total);
        var pages = this.options.total / this.options.pageSize;
        var pagesInt = parseInt(pages);
        pages = pagesInt < pages ? pagesInt + 1 : pagesInt;
        this.options.pageCount = pages;
        if (this.options.currentPage > pages) {
            this.options.currentPage = pages;
        }

        var selectors = this.element.find("[name='pageSelector']");
        selectors.empty();
        for (var p = 1; p <= pages; p++) {
            selectors.append($("<option>" + p + " / " + pages + "</option>"))
        }

        if (this.options.currentPage == 1) {
            this.element.find("[name='preview']").attr('disabled', 'disabled');
        } else {
            this.element.find("[name='preview']").removeAttr('disabled');
        }
        if (this.options.currentPage == this.options.pageCount) {
            this.element.find("[name='next']").attr('disabled', 'disabled');
        } else {
            this.element.find("[name='next']").removeAttr('disabled');
        }
        this.element.find('select[name="pageSelector"]').val(this.options.currentPage + " / " + this.options.pageCount);
        this.element.find('select[name="pageSize"]').val(this.options.pageSize);
        this.element.find('.selectpicker').selectpicker('refresh');
    },

    renderTableHeader: function() {
        var thead = this.element.find('[name="tableHead"]');
        thead.empty();
        var columns = this.options.columns;
        var header = $("<tr></tr>");
        header.appendTo(thead);
        for (var i = 0; i < columns.length; i++) {
            var col = columns[i];
            var colType = columns[i].type;
            if (colType != "hidden" && this.isColumnVisible(col.id)) {
                var th = $("<th>" + col.title + "</th>");
                th.appendTo(header);
            }
        }
    },

    renderTablePanel: function() {
        var panel = this.element.find('[name="tablePanel"]');
        if (!panel.length) {
            panel = $('<div name="tablePanel" class="panel panel-default"></div>');
            panel.appendTo(this.element);
        } else {
            panel.empty();
        }
        var panelHeader = $('<div class="panel-heading"></div>');
        var panelTitle = $('<div class="panel-title">' + this.options.title + '</div>');
        panelTitle.appendTo(panelHeader);
        panelHeader.appendTo(panel);
        var table = $("<table class='table'></table>");
        table.appendTo(panel);
        var thead = $('<thead name="tableHead"></thead>');
        thead.appendTo(table);
        var tbody = $('<tbody name="tableBody"></tbody>');
        tbody.appendTo(table);

        this.renderTableHeader();
    },

    isColumnVisible: function(columnId) {
        return this.options.columnsVisible.indexOf(columnId) >= 0;
    },

    renderRows: function() {
        var tbody = this.element.find('tbody');
        tbody.empty();
        var rowsData = this.options.data;
        var columns = this.options.columns;
        for (var j = 0; j < rowsData.length; j++) {
            var row = $("<tr></tr>");
            var rowObj = rowsData[j];
            for (var k = 0; k < columns.length; k++) {
                var column = columns[k];
                var colId = column.id;
                var colType = column.type;
                if (colType != "hidden" && this.isColumnVisible(colId)) {
                    var val = rowObj[colId];
                    if (val) {
                        if (colId == "title") {
                            var td = $('<td><a href="#' + this.options.objectType + '/' + rowObj.id + '" target="_blank">' + val + '</a></td>');
                            td.appendTo(row);
                        } else if (column.render == 'datetime') {
                            val = moment(val).format('L LT');

                            var td = $('<td>' + (val ? val : '') + '</td>');
                            td.appendTo(row);
                        } else {
                            var td = $('<td>' + (val ? val : '') + '</td>');
                            td.appendTo(row);
                        }
                    } else {
                        row.append($('<td></td>'));
                    }
                }
            }
            row.appendTo(tbody);
        }
    },

    createColumnsSelector: function(element) {
        var selector = $('<div class="dropdown btn-group">'+
            '<button type="button" class="btn btn-default btn dropdown-toggle" data-toggle="dropdown">Колонки <span class="caret"></span></button>'+
            '<ul name="columns" class="dropdown-menu">'+
            '</ul>'+
            '</div>');

        var ul = selector.find('[name="columns"]');

        for (var i = 0; i < this.options.columns.length; i++) {
            var column = this.options.columns[i];
            if (column.type != "hidden") {
                var li = $('<li><a href="#" class="" data-value="' + column.id + '" tabIndex="-1"><input type="checkbox"/>&nbsp;' + column.title + '</a></li>');
                var inp = li.find("input");
                if (this.options.columnsVisible.indexOf(column.id) > -1) {
                    inp.prop('checked', true);
                }
                ul.append(li);
            }
        }

        selector.appendTo(element);
        $(selector).find('.dropdown-menu a').on('click', $.proxy(function(event) {

            var $target = $(event.currentTarget),
                val = $target.attr('data-value'),
                $inp = $target.find('input'),
                idx;

            if (( idx = this.options.columnsVisible.indexOf(val) ) > -1) {
                this.options.columnsVisible.splice(idx, 1);
                setTimeout(function () {
                    $inp.prop('checked', false);
                }, 0);
            } else {
                this.options.columnsVisible.push(val);
                setTimeout(function () {
                    $inp.prop('checked', true);
                }, 0);
            }

            $(event.target).blur();

            this.reload();
            return false;
        }, this));
    }
});
$.widget('sokol.header', {
    options: {
        userName: ''
    },
    _create: function () {
        var leftMenu = $('<ul></ul>').addClass('nav navbar-nav');
        var rightMenu = $('<ul></ul>').addClass('nav navbar-nav navbar-right');

        this.createMenu(leftMenu, this.options.leftMenu);
        this.createMenu(rightMenu, this.options.rightMenu);

        var search = $('<form class="nav navbar-form navbar-right" role="search"></form>')
            .append($('<div class="input-group"></div>')
                .append($('<input type="text" class="form-control" placeholder="Поиск" name="srch-term" id="srch-term">'))
                .append($('<div class="input-group-btn"></div>')
                    .append('<button class="btn btn-default" type="submit"><i class="glyphicon glyphicon-search"></i></button>')));

        this.element.addClass('navbar navbar-inverse navbar-fixed-top')
            .append($('<div></div>').addClass('container')
                .append($('<div></div>').addClass('navbar-header')
                    .append($('<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">' +
                        '<span class="sr-only">Меню</span>' +
                        '<span class="icon-bar"></span>' +
                        '<span class="icon-bar"></span>' +
                        '<span class="icon-bar"></span>' +
                    '</button>'))
                    .append($('<a href="">Сокол СЭД</a>').addClass('navbar-brand')))
                .append($('<div id="navbar"></div>').addClass('navbar-collapse collapse')
                    .append(leftMenu)
                    .append(rightMenu)
                    .append(search))
        );
    },
    createMenu: function (node, menu) {
        for (var i = 0; i < menu.length; i++) {
            var item = menu[i];
            var title = item.title;
            if (title == "$userName") {
                title = this.options.userName;
            }
            if (item.submenu) {
                var submenu = $('<ul></ul>').addClass('dropdown-menu');
                for (var j = 0; j < item.submenu.length; j++) {
                    var subitem = item.submenu[j];
                    if (subitem.separator) {
                        submenu.append($('<li role="separator" class="divider"></li>'));
                    } else {
                        submenu.append($('<li><a href="' + subitem.link + '">' + subitem.title + '</a></li>'));
                    }
                }

                node.append($('<li></li>').addClass('dropdown')
                    .append($('<a href="#">' + title + ' <span class="caret"></a>').addClass('dropdown-toggle').attr('data-toggle', 'dropdown'))
                    .append(submenu));
            } else {
                node.append($('<li><a href="' + item.link + '">' + title + '</a></li>'));
            }
        }
    },
    _setOption: function (key, value) {
        this._super(key, value);
    },
    _setOptions: function (options) {
        this._super(options);
    }
});
//# sourceMappingURL=app.js.map