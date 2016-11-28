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
        columnsVisible: []
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
        var central = this.element;
        central.empty();

        var pagination = this.createPagination(central);
        this.createColumnsSelector(pagination);

        this.renderTablePanel();
        this.reload();

        this.createPagination(central);
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

    refresh: function () {
        this.renderTableHeader();
        this.renderRows();
        this.updatePagination();
    },

    reload: function() {
        if (this.options.url) {
            $.getJSON(this.options.url, {
                id: this.options.id,
                size: this.options.pageSize,
                offset: this.options.offset
            }, $.proxy(function (data) {
                this.options.data = data.data;
                this.options.total = data.total;
                this.refresh();

            }, this));
        } else {
            this.options.total = this.options.data.length;
            this.refresh();
        }
    },

    createPagination: function(container) {
        var div = $('<div style="text-align1: center; margin-bottom: 10px;"></div>').appendTo(container);
        $(
            '<span style="margin-right: 10px;">Найдено: <span name="gridItemsCount">0</span></span>' +
            '<button name="preview" class="btn btn-default" href = "#" disabled1="disable">' +
            '<span class="glyphicon glyphicon-triangle-left" aria-hidden="true"></span> Предыдущая</button>&nbsp;' +
            '<select name="pageSelector" class="selectpicker"></select>&nbsp;' +
            '<button name="next" class="btn btn-default" href = "#">Следующая ' +
            '<span class="glyphicon glyphicon-triangle-right" aria-hidden="true"></span></button>&nbsp;' +
            '<span style="margin-left: 10px;">Отображать ' +
            '<select name="pageSize" class="selectpicker"><option>20</option><option>50</option><option>100</option></select>&nbsp;' +
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
        for (var i = 0; i < columns.length; i++) {
            var col = columns[i];
            var colType = columns[i].type;
            if (colType != "hidden" && this.isColumnVisible(col.id)) {
                var th = $("<th>" + col.title + "</th>");
                th.appendTo(header);
            }
        }
    },

    renderTablePanel: function() {
        var panel = this.element.find('[name="tablePanel"]');
        if (!panel.length) {
            panel = $('<div name="tablePanel" class="panel panel-default"></div>');
            panel.appendTo(this.element);
        } else {
            panel.empty();
        }
        var panelHeader = $('<div class="panel-heading"></div>');
        var panelTitle = $('<div class="panel-title">' + this.options.title + '</div>');
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
        return this.options.columnsVisible.indexOf(columnId) >= 0;
    },

    renderRows: function() {
        var tbody = this.element.find('tbody');
        tbody.empty();
        var rowsData = this.options.data;
        var columns = this.options.columns;
        for (var j = 0; j < rowsData.length; j++) {
            var row = $("<tr></tr>");
            var rowObj = rowsData[j];
            for (var k = 0; k < columns.length; k++) {
                var colId = columns[k].id;
                var colType = columns[k].type;
                if (colType != "hidden" && this.isColumnVisible(colId)) {
                    if (colId == "title") {
                        var td = $('<td><a href="#' + rowObj.id + '" target="_blank">' + rowObj[colId] + '</a></td>');
                        td.appendTo(row);
                    } else {
                        var val = rowObj[colId];

                        var td = $('<td>' + (val ? val : '') + '</td>');
                        td.appendTo(row);
                    }
                }
            }
            row.appendTo(tbody);
        }
    },

    createColumnsSelector: function(element) {
        var selector = $('<div class="dropdown btn-group">'+
            '<button type="button" class="btn btn-default btn dropdown-toggle" data-toggle="dropdown">Колонки <span class="caret"></span></button>'+
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