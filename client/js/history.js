/*
 * Copyright 2016-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 */
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