$.widget('sokol.accessRightsGrid', {
    options: {

    },

    _create: function () {
        this.createBlock()
    },

    _destroy: function() {
        this.element.detach();
    },

    createBlock: function() {
        $.getJSON('app/getAccessRightsRecordsForGroup', {groupId: this.options.groupId},
            $.proxy(function (data) {
                var options = {
                    title: 'Права доступа для группы',
                    "columnsVisible": [
                         "spaceTitle",
                         "elementTitle",
                         "subelementTitle",
                         "level"
                    ],
                    columns: [
                        {
                            "id": "space",
                            "title": "Пространство (ИД)"
                        },
                        {
                            "id": "spaceTitle",
                            "title": "Пространство"
                        },
                        {
                            "id": "element",
                            "title": "Элемент (ИД)"
                        },
                        {
                            "id": "elementTitle",
                            "title": "Элемент"
                        },
                        {
                            "id": "subelement",
                            "title": "Подэлемент (ИД)"
                        },
                        {
                            "id": "subelementTitle",
                            "title": "Подэлемент"
                        },
                        {
                            "id": "level",
                            "title": "Разрешение"
                        }
                    ],
                    data: data,
                    id: 'accessRights',
                    selectable: true,
                    deletable: true,
                    deleteMethod: $.proxy(this.doDeleteWithConfirm, this)
                };
                if (this.grid) {
                    this.grid.destroy();
                }
                this.grid = $.sokol.grid(options, $("<div></div>").appendTo(this.element));
                $.getJSON('app/getAccessRightsElements', {}, $.proxy(function (data) {
                    this.renderAddBlock(data, this.grid.topBar);
                }, this));

            }, this)
        ).fail(function failLoadList() {
                $.notify({message: 'Не удалось загрузить список "' + id + '". Обратитесь к администратору.'},{type: 'danger', delay: 0, timer: 0});
            });
    },

    doDelete: function(data) {
        var ids = data.map(function(e) {return e.id});
        $.post('app/deleteAccessRightRecord',
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
                    this.createBlock();
                } else {
                    $.notify({message: 'Не удалось удалить эелементы'},{type: 'danger', delay: 0, timer: 0});
                }
            }, this)
        );
    },

    doDeleteWithConfirm: function(grid, objects) {
        var titles = objects.map(function(e) {return e.spaceTitle + '.' + e.elementTitle + '.' + (e.subelementTitle ? e.subelementTitle : '*') + '=' + e.level});

        $.sokol.smodal({
            title: 'Подтверждение удаления',
            body: titles.join(', '),
            confirmButtonTitle: 'Удалить',
            confirmAction: $.proxy(this.doDelete, this),
            data: objects
        });
    },

    renderAddBlock: function(settings, element) {
        var condition = $('<div class="form-inline" style="display: inline; margin-bottom: 5px; border-collapse:separate; border-spacing:5px;"></div>');
        if (element) {
            condition.appendTo(element);
        } else {
            condition.appendTo(this.body);
        }

        var spaces = settings.spaces;
        var spaceSelector = $('<select name="spaceSelector" class="selectpicker controlElementLeftMargin"></select>').appendTo(condition);
        spaceSelector.append($('<option value=""> </option>'));
        for (var i = 0; i < spaces.length; i++) {
            var space = spaces[i];
            spaceSelector.append($('<option value="' + space.id + '">' + space.title + '</option>'));
        }
        spaceSelector.selectpicker({
            width: 'auto'
        });

        spaceSelector.on('change', function(event){
            var space = $(this).find("option:selected").val();

            elementSelector.empty();
            if (space == '_system') {
                elementSelector.attr('data-el-type', 'system');
                var system = settings.systemObjects;
                for (var p = 0; p < system.length; p++) {
                    elementSelector.append($('<option value="' + system[p].id + '">' + system[p].title + '</option>'));
                }
            } else if (space == '_dictionaries') {
                elementSelector.attr('data-el-type', 'dictionaries');
                var dictionaries = settings.dictionaries;
                for (var p = 0; p < dictionaries.length; p++) {
                    elementSelector.append($('<option value="' + dictionaries[p].id + '">' + dictionaries[p].title + '</option>'));
                }
            } else if (space) {
                elementSelector.attr('data-el-type', 'documents');
                var documentTypes = settings.documentTypes;
                for (var p = 0; p < documentTypes.length; p++) {
                    elementSelector.append($('<option value="' + documentTypes[p].id + '">' + documentTypes[p].title + '</option>'));
                }
            } else {
                elementSelector.attr('data-el-type', '');
            }
            elementSelector.selectpicker('refresh');
            elementSelector.trigger('change');
        });

        var elementSelector = $('<select name="elementSelector" class="selectpicker controlElementLeftMargin"></select>').appendTo(condition);
        elementSelector.selectpicker({
            noneSelectedText: ''
        });
        elementSelector.on('change', function(event){
            var element = $(this).find("option:selected").val();
            console.log("element = ", element);
            subelementSelector.empty();
            subelementSelector.append($('<option value="" checked=true></option>'));
            if (elementSelector.attr('data-el-type') == 'documents') {
                var documentType = settings.documentTypes.find(function(el) {
                    return el.id == element;
                });

                var fieldTypes = documentType.fieldsTypes;
                for (var p = 0; p < fieldTypes.length; p++) {
                    subelementSelector.append($('<option value="' + fieldTypes[p].id + '">' + fieldTypes[p].title + '</option>'));
                }

                var actions = documentType.actions;
                for (var p = 0; p < actions.length; p++) {
                    subelementSelector.append($('<option value="*' + actions[p].id + '">{' + actions[p].title + '}</option>'));
                }
            }
            subelementSelector.selectpicker('refresh');
            refreshAR();
        });

        var refreshAR = function() {
            arSelector.empty();
            arSelector.append($('<option value="DENY" checked=true>DENY</option>'));
            var space = spaceSelector.val();
            var element = elementSelector.val();
            var subelement = subelementSelector.val();
            if ("_system" == space) {
                if ("users" == element || "groups" == element || "registrationLists" == element) {
                    addAr(["CREATE", "READ", "WRITE", "DELETE", "LIST"]);
                } else if ("documentGroups" == element) {
                    addAr(["ADD", "DELETE", "LIST"]);
                }
            } else if ("_dictionaries" == space) {
                if ("organizationPersons" == element) {
                    addAr(["READ", "LIST"]);
                } else if ("contragents" == element) {
                    addAr(["CREATE", "READ", "WRITE", "DELETE", "LIST"]);
                } else {
                    addAr(["ADD", "DELETE", "LIST"]);
                }
            } else {
                if ("" == subelement) {
                    addAr(["CREATE", "READ", "WRITE", "DELETE", "LIST"]);
                } else if (subelement.indexOf("*") == 0) {
                    addAr(["ALLOW"]);
                } else {
                    addAr(["READ", "WRITE"]);
                }
            }
            arSelector.selectpicker('refresh');
        };
        var addAr = function(ac) {
            for (var i = 0; i < ac.length; i++) {
                var ar = ac[i];
                arSelector.append($('<option value="' + ar + '">' + ar + '</option>'));
            }
        };

        var subelementSelector = $('<select name="subelementSelector" class="selectpicker controlElementLeftMargin"></select>').appendTo(condition);
        subelementSelector.selectpicker({
            noneSelectedText: ''
        });
        subelementSelector.on('change', function(event){
            refreshAR();
        });

        var ac = settings.ac;
        var arSelector = $('<select name="arSelector" class="selectpicker controlElementLeftMargin"></select>').appendTo(condition);

        arSelector.selectpicker({
            noneSelectedText: '',
            width: '100px'
        });

        var addButton = $('<div class="btn-group " style=""><button type="button" class="form-control btn btn-success controlElementLeftMargin" >' +
            'Добавить' +
            '</button></div>').appendTo(condition);
        addButton.click($.proxy(function() {
            var space = spaceSelector.val();
            var element = elementSelector.val();
            var subelement = subelementSelector.val();
            var level = arSelector.val();
            var record = {
                space: space,
                element: element,
                subelement: subelement,
                level: level
            };
            this.saveRecord(record, $.proxy(function(reloadValue) {
                this.grid.options.data.push(reloadValue);
                var tbody = this.grid.element.find('tbody');
                var row = $('<tr></tr>').prependTo(tbody);
                this.grid.renderRow(row, reloadValue);
            }, this));
        }, this));
    },

    saveRecord: function(record, callback) {
        $.post('app/addAccessRightRecord',
            {
                groupId: this.options.groupId,
                data: JSON.stringify(record)
            },
            $.proxy(function(response){
                if (response) {
                    $.notify({
                        message: 'Запись добавлена'
                    },{
                        type: 'success',
                        delay: 1000,
                        timer: 1000
                    });
                    callback(response);
                } else {
                    $.notify({message: 'Не удалось добавить запись.'},{type: 'danger', delay: 0, timer: 0});
                }
            }, this)
        ).fail(function(e) {
                if (e.responseJSON && e.responseJSON.error) {
                    $.notify({message: 'Ошибка добавления записи.'},{type: 'warning', delay: 1000, timer: 1000});
                } else {
                    $.notify({message: 'Не удалось добавить запись. Обратитесь к администратору.'},{type: 'danger', delay: 0, timer: 0});
                }
            });
    }

});
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
        if (id == 'users' || id == 'groups' || id == 'registrationLists') {
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

        } else if (id.startsWith('reports/')) {
            this.container = $.sokol.reports({id: id.substring(8), dispatcher: this}, $("<div></div>").appendTo("body"));
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
                $('<div class="alert alert-danger" role="alert">Не удалось создать документ "' + type + '". Обратитесь к администратору.</div>').appendTo(this.element);
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
        $.getJSON("app/attaches",{id: id}, $.proxy(function (listData) {
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
                id: "created",
                title: "Дата добавления"
            },
            {
                id: "authorTitle",
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
        var rowsData = listData;
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
        this.element.addClass('sokolAttachesPanel');
        this.element.attr('name', 'attachmentsPanel');

        var panelHeader = $('<div class="panel-heading"><div class="panel-title">Вложения</div></div>');
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
                url: 'upload?objectId=' + this.options.id, // point to server-side PHP script
                dataType: 'text',  // what to expect back from the PHP script, if anything
                cache: false,
                contentType: false,
                processData: false,
                data: form_data,
                type: 'post',
                success: $.proxy(function(response){
                    $.notify({message: 'Вложение сохранено'}, {type: 'success', delay: 1000, timer: 1000});
                    $("#attachField").filestyle('clear');
                    this.getAttachesList();
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
$.widget('sokol.container', {
    options: {
        mode: "read"
    },
    _create: function () {
        this.childs = [];
        this.createForm();
    },
    _destroy: function() {
        if (this.header) {
            this.header.destroy();
        }
        if (this.form) {
            this.form.destroy();
        }
        if (this.formButtons) {
            this.formButtons.destroy();
        }
        if (this.attaches) {
            this.attaches.destroy();
        }

        if (this.history) {
            this.history.destroy();
        }
        if (this.linkeddocs) {
            this.linkeddocs.destroy();
        }

        for (var i = 0; i < this.childs.length; i++) {
            var child = this.childs[i];
            if (child.destroy) {
                child.destroy();
            }
        }
        this.element.detach();
    },
    createForm: function() {
        var data = this.options.data;
        var form = this.options.form;
        var fields = form.fields;

        var container = this.element;
        container.empty();
        container.addClass('container');

        if (this.options.containerType == 'user') {
            this.header = $.sokol.userHeader({data: data, form: form}, $('<div></div>').appendTo(this.element));
        } else if (this.options.containerType == 'contragent') {
            this.header = $.sokol.titleHeader({
                title: data.title,
                subtitle: data.fullName
            }, $('<div></div>').appendTo(this.element));
        } else if (this.options.containerType == 'document' || this.options.containerType == 'task') {
            this.header = $.sokol.containerHeader({data: data, form: form}, $('<div></div>').appendTo(this.element));
        } else {
            this.header = $.sokol.titleHeader({
                title: data.title,
                subtitle: this.options.form.subtitle
            }, $('<div></div>').appendTo(this.element));
        }

        if (this.options.containerType != 'task') {
            this.formButtons = $.sokol.formButtons({
                mode: this.options.mode,
                dispatcher: this,
                containerType: this.options.containerType,
                actions: this.options.form.actions,
                deleteAction: this.options.form.deleteAction,
                id: this.options.id
            }, $('<div></div>').prependTo(this.element));
        }

        this.form = $.sokol.form({
            mode: this.options.mode,
            data: data,
            form: form,
            class: 'sokolMainAttributesPanel',
            dispatcher: this.options.dispatcher,
            containerType: this.options.containerType
        }, $('<div></div>').appendTo(this.element));

        if (this.options.containerType == 'document') {
            this.createExecutionListIfExist('approval');
            this.createExecutionListIfExist('execution');

            if (this.options.mode == 'read') {
                this.history = $.sokol.history({id: data.id}, $('<div></div>').appendTo(this.element));

                this.linkeddocs = $.sokol.linkeddocs({id: data.id}, $('<div></div>').appendTo(this.element));
            }
        }

        if (this.options.subforms) {
            for (var i = 0; i < this.options.subforms.length; i++) {
                var subform = this.options.subforms[i];
                this.createSubform(subform);
            }
        }

        if (this.options.id) {
            this.attaches = $.sokol.attachesGrid({mode: this.options.mode, id: data.id}, $('<div></div>').appendTo(this.element));
        }
    },

    execution: function(type, taskId) {
        if (!this[type + "Form"]) {
            this[type + "Form"] = $.sokol.executionForm({
                dispatcher: this,
                documentId: this.options.id,
                mode: 'create',
                type: type,
                taskId: taskId
            }, $("<div></div>").insertAfter(this.header.element));
        } else {
            $('html, body').animate({
                scrollTop: $(this[type + "Form"].element).offset().top - 50
            }, 2000);
        }
    },

    refreshExecutionList: function(type) {
        if (this[type + "Form"]) {
            this[type + "Form"].destroy();
        }
        this.createExecutionListIfExist(type);
    },

    createExecutionListIfExist: function(type, taskId) {
        $.getJSON('app/getExecutionList', {
            documentId: this.options.id,
            type: type,
            taskId: taskId
        }, $.proxy(function(data) {
            if (!$.isEmptyObject(data)) {
                this[type + "Form"] = $.sokol.executionForm({
                    dispatcher: this,
                    data: data,
                    documentId: this.options.id,
                    mode: 'read',
                    type: type,
                    taskId: taskId
                }, $("<div></div>").insertAfter(this.executionReportForm ? this.executionReportForm.element : this.form.element));
            }
        }, this));
    },

    createSubform: function(subform) {
        if (subform.form.id == 'accessRightsGrid') {
            var arGrid = $.sokol.accessRightsGrid({
                groupId: this.options.id
            }, $('<div></div>').appendTo(this.element));
            this.childs.push(arGrid);
            return;
        }
        if (subform.form.id == 'task') {
            var executionReportForm = $.sokol.executionReportForm({
                data: subform.data,
                dispatcher: this
            }, $('<div></div>').insertAfter(this.header.element));
            this.childs.push(executionReportForm);
            var taskId = subform.data.id;
            this.options.taskId = taskId;
            this.executionReportForm = executionReportForm;
            this.createExecutionListIfExist(subform.data.type, taskId);
            return;
        }
        var form = $.sokol.form({
            mode: "read",
            data: subform.data ? subform.data : this.options.data,
            form: subform.form,
            parent: this,
            dispatcher: this.options.dispatcher,
            containerType: this.options.containerType
        }, $('<div></div>').appendTo(this.element));
        this.childs.push(form);
    },

    notify: function(message) {
        $.notify({
            message: message
        },{
            type: 'success',
            delay: 1000,
            timer: 1000
        });
    },

    goToMode: function(mode) {
        this.options.mode = mode;

        this.formButtons.setMode(mode);

        this.form.setMode(mode);

        this.attaches.setMode(mode);

        for (var i = 0; i < this.childs.length; i++) {
            var child = this.childs[i];
            if (child.setMode) {
                child.setMode(mode);
            }
        }
    },

    updateCardFromTemplate: function(templateDocumentId) {
        $.getJSON('app/card', {id: templateDocumentId},
            $.proxy(function (data) {
                this.options.data = data.data;
                this.form.options.data = data.data;
                this.form.setMode('edit');
            }, this)
        ).fail($.proxy(function(e) {
                this.error = $('<div class="alert alert-danger" role="alert">Не удалось загрузить шаблон "' + id + '". Обратитесь к администратору.</div>').appendTo(this.element);
            }, this));
    },

    saveAsTemplate: function() {
        this.options.template = true;
        this.saveForm();
    },

    saveForm: function() {
        if (!this.form.validateForm()) {
            return;
        }

        var data = this.form.getData();

        for (var i = 0; i < this.childs.length; i++) {
            var child = this.childs[i];
            if (child.getData) {
                data[child.formId] = child.getData();
            }
        }

        if (this.options.template) {
            data.template = true;
        }

        var saveUrl;
        var message;
        var openType;
        if (this.options.containerType == 'user') {
            saveUrl = 'app/saveuser';
            openType = 'user';
            message = 'Не удалось сохранить карточку пользователя. Обратитесь к администратору.';
        } else if (this.options.containerType == 'contragent') {
            saveUrl = 'app/savecontragent';
            openType = 'contragent';
            message = 'Не удалось сохранить карточку контрагента. Обратитесь к администратору.';
        } else if (this.options.containerType == 'registrationlist') {
            saveUrl = 'app/saveRegistrationList';
            openType = 'registrationlist';
            message = 'Не удалось сохранить журнала регистрации. Обратитесь к администратору.';
        } else if (this.options.containerType == 'group') {
            saveUrl = 'app/saveGroup';
            openType = 'group';
            message = 'Не удалось сохранить карточку группы. Обратитесь к администратору.';
        } else {
            saveUrl = 'app/savedocument';
            openType = 'document';
            message = 'Не удалось сохранить документ. Обратитесь к администратору.';
        }

        $.post(saveUrl, JSON.stringify(data), $.proxy(function (id) {
            $.notify({message: 'Сохранено'}, {type: 'success', delay: 1000, timer: 1000});
            this.options.dispatcher.open(openType + '/' + id);
        }, this)).fail($.proxy(function() {
            $.notify({message: message},{type: 'danger', delay: 0, timer: 0});
        }, this));
    },

    reopen: function(id) {
        this.options.dispatcher.open(this.options.containerType + '/' + (id ? id : (this.options.taskId ? this.options.taskId : this.options.id)));
    },

    deleteDocument: function() {
        $.sokol.smodal({
            title: 'Подтверждение удаления',
            body: 'Удалить "' + this.options.data.title + '" ?',
            confirmButtonTitle: 'Удалить',
            confirmAction: $.proxy(this.doDeleteDocument, this)
        });
    },

    cancelExecution: function() {
        if (this.executionForm) {
            this.executionForm.destroy();
            this.executionForm = null;
        }
    },

    doDeleteDocument: function() {
        var deleteUrl;
        var errorMessage;
        var message;
        if (this.options.containerType == 'user') {
            deleteUrl = 'app/deleteuser';
            errorMessage = 'Не удалось удалить карточку пользователя. Обратитесь к администратору.';
            message = 'Карточка пользователя удалена';
        } else if (this.options.containerType == 'contragent') {
            deleteUrl = 'app/deletecontragent';
            errorMessage = 'Не удалось удалить карточку контрагента. Обратитесь к администратору.';
            message = 'Карточка контрагента удалена';
        } else if (this.options.containerType == 'registrationlist') {
            deleteUrl = 'app/deleteRegistrationList';
            errorMessage = 'Не удалось удалить карточку журнала регистрации. Обратитесь к администратору.';
            message = 'Карточка журнала регистрации удалена';
        } else if (this.options.containerType == 'group') {
            deleteUrl = 'app/deleteGroup';
            errorMessage = 'Не удалось удалить карточку группы. Обратитесь к администратору.';
            message = 'Карточка группы удалена';
        } else {
            deleteUrl = 'app/deletedocument';
            errorMessage = 'Не удалось удалить документ. Обратитесь к администратору.';
            message = 'Документ удален';
        }
        $.ajax({
            url: deleteUrl + '?id=' + this.options.id,
            type: 'DELETE',
            success: $.proxy(function(result) {
                if (result == "true") {
                    this.element.empty();
                    $('<div class="alert alert-success" role="alert">' + message + '</div>').appendTo(this.element)
                } else {
                    $.notify({message: errorMessage},{type: 'danger', delay: 0, timer: 0});
                }
            }, this),
            error: function() {
                $.notify({message: errorMessage},{type: 'danger', delay: 0, timer: 0});
            }
        });
    }
});

$.widget('sokol.containerHeader', {
    options: {
        form: null,
        data: null
    },
    _create: function () {
        var form = this.options.form;
        var data = this.options.data;
        var date = data.registrationDate ? moment(data.registrationDate, 'DD.MM.YYYY HH:mm').format("L") : '-';
        this.element.addClass('panel-body');
        this.element.addClass('sokolHeaderPanel');
        $('<h3>' + (form.typeTitle ? form.typeTitle : '-') +
            ' № ' + (data.documentNumber ? data.documentNumber : '-') +
            ' от ' + date +
            (data.documentKind ? (' (' + data.documentKind + ')') : '') +
            '</h3>' +
            (data.title ? ('<h4 class="">' + data.title + '</h4>') : '')+
            '<div>Статус: ' + (data.status ? data.status : '') + '</div>'
            ).appendTo(this.element);
    },
    _destroy: function() {
        this.element.detach();
    }
});
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

    createPagedGrid: function(id) {
        if (this.options.dispatcher) {
            this.options.dispatcher.updateHash('dictionaries/' + id);
        }
        $.getJSON('app/config', {id: 'dictionaries/' + id}, $.proxy(function(response) {
            var options = response.gridConfig;
            options.addable = 'link';
            this.grid = $.sokol.grid(options, $("<div></div>").appendTo(this.main));
            document.title = options.title;
        }, this));
    },

    _destroy: function() {
        this.element.detach();
    }
});
$.widget('sokol.executionForm', {
    options: {
        mode: 'read',
        type: null
    },

    _create: function () {
        if (!this.options.type) {
            new Error('Execution list type is not defined');
        }
        this.createHeader();
        this.createBlock();
    },

    createHeader: function() {
        this.element.addClass('panel panel-default');
        this.element.attr('name', 'attachmentsPanel');

        var panelHeader = $('<div class="panel-heading"></div>').appendTo(this.element);

        var title;
        var type = this.options.type;
        var taskId = this.options.taskId;
        if (type == 'execution') {
            if (taskId) {
                title = 'Внутренняя резолюция';
            } else {
                title = 'Резолюция';
            }
            this.element.addClass('sokolExecutionPanel');
        } else if (type == 'approval') {
            if (taskId) {
                title = 'Внутреннее согласование';
            } else {
                title = 'Согласование';
            }
            this.element.addClass('sokolApprovalPanel');
        } else if (type == 'acquaintance') {
            if (taskId) {
                title = 'Внутреннее ознакомление';
            } else {
                title = 'Ознакомление';
            }
            this.element.addClass('sokolAcquaintancePanel');
        }
        var panelTitle = $('<div class="panel-title">' + title + '</div>').appendTo(panelHeader);

        var panelBody = $('<div class="panel-body"></div>');
        panelBody.appendTo(this.element);
        this.panelBody = panelBody;
    },

    createBlock: function () {
        if (this.form) {
            this.form.destroy();
        }
        this.form = $.sokol.form({
            mode: (this.options.mode == 'create' || this.options.mode == 'edit') ? 'edit' : 'read',
            data: this.options.data ? this.options.data : {},
            form: {
                "id": "userSystem",
                "fields": [
                    {
                        "items": [
                            {
                                "id": "userId",
                                "title": "Автор",
                                "type": "users",
                                "multiple": false,
                                ar: 'read',
                                hideIfEmpty: true
                            },
                            {
                                "id": "created",
                                "title": "Дата",
                                "type": "date",
                                ar: 'read',
                                hideIfEmpty: true
                            }
                        ]
                    },
                    {
                        "id": "comment",
                        "title": this.options.type == 'resolution' ? 'Резолюция' : 'Комментарий',
                        "type": "text",
                        "mandatory": false
                    }
                ]
            },
            usePanel: false,
            dispatcher: this.options.dispatcher,
            containerType: 'execution'
        }, $('<div></div>').appendTo(this.panelBody));

        var executorsListTitle;
        var executorTitle;
        var type = this.options.type;
        if (type == 'execution') {
            executorsListTitle = 'Исполнители';
            executorTitle = 'Исполнитель';
        } else if (type = 'approval') {
            executorsListTitle = 'Согласующие';
            executorTitle = 'Согласующий';
        } else if (type == 'acquaintance') {
            executorsListTitle = 'Ознакамливающиеся';
            executorTitle = 'Ознакамливающийся';
        }
        var options = {
                'title': executorsListTitle,
                'sortable': true,
                mode: (this.options.mode == 'create' || this.options.mode == 'edit') ? 'edit' : 'read',
                'columnsVisible': [
                    'userTitle',
                    'dueDate',
                    'executedDate',
                    'status',
                    'result'
                ],
                'columns': [
                    {
                        'id': 'userId',
                        'title': executorTitle + ' (ИД)',
                        'render': 'link',
                        'linkType': 'user'
                    },
                    {
                        'id': 'userTitle',
                        'idColumn': 'userId',
                        'title': executorTitle,
                        'render': 'link',
                        'linkType': 'user',
                        'editor': 'user',
                        'width': '500px'
                    },
                    {
                        'id': 'dueDate',
                        'title': 'Срок',
                        'render': 'datetime',
                        'editor': 'date'
                    },
                    {
                        'id': 'executedDate',
                        'title': 'Завершено',
                        'render': 'datetime'
                    },
                    {
                        'id': 'result',
                        'title': 'Статус',
                        'render': 'expand',
                        'dataColumn': 'comment'
                    }
                ],
                'id': 'tasks',
                'filterable': false,
                'deleteMethod': $.proxy(this.doDelete, this)
        };

        if (type == 'execution') {
            options.columns.splice(3, 0, {
                'id': 'mainExecutor',
                'title': 'Отв.',
                'render': 'boolean',
                'editor': 'radio'
            });
            options.columnsVisible.splice(2, 0, 'mainExecutor');
        }

        options.data = (this.options.data && this.options.data.tasks) ? this.options.data.tasks : [];
        options.usePanel = false;

        if (this.grid) {
            this.grid.destroy();
        }

        this.grid = $.sokol.grid(options, $('<div class="panel panel-default" style="border-width: 0 0 1px 0; border-radius: 0;"></div>').insertAfter(this.panelBody));

        this.createButtons();
    },

    doDelete: function(grid, objects) {
        var ids = objects.map(function(e) {return e.id});
        var updatedData = [];
        $.each(grid.options.data, function(i, o) {
           if (ids.indexOf(o.id) < 0) {
               updatedData.push(o);
           }
        });
        grid.options.data = updatedData;
        grid.refresh();
    },

    saveExecution: function() {
        if (!this.form.validateForm()) {
            return;
        }

        var data = this.form.getData();

        var saveUrl = 'app/saveExecutionList';
        var type = this.options.type;
        var title;
        if (type == 'execution') {
            title = 'исполнителей';
        } else if (type = 'approval') {
            title = 'согласующих';
        } else if (type == 'acquaintance') {
            title = 'ознакамливающихся';
        }
        var message = 'Не удалось сохранить список ' + title + '. Обратитесь к администратору.';

        var rows = this.grid.element.find('tr');
        var rowsData = [];
        rows.each(function(i, row) {
            var rowData = {};
            var inputs = $(row).find('input');
            for (var j = 0; j < inputs.length; j++) {
                var input = $(inputs[j]);
                var name = input.attr('name');
                var value = input.val();
                if (name == 'mainExecutor') {
                    value = input.prop('checked');
                }
                if (name && value) {
                    rowData[name] = value;
                }
            }
            var selects = $(row).find('select');
            for (var j = 0; j < selects.length; j++) {
                var select = $(selects[j]);
                var name = select.attr('name');
                var value = select.val();
                if (name && value) {
                    if (name == 'userTitle') {
                        name = 'userId';
                    }
                    rowData[name] = value;
                }
            }
            if (!$.isEmptyObject(rowData)) {
                rowsData.push(rowData);
            }
        });

        data.executors = rowsData;
        data.type = this.options.type;
        data.documentId = this.options.documentId;
        data.taskId = this.options.taskId;

        $.post(saveUrl, JSON.stringify(data), $.proxy(function (id) {
            //this.options.dispatcher.refreshExecutionList('resolution');
            this.options.dispatcher.reopen();
            $.notify({message: 'Сохранено'}, {type: 'success', delay: 1000, timer: 1000});
        }, this)).fail($.proxy(function() {
            $.notify({message: message},{type: 'danger', delay: 0, timer: 0});
        }, this));
    },

    createButtons: function() {
        var buttons = $(this.element).find('[name="buttons"]');
        if (buttons.length == 0) {
            buttons = $('<div name="buttons" class="panel-body"></div>').appendTo(this.element);
        }
        buttons.empty();

        var saveButton = $('<button type="button" name="save" style="display: none;" class="btn btn-success controlElementLeftMargin">Сохранить</button>');
        saveButton.click($.proxy(function() {
            this.saveExecution();
        }, this));
        saveButton.appendTo(buttons);

        var editButton = $('<button type="button" name="edit" style="display: none;" class="btn btn-success controlElementLeftMargin">Редактировать</button>');
        editButton.click($.proxy(function() {
            this.options.mode = 'edit';
            this.createBlock();
        }, this));
        editButton.appendTo(buttons);

        var cancelButton = $('<button type="button" name="cancel" style="display: none;" class="btn btn-default controlElementLeftMargin">Отмена</button>');
        cancelButton.click($.proxy(function() {
            this.options.mode = 'read';
            this.createBlock();
        }, this));
        cancelButton.appendTo(buttons);

        var title;
        var type = this.options.type;
        if (type == 'execution') {
            title = 'резолюции';
        } else if (type = 'approval') {
            title = 'согласования';
        } else if (type == 'acquaintance') {
            title = 'ознакомления';
        }
        var cancelCreateButton = $('<button type="button" name="cancelCreate" style="display: none;" class="btn btn-default controlElementLeftMargin">Отмена</button>');
        cancelCreateButton.click($.proxy(function() {
            $.sokol.smodal({
                title: 'Подтверждение отмены',
                body: 'Отменить создание ' + title + '?',
                confirmButtonTitle: 'Подтвердить',
                confirmAction: $.proxy(this.options.dispatcher.cancelExecution, this.options.dispatcher)
            });
        }, this));
        cancelCreateButton.appendTo(buttons);

        this.manageButtons();
    },

    manageButtons: function() {
        var buttons = $(this.element).find('[name="buttons"]');
        buttons.children().hide();
        var mode = this.options.mode;
        if (mode == 'create') {
            buttons.children('[name="save"]').show();
            buttons.children('[name="cancelCreate"]').show();
        } else if (mode == 'edit') {
            buttons.children('[name="save"]').show();
            buttons.children('[name="cancel"]').show();
        } else {
            if (this.options.data.editable) {
                buttons.children('[name="edit"]').show();
            }
        }
    },

    _destroy: function () {
        this.element.detach();
    }
});
$.widget('sokol.executionReportForm', {
    options: {
        mode: 'read'
    },
    _create: function () {
        this.options.mode = this.options.data.status == "complete" ? "complete" : "read";

        this.createHeader();
        this.createBlock();

        if (this.options.mode == "complete") {
            this.renderReportForm();
        }
    },

    createHeader: function() {
        this.element.addClass('panel panel-default');
        this.element.attr('name', 'attachmentsPanel');

        var panelHeader = $('<div class="panel-heading"></div>').appendTo(this.element);

        var title;
        var type = this.options.data.type;
        if (type == 'execution') {
            title = 'Резолюция на исполнение';
        } else if (type = 'approval') {
            title = 'Согласование';
        } else if (type == 'acquaintance') {
            title = 'Ознакомление';
        }
        var panelTitle = $('<div class="panel-title">' + title + '</div>').appendTo(panelHeader);

        var panelBody = $('<div class="panel-body"></div>');
        panelBody.appendTo(this.element);
        this.panelBody = panelBody;
    },

    createBlock: function () {
        if (this.form) {
            this.form.destroy();
        }

        var executorsTitle;
        var commentTitle;
        var type = this.options.data.type;
        if (type == 'execution') {
            executorsTitle = 'Исполнители';
            commentTitle = 'Резолюция';
        } else if (type = 'approval') {
            executorsTitle = 'Согласующие';
            commentTitle = 'Комментарий';
        } else if (type == 'acquaintance') {
            executorsTitle = 'Ознакамливающиеся';
            commentTitle = 'Комментария';
        }

        this.form = $.sokol.form({
            mode: (this.options.mode == 'create' || this.options.mode == 'edit') ? 'edit' : 'read',
            data: this.options.data ? this.options.data : {},
            form: {
                "id": "task",
                "fields": [
                    {
                        "items": [
                            {
                                "id": "author",
                                "title": "Автор",
                                "type": "users",
                                "multiple": false,
                                ar: 'read',
                                hideIfEmpty: true
                            },
                            {
                                "id": "created",
                                "title": "Дата",
                                "type": "date",
                                ar: 'read',
                                hideIfEmpty: true
                            }
                        ]
                    },
                    {
                        'id': 'executors',
                        'title': executorsTitle,
                        'type': 'dictionary',
                        'multiple': true,
                        'mandatory': true,
                        'dictionary': 'organizationPersons'
                    },
                    {
                        'id': 'description',
                        'title': commentTitle,
                        'type': 'text',
                        'mandatory': true
                    }
                ]
            },
            usePanel: false,
            dispatcher: this.options.dispatcher,
            containerType: 'execution'
        }, $('<div></div>').appendTo(this.panelBody));

        this.createButtons();
    },

    renderReportForm: function() {
        var commentTitle;
        var reportTitle;
        var type = this.options.data.type;
        var options = [];
        if (type == 'execution') {
            commentTitle = 'Отчет';
            reportTitle = 'Отчет об исполнении';
            options = [
                {id: 'done', title: 'Исполнено'},
                {id: 'not_done', title: 'Не исполнено'}
            ];
        } else if (type = 'approval') {
            commentTitle = 'Замечание';
            reportTitle = this.options.mode == "complete" ? 'Отчет о согласовании' :'Согласование с замечанием';
            options = [
                {id: 'agreed', title: 'Согласовано'},
                {id: 'agreed_with_note', title: 'Согласовано с замечаниями'},
                {id: 'not_agreed', title: 'Не согласовано'}
            ];
        } else if (type == 'acquaintance') {
            commentTitle = 'Коментарий';
            reportTitle = 'Ознакомление с комментарием';
        }

        var panelHeader = $('<div data-name="reportHeader" class="panel-heading panel-footer" style="border-radius: 0;"></div>');
        var panelTitle = $('<div>' + reportTitle + '</div>');
        panelTitle.appendTo(panelHeader);
        panelHeader.insertAfter($(this.form.element).parent());

        var reportBody = $('<div data-name="reportBody" class="panel-body"></div>').insertAfter(panelHeader);

        this.reportForm = $.sokol.form({
            mode: this.options.mode == "complete" ? "read" : 'edit',
            data: this.options.data ? this.options.data : {},
            form: {
                "id": "task",
                "fields": [
                    {
                        "items": [
                            {
                                "id": "result",
                                "title": "Статус",
                                "type": "select",
                                "dictionary": "executionReportStatus",
                                "width": "200px",
                                "mandatory": type == 'execution',
                                "hideIfEmpty": type != 'execution',
                                'options' : options,
                                'valueField': 'id'
                            },
                            {
                                "id": "executedDate",
                                "title": "Дата",
                                "type": "date",
                                "ar": "read",
                                "hideIfEmpty": true
                            }
                        ]
                    },
                    {
                        'id': 'comment',
                        'title': commentTitle,
                        'type': 'text',
                        'mandatory': true,
                        'hideIfEmpty': this.options.mode == "complete"
                    }
                ]
            },
            usePanel: false,
            dispatcher: this.options.dispatcher,
            containerType: 'report'
        }, $('<div></div>').appendTo(reportBody));
        this.manageButtons();
    },

    removeReportForm: function() {
        if (this.reportForm) {
            this.reportForm.destroy();
        }
        this.element.find("[data-name='reportHeader']").remove();
        this.element.find("[data-name='reportBody']").remove();
        this.manageButtons();
    },

    saveReportForm: function(result) {
        if (!result && !this.reportForm.validateForm()) {
            return;
        }
        var data = result ? {fields: {}} : this.reportForm.getData();
        data.id = this.options.data.id;

        var type = this.options.data.type;
        if (type == 'approval') {
            data.fields['result'] = result ? result : 'agreed_with_note';
        }

        var saveUrl = 'app/saveTaskReport';
        var message = 'Не удалось сохранить отчет. Обратитесь к администратору.';

        $.post(saveUrl, JSON.stringify(data), $.proxy(function (response) {
            $.notify({message: 'Сохранено'}, {type: 'success', delay: 1000, timer: 1000});
            this.options.dispatcher.reopen(this.options.data.id);
            //this.options.mode = 'complete';
            //
            //if (this.reportForm) {
            //    this.reportForm.options.data.comment = response.comment;
            //    this.reportForm.options.data.result = response.result;
            //    this.reportForm.setMode('read');
            //}
            //
            //var buttons = $(this.element).find('[name="buttons"]');
            //buttons.remove();
        }, this)).fail($.proxy(function() {
            $.notify({message: message},{type: 'danger', delay: 0, timer: 0});
        }, this));
    },

    createButtons: function() {
        if (this.options.mode == 'complete') {
            return;
        }

        var executionsTitle;
        var reportTitle;
        var saveTitle;
        var type = this.options.data.type;
        if (type == 'execution') {
            executionsTitle = 'Создать внутреннюю резолюцию';
            reportTitle = 'Создать отчет';
            saveTitle = 'Сохранить отчет';
        } else if (type = 'approval') {
            executionsTitle = 'Создать внутреннее согласование';
            reportTitle = 'Согласовать с замечанием';
            saveTitle = 'Согласовать';
        } else if (type == 'acquaintance') {
            executionsTitle = 'Создать внутреннее ознакомление';
            reportTitle = 'Ознакомиться с комментарием';
            saveTitle = 'Ознакомиться';
        }

        var buttons = $(this.element).find('[name="buttons"]');
        if (buttons.length == 0) {
            buttons = $('<div name="buttons" class="panel-body execution-report-form-buttons"></div>').appendTo(this.element);
        }
        buttons.empty();

        var saveButton = $('<button type="button" name="save" style="display: none;" class="btn btn-success controlElementLeftMargin">' + saveTitle + '</button>');
        saveButton.click($.proxy(function() {
            this.saveReportForm();
        }, this));
        saveButton.appendTo(buttons);

        var executionButton = $('<button type="button" name="executionButton" style="display: none;" class="btn btn-info controlElementLeftMargin">' + executionsTitle + '</button>');
        executionButton.click($.proxy(function() {
            this.options.dispatcher.execution(type, this.options.data.id);
        }, this));
        executionButton.appendTo(buttons);

        var cancelButton = $('<button type="button" name="cancel" style="display: none;" class="btn btn-default controlElementLeftMargin">Отмена</button>');
        cancelButton.click($.proxy(function() {
            this.options.mode = 'read';
            this.removeReportForm();
        }, this));
        cancelButton.appendTo(buttons);

        var approveButton = $('<button type="button" name="approveButton" style="display: none;" class="btn btn-success controlElementLeftMargin">Согласовать</button>');
        approveButton.click($.proxy(function() {
            this.saveReportForm("agreed");
        }, this));
        approveButton.appendTo(buttons);

        var reportButton = $('<button type="button" name="reportButton" style="display: none;" class="btn controlElementLeftMargin">' + reportTitle + '</button>');
        if (type == 'approval') {
            reportButton.addClass('btn-default');
        } else {
            reportButton.addClass('btn-success');
        }
        reportButton.click($.proxy(function() {
            this.options.mode = 'edit';
            this.renderReportForm();
        }, this));
        reportButton.appendTo(buttons);

        var notApproveButton = $('<button type="button" name="notApproveButton" style="display: none;" class="btn btn-default controlElementLeftMargin">Не согласовывать</button>');
        notApproveButton.click($.proxy(function() {
            this.saveReportForm("not_agreed");
        }, this));
        notApproveButton.appendTo(buttons);

        this.manageButtons();
    },

    manageButtons: function() {
        var buttons = $(this.element).find('[name="buttons"]');
        buttons.children().hide();
        var mode = this.options.mode;
        if (!this.options.data.editable) {
            return;
        }
        if (mode == 'read') {
            if (!this.options.data.hasinternal) {
                buttons.children('[name="executionButton"]').show();
            }
            buttons.children('[name="reportButton"]').show();
            if (this.options.data.type == 'approval') {
                buttons.children('[name="approveButton"]').show();
                buttons.children('[name="notApproveButton"]').show();
            }
        } else if (mode == 'edit') {
            buttons.children('[name="save"]').show();
            buttons.children('[name="cancel"]').show();
        }
    },

    _destroy: function () {
        this.element.detach();
    }
});
var primary = $.fn.filter;
$.widget('sokol.filter', {
    options: {
        columns: null,
        parent: null
    },
    _create: function () {
        this.element.addClass("collapse");
        var filterPanel = $('<div class="panel panel-default"></div>').appendTo(this.element);
        var header = $('<div class="panel-heading"><div class="panel-title">Фильтр</div></div>').appendTo(filterPanel);
        this.body = $('<div class="panel-body"></div>').appendTo(filterPanel);
        var footer = $('<div class="panel-footer"></div>').appendTo(filterPanel);


        var addButton = $('<button type="button" name="add" style="margin-right: 5px;" class="btn btn-success">Добавить условие</button>');
        addButton.click($.proxy(function() {
            this.addCondition();
        }, this));
        addButton.appendTo(footer);

        var filterButton = $('<button type="button" name="filter" style="margin-right: 5px;" class="btn btn-primary">Применить</button>');
        filterButton.click($.proxy(function() {
            this.collectData();
        }, this));
        filterButton.appendTo(footer);
    },

    collectData: function() {
        this.conditions = this.body.find('.form-inline').map(function(i, e) {
            var $e = $(e);
            var conditionVal = $e.find('[name="conditionSelector"]').val();
            var columnVal = $e.find('[name="columnSelector"]').val();
            var operationVal = $e.find('[name="operationSelector"]').val();
            var value = $e.find('[name="valueBox"]').val();
            var condition = {
                condition: conditionVal,
                column: columnVal,
                operation: operationVal,
                value: value
            };
            return condition;
        }).toArray();
        this.options.parent.reload();
    },

    addCondition: function(element) {
        var condition = $('<div class="form-inline" style="display: table; width: 100%; margin-bottom: 5px; border-collapse:separate; border-spacing:5px;"></div>');
        if (element) {
            condition.insertAfter(element);
        } else {
            condition.appendTo(this.body);
        }

        //var delButton = $('<button type="button" class="form-control btn btn-danger btn-sm tableCell" style="width: 50px;border: 2px solide red;"><span class="glyphicon glyphicon-trash"></span></button>').appendTo(condition);
        var delButton = $('<div class="btn-group tableCell" style=""><button type="button" class="form-control btn btn-danger btn-sm" >' +
            '<span class="glyphicon glyphicon-trash" ></span>' +
            '</button></div>').appendTo(condition);
        delButton.click(function() {
            condition.remove();
        });
        var addButton = $('<div class="btn-group tableCell" style=""><button type="button" class="form-control btn btn-success btn-sm" >' +
            '<span class="glyphicon glyphicon-plus" ></span>' +
            '</button></div>').appendTo(condition);
        addButton.click($.proxy(function() {
            this.addCondition(condition);
        }, this));

        var conditionSelector = $('<select name="conditionSelector" class="selectpicker tableCell"></select>').appendTo(condition);
        conditionSelector.append($('<option value="">&nbsp;</option>'));
        conditionSelector.append($('<option value="and_block">И (</option>'));
        conditionSelector.append($('<option value="or_block">ИЛИ (</option>'));
        conditionSelector.append($('<option value="end_block">)</option>'));
        conditionSelector.selectpicker({
            width: 'auto'
        });
        conditionSelector.on('change', function(event){
            var condition = $(this).find("option:selected").val();
            if (condition) {
                fieldSelector.selectpicker('hide');
                operationSelector.selectpicker('hide');
                input.hide();
            } else {
                fieldSelector.selectpicker('show');
                operationSelector.selectpicker('show');
                input.show();
            }
        });

        var fieldSelector = $('<select name="columnSelector" class="selectpicker tableCell"></select>').appendTo(condition);
        fieldSelector.append($('<option value="">&nbsp;</option>'));
        var columns = this.options.columns;
        for (var c = 0; c < columns.length; c++) {
            if (columns[c].title) {
                fieldSelector.append($('<option value="' + columns[c].id + '">' + columns[c].title + '</option>'));
            }
        }
        fieldSelector.selectpicker({
            //noneSelectedText: '',
            width: '250px'
        });

        var operationSelector = $('<select name="operationSelector" class="selectpicker tableCell"></select>').appendTo(condition);
        operationSelector.append($('<option value="">&nbsp;</option>'));
        operationSelector.append($('<option value="EQUAL">равно</option>'));
        operationSelector.append($('<option value="NOT_EQUAL">не равно</option>'));
        operationSelector.append($('<option value="GREAT">больше</option>'));
        operationSelector.append($('<option value="LESS">меньше</option>'));
        operationSelector.append($('<option value="GREAT_OR_EQUAL">больше или равно</option>'));
        operationSelector.append($('<option value="LESS_OR_EQUAL">меньше или равно</option>'));
        operationSelector.append($('<option value="LIKE">содержит</option>'));
        operationSelector.append($('<option value="STARTS">начинается с</option>'));
        operationSelector.append($('<option value="ENDS">заканчивается на</option>'));
        operationSelector.selectpicker({
            width: 'auto'
        });

        var input = $('<div style="padding-left: 587px;"><input type="text" name="valueBox" class="form-control" style="width: 100%;"></div>').appendTo(condition);

    },

    _destroy: function() {
        this.element.detach();
    }
});
$.fn.filter = primary;
$.widget('sokol.form', {
    options: {
        mode: 'read',
        objectType: '',
        usePanel: true
    },

    _create: function () {
        this.fieldsInfo = [];
        this.fieldsInfoMap = [];
        this.createForm();
    },

    _destroy: function() {
        this.element.detach();
    },

    createForm: function() {
        this.formId = this.options.form.id;
        var data = this.options.data;
        var form = this.options.form;

        var container = this.element;
        container.empty();

        this.createMainBlock(container, form, data, this.options.mode == "edit");
    },

    createBlock: function(container, title) {
        var panel = $('<div class="panel panel-default"></div>');
        if (this.options.class) {
            panel.addClass(this.options.class);
        }
        panel.appendTo(container);
        var panelHeader = $('<div class="panel-heading"><div class="panel-title">' + title + '</div></div>');
        panelHeader.appendTo(panel);
        var panelBody = $('<div class="panel-body"></div>');
        panelBody.appendTo(panel);
        this.body = panelBody;
        return panelBody;
    },

    createButton: function(formNode, field, value, edit) {
        var button = $('<button type="button" class="btn">' + field.title + '</button>');
        if (field.class) {
            button.addClass(field.class);
        } else {
            button.addClass('btn-default');
        }
        button.click($.proxy(function() {
            if (this[field.method]) {
                this[field.method]();
            } else if (this.options.dispatcher[field.method]) {
                this.options.dispatcher[field.method]();
            }
        }, this));
        button.appendTo(formNode);
    },

    resetPassword: function() {
        $.post('app/resetPassword', {id: this.options.data.id}, function(data) {
            $.sokol.smodal({
                title: 'Новый пароль',
                body: data
            });
        });
    },

    createFieldPassword: function(formNode, field, value, edit) {
        $(formNode).append('' +
            '<div class="form-group' + (field.mandatory && edit ? ' formGroupRequired' : '') + '" style="' + (field.type == 'smallstring' ? 'width: 50%;' : '') + '">' +
            '<label class="control-label">' + field.title + ':</label>' +
            (edit ? ('<input name="' + field.id + '" class="form-control" type="password" value="' + value + '">') :
                ('<div>' + value + '</div>')) +
            '</div>');
    },

    createFieldString: function(formNode, field, value, edit) {
        $(formNode).append('' +
            '<div class="form-group' + (field.mandatory && edit ? ' formGroupRequired' : '') + '" style="' + (field.type == 'smallstring' ? 'width: 50%;' : '') + '">' +
            '<label class="control-label">' + field.title + ':</label>' +
            (edit ? ('<input name="' + field.id + '" class="form-control" type="text" value="' + value + '">') :
                ('<div>' + value + '</div>')) +
            '</div>');
    },

    createFieldText: function(formNode, field, value, edit) {
        if (!value && field.hideIfEmpty) {
            return;
        }

        $(formNode).append('' +
            '<div class="form-group' + (field.mandatory && edit ? ' formGroupRequired' : '') + '" style="' + (field.type == 'smallstring' ? 'width: 50%;' : '') + '">' +
            '<label class="control-label">' + field.title + ':</label>' +
            (edit ? ('<textarea rows="3" name="' + field.id + '" class="form-control">' + value + '</textarea>') :
                ('<div>' + value + '</div>')) +
            '</div>');
    },

    createFieldDate: function (formNode, field, value, edit) {
        value = value ? moment(value, 'DD.MM.YYYY HH:mm').format("L LT") : '';

        if (!value && field.hideIfEmpty) {
            return;
        }

        if (!edit || field.ar == 'read') {
            $(formNode).append('' +
                '<div class="form-group' + (field.mandatory && edit? ' formGroupRequired' : '') + '" style="' + (field.width ? 'width: ' + field.width + ';' : '') + '">' +
                '<label class="control-label">' + field.title + ':</label>' +
                '<div>' + value + '</div>' +
                '</div>' +
                '');
            return;
        }
        var dateNode = $('' +
            '<div class="form-group' + (field.mandatory && edit ? ' formGroupRequired' : '') + '" style="width: 200px;">' +
            '<label class="control-label">' + field.title + ':</label>' +
            '<div class="input-group date">' +
            '<input name="' + field.id + '" type="text" class="form-control" value="' + value + '"/>' +
            '<span class="input-group-addon">' +
            '<span class="glyphicon glyphicon-calendar"></span>' +
            '</span>' +
            '</div>' +
            '</div>');
        dateNode.appendTo(formNode);
        dateNode.find(".date").datetimepicker({
            //format: 'L',
            locale: 'ru'
        });

    },
    createFieldSelect: function(formNode, field, value, edit) {
        if (!value && field.hideIfEmpty) {
            return;
        }

        if (!edit) {
            if (field.options && field.valueField) {
                var vo =field.options.find(function (o) {
                    return o[field.valueField] == value;
                });
                if (vo) {
                    value = vo.title;
                }
            }
            $(formNode).append('' +
                '<div class="form-group' + (field.mandatory && edit ? ' formGroupRequired' : '') + '" style="' + (field.width ? 'width: ' + field.width + ';' : '') + '">' +
                '<label class="control-label">' + field.title + ':</label>' +
                '<div>' + value + '</div>' +
                '</div>' +
                '');
            return;
        }
        var option = value ? ('<option value="' + value + '">' + value + '</option>') : '';
        $(formNode).append('' +
            '<div class="form-group single' + (field.mandatory && edit? ' formGroupRequired' : '') + '" style="' + (field.width ? 'width: ' + field.width + ';' : '') + '">' +
            '<label class="control-label">' + field.title + ':</label>' +
            '<select name="' + field.id + '" class="demo-default">' + option + '</select>' +
            '</div>' +
            '');
        this.element.find('[name=' + field.id + ']').selectize({
            maxItems: 1,
            //plugins: ['remove_button'],
            valueField: field.valueField ? field.valueField : 'title',
            labelField: 'title',
            searchField: 'title',
            preload: true,
            options: field.options ? field.options : [],
            load: field.options ? null : function(query, callback) {
                $.getJSON('app/simpledictionary', {
                                id: field.dictionary
                            }, callback).fail(function() {
                    $.notify({message: 'Не удалось загрузить данные для справочника. Обратитесь к администратору.'},{type: 'danger', delay: 0, timer: 0});
                });
            },
            create: false
        });
    },
    createFieldDictionary: function(formNode, field, value, valueTitle, edit) {
        if( Object.prototype.toString.call(value) !== '[object Array]' ) {
            value = value ? [value] : [];
            valueTitle = valueTitle ? [valueTitle] : [];
        }

        var options = [];
        var initialValues = [];
        var titles = [];

        for (var i = 0; i < value.length; i++) {
            options.push({
                id: value[i],
                title: valueTitle[i]
            });
            initialValues.push(value[i]);
            titles.push(valueTitle[i]);
        }

        if (!edit) {
            $(formNode).append('' +
                '<div class="form-group' + (field.mandatory && edit ? ' formGroupRequired' : '') + '" style="' + (field.width ? 'width: ' + field.width + ';' : '') + '">' +
                '<label class="control-label">' + field.title + ':</label>' +
                '<div>' + titles.join(", ") + '</div>' +
                '</div>' +
                '');
            return;
        }

        var selector = $('<div class="form-group no-dropdown' + (field.mandatory ? ' formGroupRequired' : '') + '" style="' + (field.width ? 'width: ' + field.width + ';' : '') + '">' +
            '<label class="control-label">' + field.title + ':</label>' +
            '<select name="' + field.id + '" class="demo-default" id="selector_' + field.id + '">' + '</select>' +
            '' +
            '</div>' +
            '');
        $(formNode).append(selector);

        selector.find('select').selectize({
            maxItems: field.multiple ? 1000 : 1,
            plugins: ['restore_on_backspace', 'remove_button'],
            valueField: 'id',
            labelField: 'title',
            searchField: 'title',
            preload: field.preload ? true : false,
            closeAfterSelect: true,
            options: options,
            load: function(query, callback) {
                $.getJSON('app/dictionary', {
                    id: field.dictionary,
                    query: query
                }, callback).fail(function() {
                    $.notify({message: 'Не удалось загрузить данные для справочника. Обратитесь к администратору.'},{type: 'danger', delay: 0, timer: 0});
                });
            },
            create: false
        });
        selector.find('select')[0].selectize.setValue(initialValues);
    },

    createFieldGroup: function(formNode, field, value, valueTitle, edit) {
        if( Object.prototype.toString.call(value) !== '[object Array]' ) {
            value = value ? [value] : [];
            valueTitle = valueTitle ? [valueTitle] : [];
        }

        var options = [];
        var initialValues = [];
        var titles = [];

        for (var i = 0; i < value.length; i++) {
            options.push({
                id: value[i],
                title: valueTitle[i]
            });
            initialValues.push(value[i]);
            titles.push(valueTitle[i]);
        }

        if (!edit) {
            $(formNode).append('' +
                '<div class="form-group' + (field.mandatory && edit ? ' formGroupRequired' : '') + '" style="' + (field.width ? 'width: ' + field.width + ';' : '') + '">' +
                '<label class="control-label">' + field.title + ':</label>' +
                '<div>' + titles.join(", ") + '</div>' +
                '</div>' +
                '');
            return;
        }

        var selector = $('<div class="form-group no-dropdown' + (field.mandatory ? ' formGroupRequired' : '') + '" style="' + (field.width ? 'width: ' + field.width + ';' : '') + '">' +
            '<label class="control-label">' + field.title + ':</label>' +
            '<select name="' + field.id + '" class="demo-default" id="selector_' + field.id + '">' + '</select>' +
            '' +
            '</div>' +
            '');
        $(formNode).append(selector);

        selector.find('select').selectize({
            maxItems: 100,
            plugins: ['restore_on_backspace', 'remove_button'],
            valueField: 'id',
            labelField: 'title',
            searchField: 'title',
            preload: true,
            closeAfterSelect: false,
            options: options,
            load: function(query, callback) {
                $.getJSON('app/groups', {
                    size: 100,
                    conditions: JSON.stringify([{'condition':'','column':'title','operation':'LIKE','value':query}])
                }, function(response) {
                    callback(response.data);
                }).fail(function() {
                    $.notify({message: 'Не удалось загрузить данные для справочника. Обратитесь к администратору.'},{type: 'danger', delay: 0, timer: 0});
                });
            },
            create: false
        });
        selector.find('select')[0].selectize.setValue(initialValues);
    },

    createFieldUser: function(formNode, field, value, valueTitle, edit) {
        if( Object.prototype.toString.call(value) !== '[object Array]' ) {
            value = value ? [value] : [];
            valueTitle = valueTitle ? [valueTitle] : [];
        }

        if (value.length == 0 && field.hideIfEmpty) {
            return;
        }

        var options = [];
        var initialValues = [];
        var titles = [];

        for (var i = 0; i < value.length; i++) {
            options.push({
                id: value[i],
                title: valueTitle[i]
            });
            initialValues.push(value[i]);
            titles.push(valueTitle[i]);
        }

        if (!edit || field.ar == 'read') {
            $(formNode).append('' +
                '<div class="form-group' + (field.mandatory && edit ? ' formGroupRequired' : '') + '" style="' + (field.width ? 'width: ' + field.width + ';' : '') + '">' +
                '<label class="control-label">' + field.title + ':</label>' +
                '<div>' + titles.join(", ") + '</div>' +
                '</div>' +
                '');
            return;
        }

        var selector = $('<div class="form-group no-dropdown' + (field.mandatory ? ' formGroupRequired' : '') + '" style="' + (field.width ? 'width: ' + field.width + ';' : '') + '">' +
            '<label class="control-label">' + field.title + ':</label>' +
            '<select name="' + field.id + '" class="demo-default" id="selector_' + field.id + '">' + '</select>' +
            '' +
            '</div>' +
            '');
        $(formNode).append(selector);

        selector.find('select').selectize({
            maxItems: field.multiple ? 1000 : 1,
            plugins: ['restore_on_backspace', 'remove_button'],
            valueField: 'id',
            labelField: 'title',
            searchField: 'title',
            preload: true,
            closeAfterSelect: false,
            options: options,
            load: function(query, callback) {
                $.getJSON('app/users', {
                    size: 1000,
                    conditions: JSON.stringify([{'condition':'','column':'title','operation':'LIKE','value':query}])
                }, function(response) {
                    callback(response.data);
                }).fail(function() {
                    $.notify({message: 'Не удалось загрузить данные. Обратитесь к администратору.'},{type: 'danger', delay: 0, timer: 0});
                });
            },
            create: false
        });
        selector.find('select')[0].selectize.setValue(initialValues);
    },

    createFieldNumber: function(formNode, field, value, edit) {
        if (!edit) {
            $(formNode).append('' +
                '<div class="form-group' + (field.mandatory ? ' formGroupRequired' : '') + '" style="' + (field.width ? 'width: ' + field.width + ';' : '') + '">' +
                '<label class="control-label">' + field.title + ':</label>' +
                '<div>' + value + '</div>' +
                '</div>' +
                '');
            return;
        }
        $(formNode).append('' +
            '<div class="form-group' + (field.mandatory ? ' formGroupRequired' : '') + '" style="width: 200px;">' +
            '<label class="control-label">' + field.title + '</label>' +
            '<input name="' + field.id + '" class="form-control" type="text" value="' + value + '">' +
            '</div>');
    },
    createMainBlock: function(container, form, data, edit) {
        var formNode;
        if (this.options.usePanel) {
            formNode = this.element.find('[name="mainForm"]');
            if (formNode.length == 0) {
                formNode = $('<form name="mainForm"></form>');
                var blockTitle = form.title;
                var blockNode = this.createBlock(container, blockTitle);
                formNode.appendTo(blockNode);
            } else {
                formNode.children().remove();
            }
        } else {
            container.empty();
            formNode = $('<form name="mainForm"></form>').appendTo(container);
        }
        for (var i = 0; i < form.fields.length; i++) {
            var field = form.fields[i];
            this.createField(formNode, field, data, edit);
        }
    },

    createField: function(formNode, field, data, edit) {
        if (field.delimeter) {
            $('<div class="">&nbsp;</div>').appendTo(formNode);
            return;
        }
        if (field.items) {
            var row = $('<div class="row" style="width: 100%; display1: table-row;"></div>');
            row.appendTo(formNode);
            var colsNum =  field.cols ? field.cols : field.items.length;
            var getColNumClass = function(cols) {
                var n = 12 / colsNum;
                return "col-md-" + (n * cols);
            };

            for (var i = 0; i < field.items.length; i++) {
                var subfield = field.items[i];
                if (subfield.type == "smallstring") {
                    subfield.type = "string";
                }
                var td = $('<div class="' + getColNumClass(subfield.cols ? subfield.cols : 1) + '"></div>');
                td.appendTo(row);
                this.createField(td, subfield, data, edit);
            }
            var ctd = $('<div style="clear: both" />');
            ctd.appendTo(row);
            return;
        }

        var id = field.id;
        var value = data[id];
        var valueTitle = data[id + "Title"];
        var type = field.type;

        this.fieldsInfo.push(field);
        this.fieldsInfoMap[id] = field;

        if (!value && value !== 0) {
            value = "";
        }
        if (type == "string" || type == "smallstring") {
            this.createFieldString(formNode, field, value, edit);
        } else if (type == "text") {
            this.createFieldText(formNode, field, value, edit);
        } else if (type == "password") {
            this.createFieldPassword(formNode, field, value, edit);
        } else if (type == "button") {
            this.createButton(formNode, field, value, edit);
        } else if (type == "date") {
            this.createFieldDate(formNode, field, value, edit);
        } else if (type == "select") {
            this.createFieldSelect(formNode, field, value, edit);
        } else if (type == "dictionary") {
            this.createFieldDictionary(formNode, field, value, valueTitle, edit);
        } else if (type == "group") {
            this.createFieldGroup(formNode, field, value, valueTitle, edit);
        } else if (type == "users") {
            this.createFieldUser(formNode, field, value, valueTitle, edit);
        } else if (type == "number") {
            this.createFieldNumber(formNode, field, value, edit);
        }
    },

    notify: function(message) {
        $.notify({
            message: message
        },{
            type: 'success',
            delay: 1000,
            timer: 1000
        });
    },

    setMode: function(mode) {
        this.options.mode = mode;

        this.createMainBlock(this.element, this.options.form, this.options.data, mode == "edit");
    },

    validateForm: function() {
        var mainForm = this.element.find('[name="mainForm"]');
        var valuesList = mainForm.serializeArray();
        var values = [];
        for (var j = 0; j < valuesList.length; j++) {
            var val = valuesList[j];
            values[val.name] = val;
        }

        var valid = true;
        var numberRegex = /^-?\d+$/

        for (var i = 0; i < this.fieldsInfo.length; i++) {
            var field = this.fieldsInfo[i];
            var val = values[field.id];

            var fieldDiv = mainForm.find("[name=" + field.id + "]").parent();
            if (fieldDiv.hasClass('date')) {
                fieldDiv = fieldDiv.parent();
            }
            if (field.mandatory) {
                if (val && val.value) {
                    fieldDiv.removeClass("has-error");
                    var helpBlock = fieldDiv.find(".help-block");
                    helpBlock.remove();
                } else {
                    if (!fieldDiv.hasClass('has-error')) {
                        fieldDiv.append($('<span class="help-block">Поле не может быть пустое</span>'));
                    }
                    fieldDiv.addClass("has-error");
                    valid = false;
                }

            } else if(field.type == 'number') {
                if (val.value && !val.value.match(numberRegex)) {
                    fieldDiv.addClass("has-error");
                    valid = false;
                } else {
                    fieldDiv.removeClass("has-error");
                }
            } else if(field.validation) {
                if (val.value.search(field.validation) < 0) {
                    fieldDiv.addClass("has-error");
                    valid = false;
                } else {
                    fieldDiv.removeClass("has-error");
                }
            }
        }
        return valid;
    },

    getData: function() {
        var valuesList = this.element.find('[name="mainForm"]').serializeArray();
        var fields = {};
        for (var i = 0; i < valuesList.length; i++) {
            var value = valuesList[i];
            var fieldInfo = this.fieldsInfoMap[value.name];
            if (fieldInfo.multiple) {
                if (fields[value.name]) {
                    fields[value.name].push(value.value);
                } else {
                    fields[value.name] = [value.value];
                }
            } else {
                fields[value.name] = value.value;
            }
        }

        var data = {
            id: this.options.data.id,
            fields: fields,
            type: this.options.data.type
        };
        if (this.options.isNew) {
            data.isNew = this.options.isNew;
            data.type = this.options.data.type;
        }
        return data;
    },

    //todo remove
    saveForm: function() {
        console.log(">>>> form ", this.options.form);
        console.log(">>>> fieldsInfo ", this.options.fieldsInfo);
        //if (!this.validateForm()) {
        //    return;
        //}
        //console.log("Data: ", this.getData());
    }
});

$.widget('sokol.formButtons', {
    options: {
        mode: "read",
        actions: []
    },

    _create: function () {
        var buttons = this.element;
        buttons.addClass('formMainPanel');

        var saveButton = $('<button type="button" name="save" style="display: none;" class="btn btn-success controlElementLeftMargin">Сохранить</button>');
        saveButton.click($.proxy(function() {
            this.options.dispatcher.saveForm();
        }, this));
        saveButton.appendTo(buttons);

        var editButton = $('<button type="button" name="edit" style="display: none;" class="btn btn-default controlElementLeftMargin">Редактировать</button>');
        editButton.click($.proxy(function() {
            this.options.dispatcher.goToMode("edit");
        }, this));
        editButton.appendTo(buttons);

        if (this.options.id) {
            var cancelButton = $('<button type="button" name="cancel" style="display: none;" class="btn btn-default controlElementLeftMargin">Отменить</button>');
            cancelButton.click($.proxy(function () {
                this.options.dispatcher.goToMode("read");
            }, this));
            cancelButton.appendTo(buttons);
        }

        if (this.options.dispatcher.options.data.status == 'Черновик') {
            this.addTemplatesButton(buttons);
        }

        if (this.options.deleteAction) {
            var deleteButton = $('<button type="button" name="delete" style="display: none;" class="btn btn-danger controlElementLeftMargin">Удалить</button>');
            deleteButton.click($.proxy(function () {
                this.options.dispatcher.deleteDocument();
            }, this));
            deleteButton.appendTo(buttons);
        }

        var actions = this.options.actions;

        $.each(actions, $.proxy(function(i, a) {
            var actionButton = $('<button data-type="action" type="button" name="' + a.id + '" style="display: none;" class="btn btn-default controlElementLeftMargin">' + a.title + '</button>');
            actionButton.click($.proxy(function() {
                if (a.form) {
                    if (a.form == 'execution') {
                        this.options.dispatcher.execution('execution');
                    } else if (a.form == 'approval') {
                        this.options.dispatcher.execution('approval');
                    }
                } else {
                    this.doAction(a);
                }
            }, this));
            actionButton.appendTo(buttons);
        }, this));

        this.manageButtons();
    },

    addTemplatesButton: function(buttons) {
        var fromTemplatesButton = $('<div name="templates" class="btn-group">' +
            '<button type="button" class="btn btn-info dropdown-toggle controlElementLeftMargin" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">' +
            'Из шаблона <span class="caret"></span>' +
            '</button>' +
            '<ul class="dropdown-menu">' +
            '</ul>' +
            '</div>');
        var fromTemplateUl = fromTemplatesButton.find('ul');

        var type = this.options.dispatcher.options.data.type;
        $.getJSON('app/documentTemplates', {type: type}, $.proxy(function(res) {
            var data = res.data;
            data.forEach($.proxy(function(t) {
                var aTemplate = $('<a href="#">' + t.title + '</a>');
                aTemplate.click($.proxy(function(e) {
                    e.preventDefault();
                    this.updateCardFromTemplate(t.id);
                }, this));
                var liTemplate = $('<li></li>').appendTo(fromTemplateUl);
                aTemplate.appendTo(liTemplate);
            }, this));

            var toTemplate = $('<a href="#">Сохранить как шаблон</a>');
            toTemplate.click($.proxy(function(e) {
                e.preventDefault();
                this.saveAsTemplate();
            }, this));
            $('<li role="separator" class="divider"></li>').appendTo(fromTemplateUl);
            var liToTemplate = $('<li></li>').appendTo(fromTemplateUl);
            toTemplate.appendTo(liToTemplate);
        }, this));

        fromTemplatesButton.appendTo(buttons);
    },

    saveAsTemplate: function() {
        this.options.dispatcher.saveAsTemplate();
    },

    updateCardFromTemplate: function(templateDocumentId) {
        this.options.dispatcher.updateCardFromTemplate(templateDocumentId);
    },

    doAction: function(action) {
        $.post('app/doAction', JSON.stringify({documentId: this.options.id, action: action.id}), $.proxy(function(result) {
            if (result == 'true') {
                this.options.dispatcher.reopen();
            }
        }, this)).fail($.proxy(function() {
            $.notify({message: 'Не удалось выполнить действие.'},{type: 'danger', delay: 0, timer: 0});
        }, this));
    },

    setMode: function(mode) {
        this.options.mode = mode;
        this.manageButtons();
    },

    manageButtons: function() {
        var buttons = this.element;
        buttons.children().hide();
        if (this.options.mode == 'read') {
            buttons.children().each(function(i, c) {
                var $c = $(c);
                if ($c.attr('data-type') == 'action') {
                    $c.show();
                }
            });
            buttons.children('[name="edit"]').show();
            buttons.children('[name="delete"]').show();
        } else if (this.options.mode == "edit") {
            if (!this.options.isNew) {
                buttons.children('[name="cancel"]').show();
            }
            buttons.children('[name="save"]').show();

            if (this.options.dispatcher.options.data.status == 'Черновик') {
                buttons.children('[name="templates"]').show();
                buttons.children('[name="saveAsTemplate"]').show();
            }
        }
    },

    _destroy: function() {
        this.element.detach();
    }
});
$.widget("sokol.grid", {
    options: {
        url: null,
        id: "",
        pageSize: 20,
        offset: 0,
        pageCount: 0,
        currentPage: 1,
        total: 0,
        title: "",
        columnsVisible: null,
        objectType: null,
        linkColumn: null,
        selectable: false,
        deletable: false,
        deleteMethod: null,
        addable: false,
        addMethod: null,
        usePanel: true,
        bottomPagination: true,
        showTableHeader: true
    },

    _create: function () {
        this.sortColumn = this.options.sortColumn;
        this.sortAsc = this.options.sortAsc;
        this.createList();
    },

    _destroy: function() {
        this.element.detach();
    },

    _setOptions: function (options) {
        this._super(options);
        this.reload();
    },

    createList: function() {
        if (this.options.mode == 'edit') {
            this.options.addable = true;
            this.options.selectable = true;
            this.options.deletable = true;
        }
        var central = this.element;
        if (this.options.usePanel) {
            central.empty();
        }

        this.createButtons();

        this.renderTablePanel();
        this.reload();
        if (!this.options.data && !this.options.noControls && this.options.bottomPagination) {
            var bottomBar = this.createButtonsBar(central);
            this.createPagination(bottomBar);
        }
    },

    createButtons: function() {
        var topBar;

        if (this.topBar) {
            topBar = this.topBar.empty();
        } else {
            var topBar = $('<div style="margin-bottom: 10px;"></div>');
            topBar.appendTo(this.element);

            if (!this.options.usePanel) {
                topBar.css('margin-left', '15px');
            }
            this.topBar = topBar;
        }

        if (!this.options.data && !this.options.noControls) {
            var pagination = this.createPagination(topBar);
        } else {
            $('<span style="margin-right: 10px;">Найдено: <span name="gridItemsCount">' + ((this.options.data && this.options.data.length) ? this.options.data.length : '') + '</span></span>').appendTo(topBar);
        }
        if (!this.options.noControls) {
            if (this.options.columnsVisible) {
                this.createColumnsSelector(topBar);
            }
            if (this.options.deletable) {
                this.createDeleteButton(topBar);
            }
            if (this.options.filterable) {
                this.createFilterButton(topBar);
            }
            if (this.options.addable) {
                this.createAddButton(topBar);
            }
        }
    },

    setPage: function(page) {
        if (page > 0 && page < this.options.pageCount + 1) {
            this.options.currentPage = page;
            this.options.offset = this.options.pageSize * (page - 1);
            this.reload();
        }
    },

    nextPage: function() {
        this.setPage(this.options.currentPage + 1)
    },

    previewPage: function() {
        this.setPage(this.options.currentPage - 1);
    },

    goToMode: function(mode) {
        if (mode == 'edit') {
            this.options.addable = true;
            this.options.selectable = true;
            this.options.deletable = true;
        } else {
            this.options.addable = false;
            this.options.selectable = false;
            this.options.deletable = false;
        }
        this.createButtons();
        this.refresh();
    },

    refresh: function () {
        this.renderTableHeader();
        this.renderRows();
        if (this.options.url) {
            this.updatePagination();
        }
    },

    reload: function() {
        if (this.options.url) {
            $.getJSON(this.options.url, {
                listId: this.options.id,
                size: this.options.pageSize,
                offset: this.options.offset,
                conditions: (this.filter && this.filter.conditions) ? JSON.stringify(this.filter.conditions) : null,
                sort: this.sortColumn,
                sortAsc: this.sortAsc,
                searchtext: this.options.searchtext,
                docId: this.options.docId
            }, $.proxy(function (data) {
                this.options.data = data.data;
                this.options.total = data.total;
                this.refresh();

                if (this.options.dataLoadedCallback) {
                    this.options.dataLoadedCallback(this);
                }

            }, this)).fail(function() {
                $.notify({message: 'Не удалось загрузить данные. Обратитесь к администратору.'}, {type: 'danger', delay: 0, timer: 0});
            });
        } else {
            this.options.total = this.options.data.length;
            this.refresh();
        }
    },

    createButtonsBar: function(container) {
        var div = $('<div style="margin-bottom: 10px;"></div>').appendTo(container);
        return div;
    },

    createPagination: function(div) {
        $(
            '<span style="margin-right: 10px;">Найдено: <span name="gridItemsCount">0</span></span>' +
            '<button name="preview" class="btn btn-default" href = "#" disabled1="disable">' +
            '<span class="glyphicon glyphicon-triangle-left" aria-hidden="true"></span> Предыдущая</button>&nbsp;' +
            '<select name="pageSelector" class="selectpicker pageSelector"></select>&nbsp;' +
            '<button name="next" class="btn btn-default" href = "#">Следующая ' +
            '<span class="glyphicon glyphicon-triangle-right" aria-hidden="true"></span></button>&nbsp;' +
            '<span style="margin-left: 10px;">Отображать ' +
            '<select name="pageSize" class="selectpicker"><option>5</option><option>20</option><option>50</option><option>100</option></select>&nbsp;' +
            '</span>&nbsp;'
        ).appendTo(div);

        $(div).find('.selectpicker').selectpicker({
            noneSelectedText: '0 / 0',
            width: 'fit'
        });
        $(div).find('.selectpicker[name="pageSelector"]').on('change', {grid: this}, function(event){
            var selected = $(this).find("option:selected").val();
            var val = selected.split(" / ")[0];
            event.data.grid.setPage(val);
        });
        $(div).find('.selectpicker[name="pageSize"]').on('change', {grid: this}, function(event){
            var size = $(this).find("option:selected").val();
            var grid = event.data.grid;
            grid.options.pageSize = size;
            grid.reload();

        });
        $(div).find("[name='preview']").on('click', $.proxy(this.previewPage, this));
        $(div).find("[name='next']").on('click', $.proxy(this.nextPage, this));

        return div;
    },
    
    updatePagination: function() {
        this.element.find("[name='gridItemsCount']").text(this.options.total);
        var pages = this.options.total / this.options.pageSize;
        var pagesInt = parseInt(pages);
        pages = pagesInt < pages ? pagesInt + 1 : pagesInt;
        this.options.pageCount = pages;
        if (this.options.currentPage > pages) {
            this.options.currentPage = pages;
        }
        if (this.options.pageCount > 1) {
            this.element.find("[name='preview']").show();
            this.element.find("[name='next']").show();
            this.element.find(".pageSelector").show();
        }

        var selectors = this.element.find("[name='pageSelector']");
        selectors.empty();
        for (var p = 1; p <= pages; p++) {
            selectors.append($("<option>" + p + " / " + pages + "</option>"))
        }

        if (this.options.currentPage == 1) {
            this.element.find("[name='preview']").attr('disabled', 'disabled');
        } else {
            this.element.find("[name='preview']").removeAttr('disabled');
        }
        if (this.options.currentPage == this.options.pageCount) {
            this.element.find("[name='next']").attr('disabled', 'disabled');
        } else {
            this.element.find("[name='next']").removeAttr('disabled');
        }
        this.element.find('select[name="pageSelector"]').val(this.options.currentPage + " / " + this.options.pageCount);
        this.element.find('select[name="pageSize"]').val(this.options.pageSize);
        this.element.find('.selectpicker').selectpicker('refresh');

        if (this.options.pageCount <= 1) {
            this.element.find("[name='preview']").hide();
            this.element.find("[name='next']").hide();
            this.element.find(".pageSelector").hide();
        }
    },

    renderTableHeader: function() {
        var thead = this.element.find('[name="tableHead"]');
        thead.empty();
        var columns = this.options.columns;
        var header = $("<tr></tr>");
        header.appendTo(thead);
        if (this.options.selectable) {
            $('<th style="width: 40px;"></th>').appendTo(header);
        }
        for (var i = 0; i < columns.length; i++) {
            var col = columns[i];
            var colType = columns[i].type;
            if (colType != "hidden" && this.isColumnVisible(col.id)) {
                var th = $("<th>" + col.title + "</th>");

                if (this.options.sortable) {
                    if (this.sortColumn == col.id) {
                        if (this.sortAsc) {
                            var sortLabel = $('<span class="glyphicon glyphicon-triangle-top" style="margin-left: 5px;"></span>');
                            th.append(sortLabel);
                        } else {
                            var sortLabel = $('<span class="glyphicon glyphicon-triangle-bottom" style="margin-left: 5px;"></span>');
                            th.append(sortLabel);
                        }
                    }

                    th.click($.proxy(function(colId) {
                        return $.proxy(function() {
                            if (this.sortColumn && this.sortColumn == colId) {
                                if (this.sortAsc) {
                                    this.sortAsc = false;
                                } else {
                                    this.sortColumn = null;
                                }
                            } else {
                                this.sortColumn = colId;
                                this.sortAsc = true;
                            }
                            this.reload();
                        }, this);
                    }, this)(col.id));
                }

                th.appendTo(header);
            }
        }
    },

    renderTablePanel: function() {
        var panel;
        if (this.options.usePanel) {
            panel = this.element.find('[name="tablePanel"]');
            if (!panel.length) {
                panel = $('<div name="tablePanel" class="panel panel-default"></div>');
                panel.appendTo(this.element);
            } else {
                panel.empty();
            }
        } else {
            panel = this.element;
        }

        if (this.options.showTableHeader) {
            var panelHeader = $('<div class="panel-heading"></div>');
            if (!this.options.usePanel) {
                panelHeader.addClass('panel-footer');
                panelHeader.css('border-radius', '0');
            }
            var panelTitle = $('<div>' + this.options.title + '</div>');
            if (this.options.usePanel) {
                panelTitle.addClass('panel-title');
            }
            panelTitle.appendTo(panelHeader);
            panelHeader.appendTo(panel);
        }

        var table = $("<table class='table'></table>");
        if (this.options.topBorder) {
            table.css('border-top', '1px solid #ddd')
        }
        table.appendTo(panel);
        var thead = $('<thead name="tableHead"></thead>');
        thead.appendTo(table);
        var tbody = $('<tbody name="tableBody"></tbody>');
        tbody.appendTo(table);

        this.renderTableHeader();
    },

    isColumnVisible: function(columnId) {
        return !this.options.columnsVisible || this.options.columnsVisible.indexOf(columnId) >= 0;
    },

    renderCheckboxColumn: function(row, id) {
        var td = $('<td></td>');
        var checkbox = $('<input type="checkbox" dataId="' + id + '">');
        checkbox.click($.proxy(function(e) {
            this.updateButtons();
            e.stopPropagation();
        }, this));
        checkbox.appendTo(td);
        td.appendTo(row);

        td.click($.proxy(function(e){
            var cb = $(e.target).find('input[type=checkbox]');
            var checked = cb.prop('checked');
            cb.prop('checked', checked ? '' : 'checked');
            this.updateButtons();
        }, this));
    },

    renderRow: function(row, rowObj) {
        var columns = this.options.columns;
        var objectId = this.options.idField ? rowObj[this.options.idField] : rowObj.id;
        if (this.options.selectable) {
            this.renderCheckboxColumn(row, objectId);
        }
        for (var k = 0; k < columns.length; k++) {
            var column = columns[k];
            var colId = column.id;
            var colType = column.type;
            if (colType != "hidden" && this.isColumnVisible(colId)) {
                var val = rowObj[colId];
                if (this.options.mode == 'edit' && column.editor) {
                    if (column.editor == "text") {
                        var td = $('<td></td>');
                        var input = $('<input name="' + colId + '" type="text">').appendTo(td);

                        td.appendTo(row);
                    } else if (column.editor == "user") {
                        this.createEditorUser(row, column, rowObj[column.idColumn], val);
                    } else if (column.editor == "radio") {
                        this.createEditorRadio(row, column, val);
                    } else if (column.editor == "date") {
                        this.createEditorDate(row, column, val);
                    } else {
                        row.append($('<td></td>'));
                    }
                } else if (val || colId == "title") {
                    if (colId == this.options.linkColumn || column.render == 'link') {
                        if (!val || 0 === val.length) {
                            val = "[Заголовок не указан]";
                        }
                        var linkType = column.linkType ? column.linkType : this.options.objectType;
                        var id = column.idColumn ? rowObj[column.idColumn] : rowObj.id;
                        var td = $('<td><a href="#' + linkType + '/' + id + '" target="_blank">' + val + '</a></td>');
                        td.appendTo(row);
                    } else if (column.render == 'datetime') {
                        val = moment(val, 'DD.MM.YYYY HH:mm').format('L LT');

                        var td = $('<td>' + (val ? val : '') + '</td>');
                        td.appendTo(row);
                    } else if (column.render == 'boolean') {
                        if (val) {
                            var td = $('<td><span class="glyphicon glyphicon-ok" aria-hidden="true"></span></td>');
                            td.appendTo(row);
                        } else {
                            row.append($('<td></td>'));
                        }
                    } else if (column.render == 'expand') {
                        var td = $('<td></td>');
                        if (rowObj[column.dataColumn]) {
                            var a = $('<a href="#">' + (val ? val : '') + '</a>').appendTo(td);
                            a.click((function(row, column) {
                                return function(e) {
                                    e.preventDefault();
                                    var expandTr = $(row).next();
                                    if (expandTr.attr("data-name") == "reportComment") {
                                        expandTr.remove();
                                    } else {
                                        var expandData = rowObj[column.dataColumn];
                                        expandTr = $('<tr data-name="reportComment"><td colspan="100">' + (expandData ? expandData : '') + '</td></tr>');
                                        expandTr.insertAfter(row);
                                    }
                                };
                            })(row, column));
                        } else {
                            var s = $('<span>' + (val ? val : '') + '</span>').appendTo(td);
                        }

                        td.appendTo(row);
                    } else if (column.render == 'history') {
                        var td = $('<td></td>');
                        if (rowObj[column.dataColumn]) {
                            var a = $('<a href="#">' + (val ? val : '') + '</a>').appendTo(td);
                            a.click((function(row, column) {
                                return function(e) {
                                    e.preventDefault();
                                    var expandTr = $(row).next();
                                    if (expandTr.attr("data-name") == "reportComment") {
                                        expandTr.remove();
                                    } else {
                                        var historyItemsFields = rowObj[column.dataColumn];
                                        var expandData = $('<table class="table table-striped table-condensed noTopBorderTable"></table>');
                                        var expandDataBody = $('<tbody></tbody>').appendTo(expandData);
                                        $('<thead><tr><th>Поле</th><th>Старое значение</th><th>Новое значение</th></tr></thead>').appendTo(expandData);
                                        for (var i = 0; i < historyItemsFields.length; i++) {
                                            var f = historyItemsFields[i];
                                            $('<tr><td>' + f.field + '</td><td>' + f.oldValue + '</td><td>' + f.newValue + '</td></tr>').appendTo(expandDataBody);
                                        }
                                        expandTr = $('<tr data-name="reportComment"><td colspan="100"></td></tr>');
                                        expandTr.find("td").append(expandData);
                                        expandTr.insertAfter(row);
                                    }
                                };
                            })(row, column));
                        } else {
                            var s = $('<span>' + (val ? val : '') + '</span>').appendTo(td);
                        }

                        td.appendTo(row);
                    } else {
                        if(Object.prototype.toString.call(val) === '[object Array]' ) {
                            val = val.join(", ");
                        }
                        var td = $('<td>' + (val ? val : '') + '</td>');
                        if (column.class) {
                            td.addClass(column.class);
                        }
                        td.appendTo(row);
                    }
                } else {
                    row.append($('<td></td>'));
                }
            }
        }
    },

    renderRows: function() {
        var tbody = this.element.find('tbody');
        tbody.empty();
        var rowsData = this.options.data;
        for (var j = 0; j < rowsData.length; j++) {
            var row = $("<tr></tr>");
            var rowObj = rowsData[j];
            this.renderRow(row, rowObj);
            row.appendTo(tbody);
        }
    },

    updateButtons: function() {
        if (this.element.find('tbody input:checked').length > 0) {
            this.deleteButton.attr("disabled", false);
        } else {
            this.deleteButton.attr("disabled", true);
        }
    },

    createDeleteButton: function(element) {
        var deleteButton = $('<button type="button" disabled="disabled" name="delete" class="btn btn-danger controlElementLeftMargin">Удалить</button>');
        deleteButton.click($.proxy(function() {
            var ids = this.element.find('tbody input:checked').map(function (i, el) {
                return $(el).attr('dataId');
            }).toArray();

            var objects = this.options.data.filter($.proxy(function(element) {
                var objectId = this.options.idField ? element[this.options.idField] : element.id;
                return ids.indexOf(objectId) >= 0;
            }, this));

            if (this.options.deleteMethod) {
                if (objects.length > 0) {
                    this.options.deleteMethod(this, objects);
                } else {
                    console.error("No selected for delete");
                }
            } else {
                console.error("No deleteMethod found");
            }
        }, this));
        deleteButton.appendTo(element);
        this.deleteButton = deleteButton;
    },

    createFilterButton: function(buttonBar) {
        this.filter = $.sokol.filter({
            columns: this.options.columns,
            parent: this
        }, $('<div></div>').appendTo(this.element));

        var filterButton = $('<button type="button" name="filter" style="" class="btn btn-default controlElementLeftMargin">Фильтр</button>');
        filterButton.click($.proxy(function() {
            this.filter.element.slideToggle();
            if (this.filter.conditions && this.filter.conditions.length > 0) {
                filterButton.addClass('activeFilterButton');
            } else {
                filterButton.removeClass('activeFilterButton');
            }
        }, this));
        filterButton.appendTo(buttonBar);
    },

    createAddButton: function(buttonBar) {
        if (this.options.addable == 'link') {
            var addButton = $('<a type="button" name="add" target="_blank" href="#new/' + this.options.addableType + '" style="" class="btn btn-success controlElementLeftMargin">Создать</a>');
            addButton.appendTo(buttonBar);
        } else {
            var addButton = $('<button type="button" name="add" style="" class="btn btn-success controlElementLeftMargin">Добавить</button>');
            addButton.click($.proxy(function() {
                this.renderAddNewRow();
            }, this));
            addButton.appendTo(buttonBar);
        }
    },

    doAddElement: function(row) {
        row = this.element.find('tbody tr').first();
        var values = row.find("input, select").map(function(i, e) {
            var ee = $(e);
            return {
                id: ee.attr('name'),
                value: ee.val()
            }
        }).toArray();

        if (this.options.addMethod) {
            this.options.addMethod(values, $.proxy(function(reloadValue) {
                var tbody = this.element.find('tbody');
                row.remove();

                if ('reload' == reloadValue) {
                    this.reload();
                } else {
                    row = $('<tr></tr>').prependTo(tbody);
                    this.options.data.push(reloadValue);
                    this.renderRow(row, reloadValue);
                }

            }, this));
        }
    },

    renderAddNewRow: function() {
        var tbody = this.element.find('tbody');
        var columns = this.options.columns;
        var row = $("<tr></tr>");
        if (this.options.selectable) {
            if (this.options.usePanel) {
                $('<td></td>').appendTo(row);
            } else {
                this.renderCheckboxColumn(row, '_new' + new Date().getTime());
            }
        }
        for (var k = 0; k < columns.length; k++) {
            var column = columns[k];
            var colId = column.id;
            var colType = column.type;
            var editor = column.editor;
            if (colType != "hidden" && this.isColumnVisible(colId)) {
                if (editor == "text") {
                    var td = $('<td></td>');
                    var input = $('<input class="form-control" name="' + colId + '" type="text">').appendTo(td);

                    input.bind("keypress", $.proxy(function(event) {
                        if(event.which == 13) {
                            event.preventDefault();
                            this.doAddElement(row);
                        }
                    }, this));

                    td.appendTo(row);
                } else if (editor == "user") {
                    this.createEditorUser(row, column);
                } else if (editor == "dictionary") {
                    this.createEditorDictionary(row, column);
                } else if (editor == "boolean") {
                    this.createEditorBoolean(row, column);
                } else if (column.editor == "radio") {
                    this.createEditorRadio(row, column);
                } else if (editor == "date") {
                    this.createEditorDate(row, column);
                } else {
                    row.append($('<td></td>'));
                }
            }
        }
        row.prependTo(tbody);
    },

    createEditorRadio: function (formNode, field, value) {
        var dateNode = $('<td>' +
            '<div class="radio">' +
            '<label>' +
            '<input name="' + field.id + '" type="radio" ' + (value ? 'checked="checked"' : '') + '>' +
            '</label>' +
            '</div>' +
            //'<input name="' + field.id + '" type="checkbox" class="" ' + (value ? 'checked="checked"' : '') + '/>' +
            '</td>');
        dateNode.appendTo(formNode);
    },

    createEditorDate: function (formNode, field, value) {
        value = value ? moment(value, 'DD.MM.YYYY HH:mm').format("L LT") : '';
        var dateNode = $('<td>' +
            '<div class="' + (field.mandatory && edit ? ' formGroupRequired' : '') + '" style="width: 200px;">' +
            '<div class="input-group date">' +
            '<input name="' + field.id + '" type="text" class="form-control" value="' + value + '"/>' +
            '<span class="input-group-addon">' +
            '<span class="glyphicon glyphicon-calendar"></span>' +
            '</span>' +
            '</div>' +
            '</div></td>');
        dateNode.appendTo(formNode);
        dateNode.find(".date").datetimepicker({
            format: 'L',
            locale: 'ru'
        });

    },

    createEditorDictionary: function(formNode, field, value, valueTitle) {
        if( Object.prototype.toString.call(value) !== '[object Array]' ) {
            value = value ? [value] : [];
            valueTitle = valueTitle ? [valueTitle] : [];
        }

        var options = [];
        var initialValues = [];
        var titles = [];

        for (var i = 0; i < value.length; i++) {
            options.push({
                id: value[i],
                title: valueTitle[i]
            });
            initialValues.push(value[i]);
            titles.push(valueTitle[i]);
        }

        var selector = $('<td><div class="no-dropdown' + (field.mandatory ? ' formGroupRequired' : '') + '" style="' + (field.width ? 'width: ' + field.width + ';' : '') + '">' +
            '<select name="' + field.id + '" class="demo-default" id="selector_' + field.id + '">' + '</select>' +
            '' +
            '</div>' +
            '</td>');
        $(formNode).append(selector);

        selector.find('select').selectize({
            maxItems: field.multiple ? 1000 : 1,
            plugins: ['restore_on_backspace', 'remove_button'],
            valueField: 'id',
            labelField: 'title',
            searchField: 'title',
            preload: true,
            closeAfterSelect: true,
            options: options,
            load: function(query, callback) {
                $.getJSON('app/dictionary', {
                    id: field.dictionary,
                    query: query
                },  function(response) {
                    callback(response);
                }).fail(function() {
                    $.notify({message: 'Не удалось загрузить данные для справочника.'},{type: 'danger', delay: 0, timer: 0});
                });
            },
            create: false
        });
        selector.find('select')[0].selectize.setValue(initialValues);
    },

    createEditorUser: function(formNode, field, value, valueTitle) {
        if( Object.prototype.toString.call(value) !== '[object Array]' ) {
            value = value ? [value] : [];
            valueTitle = valueTitle ? [valueTitle] : [];
        }

        var options = [];
        var initialValues = [];
        var titles = [];

        for (var i = 0; i < value.length; i++) {
            options.push({
                id: value[i],
                title: valueTitle[i]
            });
            initialValues.push(value[i]);
            titles.push(valueTitle[i]);
        }

        var selector = $('<td><div class="no-dropdown' + (field.mandatory ? ' formGroupRequired' : '') + '" style="' + (field.width ? 'width: ' + field.width + ';' : '') + '">' +
            '<select name="' + field.id + '" class="demo-default" id="selector_' + field.id + '">' + '</select>' +
            '' +
            '</div>' +
            '</td>');
        $(formNode).append(selector);

        selector.find('select').selectize({
            maxItems: field.multiple ? 1000 : 1,
            plugins: ['restore_on_backspace', 'remove_button'],
            valueField: 'id',
            labelField: 'title',
            searchField: 'title',
            preload: true,
            closeAfterSelect: true,
            options: options,
            load: function(query, callback) {
                $.getJSON('app/dictionary', {
                    id: 'organizationPersons',
                    query: query
                },  function(response) {
                    callback(response);
                }).fail(function() {
                    $.notify({message: 'Не удалось загрузить данные для справочника. Обратитесь к администратору.'},{type: 'danger', delay: 0, timer: 0});
                });
            },
            create: false
        });
        selector.find('select')[0].selectize.setValue(initialValues);
    },

    createColumnsSelector: function(element) {
        var selector = $('<div class="dropdown btn-group">'+
            '<button type="button" style="" class="btn btn-default btn dropdown-toggle" data-toggle="dropdown">Колонки <span class="caret"></span></button>'+
            '<ul name="columns" class="dropdown-menu">'+
            '</ul>'+
            '</div>');

        var ul = selector.find('[name="columns"]');

        for (var i = 0; i < this.options.columns.length; i++) {
            var column = this.options.columns[i];
            if (column.type != "hidden") {
                var li = $('<li><a href="#" class="" data-value="' + column.id + '" tabIndex="-1"><input type="checkbox"/>&nbsp;' + column.title + '</a></li>');
                var inp = li.find("input");
                if (this.options.columnsVisible.indexOf(column.id) > -1) {
                    inp.prop('checked', true);
                }
                ul.append(li);
            }
        }

        selector.appendTo(element);
        $(selector).find('.dropdown-menu a').on('click', $.proxy(function(event) {

            var $target = $(event.currentTarget),
                val = $target.attr('data-value'),
                $inp = $target.find('input'),
                idx;

            if (( idx = this.options.columnsVisible.indexOf(val) ) > -1) {
                this.options.columnsVisible.splice(idx, 1);
                setTimeout(function () {
                    $inp.prop('checked', false);
                }, 0);
            } else {
                this.options.columnsVisible.push(val);
                setTimeout(function () {
                    $inp.prop('checked', true);
                }, 0);
            }

            $(event.target).blur();

            this.reload();
            return false;
        }, this));
    }
});
$.widget('sokol.header', {
    options: {
        userName: ''
    },
    _create: function () {
        var leftMenu = $('<ul></ul>').addClass('nav navbar-nav');
        var rightMenu = $('<ul></ul>').addClass('nav navbar-nav navbar-right');

        this.createMenu(leftMenu, this.options.leftMenu);
        this.createMenu(rightMenu, this.options.rightMenu);

        //var search = $('<form class="nav navbar-form navbar-right" role="search"></form>')
        //    .append($('<div class="input-group"></div>')
        //        .append($('<input type="text" class="form-control" placeholder="Поиск" name="srch-term" id="srch-term">'))
        //        .append($('<div class="input-group-btn"></div>')
        //            .append('<button class="btn btn-default" type="submit"><i class="glyphicon glyphicon-search"></i></button>')));

        this.element.addClass('navbar navbar-inverse navbar-fixed-top')
            .append($('<div></div>').addClass('container')
                .append($('<div></div>').addClass('navbar-header')
                    .append($('<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">' +
                        '<span class="sr-only">Меню</span>' +
                        '<span class="icon-bar"></span>' +
                        '<span class="icon-bar"></span>' +
                        '<span class="icon-bar"></span>' +
                    '</button>'))
                    .append($('<a href="">Сокол СЭД</a>').addClass('navbar-brand')))
                .append($('<div id="navbar"></div>').addClass('navbar-collapse collapse')
                    .append(leftMenu)
                    .append(rightMenu))
                    //.append(search))
        );
    },
    createMenu: function (node, menu) {
        for (var i = 0; i < menu.length; i++) {
            var item = menu[i];
            var title = item.title;
            if (title == "$userName") {
                title = this.options.userName;
            }

            var produceHandler = $.proxy(function produceHandler(id) {
                return $.proxy(function handleCreateClick(e) {
                    e.preventDefault();
                    this.options.dispatcher.open(id);
                }, this)
            }, this);

            if (item.submenu) {
                var submenu = $('<ul></ul>').addClass('dropdown-menu');
                for (var j = 0; j < item.submenu.length; j++) {
                    var subitem = item.submenu[j];
                    if (subitem.separator) {
                        submenu.append($('<li role="separator" class="divider"></li>'));
                    } else {
                        if (subitem.link) {
                            submenu.append($('<li><a class="sokolHeaderMenuItem" href="' + subitem.link + '">' + subitem.title + '</a></li>'));
                        } else if (subitem.window) {
                            submenu.append($('<li><a class="sokolHeaderMenuItem" target="_blank" href="' + subitem.window + '">' + subitem.title + '</a></li>'));
                        } else if (subitem.open) {
                            var openItem = $('<li><a class="sokolHeaderMenuItem" href="">' + subitem.title + '</a></li>');
                            openItem.find('a').click(produceHandler(subitem.open));
                            submenu.append(openItem);
                        }
                    }
                }

                node.append($('<li></li>').addClass('dropdown')
                    .append($('<a href="#">' + title + ' <span class="caret"></a>').addClass('dropdown-toggle').attr('data-toggle', 'dropdown'))
                    .append(submenu));
            } else {
                node.append($('<li><a href="' + item.link + '">' + title + '</a></li>'));
            }
        }
    },
    _setOption: function (key, value) {
        this._super(key, value);
    },
    _setOptions: function (options) {
        this._super(options);
    }
});
$.widget('sokol.history', {
    options: {

    },

    _create: function () {
        this.createHeader();
        this.createHistoryList();
    },

    createHeader: function() {
        this.element.addClass('panel panel-default');

        var panelHeader = $('<div class="panel-heading"></div>').appendTo(this.element);

        var panelTitle = $('<div class="panel-title">История изменений</div>').appendTo(panelHeader);

        var panelBody = $('<div style="height: 10px;"></div>');
        panelBody.appendTo(this.element);
        this.panelBody = panelBody;
    },

    _destroy: function() {
        this.element.detach();
    },

    createHistoryList: function(listData, objectId) {
        var columns = [
            {
                id: 'date',
                title: 'Дата изменения',
                render: 'datetime'
            },
            {
                id: 'userTitle',
                title: 'Пользователь'
            },
            {
                id: 'type',
                title: 'Тип',
                render: 'history',
                dataColumn: 'fields'
            }
            //,
            //{
            //    id: 'fields',
            //    title: 'Информация',
            //    render: 'history'
            //    //dataColumn: 'fullInfo'
            //}
        ];

        var options = {
            title: 'История изменений',
            //columnsVisible: ['date', 'userTitle', 'type', 'fields'],
            columns: columns,
            url: 'app/history',
            id: this.options.id,
            //filterable: true,
            sortable: false,
            pageSize: 5,
            showTableHeader: false,
            usePanel: false,
            bottomPagination: false,
            topBorder: true
        };

        this.grid = $.sokol.grid(options, $("<div></div>").appendTo(this.element));
    }
});
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
$.widget('sokol.list', {
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
        $.getJSON('app/config', {id: 'navigation/main'}, $.proxy(function(data) {
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
        if (this.options.id) {
            this.createGrid(this.options.id);
        }
    },

    createGrid: function(id) {
        var fullId = 'lists/' + id + 'List';
        if (this.grid) {
            this.grid.destroy();
        }
        this.sidebar.find('li').removeClass('active');
        setTimeout($.proxy(function() {
            this.sidebar.find('[name="category_' + id + '"]').addClass('active');
        }, this), 0);
        $.getJSON('app/config', {id: fullId},
            $.proxy(function (data) {
                var options = {
                    title: data.title,
                    columnsVisible: data.columnsVisible,
                    columns: data.columns,
                    url: data.url ? data.url : 'app/documents',
                    id: id,
                    filterable: true,
                    sortable: true,
                    sortColumn: data.sortColumn,
                    sortAsc: data.sortAsc
                };
                this.grid = $.sokol.grid(options, $("<div></div>").appendTo(this.main));
                if (this.options.dispatcher) {
                    this.options.dispatcher.updateHash('lists/' + id);
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
$.widget('sokol.smodal', {
    options: {
        title: '',
        body: '',
        confirmButtonTitle: null,
        confirmAction: null,
        data: null
    },
    _create: function () {
        this.createModal();
    },
    createModal: function() {
        var modal = $('<div class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">' +
            '    <div class="modal-dialog" role="document">' +
            '        <div class="modal-content">' +
            '            <div class="modal-header">' +
            '                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>' +
            '                <h4 class="modal-title" id="myModalLabel">' + this.options.title + '</h4>' +
            '            </div>' +
            '            <div class="modal-body">' +
            this.options.body +
            '            </div>' +
            '            <div class="modal-footer">' +
            '                <button type="button" class="btn btn-default" data-dismiss="modal">' + (this.options.confirmButtonTitle ? 'Отмена' : 'ОК') + '</button>' +
            (this.options.confirmButtonTitle ? '                <button type="button" name="confirmButton" class="btn btn-danger">' + this.options.confirmButtonTitle + '</button>' : '') +
            '            </div>' +
            '        </div>' +
            '    </div>' +
            '</div>');
        modal.find('[name="confirmButton"]').click($.proxy(function confirmModal() {
            modal.modal('hide');
            this.options.confirmAction(this.options.data);
        }, this));
        modal.modal();
    }
});
$.widget('sokol.profile', {
    options: {

    },
    _create: function () {
        this.createHeader();
        this.createBlock();
    },
    _destroy: function() {
        this.element.detach();
    },

    createHeader: function() {
        this.element.addClass('panel panel-default');
        this.element.attr('name', 'attachmentsPanel');

        var panelHeader = $('<div class="panel-heading"></div>').appendTo(this.element);

        var panelTitle = $('<div class="panel-title">Профиль пользователя</div>').appendTo(panelHeader);

        var panelBody = $('<div class="panel-body"></div>');
        panelBody.appendTo(this.element);
        this.panelBody = panelBody;
    },

    createBlock: function () {
        if (this.form) {
            this.form.destroy();
        }
        this.form = $.sokol.form({
            mode: "edit",
            data: this.options.data ? this.options.data : {},
            form: {
                "id": "userSystem",
                "fields": [
                    {
                        "id": "oldPassword",
                        "title": "Старый пароль",
                        "type": "password",
                        "mandatory": true
                    },
                    {
                        "id": "newPassword",
                        "title": "Новый пароль",
                        "type": "password",
                        "mandatory": true
                    },
                    {
                        "id": "newPasswordConfirm",
                        "title": "Новый пароль подтверждение",
                        "type": "password",
                        "mandatory": true
                    },
                    {
                        "id": "newPasswordButton",
                        "title": "Сохранить",
                        "type": "button",
                        "method": "saveProfile",
                        "class": "btn-danger"
                    }
                ]
            },
            usePanel: false,
            dispatcher: this,
            containerType: 'profile'
        }, $('<div></div>').appendTo(this.panelBody));

        //this.createButtons();
    },

    saveProfile: function() {
        if (!this.form.validateForm()) {
            return;
        }

        var data = this.form.getData();
        var saveUrl = 'app/saveProfile';
        var message = 'Не удалось сохранить профиль пользователя.';

        $.post(saveUrl, JSON.stringify(data), $.proxy(function (id) {
            $.notify({message: 'Сохранено'}, {type: 'success', delay: 1000, timer: 1000});
            this.options.dispatcher.open('profile');
        }, this)).fail($.proxy(function() {
            $.notify({message: message},{type: 'danger', delay: 0, timer: 0});
        }, this));
    }
});

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
        //if (this.options.id) {
        //    this.createReportForm(id);
        //} else {
            this.info = $('<div class="jumbotron" role="alert"><div class="container">Выберите отчет</div></div>').appendTo(this.main);
        //}

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

            this.attaches = $.sokol.attachesGrid({mode: 'edit', id: res.attachesId}, $('<div></div>').appendTo(this.main));
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
$.widget('sokol.search', {
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
        $.getJSON('app/config', {id: 'navigation/search'}, $.proxy(function(data) {
            this.data = data;
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

                    category.find('a').click(produceHandler(item));
                    if (item.id == 'all') {
                        category.addClass('active');
                    }
                }
            }, this));
        }, this));

        this.text = $('<form style="margin-top: 10px; margin-bottom: 10px;">' +
            '<div class="form-group">' +
            '<label>Поисковый запрос:</label>' +
            '<input name="searchtext" class="form-control">' +
            '</div>' +
            '<button type="button" class="btn btn-default">Найти</button>' +
            '</form>').appendTo(this.main);
        this.text.find("button").click($.proxy(function() {
            if (!this.options.id || this.options.id == 'all') {
                this.createGrid('all');
            } else {
                if (this.grid) {
                    var searchtext = this.text.find('input[name="searchtext"]').val();
                    this.grid.options.searchtext = searchtext;
                    this.grid.reload();

                    var category = this.sidebar.find('li');
                    var a = category.find('a');
                    a.find('span').remove();
                }
            }
        }, this));
    },

    //    $.getJSON('app/config', {id: 'navigation/search'}, $.proxy(function(data) {
    //        var categoryAll = $('<ul class="nav nav-sidebar"><li style="font-weight: bold;"><div class="checkbox">' +
    //            '<label>' +
    //            '<input type="checkbox" value="" checked="true">' +
    //            'Все' +
    //            '</label>' +
    //            '</div></li></ul>').appendTo(sidebar);
    //        categoryAll.find("input").click(function() {
    //            var checked = categoryAll.find('input').prop('checked');
    //            var inputs = sidebar.find('input.categoryCheckBox');
    //            inputs.prop('checked', checked ? 'checked' : '');
    //        });
    //        data.items.forEach($.proxy(function (item) {
    //            if (item.type == 'header') {
    //                if (item.title) {
    //                    var header = $('<ul class="nav nav-sidebar"><li style="font-weight: bold;" name="category_' + item.id + '"><a href="">' + item.title + '</a></li></ul>').appendTo(sidebar);
    //                    currentNode = header;
    //                    header.find("a").click(produceHandler(item));
    //                } else {
    //                    var block = $('<ul class="nav nav-sidebar"></ul>').appendTo(sidebar);
    //                    currentNode = block;
    //                }
    //            } else {
    //                if (!currentNode) {
    //                    currentNode = $('<ul class="nav nav-sidebar"></ul>').appendTo(sidebar);
    //                }
    //                var category = $('<li><div class="checkbox">' +
    //                    '<label>' +
    //                    '<input type="checkbox" class="categoryCheckBox" value="" checked="true">' +
    //                     item.title +
    //                '</label>' +
    //                '</div></li>').appendTo(currentNode);
    //                //var category = $('<li name="category_' + item.id + '"><span>' + item.title + '</span></li>').appendTo(currentNode);
    //                category.find("input").click(function() {
    //                    var checked = $(this).prop('checked');
    //                    categoryAll.find('input').prop('checked', '');
    //                });
    //            }
    //        }, this));

    createGrid: function(id) {
        if (this.grid) {
            this.grid.destroy();
        }
        if (this.grids) {
            this.grids.forEach(function(g) {
                g.destroy();
            });
        }
        if (this.info) {
            this.info.remove();
        }

        this.sidebar.find('li').removeClass('active');
        this.options.id = id;

        setTimeout($.proxy(function() {
            var category = this.sidebar.find('[name="category_' + id + '"]');
            category.addClass('active');
        }, this), 0);

        if (id == 'all') {
            this.createAllGrid();
            return;
        }

        var searchId = 'search' + id.substring(0, 1).toUpperCase() + id.substring(1);
        var fullId = 'lists/' + searchId + 'List';

        $.getJSON('app/config', {id: fullId},
            $.proxy(function (data) {
                var options = {
                    title: data.title,
                    columnsVisible: data.columnsVisible,
                    columns: data.columns,
                    url: data.url ? data.url : 'app/documents',
                    id: searchId,
                    filterable: true,
                    sortable: true
                };
                var searchtext = this.text.find('input[name="searchtext"]').val();
                options.searchtext = searchtext;
                this.grid = $.sokol.grid(options, $("<div></div>").appendTo(this.main));

            }, this)
        ).fail(function failLoadList() {
                $.notify({message: 'Не удалось загрузить список "' + id + '". Обратитесь к администратору.'},{type: 'danger', delay: 0, timer: 0});
            });
    },

    addGrid: function(id, el) {
        var searchId = 'search' + id.substring(0, 1).toUpperCase() + id.substring(1);
        var fullId = 'lists/' + searchId + 'List';
        this.grids = [];
        $.getJSON('app/config', {id: fullId},
            $.proxy(function (data) {
                var options = {
                    title: data.title,
                    columnsVisible: data.columnsVisible,
                    columns: data.columns,
                    url: data.url ? data.url : 'app/documents',
                    id: searchId,
                    filterable: true,
                    sortable: true,
                    pageSize: 5,
                    noControls: true
                };
                var searchtext = this.text.find('input[name="searchtext"]').val();
                options.searchtext = searchtext;
                options.dataLoadedCallback = $.proxy(function(grid) {
                    var category = this.sidebar.find('[name="category_' + id + '"]');
                    var a = category.find('a');
                    a.find('span').remove();
                    a.html(a.html() + ' <span class="badge">' + grid.options.total + '</span>');
                }, this);
                var grid = $.sokol.grid(options, el);
                this.grids.push(grid);
            }, this)
        ).fail(function failLoadList() {
                $.notify({message: 'Не удалось загрузить список "' + id + '". Обратитесь к администратору.'},{type: 'danger', delay: 0, timer: 0});
            });
    },

    createAllGrid: function() {
        var searchtext = this.text.find('input[name="searchtext"]').val();
        if (searchtext) {
            if (this.data.items) {
                this.data.items.forEach($.proxy(function (item) {
                    if (item.id && item.id != 'all') {
                        this.addGrid(item.id, $("<div></div>").appendTo(this.main));
                    }
                }, this));
            }
        }
    },

    _destroy: function() {
        this.element.detach();
    }
});
$.widget('sokol.titleHeader', {
    options: {
        title: null,
        subtitle: null,
        status: null
    },
    _create: function () {
        this.element.addClass('panel-body');
        $('<h3>' + (this.options.title ? this.options.title : '') +
            '</h3>' +
            (this.options.subtitle ? ('<h4 class="">' + this.options.subtitle + '</h4>') : '')+
            (this.options.status ? '<div>Статус записи: ' + this.options.status  + '</div>' : '')
            ).appendTo(this.element);
    },
    _destroy: function() {
        this.element.detach();
    }
});
$.widget('sokol.userHeader', {
    options: {
        form: null,
        data: null
    },
    _create: function () {
        var form = this.options.form;
        var data = this.options.data;
        this.element.addClass('panel-body');
        $('<h3>' + (data.title ? data.title : '') +
            '</h3>' +
            (data.lastName ? ('<h4 class="">' + data.lastName + ' ' + data.middleName + ' ' + data.firstName + '</h4>') : '')+
            '<div>Статус записи: ' + (data.status ? data.status : '') + '</div>'
            ).appendTo(this.element);
    },
    _destroy: function() {
        this.element.detach();
    }
});
//# sourceMappingURL=app.js.map