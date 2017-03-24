$.widget('sokol.containerHeader', {
    options: {
        form: null,
        data: null
    },
    _create: function () {
        var form = this.options.form;
        var data = this.options.data;
        var date = data.registrationDate ? moment(data.registrationDate, 'DD.MM.YYYY HH:mm').format("L") : '-';
        this.element.addClass('panel-body');
        this.element.addClass('sokolHeaderPanel');
        $('<h3>' + (form.typeTitle ? form.typeTitle : '-') +
            ' № ' + (data.documentNumber ? data.documentNumber : '-') +
            ' от ' + date +
            (data.documentKind ? (' (' + data.documentKind + ')') : '') +
            '</h3>' +
            (data.title ? ('<h4 class="">' + data.title + '</h4>') : '')+
            '<div>Статус: ' + (data.status ? data.status : '') + '</div>'
            ).appendTo(this.element);
    },
    _destroy: function() {
        this.element.detach();
    }
});