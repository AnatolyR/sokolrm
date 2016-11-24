$.widget('sokol.header', {
    options: {
        userName: ''
    },
    _create: function () {
        var leftMenu = $('<ul></ul>').addClass('nav navbar-nav');
        var rightMenu = $('<ul></ul>').addClass('nav navbar-nav navbar-right');

        this.createMenu(leftMenu, this.options.leftMenu);
        this.createMenu(rightMenu, this.options.rightMenu);

        var search = $('<form class="nav navbar-form navbar-right" role="search"></form>')
            .append($('<div class="input-group"></div>')
                .append($('<input type="text" class="form-control" placeholder="Поиск" name="srch-term" id="srch-term">'))
                .append($('<div class="input-group-btn"></div>')
                    .append('<button class="btn btn-default" type="submit"><i class="glyphicon glyphicon-search"></i></button>')));

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
                    .append(rightMenu)
                    .append(search))
        );
    },
    createMenu: function (node, menu) {
        for (var i = 0; i < menu.length; i++) {
            var item = menu[i];
            var title = item.title;
            if (title == "$userName") {
                title = this.options.userName;
            }
            if (item.submenu) {
                var submenu = $('<ul></ul>').addClass('dropdown-menu');
                for (var j = 0; j < item.submenu.length; j++) {
                    var subitem = item.submenu[j];
                    if (subitem.separator) {
                        submenu.append($('<li role="separator" class="divider"></li>'));
                    } else {
                        submenu.append($('<li><a href="' + subitem.link + '">' + subitem.title + '</a></li>'));
                    }
                }

                node.append($('<li></li>').addClass('dropdown')
                    .append($('<a href="#">' + title + ' <span class="caret"></a>').addClass('dropdown-toggle').attr('data-toggle', 'dropdown'))
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