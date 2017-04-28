$.widget('sokol.linkeddocs', {
    options: {

    },

    _create: function () {
        this.createHeader();
        this.createList();
    },

    createHeader: function() {
        this.element.addClass('panel panel-default');

        var panelHeader = $('<div class="panel-heading"></div>').appendTo(this.element);

        var panelTitle = $('<div class="panel-title">Связанные документы</div>').appendTo(panelHeader);

        var panelBody = $('<div style="height: 10px;"></div>');
        panelBody.appendTo(this.element);
        this.panelBody = panelBody;
    },

    _destroy: function() {
        this.element.detach();
    },

    createList: function(listData, objectId) {
        var place = $("<div></div>").appendTo(this.element);

        $.getJSON('app/config', {id: 'lists/linkedDocumentsList'},
            $.proxy(function (data) {
                var options = {
                    title: data.title,
                    columnsVisible: data.columnsVisible,
                    columns: data.columns,
                    url: data.url ? data.url : 'app/documents',
                    id: 'linkedDocuments',
                    docId: this.options.id,
                    pageSize: 5,
                    filterable: false,
                    sortable: true,
                    usePanel: false,
                    showTableHeader: false,
                    bottomPagination: false,
                    topBorder: true,
                    selectable: true,
                    deletable: true,
                    deleteMethod: $.proxy(this.doDeleteWithConfirm, this),
                    addable: true,
                    addMethod: $.proxy(this.doAdd, this),
                    idField: data.idField
                };

                this.grid = $.sokol.grid(options, place);

            }, this)
        ).fail(function failLoadList() {
                $.notify({message: 'Не удалось загрузить список связанных документов.'},{type: 'danger', delay: 0, timer: 0});
            });


    },

    doAdd: function(data, callback) {
        var obj = {};

        data.forEach(function(e) {
            obj[e.id] = e.value;
        });

        if (!obj['d.documentNumber'] || obj['d.documentNumber'].length === 0 || !obj['d.documentNumber'].trim()) {
            $.notify({message: 'Номер документа не может быть пустым.'},{type: 'warning', delay: 1000, timer: 1000});
            return;
        }
        $.post('app/addDocumentLink',
            {
                docId: this.options.id,
                data: JSON.stringify(obj)
            },
            $.proxy(function(response){
                if (response) {
                    $.notify({
                        message: 'Документ добавлен'
                    },{
                        type: 'success',
                        delay: 1000,
                        timer: 1000
                    });
                    callback(response);
                } else {
                    $.notify({message: 'Не удалось добавить документ'},{type: 'danger', delay: 0, timer: 0});
                }
            }, this)
        ).fail(function(e) {
            $.notify({message: 'Не удалось добавить эелемент.'},{type: 'danger', delay: 0, timer: 0});
        });
    },

    doDelete: function(data) {
        var ids = data.map(function(e) {return e["l.id"]});
        $.post('app/deleteDocumentLinks',
            {ids: ids},
            $.proxy(function(response){
                if (response === 'reload') {
                    $.notify({
                        message: 'Ссылки удалены'
                    },{
                        type: 'success',
                        delay: 1000,
                        timer: 1000
                    });
                    this.grid.reload();
                } else {
                    $.notify({message: 'Не удалось удалить эелементы'},{type: 'danger', delay: 0, timer: 0});
                }
            }, this)
        );
    },

    doDeleteWithConfirm: function(grid, objects) {
        var titles = objects.map(function(e) {return e.title});

        $.sokol.smodal({
            title: 'Подтверждение удаления ссылок',
            body: titles.join(', '),
            confirmButtonTitle: 'Удалить',
            confirmAction: $.proxy(this.doDelete, this),
            data: objects
        });
    }
});