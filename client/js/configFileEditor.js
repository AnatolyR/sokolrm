$.widget('sokol.configFileEditor', {
    options: {
        mode: 'read'
    },
    _create: function () {
        this.createHeader();
        this.createBlock();
    },

    createHeader: function() {
        this.element.addClass('panel panel-default');
        this.element.attr('name', 'sokolFileConfigPanel');

        var panelHeader = $('<div class="panel-heading"></div>').appendTo(this.element);

        this.paneTitleText = 'Файл конфигурации';
        var panelTitle = $('<div name="panelTitle" class="panel-title">' + this.paneTitleText + '</div>').appendTo(panelHeader);
        this.panelTitle = panelTitle;

        var panelBody = $('<div class="panel-body"></div>');
        panelBody.appendTo(this.element);
        this.panelBody = panelBody;       
    },

    createBlock: function () {
        if (this.options.configFileId) {
            $.getJSON('app/rawConfig', {id: this.options.configFileId}, $.proxy(function (data) {
                this.renderConfig(data);
            }, this));
        }
    },
    
    markChanges: function() {
        if (!this.markedChanges) {
            this.saveButton.attr("disabled", false);
            this.panelTitle.text(this.paneTitleText + " *");
            this.markedChanges = true;
        }
    },

    unmarkChanges: function() {
        this.saveButton.attr("disabled", true);
        this.panelTitle.text(this.paneTitleText);
        this.markedChanges = false;
    },
    
    renderConfig: function(data) {
        this.createButtons();

        var container = $('<div class="panel panel-default" id="jsoneditor" style="width: 100%; height: 600px; margin-bottom: 2px;"></div>');
        container.appendTo(this.element);
        $(container).resizable();
        
        var options = {
            mode: 'code',
            modes: ['code', 'form', 'text', 'tree', 'view'],
            onChange: $.proxy(function() {
                this.markChanges();
            }, this)
            //autocomplete: {
            //    getOptions: function (text, path, input, editor) {
            //        if (path[path.length - 1] == 'type') {
            //            return ['string', 'link', 'number'];
            //        }
            //        return [];
            //    }
            //},
            //templates: [
            //    {
            //        text: 'Field',
            //        value: {
            //            'id': '',
            //            'title': '',
            //            'type': ''
            //        }
            //    }
            //]
        };
        var editor = new JSONEditor(container.get(0), options);
        this.editor = editor;
        editor.set(data);
    },

    saveConfigFile: function() {
        var data = {};
        data.content = this.editor.get();
        data.id = this.options.configFileId;
        
        var message = 'Не удалось сохранить файл конфигурации.';

        $.post('app/saveConfig', JSON.stringify(data), $.proxy(function (response) {
            $.notify({message: 'Сохранено'}, {type: 'success', delay: 1000, timer: 1000});
            this.unmarkChanges();
        }, this)).fail($.proxy(function() {
            $.notify({message: message},{type: 'danger', delay: 0, timer: 0});
        }, this));
    },

    createButtons: function() {
        var saveButton = $('<button type="button" name="save" style="" class="btn btn-success controlElementLeftMargin">Сохранить</button>');
        saveButton.addClass('executionReportButton');
        saveButton.attr("disabled", true);
        saveButton.click($.proxy(function() {
            this.saveConfigFile();
        }, this));
        saveButton.appendTo(this.panelBody);
        this.saveButton = saveButton;

    },

    _destroy: function () {
        this.element.detach();
    }
});