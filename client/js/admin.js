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
                    this.sidebar.find('[name="category_' + this.options.id + '"]').addClass('active');
                }, this), 0);
            }
        }, this));
        //if (this.options.id) {
        //    this.createGrid(this.options.id);
        //}else {
        //    this.info = $('<div class="jumbotron" role="alert"><div class="container">Выберите раздел</div></div>').appendTo(this.main);
        //}
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
        if (id == 'users') {
            this.createPagedGrid(id);
            return;
        }
    },

    _destroy: function() {
        this.element.detach();
    }
});