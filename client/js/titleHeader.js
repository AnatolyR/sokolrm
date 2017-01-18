$.widget('sokol.titleHeader', {
    options: {
        title: null,
        subtitle: null,
        status: null
    },
    _create: function () {
        this.element.addClass('panel-body');
        $('<h3>' + (this.options.title ? this.options.title : '') +
            '</h3>' +
            (this.options.subtitle ? ('<h4 class="">' + this.options.subtitle + '</h4>') : '')+
            (this.options.status ? '<div>Статус записи: ' + this.options.status  + '</div>' : '')
            ).appendTo(this.element);
    },
    _destroy: function() {
        this.element.detach();
    }
});