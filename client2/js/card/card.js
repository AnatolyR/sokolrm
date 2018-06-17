$.widget("sokolui.card", {
    options: {
        buttons: [],
        header: {}
    },
    _create: function() {
        this.container = $("<div>").addClass("container s-card").css("margin-bottom", "1em").appendTo(this.element);
        
        this._render();
    },
    
    _render: function() {
        this._renderButtons();
        this._renderHeader();

        this._renderContent();
    },
    
    _renderButtons: function() {
        var buttonsPanel = $("<div>").addClass("s-buttons-panel").attr("name", "formButtonsPanel").appendTo(this.container);
        this.buttonsPanel = buttonsPanel;
        this.options.buttons.forEach(function(b) {
            $("<button>").attr("type", "button")
                .attr("name", b.name)
                .addClass("btn btn-default s-button-action")
                .text(b.title).appendTo(buttonsPanel);
        });
        
    },
    
    _renderHeader: function() {
        var header = $("<div>").addClass("panel-body s-header").appendTo(this.container);
        this.header = header;
        var headerData = this.options.header;
        var mainTitle = $("<h3>").text(headerData.title).appendTo(header);
        var subTitle = $("<h4>").text(headerData.subTitle).appendTo(header);
        var additionalText = $("<div>").text(headerData.additionalText).appendTo(header);
    },
    
    _renderContent: function() {
        this._renderTabSelector();  
        this._renderTabPlaceholders();
        this._renderActiveTab();
    },
    
    _renderTabSelector: function() {
        var tabPanel = $("<ul>").addClass("nav nav-tabs").attr("role", "tablist").appendTo(this.container);
        this.tabPanel = tabPanel;
        this.options.tabs.forEach(function(t) {
            var tab = $("<li>").attr("role", "presentation").appendTo(tabPanel);
            var tabLink = $("<a>").attr("href", "#" + t.id)
                .attr("aria-controls", t.id).attr("role", "tab")
                .attr("data-toggle", "tab").text(t.title).appendTo(tab);
            if (t.active) {
                tab.addClass("active");
            }
        });
    },
    
    _renderTabPlaceholders: function() {
        var tabs = $("<div>").addClass("tab-content").appendTo(this.container);
        this.options.tabs.forEach(function(t) {
            var tab = $("<div>").attr("id", t.id).attr("role", "tabpanel").addClass("tab-pane").appendTo(tabs);
            if (t.active) {
                tab.addClass("active");
            }
        });  
    },
    
    _renderActiveTab: function() {
        var t = this.options.tabs.find(function(t) {return t.active == true});
        if (t && t.stype) {
            var widget = $.sokolui[t.stype];
            var options = t.options || {};
            options.data = this.options.data;
            widget(options, $("<div>").appendTo(this.container.find(".tab-pane.active")));
        }
    }
});