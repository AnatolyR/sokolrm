$.widget('sokol.history', {
    options: {

    },

    _create: function () {
        this.createHeader();
        this.createHistoryList();
    },

    createHeader: function() {
        this.element.addClass('panel panel-default');
        this.element.attr('name', 'sokolHistoryPanel');

        var panelHeader = $('<div class="panel-heading"></div>').appendTo(this.element);

        var panelTitle = $('<div class="panel-title">История изменений</div>').appendTo(panelHeader);

        var panelBody = $('<div style="height: 10px;"></div>');
        panelBody.appendTo(this.element);
        this.panelBody = panelBody;
    },

    _destroy: function() {
        this.element.detach();
    },

    createHistoryList: function(listData, objectId) {
        var columns = [
            {
                id: 'date',
                title: 'Дата изменения',
                render: 'datetime'
            },
            {
                id: 'userTitle',
                title: 'Пользователь'
            },
            {
                id: 'type',
                title: 'Тип',
                render: 'history',
                dataColumn: 'fields'
            }
            //,
            //{
            //    id: 'fields',
            //    title: 'Информация',
            //    render: 'history'
            //    //dataColumn: 'fullInfo'
            //}
        ];

        var options = {
            title: 'История изменений',
            //columnsVisible: ['date', 'userTitle', 'type', 'fields'],
            columns: columns,
            url: 'app/history',
            id: this.options.id,
            //filterable: true,
            sortable: false,
            pageSize: 5,
            showTableHeader: false,
            usePanel: false,
            bottomPagination: false,
            topBorder: true
        };

        this.grid = $.sokol.grid(options, $("<div></div>").appendTo(this.element));
    }
});