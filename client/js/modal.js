$.widget('sokol.smodal', {
    options: {
        title: '',
        body: '',
        confirmButtonTitle: null,
        confirmAction: null,
        data: null
    },
    _create: function () {
        this.createModal();
    },
    createModal: function() {
        var modal = $('<div class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">' +
            '    <div class="modal-dialog" role="document">' +
            '        <div class="modal-content">' +
            '            <div class="modal-header">' +
            '                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>' +
            '                <h4 class="modal-title" id="myModalLabel">' + this.options.title + '</h4>' +
            '            </div>' +
            '            <div class="modal-body">' +
            this.options.body +
            '            </div>' +
            '            <div class="modal-footer">' +
            '                <button type="button" class="btn btn-default" data-dismiss="modal">' + (this.options.confirmButtonTitle ? 'Отмена' : 'ОК') + '</button>' +
            (this.options.confirmButtonTitle ? '                <button type="button" name="confirmButton" class="btn btn-danger confirmButton">' + this.options.confirmButtonTitle + '</button>' : '') +
            '            </div>' +
            '        </div>' +
            '    </div>' +
            '</div>');
        modal.find('[name="confirmButton"]').click($.proxy(function confirmModal() {
            modal.modal('hide');
            this.options.confirmAction(this.options.data);
        }, this));
        modal.modal();
    }
});