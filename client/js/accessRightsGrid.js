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
                if ("users" == element || "groups" == element || "registrationLists" == element || "configFiles" == element) {
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