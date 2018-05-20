$.widget("sokolui.checkboxdropdown", {
    options: {
        items: [],
        title: "[no title]"
    },
    _create: function() {
        var selector = $('<div class="dropdown btn-group">'+
            '<button type="button" style="" class="btn btn-default btn dropdown-toggle " data-toggle="dropdown">' +
            this.options.title +
            ' <span class="caret"></span></button>'+
            '<ul name="columns" class="dropdown-menu">'+
            '</ul>'+
            '</div>');

        var ul = selector.find('ul');

        this.options.items.forEach(function(item) {
            var li = $('<li><a href="#" class="" data-value="' +
                item.id +
                '" tabIndex="-1"><input type="checkbox" style="margin-right: 10px;"/>' +
                item.title + '</a></li>');
            var inp = li.find("input");
            if (item.checked === true) {
                inp.prop("checked", true);
            }
            ul.append(li);
        });

        selector.appendTo(this.element);
            
        var that = this;
        
        this._on(this.element, {'click .dropdown-menu a': function(event) {
            var target = $(event.currentTarget),
                itemId = target.attr("data-value"),
                checkbox = target.find("input");

            this.options.items.forEach($.proxy(function(item) {
                if (item.id == itemId) {
                    checkbox.prop("checked", !checkbox.prop("checked"));
                    item.checked = checkbox.prop("checked");
                    that._trigger("checked", event, {itemId: item.id, checked: item.checked});
                }
            }, this));

            $(event.target).blur();            
            return false;
        }});
    },
    
    update: function() {
    
    }
});