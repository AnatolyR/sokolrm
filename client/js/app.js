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
                this.open("list/documents");
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

        if (id.startsWith('list/')) {
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
        );
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

        $.getJSON('app/gridsettings', {id: id},
            $.proxy(function (data) {
                var options = data;
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
