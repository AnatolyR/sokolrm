$.widget("sokolui.grid", {
    options: {
        columns: [],
        data: [],
        sort: {
            column: null,
            order: "asc"
        },
        datasource: null
    },
    _create: function() {
        this.table = $("<table>").addClass("table table-bordered").appendTo(this.element);
        this.reload();
    },
    
    render: function() {
        this.table.empty();
        this._renderHeader();
        this._renderBody();
    },
    
    reload: function() {
        var ds = this.options.datasource;
        if (ds && typeof ds === "function") {
            ds($.proxy(function(data) {
                this.options.data = data;
                this.render();
            }, this), {
                "sort": this.options.sort
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