$.widget("sokolui.grid", {
    options: {
        columns: [],
        data: [],
        sort: {
            column: null,
            order: "asc"
        },
        datasource: null,
        columnSelector: false,
        pageSelector: false,
        pageSize: 20,
        page: 1,
        totalSize: 0,
        loading: false
    },
    _create: function() {
        var panel = $("<div>").css("margin-bottom", "1em").appendTo(this.element);
        if (this.options.columnSelector) {
            this._renderColumnsSelector(panel);
        }
        if (this.options.pageSelector) {
            this._renderPageSelector(panel);
        }
        
        this.table = $("<table>").addClass("table table-bordered").appendTo(this.element);
        this.reload();
    },

    _destroy: function() {
        if (this.columnsSelector) {
            this.columnsSelector.destroy();
        }
        if (this.pageSelector) {
            this.pageSelector.destroy();
        }
    },

    _renderPageSelector: function(element) {
        this.pageSelector = $.sokolui.pagingcontrol({
            page: this.options.page,
            pageSize: this.options.pageSize
        }, element);
        this._on(this.pageSelector.element, {
            pagingcontrolpagechange: function(event, data) {
                this.options.page = data.page;
                this.reload();
            },
            pagingcontrolpagesizechange: function(event, data) {
                this.options.page = data.page;
                this.options.pageSize = data.pageSize;
                this.reload();
            }
        });
    },
    _renderColumnsSelector: function(element) {
        this.columnsSelector = $.sokolui.checkboxdropdown({
            title: sokol.t["grid.columns"],
            items: this.options.columns.map(function(col) {
                return {id: col.id, title: col.title, checked: col.visible !== false};
            })
        }, element);
       
        $(this.columnsSelector.element).on("checkboxdropdownchecked", $.proxy(function (e, data) {
            var col = this.options.columns.find(function(col) {return col.id === data.itemId});
            if (col) {
                col.visible = data.checked;
            }
            this.reload();
        }, this));
    },
    
    render: function() {
        this.table.empty();
        this._renderHeader();
        this._renderBody();
    },
    
    reload: function() {
        var opt = this.options;
        var ds = opt.datasource;
        if (ds && typeof ds === "function") {
            ds($.proxy(function(data, totalSize) {
                opt.data = data;
                opt.totalSize = totalSize;
                this.render();
                if (this.pageSelector) {
                    this.pageSelector.setOption("totalSize", totalSize);
                }
            }, this), {
                sort: opt.sort,
                size: opt.pageSize,
                offset: (opt.page - 1) * opt.pageSize
            });
        } else {
            this.render();
        }
    },

    _renderHeader: function() {
        var thead = $("<thead>").appendTo(this.table);
        var tr = $("<tr>").appendTo(thead);
        this.options.columns.forEach($.proxy(function(col) {
            if (col.visible !== false) {
                var th = $("<th>").attr("scope", "col").text(col.title).appendTo(tr);
                this._renderHeaderSort(th, col);
            }
        }, this));
    },
    
    sort: function (colId) {
        var sort = this.options.sort;
        if (sort.column == colId) {
            if (sort.order === "asc") {
                sort.order = "desc";
            } else {
                sort.column = null;
            }
        } else {
            sort.column = colId;
            sort.order = "asc";
        }
        this.reload();
    },
    
    _renderHeaderSort: function(th, col) {
        if (this.options.sort.column == col.id) {
            if (this.options.sort.order === "asc") {
                $("<i>").addClass("fas fa-caret-up").css("margin-left", "0.5em").appendTo(th);
            } else if (this.options.sort.order === "desc") {
                $("<i>").addClass("fas fa-caret-down").css("margin-left", "0.5em").appendTo(th);
            } else {
                console.error("Unknown sort order ", this.options.sort.order);
            }
        } else if (col.sortable) {
            $("<i>").addClass("fas fa-sort text-secondary").css("margin-left", "0.5em").appendTo(th);
        }
        if (col.sortable) {
            th.click($.proxy(this.sort, this, col.id));
        }
    },

    _renderBody: function() {
        var tbody = $("<tbody>").appendTo(this.table);
        this.options.data.forEach($.proxy(function (item) {
            this._renderRow(tbody, item);
        }, this));
    },

    _renderRow: function(tbody, item) {
        var tr = $("<tr>").attr("scope", "row").appendTo(tbody);
        this.options.columns.forEach(function(col) {
            if (col.visible !== false) {
                var value = item[col.id];
                var td = $("<td>").text(value).appendTo(tr);
            }
        });
    }
});