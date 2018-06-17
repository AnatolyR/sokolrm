$.widget("sokolui.test", {
    options: {
        
    },
    _create: function() {
        this.container = $("<div>").addClass("container").appendTo(this.element);

        this._render();
    },

    _render: function() {
        var text = $("<h2>").text("TEST").appendTo(this.container);
    }
});