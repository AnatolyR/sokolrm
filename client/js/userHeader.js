$.widget('sokol.userHeader', {
    options: {
        form: null,
        data: null
    },
    _create: function () {
        var form = this.options.form;
        var data = this.options.data;
        this.element.addClass('panel-body');
        $('<h3>' + (data.title ? data.title : '') +
            '</h3>' +
            (data.lastName ? ('<h4 class="">' + data.lastName + ' ' + data.middleName + ' ' + data.firstName + '</h4>') : '')+
            '<div>Статус записи: ' + (data.status ? data.status : '') + '</div>'
            ).appendTo(this.element);
    },
    _destroy: function() {
        this.element.detach();
    }
});