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
$.widget('sokol.header', {
    options: {
        userName: ''
    },
    _create: function () {
        var leftMenu = $('<ul></ul>').addClass('nav navbar-nav');
        var rightMenu = $('<ul></ul>').addClass('nav navbar-nav navbar-right');

        this.createMenu(leftMenu, this.options.leftMenu);
        this.createMenu(rightMenu, this.options.rightMenu);

        //var search = $('<form class="nav navbar-form navbar-right" role="search"></form>')
        //    .append($('<div class="input-group"></div>')
        //        .append($('<input type="text" class="form-control" placeholder="Поиск" name="srch-term" id="srch-term">'))
        //        .append($('<div class="input-group-btn"></div>')
        //            .append('<button class="btn btn-default" type="submit"><i class="glyphicon glyphicon-search"></i></button>')));

        this.element.addClass('navbar navbar-inverse navbar-fixed-top')
            .append($('<div></div>').addClass('container')
                .append($('<div></div>').addClass('navbar-header')
                    .append($('<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">' +
                        '<span class="sr-only">Меню</span>' +
                        '<span class="icon-bar"></span>' +
                        '<span class="icon-bar"></span>' +
                        '<span class="icon-bar"></span>' +
                    '</button>'))
                    .append($('<a href="">Сокол СЭД</a>').addClass('navbar-brand')))
                .append($('<div id="navbar"></div>').addClass('navbar-collapse collapse')
                    .append(leftMenu)
                    .append(rightMenu))
                    //.append(search))
        );
    },
    createMenu: function (node, menu) {
        for (var i = 0; i < menu.length; i++) {
            var item = menu[i];
            var title = item.title;
            var additionalClass = '';
            if (title == "$userName") {
                title = this.options.userName;
                additionalClass = 'class="headerUserMenu"';
            }

            var produceHandler = $.proxy(function produceHandler(id) {
                return $.proxy(function handleCreateClick(e) {
                    e.preventDefault();
                    this.options.dispatcher.open(id);
                }, this)
            }, this);

            if (item.submenu) {
                var submenu = $('<ul></ul>').addClass('dropdown-menu');
                for (var j = 0; j < item.submenu.length; j++) {
                    var subitem = item.submenu[j];
                    if (subitem.separator) {
                        submenu.append($('<li role="separator" class="divider"></li>'));
                    } else {
                        if (subitem.link) {
                            submenu.append($('<li><a class="sokolHeaderMenuItem" href="' + subitem.link + '">' + subitem.title + '</a></li>'));
                        } else if (subitem.window) {
                            submenu.append($('<li><a class="sokolHeaderMenuItem" target="_blank" href="' + subitem.window + '">' + subitem.title + '</a></li>'));
                        } else if (subitem.open) {
                            var openItem = $('<li><a class="sokolHeaderMenuItem" href="">' + subitem.title + '</a></li>');
                            openItem.find('a').click(produceHandler(subitem.open));
                            submenu.append(openItem);
                        }
                    }
                }

                node.append($('<li></li>').addClass('dropdown')
                    .append($('<a href="#" ' + additionalClass + '>' + title + ' <span class="caret"></a>').addClass('dropdown-toggle').attr('data-toggle', 'dropdown'))
                    .append(submenu));
            } else {
                node.append($('<li><a href="' + item.link + '">' + title + '</a></li>'));
            }
        }
    },
    _setOption: function (key, value) {
        this._super(key, value);
    },
    _setOptions: function (options) {
        this._super(options);
    }
});