$.widget("sokolui.dictionaryField", $.sokolui.stringField, {
    options: {
        field: {},
        value: null,
        valueTitle: null,
        edit: false
    },

    _renderControl: function() {
        var field = this.options.field;
        var value = this.options.value;
        var valueTitle = this.options.valueTitle;

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
        
        var selector = $("<select>").attr("name", field.id).attr("id", "selector_" + field.id).appendTo(this.group);
        
        selector.selectize({
            maxItems: field.multiple ? 1000 : 1,
            plugins: ["restore_on_backspace", "remove_button"],
            valueField: "id",
            labelField: "title",
            searchField: "title",
            preload: field.preload ? true : false,
            closeAfterSelect: true,
            options: options,
            items: initialValues,
            load: function(query, callback) {
                $.getJSON("app/dictionary", {
                    id: field.dictionary,
                    query: query
                }, callback).fail(function(a, b) {
                    console.log(a, b);
                    $.notify({message: sokol.t["select.cannot_load"]},{type: "danger", delay: 0, timer: 0});
                });
            },
            create: false
        });
        //selector[0].selectize.setValue(initialValues);
    }
});