$.widget('sokol.app', {
    options: {

    },
    _create: function () {
        console.debug("app.init");
        moment.locale('ru');
        $.ajaxSetup({traditional: true}); //Do not use a[] for sending arrays to server
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

        $(document).ajaxError(function(event, jqxhr, settings, exception) {
            if (jqxhr.status == 401) {
                $.notify({message: 'Не выполнен вход.'}, {type: 'warning', delay: 0, timer: 0});
            } else if (jqxhr.status == 403) {
                $.notify({message: 'Нет прав для выполнения действия.'}, {type: 'warning', delay: 0, timer: 0});
            }
        });
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
        document.title = 'Сокол СЭД'; //Reset title
        this.updateHash(id);

        if (this.container) {
            this.container.destroy();
        }

        if (this.list) {
            this.list.destroy();
        }

        if (this.error) {
            this.error.remove();
        }

        if (id.startsWith('lists/')) {
            this.createListWithNavigation(id.substring(6))

        } else if (id.startsWith('new/user')) {
            this.createForm('user', 'new/user', 'edit', 'Не удалось загрузить карточку пользователя');
        } else if (id.startsWith('user/')) {
            this.createForm('user', id.substring(5), mode, 'Не удалось загрузить карточку пользователя');

        } else if (id.startsWith('task/')) {
            this.createForm('task', id.substring(5), mode, 'Не удалось загрузить карточку задачи');

        } else if (id.startsWith('new/group')) {
            this.createForm('group', 'new/group', 'edit', 'Не удается создать карточку группы');
        } else if (id.startsWith('group/')) {
            this.createForm('group', id.substring(6), mode, 'Не удалось загрузить карточку группы');

        } else if (id.startsWith('new/registrationlist')) {
            this.createForm('registrationlist', 'new/registrationlist', 'edit', 'Не удается создать карточку журнала регистрации');
        } else if (id.startsWith('registrationlist/')) {
            this.createForm('registrationlist', id.substring(17), mode, 'Не удалось загрузить карточку журнала регистрации');

        } else if (id.startsWith('new/contragent')) {
            this.createForm('contragent', 'new/contragent', 'edit', 'Не удалось загрузить карточку контрагента');
        } else if (id.startsWith('contragent/')) {
            this.createForm('contragent', id.substring(11), mode, 'Не удалось загрузить карточку контрагента');

        } else if (id.startsWith('document/')) {
            this.createDocumentForm(id.substring(9), mode);
        } else if (id.startsWith('new/')) {
            var type = id.substring(4);
            this.createDocument(type);

        } else if (id.startsWith('report/')) {
            this.container = $.sokol.reports({id: id.substring(7), dispatcher: this}, $("<div></div>").appendTo("body"));
        } else if (id == 'reports') {
            this.container = $.sokol.reports({id: null, dispatcher: this}, $("<div></div>").appendTo("body"));

        //} else if (id.startsWith('search/')) {
        //    this.container = $.sokol.search({id: id.substring(7), dispatcher: this}, $("<div></div>").appendTo("body"));
        } else if (id == 'search') {
            this.container = $.sokol.search({id: null, dispatcher: this}, $("<div></div>").appendTo("body"));

        } else if (id == 'archive') {
            this.container = $('<div>Раздел Архив в разработке</div>').appendTo('body');
            this.container.destroy = this.container.remove;

        } else if (id.startsWith('dictionaries')) {
            this.container = $.sokol.dictionaries({id: id, dispatcher: this}, $("<div></div>").appendTo("body"));

        } else if (id.startsWith('admin')) {
            this.container = $.sokol.admin({id: id, dispatcher: this}, $("<div></div>").appendTo("body"));

        } else if (id.startsWith('profile')) {
            this.container = $.sokol.profile({id: id, dispatcher: this}, $("<div></div>").appendTo("body"));

        } else {
            this.error = $('<div class="alert alert-danger" role="alert">Не удалось загрузить объект "' + id + '". Обратитесь к администратору.</div>').appendTo(this.element);
        }

    },

    createForm: function(type, id, mode, errorMessage) {
        $.getJSON('app/' + type + 'card', {id: id},
            $.proxy(function (data) {
                var options = {
                    id: data.data.id,
                    data: data.data,
                    form: data.form,
                    containerType: type,
                    subforms: data.subforms
                };
                if (mode) {
                    options.mode = mode;
                }
                options.dispatcher = this;
                this.container = $.sokol.container(options, $('<div></div>').appendTo("body"));
            }, this)
        ).fail($.proxy(function(e) {
                this.error = $('<div class="alert alert-danger" role="alert">' + errorMessage + ' "' + id + '". Обратитесь к администратору.</div>').appendTo(this.element);
            }, this));
    },

    createDocument: function(type) {
        $.post('app/createdocument', {type: type},
            $.proxy(function (id) {
                this.open('document/' + id, "edit");
            }, this)
        ).fail($.proxy(function(e) {
                this.error = $('<div class="alert alert-danger" role="alert">Не удалось создать документ "' + type + '". Обратитесь к администратору.</div>').appendTo(this.element);
            }, this));
    },
    createDocumentForm: function(id, mode) {
        $.getJSON('app/card', {id: id},
            $.proxy(function (data) {
                var options = {
                    id: data.data.id,
                    data: data.data,
                    form: data.form,
                    containerType: 'document'
                };
                if (mode) {
                    options.mode = mode;
                }
                options.dispatcher = this;
                this.container = $.sokol.container(options, $('<div></div>').appendTo("body"));
            }, this)
        ).fail($.proxy(function(e) {
                this.error = $('<div class="alert alert-danger" role="alert">Не удалось загрузить документ "' + id + '". Обратитесь к администратору.</div>').appendTo(this.element);
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
