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