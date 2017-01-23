var primary = $.fn.filter;
$.widget('sokol.filter', {
    options: {
        columns: null,
        parent: null
    },
    _create: function () {
        this.element.addClass("collapse");
        var filterPanel = $('<div class="panel panel-default"></div>').appendTo(this.element);
        var header = $('<div class="panel-heading"><div class="panel-title">Фильтр</div></div>').appendTo(filterPanel);
        this.body = $('<div class="panel-body"></div>').appendTo(filterPanel);
        var footer = $('<div class="panel-footer"></div>').appendTo(filterPanel);


        var addButton = $('<button type="button" name="add" style="margin-right: 5px;" class="btn btn-success">Добавить условие</button>');
        addButton.click($.proxy(function() {
            this.addCondition();
        }, this));
        addButton.appendTo(footer);

        var filterButton = $('<button type="button" name="filter" style="margin-right: 5px;" class="btn btn-primary">Применить</button>');
        filterButton.click($.proxy(function() {
            this.collectData();
        }, this));
        filterButton.appendTo(footer);
    },

    collectData: function() {
        this.conditions = this.body.find('.form-inline').map(function(i, e) {
            var $e = $(e);
            var conditionVal = $e.find('[name="conditionSelector"]').val();
            var columnVal = $e.find('[name="columnSelector"]').val();
            var operationVal = $e.find('[name="operationSelector"]').val();
            var value = $e.find('[name="valueBox"]').val();
            var condition = {
                condition: conditionVal,
                column: columnVal,
                operation: operationVal,
                value: value
            };
            return condition;
        }).toArray();
        this.options.parent.reload();
    },

    addCondition: function(element) {
        var condition = $('<div class="form-inline" style="display: table; width: 100%; margin-bottom: 5px; border-collapse:separate; border-spacing:5px;"></div>');
        if (element) {
            condition.insertAfter(element);
        } else {
            condition.appendTo(this.body);
        }

        //var delButton = $('<button type="button" class="form-control btn btn-danger btn-sm tableCell" style="width: 50px;border: 2px solide red;"><span class="glyphicon glyphicon-trash"></span></button>').appendTo(condition);
        var delButton = $('<div class="btn-group tableCell" style=""><button type="button" class="form-control btn btn-danger btn-sm" >' +
            '<span class="glyphicon glyphicon-trash" ></span>' +
            '</button></div>').appendTo(condition);
        delButton.click(function() {
            condition.remove();
        });
        var addButton = $('<div class="btn-group tableCell" style=""><button type="button" class="form-control btn btn-success btn-sm" >' +
            '<span class="glyphicon glyphicon-plus" ></span>' +
            '</button></div>').appendTo(condition);
        addButton.click($.proxy(function() {
            this.addCondition(condition);
        }, this));

        var conditionSelector = $('<select name="conditionSelector" class="selectpicker tableCell"></select>').appendTo(condition);
        conditionSelector.append($('<option value="">&nbsp;</option>'));
        conditionSelector.append($('<option value="and_block">И (</option>'));
        conditionSelector.append($('<option value="or_block">ИЛИ (</option>'));
        conditionSelector.append($('<option value="end_block">)</option>'));
        conditionSelector.selectpicker({
            width: 'auto'
        });
        conditionSelector.on('change', function(event){
            var condition = $(this).find("option:selected").val();
            if (condition) {
                fieldSelector.selectpicker('hide');
                operationSelector.selectpicker('hide');
                input.hide();
            } else {
                fieldSelector.selectpicker('show');
                operationSelector.selectpicker('show');
                input.show();
            }
        });

        var fieldSelector = $('<select name="columnSelector" class="selectpicker tableCell"></select>').appendTo(condition);
        fieldSelector.append($('<option value="">&nbsp;</option>'));
        var columns = this.options.columns;
        for (var c = 0; c < columns.length; c++) {
            if (columns[c].title) {
                fieldSelector.append($('<option value="' + columns[c].id + '">' + columns[c].title + '</option>'));
            }
        }
        fieldSelector.selectpicker({
            //noneSelectedText: '',
            width: 'auto'
        });

        var operationSelector = $('<select name="operationSelector" class="selectpicker tableCell"></select>').appendTo(condition);
        operationSelector.append($('<option value="">&nbsp;</option>'));
        operationSelector.append($('<option value="EQUAL">равно</option>'));
        operationSelector.append($('<option value="NOT_EQUAL">не равно</option>'));
        operationSelector.append($('<option value="GREAT">больше</option>'));
        operationSelector.append($('<option value="LESS">меньше</option>'));
        operationSelector.append($('<option value="GREAT_OR_EQUAL">больше или равно</option>'));
        operationSelector.append($('<option value="LESS_OR_EQUAL">меньше или равно</option>'));
        operationSelector.append($('<option value="LIKE">содержит</option>'));
        operationSelector.append($('<option value="STARTS">начинается с</option>'));
        operationSelector.append($('<option value="ENDS">заканчивается на</option>'));
        operationSelector.selectpicker({
            width: 'auto'
        });

        var input = $('<div style="padding-left: 500px;"><input type="text" name="valueBox" class="form-control" style="width: 100%;"></div>').appendTo(condition);

    },

    _destroy: function() {
        this.element.detach();
    }
});
$.fn.filter = primary;