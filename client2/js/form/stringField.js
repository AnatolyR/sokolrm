$.widget("sokolui.stringField", {
    options: {
        field: {},
        value: null,
        edit: false
    },
    _create: function() {
        this.group = this.element.addClass("form-group s-field");
        var field = this.options.field;
        if (field.mandatory && this.options.edit) {
            this.group.addClass("s-required");
        }
        if (field.type === "smallstring") {
            this.group.addClass("s-smallstring");
        }
        if (field.width) {
            this.group.css("width", field.width);
        }
        this._renderField();
    },

    _renderField: function() {
        this._renderLabel();
        
        if (this.options.edit) {
            this._renderControl();
        } else {
            this._renderText();
        }
    },
    
    _renderLabel: function() {
        var label = $("<label>").addClass("control-label").text(this.options.field.title).appendTo(this.group);
    },
    
    _renderControl: function() {
        var field = this.options.field;
        $("<input>").attr("name", field.id).addClass("form-control")
            .attr("type", "text").attr("disabled", field.disable)
            .val(this.options.value).appendTo(this.group);
    },
    
    _renderText: function() {
        $("<div>").text(this.options.value).appendTo(this.group);
    }
});