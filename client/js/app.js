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
                this.open("lists/documents");
            }

        }, this));
        $(window).trigger('hashchange');
    },
    _destroy: function() {
        this.header.destroy();
        //this.grid.destroy();
        this.list.destroy();
        this.container.destroy();
    },

    showHeader: function() {
        $.getJSON('app/config', {id: 'appSettings'},
            $.proxy(function (data) {
                var options = data;
                options.dispatcher = this;
                this.headerObject = $.sokol.header(options, $("<nav></nav>").prependTo("body"));
            }, this)
        );
    },

    open:function(id, mode) {
        this.updateHash(id);

        if (this.container) {
            this.container.destroy();
        }

        if (this.list) {
            this.list.destroy();
        }

        if (id.startsWith('lists/')) {
            this.createListWithNavigation(id.substring(6))
        } else if (id.startsWith('document/')) {
            this.createDocumentForm(id.substring(9), mode);
        } else if (id.startsWith('new/')) {
            var type = id.substring(4);
            this.createDocument(type);
        } else if (id == 'reports') {
            this.container = $('<div>Раздел Отчеты в разработке</div>').appendTo('body');
            this.container.destroy = this.container.remove;
        } else if (id == 'search') {
            this.container = $('<div>Раздел Поиск в разработке</div>').appendTo('body');
            this.container.destroy = this.container.remove;
        } else if (id == 'archive') {
            this.container = $('<div>Раздел Архив в разработке</div>').appendTo('body');
            this.container.destroy = this.container.remove;
        } else if (id.startsWith('dictionaries')) {
            this.container = $.sokol.dictionaries({id: id, dispatcher: this}, $("<div></div>").appendTo("body"));
        } else if (id.startsWith('admin')) {
            this.container = $.sokol.admin({id: id, dispatcher: this}, $("<div></div>").appendTo("body"));
        }

    },
    createDocument: function(type) {
        $.post('app/createdocument', {type: type},
            $.proxy(function (id) {
                this.open('document/' + id, "edit");
            }, this)
        ).fail($.proxy(function(e) {
                $('<div class="alert alert-danger" role="alert">Не удалось создать документ "' + type + '". Обратитесь к администратору.</div>').appendTo(this.element);
            }, this));
    },
    createDocumentForm: function(id, mode) {
        $.getJSON('app/card', {id: id},
            $.proxy(function (data) {
                var options = {
                    id: data.id,
                    data: data.data,
                    form: data.form
                };
                if (mode) {
                    options.mode = mode;
                }
                options.dispatcher = this;
                this.container = $.sokol.container(options, $('<div></div>').appendTo("body"));
            }, this)
        ).fail($.proxy(function(e) {
                $('<div class="alert alert-danger" role="alert">Не удалось загрузить документ "' + id + '". Обратитесь к администратору.</div>').appendTo(this.element);
            }, this));
    },

    createListWithNavigation: function(id) {
        this.list = $.sokol.list({id: id, dispatcher: this}, $("<div></div>").appendTo("body"));
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
        if (location.hash == "#" + hash || hash.indexOf('new/') >= 0) {
            return;
        }
        if(history.pushState) {
            history.pushState(null, null, "#" + hash);
        } else {
            location.hash = "#" + hash;
        }
    }
});
