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