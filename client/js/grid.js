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
        bottomPagination: true
    },

    _create: function () {
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
                searchtext: this.options.searchtext
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
            '<select name="pageSelector" class="selectpicker"></select>&nbsp;' +
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
        var table = $("<table class='table'></table>");
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
        if (this.options.selectable) {
            this.renderCheckboxColumn(row, rowObj.id);
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

            var objects = this.options.data.filter(function(element) {
                return ids.indexOf(element.id) >= 0;
            });

            if (this.options.deleteMethod && objects.length > 0) {
                this.options.deleteMethod(this, objects);
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
        var values = row.find("input").map(function(i, e) {
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
                row = $('<tr></tr>').prependTo(tbody);
                this.options.data.push(reloadValue);
                this.renderRow(row, reloadValue);
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
                    var input = $('<input name="' + colId + '" type="text">').appendTo(td);

                    input.bind("keypress", $.proxy(function(event) {
                        if(event.which == 13) {
                            event.preventDefault();
                            this.doAddElement(row);
                        }
                    }, this));

                    td.appendTo(row);
                } else if (editor == "user") {
                    this.createEditorUser(row, column);
                } else if (editor == "boolean") {
                    this.createEditorBoolean(row, column);
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