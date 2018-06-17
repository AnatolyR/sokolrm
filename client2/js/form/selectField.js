$.widget("sokolui.selectField", $.sokolui.stringField, {
    options: {
        field: {},
        value: null,
        edit: false
    },

    _renderControl: function() {
        var field = this.options.field;
        var value = this.options.value;
        var select = $("<select>").attr("name", field.id).appendTo(this.group);
        if (value) {
            $("<option>").attr("value", value).text(value).appendTo(select);
        }

        this.element.find('select').selectize({
            maxItems: 1,
            //plugins: ['remove_button'],
            valueField: field.valueField ? field.valueField : 'title',
            labelField: 'title',
            searchField: 'title',
            preload: true,
            options: field.options ? field.options : [],
            load: field.options ? null : function(query, callback) {
                $.getJSON('app/list', {
                    id: field.dictionary
                }, callback).fail(function() {
                    $.notify({message: sokol.t["select.cannot_load"]},{type: 'danger', delay: 0, timer: 0});
                });
            },
            create: false
        });
    }
});