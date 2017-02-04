$.widget('sokol.admin', {
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
        $.getJSON('app/config', {id: 'navigation/admin'}, $.proxy(function(data) {
            data.items.forEach($.proxy(function (item) {
                if (item.type == 'header') {
                    if (item.title) {
                        var header = $('<ul class="nav nav-sidebar"><li style="font-weight: bold;" name="category_' + item.id + '"><a href="">' + item.title + '</a></li></ul>').appendTo(sidebar);
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
                    var category = $('<li name="category_' + item.id + '"><a href="">' + item.title + '</a></li>').appendTo(currentNode);
                    category.find("a").click(produceHandler(item));
                }
            }, this));
            if (this.options.id) {
                setTimeout($.proxy(function () {
                    this.sidebar.find('[name="category_' + this.options.id.substring(6) + '"]').addClass('active');
                }, this), 0);
            }
        }, this)).fail(function (jqXHR, textStatus, errorThrown) {
            $.notify({message: 'Не удалось получить данные. Обратитесь к администратору.'},{type: 'danger', delay: 0, timer: 0});
        });
        if (this.options.id) {
            if (this.options.id.startsWith("admin/")) {
                this.createGrid(this.options.id.substring(6));
            } else {
                this.info = $('<div class="jumbotron" role="alert"><div class="container">Выберите категорию</div></div>').appendTo(this.main);
            }
        }
    },

    createPagedGrid: function(id) {
        if (this.options.dispatcher) {
            this.options.dispatcher.updateHash('admin/' + id);
        }
        $.getJSON('app/config', {id: 'dictionaries/' + id}, $.proxy(function(response) {
            var options = response.gridConfig;
            options.addable = 'link';
            this.grid = $.sokol.grid(options, $("<div></div>").appendTo(this.main));
            document.title = options.title;
        }, this));
    },

    createGrid: function(id) {
        this.options.id = "admin/" + id;
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
        if (id == 'users' || id == 'groups') {
            this.createPagedGrid(id);
            return;
        }
        if (this.options.dispatcher) {
            this.options.dispatcher.updateHash('admin/' + id);
        }
        $.getJSON('app/spaces', {id: id},
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
                    selectable: true,
                    deletable: true,
                    deleteMethod: $.proxy(this.doDeleteWithConfirm, this),
                    addable: true,
                    addMethod: $.proxy(this.doAdd, this)
                };
                this.grid = $.sokol.grid(options, $("<div></div>").appendTo(this.main));
                document.title = data.title;
            }, this)
        ).fail(function failLoadList() {
                $.notify({message: 'Не удалось загрузить список "' + id + '". Обратитесь к администратору.'},{type: 'danger', delay: 0, timer: 0});
            });
    },

    doDelete: function(data) {
        var ids = data.map(function(e) {return e.id});
        $.post('app/deleteSpaces',
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
                    if (this.options.id.startsWith("admin/")) {
                        this.createGrid(this.options.id.substring(6));
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
            $.notify({message: 'Название пространства не может быть пустым.'},{type: 'warning', delay: 1000, timer: 1000});
            return;
        }
        var id = this.options.id.substring(6);
        $.post('app/addSpace',
            {
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
                    $.notify({message: 'Пространство с таким названием уже существует.'},{type: 'warning', delay: 1000, timer: 1000});
                } else {
                    $.notify({message: 'Не удалось добавить эелемент. Обратитесь к администратору.'},{type: 'danger', delay: 0, timer: 0});
                }
            });
    },

    _destroy: function() {
        this.element.detach();
    }
});