$.widget("sokolui.form", {
    options: {
        form: {
            fields: []
        },
        data: {}
    },
    _create: function() {
        this.panel = $("<div>").addClass("panel-body").appendTo(this.element);

        this._render();
    },

    _render: function() {
        var form = $("<form>").attr("name", "mainForm").appendTo(this.panel);
        var group = $("<div>").addClass("form-group").appendTo(form);
        
        this.options.form.fields.forEach($.proxy(function(f) {
            this._renderField(group, f, this.options.data, false);
        }, this));
    },

    _renderField: function(formNode, field, data, edit) {
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
                this._renderField(td, subfield, data, edit);
            }
            var ctd = $('<div style="clear: both" />');
            ctd.appendTo(row);
            return;
        }

        var id = field.id;
        var value = data[id];
        var valueTitle = data[id + "Title"];
        var type = field.type;

        //this.fieldsInfo.push(field);
        //this.fieldsInfoMap[id] = field;

        if (!value && value !== 0) {
            value = "";
        }
        edit =  edit && !(field.ar == 'read');

        var fieldConstructor = $.sokolui[type + "Field"];
        if (fieldConstructor) {
            fieldConstructor({field: field, value: value, valueTitle: valueTitle, edit: true}, $("<div>").appendTo(formNode))
        }
    }
});