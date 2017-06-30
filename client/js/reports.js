$.widget('sokol.reports', {
    options: {
        mode: 'normal'
    },
    _create: function () {
        var row = $('<div class="row"></div>').appendTo(this.element);
        var sidebar = $('<div class="col-sm-3 col-md-2 sidebar"></div>').appendTo(row);
        var mainC = $('<div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2"></div>').appendTo(row);
        var main = $('<div style="margin-left: 10px;"></div>').appendTo(mainC);
        this.main = main;
        this.sidebar = sidebar;
        var currentNode = null;

        var produceHandler = $.proxy(function produceHandler(item) {
            return $.proxy(function handleCategoryClick(e) {
                e.preventDefault();
                this.createReportForm(item.id);
            }, this)
        }, this);
        $.getJSON('app/config', {id: 'navigation/reports'}, $.proxy(function(data) {
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
                    if (item.disabled) {
                        category.addClass('disabled');
                        category.find('a').click(function handleCategoryClick(e) {
                            e.preventDefault();
                        });
                    } else {
                        category.find('a').click(produceHandler(item));
                    }
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
            this.createReportForm(this.options.id);
        } else {
            this.info = $('<div class="jumbotron" role="alert"><div class="container">Выберите отчет</div></div>').appendTo(this.main);
        }

    },

    createReportForm: function(id) {
        if (this.header) {
            this.header.destroy();
        }
        if (this.form) {
            this.form.destroy();
        }
        if (this.attaches) {
            this.attaches.destroy();
        }
        if (this.info) {
            this.info.remove();
        }
        this.options.id = id;
        this.sidebar.find('li').removeClass('active');
        setTimeout($.proxy(function() {
            this.sidebar.find('[name="category_' + id + '"]').addClass('active');
        }, this), 0);

        $.getJSON('app/report', {id: id}, $.proxy(function(res) {
            this.header = $.sokol.titleHeader({
                title: res.title,
                subtitle: "Отчет"
            }, $('<div></div>').appendTo(this.main));

            if (res.status == 'generating') {
                this.info = $('<div class="alert alert-info" role="alert"><div>Идет генерация отчета... (Запущено ' + res.started + ')</div><div class="progress" style="margin-bottom: 0;"><div class="progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="45" aria-valuemin="0" aria-valuemax="100" style="width: 100%"></div></div></div>').appendTo(this.main);
            } else if (res.status == 'generated') {
                this.info = $('<div class="alert alert-success" role="alert"><div>Отчет сгенерирован ' + res.generated + ' и помещен во вложения.</div></div>').appendTo(this.main);
            }

            this.form = $.sokol.form({
                mode: 'edit',
                data: this.options.data ? this.options.data : {},
                form: res.form,
                dispatcher: this.options.dispatcher,
                containerType: 'reportgeneration'
            }, $('<div></div>').appendTo(this.main));

            var button = $('<div><button type="button" class="btn btn-info">Сгенерировать отчет</button></div>').appendTo(this.form.body);
            if (res.status == 'generating') {
                button.find('button').addClass('disabled');
            }
            button.click($.proxy(function() {
                button.find('button').addClass('disabled');
                if (this.info) {
                    this.info.remove();
                }
                this.info = $('<div class="alert alert-info" role="alert"><div>Идет генерация отчета... (Запущено ' + new Date() + ')</div>' +
                    '<div class="progress" style="margin-bottom: 0;">' +
                    '<div class="progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="45" aria-valuemin="0" aria-valuemax="100" style="width: 100%">' +
                    '</div></div></div>').insertAfter(this.header.element);
                this.doGenerate();
            }, this));

            this.attaches = $.sokol.attachesGrid({mode: 'edit', id: res.attachesId, objectType: 'report'}, $('<div></div>').appendTo(this.main));
        }, this)).fail(function failLoadReportForm() {
            $.notify({message: 'Не удалось загрузить форму отчета "' + id + '". Обратитесь к администратору.'},{type: 'danger', delay: 0, timer: 0});
        });
    },

    doGenerate: function() {
        $.post('app/generate', JSON.stringify({id: this.options.id}), $.proxy(function(result) {
            if (result == 'true') {
                this.form.body.find('button').removeClass('disabled');
                if (this.info) {
                    this.info.remove();
                }
                this.info = $('<div class="alert alert-success" role="alert">' +
                    '<div>Отчет сгенерирован и помещен во вложения.</div>' +
                    '</div>').insertAfter(this.header.element);
                this.attaches.setMode('edit');
            }
        }, this)).fail($.proxy(function() {
            $.notify({message: 'Не удалось выполнить действие.'},{type: 'danger', delay: 0, timer: 0});
        }, this));
    },

    _destroy: function() {
        this.element.detach();
    }
});