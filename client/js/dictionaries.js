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
                    var header = $('<ul class="nav nav-sidebar"><li style="font-weight: bold;" name="category_' + item.id + '"><a href="">' + item.title + '</a></li></ul>').appendTo(sidebar);
                    currentNode = header;
                    header.find("a").click(produceHandler(item));
                } else {
                    if (!currentNode) {
                        currentNode = $('<ul class="nav nav-sidebar"></ul>').appendTo(sidebar);
                    }
                    var category = $('<li name="category_' + item.id + '"><a href="">' + item.title + '</a></li>').appendTo(currentNode);
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
            }
        }
    },

    createGrid: function(id) {
        if (this.grid) {
            this.grid.destroy();
        }
        this.sidebar.find('li').removeClass('active');
        setTimeout($.proxy(function() {
            this.sidebar.find('[name="category_' + id + '"]').addClass('active');
        }, this), 0);
        $.getJSON('app/dictionaryinfo', {id: id},
            $.proxy(function (data) {
                var preparedData = [];
                data.data.forEach(function(item) {
                    preparedData.push({
                        id: item,
                        value: item
                    });
                });
                var options = {
                    title: data.title,
                    columnsVisible: data.gridConfig.columnsVisible,
                    columns: data.gridConfig.columns,
                    data: preparedData,
                    id: id
                };
                this.grid = $.sokol.grid(options, $("<div></div>").appendTo(this.main));
                if (this.options.dispatcher) {
                    this.options.dispatcher.updateHash('dictionaries/' + id);
                }
            }, this)
        ).fail(function failLoadList() {
            $.notify({message: 'Не удалось загрузить список "' + id + '". Обратитесь к администратору.'},{type: 'danger', delay: 0, timer: 0});
        });
    },

    _destroy: function() {
        this.element.detach();
    }
});