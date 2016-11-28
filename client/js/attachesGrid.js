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
        var id = this.options.id;
        $.getJSON("app/attaches",{objectId: id}, $.proxy(function (listData) {
            this.createAttachesList(listData, id);
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