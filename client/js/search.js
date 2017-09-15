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
$.widget('sokol.search', {
    options: {

    },

    _create: function () {
        var row = $('<div class="row"></div>').appendTo(this.element);
        var sidebar = $('<div class="col-sm-3 col-md-2 sidebar"></div>').appendTo(row);
        var main = $('<div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2"></div>').appendTo(row);
        this.main = main;
        this.sidebar = sidebar;
        var currentNode = null;

        var produceHandler = $.proxy(function produceHandler(item) {
            return $.proxy(function handleCategoryClick(e) {
                e.preventDefault();
                this.createGrid(item.id);
            }, this)
        }, this);
        $.getJSON('app/config', {id: 'navigation/search'}, $.proxy(function(data) {
            this.data = data;
            data.items.forEach($.proxy(function (item) {
                if (item.type == 'header') {
                    if (item.title) {
                        var header = $('<ul class="nav nav-sidebar"><li style="font-weight: bold;" name="category_' + item.id + '"><a href="">' + item.title + '</a></li></ul>').appendTo(sidebar);
                        currentNode = header;
                        header.find("a").click(produceHandler(item));
                    } else {
                        var block = $('<ul class="nav nav-sidebar"></ul>').appendTo(sidebar);
                        currentNode = block;
                    }
                } else {
                    if (!currentNode) {
                        currentNode = $('<ul class="nav nav-sidebar"></ul>').appendTo(sidebar);
                    }
                    var category = $('<li name="category_' + item.id + '"><a href="" class="sokolSearchCategoryItem">' + item.title + '</a></li>').appendTo(currentNode);

                    category.find('a').click(produceHandler(item));
                    if (item.id == 'all') {
                        category.addClass('active');
                    }
                }
            }, this));
        }, this));

        this.text = $('<form style="margin-top: 10px; margin-bottom: 10px;">' +
            '<div class="form-group">' +
            '<label>Поисковый запрос:</label>' +
            '<input name="searchtext" class="form-control">' +
            '</div>' +
            '<button type="button" class="btn btn-default">Найти</button>' +
            '</form>').appendTo(this.main);
        var doSearch = $.proxy(function() {
            if (!this.options.id || this.options.id == 'all') {
                this.createGrid('all');
            } else {
                if (this.grid) {
                    var searchtext = this.text.find('input[name="searchtext"]').val();
                    this.grid.options.searchtext = searchtext;
                    this.grid.reload();

                    var category = this.sidebar.find('li');
                    var a = category.find('a');
                    a.find('span').remove();
                }
            }
        }, this);
        this.text.find("button").click(doSearch);
        this.text.keypress(
            function (event) {
                if (event.which == '13') {
                    event.preventDefault();
                    doSearch();
                }
            }
        );
    },

    //    $.getJSON('app/config', {id: 'navigation/search'}, $.proxy(function(data) {
    //        var categoryAll = $('<ul class="nav nav-sidebar"><li style="font-weight: bold;"><div class="checkbox">' +
    //            '<label>' +
    //            '<input type="checkbox" value="" checked="true">' +
    //            'Все' +
    //            '</label>' +
    //            '</div></li></ul>').appendTo(sidebar);
    //        categoryAll.find("input").click(function() {
    //            var checked = categoryAll.find('input').prop('checked');
    //            var inputs = sidebar.find('input.categoryCheckBox');
    //            inputs.prop('checked', checked ? 'checked' : '');
    //        });
    //        data.items.forEach($.proxy(function (item) {
    //            if (item.type == 'header') {
    //                if (item.title) {
    //                    var header = $('<ul class="nav nav-sidebar"><li style="font-weight: bold;" name="category_' + item.id + '"><a href="">' + item.title + '</a></li></ul>').appendTo(sidebar);
    //                    currentNode = header;
    //                    header.find("a").click(produceHandler(item));
    //                } else {
    //                    var block = $('<ul class="nav nav-sidebar"></ul>').appendTo(sidebar);
    //                    currentNode = block;
    //                }
    //            } else {
    //                if (!currentNode) {
    //                    currentNode = $('<ul class="nav nav-sidebar"></ul>').appendTo(sidebar);
    //                }
    //                var category = $('<li><div class="checkbox">' +
    //                    '<label>' +
    //                    '<input type="checkbox" class="categoryCheckBox" value="" checked="true">' +
    //                     item.title +
    //                '</label>' +
    //                '</div></li>').appendTo(currentNode);
    //                //var category = $('<li name="category_' + item.id + '"><span>' + item.title + '</span></li>').appendTo(currentNode);
    //                category.find("input").click(function() {
    //                    var checked = $(this).prop('checked');
    //                    categoryAll.find('input').prop('checked', '');
    //                });
    //            }
    //        }, this));

    createGrid: function(id) {
        if (this.grid) {
            this.grid.destroy();
        }
        if (this.grids) {
            this.grids.forEach(function(g) {
                g.destroy();
            });
        }
        if (this.info) {
            this.info.remove();
        }

        this.sidebar.find('li').removeClass('active');
        this.options.id = id;

        setTimeout($.proxy(function() {
            var category = this.sidebar.find('[name="category_' + id + '"]');
            category.addClass('active');
        }, this), 0);

        if (id == 'all') {
            this.createAllGrid();
            return;
        }

        var searchId = 'search' + id.substring(0, 1).toUpperCase() + id.substring(1);
        var fullId = 'lists/' + searchId + 'List';

        $.getJSON('app/config', {id: fullId},
            $.proxy(function (data) {
                var options = {
                    title: data.title,
                    columnsVisible: data.columnsVisible,
                    columns: data.columns,
                    url: data.url ? data.url : 'app/documents',
                    id: searchId,
                    filterable: true,
                    sortable: true
                };
                var searchtext = this.text.find('input[name="searchtext"]').val();
                options.searchtext = searchtext;
                this.grid = $.sokol.grid(options, $("<div></div>").appendTo(this.main));

            }, this)
        ).fail(function failLoadList() {
                $.notify({message: 'Не удалось загрузить список "' + id + '". Обратитесь к администратору.'},{type: 'danger', delay: 0, timer: 0});
            });
    },

    addGrid: function(id, el) {
        var searchId = 'search' + id.substring(0, 1).toUpperCase() + id.substring(1);
        var fullId = 'lists/' + searchId + 'List';
        this.grids = [];
        $.getJSON('app/config', {id: fullId},
            $.proxy(function (data) {
                var options = {
                    title: data.title,
                    columnsVisible: data.columnsVisible,
                    columns: data.columns,
                    url: data.url ? data.url : 'app/documents',
                    id: searchId,
                    filterable: true,
                    sortable: true,
                    pageSize: 5,
                    noControls: true
                };
                var searchtext = this.text.find('input[name="searchtext"]').val();
                options.searchtext = searchtext;
                options.dataLoadedCallback = $.proxy(function(grid) {
                    var category = this.sidebar.find('[name="category_' + id + '"]');
                    var a = category.find('a');
                    a.find('span').remove();
                    a.html(a.html() + ' <span class="badge">' + grid.options.total + '</span>');
                }, this);
                var grid = $.sokol.grid(options, el);
                this.grids.push(grid);
            }, this)
        ).fail(function failLoadList() {
                $.notify({message: 'Не удалось загрузить список "' + id + '". Обратитесь к администратору.'},{type: 'danger', delay: 0, timer: 0});
            });
    },

    createAllGrid: function() {
        var searchtext = this.text.find('input[name="searchtext"]').val();
        if (searchtext) {
            if (this.data.items) {
                this.data.items.forEach($.proxy(function (item) {
                    if (item.id && item.id != 'all') {
                        this.addGrid(item.id, $("<div></div>").appendTo(this.main));
                    }
                }, this));
            }
        }
    },

    _destroy: function() {
        this.element.detach();
    }
});