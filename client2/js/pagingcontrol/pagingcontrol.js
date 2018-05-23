$.widget("sokolui.pagingcontrol", {
    options: {
        page: 1,
        pageSize: 20,
        totalSize: null
    },
    _create: function() {
        this._createControls();
        this._update();
    },
    
    _createControls: function() {
        var div = $(
            '<span style="margin-right: 10px;">Найдено: <span name="gridItemsCount">0</span></span>' +
            '<button name="preview" class="btn btn-default" href = "#" disabled1="disable">' +
            '<span class="glyphicon glyphicon-triangle-left" aria-hidden="true"></span> Предыдущая</button>&nbsp;' +
            '<select name="pageSelector" class="selectpicker"></select>&nbsp;' +
            '<button name="next" class="btn btn-default" href = "#">Следующая ' +
            '<span class="glyphicon glyphicon-triangle-right" aria-hidden="true"></span></button>&nbsp;' +
            '<span style="margin-left: 10px;">Отображать ' +
            '<select name="pageSize" class="selectpicker"><option>10</option><option>20</option><option>50</option><option>100</option></select>&nbsp;' +
            '</span>&nbsp;'
        ).appendTo(this.element);

        $(this.element).find('.selectpicker').selectpicker({
            noneSelectedText: '0 / 0',
            width: 'fit'
        });
        $(this.element).find('.selectpicker[name="pageSelector"]').on('change', {}, $.proxy(function(event){
            var selected = $(event.target).find("option:selected").val();
            var val = selected.split(" / ")[0];
            this.setPage(val);
        }, this));
        $(this.element).find('.selectpicker[name="pageSize"]').on('change', {}, $.proxy(function(event){
            var size = $(event.target).find("option:selected").val();
            this.options.pageSize = size;
            this._update();
            this._trigger("pagesizechange", event, {pageSize: size, page: this.options.page});
        }, this));
        $(this.element).find("[name='preview']").on('click', $.proxy(this.previewPage, this));
        $(this.element).find("[name='next']").on('click', $.proxy(this.nextPage, this));
    },

    setPage: function(page) {
        if (page > 0 && page < this.options.pageCount + 1) {
            this.options.page = page;
            this._trigger("pagechange", null, {page: page});
        }
    },

    nextPage: function() {
        this.setPage(this.options.page + 1)
    },

    previewPage: function() {
        this.setPage(this.options.page - 1);
    },
    
    setOption: function(key, val) {
        this.options[key] = val;
        this._update();
    },
    
    _update: function() {
        if (!this.options.totalSize) {
            return;
        }
        
        this.element.find("[name='gridItemsCount']").text(this.options.totalSize);
        var pages = this.options.totalSize / this.options.pageSize;
        var pagesInt = parseInt(pages);
        pages = pagesInt < pages ? pagesInt + 1 : pagesInt;
        this.options.pageCount = pages;
        if (this.options.page > pages) {
            this.options.page = pages;
        }
        if (this.options.pageCount > 1) {
            this.element.find("[name='preview']").show();
            this.element.find("[name='next']").show();
            this.element.find(".pageSelector").show();
        }

        var selectors = this.element.find("[name='pageSelector']");
        selectors.empty();
        for (var p = 1; p <= pages; p++) {
            selectors.append($("<option>" + p + " / " + pages + "</option>"))
        }

        if (this.options.page == 1) {
            this.element.find("[name='preview']").attr('disabled', 'disabled');
        } else {
            this.element.find("[name='preview']").removeAttr('disabled');
        }
        if (this.options.page == this.options.pageCount) {
            this.element.find("[name='next']").attr('disabled', 'disabled');
        } else {
            this.element.find("[name='next']").removeAttr('disabled');
        }
        this.element.find('select[name="pageSelector"]').val(this.options.page + " / " + this.options.pageCount);
        this.element.find('select[name="pageSize"]').val(this.options.pageSize);
        this.element.find('.selectpicker').selectpicker('refresh');

        if (this.options.pageCount <= 1) {
            this.element.find("[name='preview']").hide();
            this.element.find("[name='next']").hide();
            this.element.find(".pageSelector").hide();
        }
    }
});