$.widget('sokol.dictionaries', {
    options: {

    },
    _create: function () {
        var row = $('<div class="row"></div>').appendTo(this.element);
        var sidebar = $('<div class="col-sm-3 col-md-2 sidebar"></div>').appendTo(row);
        var main = $('<div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2"></div>').appendTo(row);
        this.main = main;
        this.sidebar = sidebar;
        var currentNode = null;

        var produceHandler = $.proxy(function produceHandler(item) {
            return $.proxy(function handleCategoryClick(e) {
                e.preventDefault();
                this.createGrid(item.id);
            }, this)
        }, this);
        $.getJSON('app/config', {id: 'navigation/dictionaries'}, $.proxy(function(data) {
            data.items.forEach($.proxy(function (item) {
                if (item.type == 'header') {
                    if (item.title) {
                        var header = $('<ul class="nav nav-sidebar"><li style="font-weight: bold;" name="category_' + item.id + '"><a class="sokolDictionaryListItem" href="">' + item.title + '</a></li></ul>').appendTo(sidebar);
                        currentNode = header;
                        header.find("a").click(produceHandler(item));
                    } else {
                        var block = $('<ul class="nav nav-sidebar"></ul>').appendTo(sidebar);
                        currentNode = block;
                    }
                } else {
                    if (!currentNode) {
                        currentNode = $('<ul class="nav nav-sidebar"></ul>').appendTo(sidebar);
                    }
                    var category = $('<li name="category_' + item.id + '"><a class="sokolDictionaryListItem" href="">' + item.title + '</a></li>').appendTo(currentNode);
                    category.find("a").click(produceHandler(item));
                }
            }, this));
            if (this.options.id && this.options.id.startsWith("dictionaries/")) {
                setTimeout($.proxy(function () {
                    this.sidebar.find('[name="category_' + this.options.id.substring(13) + '"]').addClass('active');
                }, this), 0);
            }
        }, this));
        if (this.options.id) {
            if (this.options.id.startsWith("dictionaries/")) {
                this.createGrid(this.options.id.substring(13));
            } else {
                this.info = $('<div class="jumbotron" role="alert"><div class="container">Выберите справочник</div></div>').appendTo(this.main);
            }
        }
    },

    doDelete: function(data) {
        var ids = data.map(function(e) {return e.id});
        $.post('app/deleteDictionaryValues',
            {ids: ids},
            $.proxy(function(response){
                if (response === 'true') {
                    $.notify({
                        message: 'Элементы удалены'
                    },{
                        type: 'success',
                        delay: 1000,
                        timer: 1000
                    });
                    if (this.options.id.startsWith("dictionaries/")) {
                        this.createGrid(this.options.id.substring(13));
                    }
                } else {
                    $.notify({message: 'Не удалось удалить эелементы'},{type: 'danger', delay: 0, timer: 0});
                }
            }, this)
        );
    },

    doDeleteWithConfirm: function(grid, objects) {
        var titles = objects.map(function(e) {return e.title});

        $.sokol.smodal({
            title: 'Подтверждение удаления',
            body: titles.join(', '),
            confirmButtonTitle: 'Удалить',
            confirmAction: $.proxy(this.doDelete, this),
            data: objects
        });
    },

    doAdd: function(data, callback) {
        var obj = {};

        data.forEach(function(e) {
            obj[e.id] = e.value;
        });

        if (!obj['title'] || obj['title'].length === 0 || !obj['title'].trim()) {
            $.notify({message: 'Значение словаря не может быть пустым.'},{type: 'warning', delay: 1000, timer: 1000});
            return;
        }
        var id = this.options.id.substring(13);
        $.post('app/addDictionaryValue',
            {
                dictionaryId: id,
                data: JSON.stringify(obj)
            },
            $.proxy(function(response){
                if (response) {
                    $.notify({
                        message: 'Элемент добавлен'
                    },{
                        type: 'success',
                        delay: 1000,
                        timer: 1000
                    });
                    callback(response);
                } else {
                    $.notify({message: 'Не удалось добавить эелемент'},{type: 'danger', delay: 0, timer: 0});
                }
            }, this)
        ).fail(function(e) {
            if (e.responseJSON && e.responseJSON.error) {
                $.notify({message: 'Значение словаря уже существует.'},{type: 'warning', delay: 1000, timer: 1000});
            } else {
                $.notify({message: 'Не удалось добавить эелемент. Обратитесь к администратору.'},{type: 'danger', delay: 0, timer: 0});
            }
        });
    },

    createGrid: function(id) {
        this.options.id = "dictionaries/" + id;
        if (this.grid) {
            this.grid.destroy();
        }
        if (this.info) {
            this.info.remove();
        }
        this.sidebar.find('li').removeClass('active');
        setTimeout($.proxy(function() {
            this.sidebar.find('[name="category_' + id + '"]').addClass('active');
        }, this), 0);
        if (id == 'organizationPersons' || id == 'contragents') {
            this.createPagedGrid(id);
            return;
        }
        if (this.options.dispatcher) {
            this.options.dispatcher.updateHash('dictionaries/' + id);
        }
        $.getJSON('app/dictionaryinfo', {id: id},
            $.proxy(function (data) {
                var preparedData = [];
                data.data.forEach(function(item) {
                    preparedData.push({
                        id: item.id,
                        title: item.title
                    });
                });
                var options = {
                    title: data.title,
                    columnsVisible: data.gridConfig.columnsVisible,
                    columns: data.gridConfig.columns,
                    data: preparedData,
                    id: id,
                    selectable: data.selectable,
                    deletable: data.deletable,
                    deleteMethod: $.proxy(this.doDeleteWithConfirm, this),
                    addable: data.addable,
                    addMethod: $.proxy(this.doAdd, this)
                };
                this.grid = $.sokol.grid(options, $("<div></div>").appendTo(this.main));
                document.title = data.title;
            }, this)
        ).fail(function failLoadList() {
            $.notify({message: 'Не удалось загрузить список "' + id + '". Обратитесь к администратору.'},{type: 'danger', delay: 0, timer: 0});
        });
    },

    createPagedGrid: function(id) {
        if (this.options.dispatcher) {
            this.options.dispatcher.updateHash('dictionaries/' + id);
        }
        $.getJSON('app/config', {id: 'dictionaries/' + id}, $.proxy(function(response) {
            var options = response.gridConfig;
            options.addable = response.addable;
            this.grid = $.sokol.grid(options, $("<div></div>").appendTo(this.main));
            document.title = options.title;
        }, this));
    },

    _destroy: function() {
        this.element.detach();
    }
});