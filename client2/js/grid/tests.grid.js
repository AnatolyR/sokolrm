QUnit.module("Grid");

QUnit.test( "Simple Grid", function( assert ) {
    var grid = $.sokolui.grid({
        columns: [
            {"id": "num", "title": "#"},
            {"id": "first", "title": "First"},
            {"id": "last", "title": "Last"},
            {"id": "handle", "title": "Handle"}
        ],
        data: [
            {"num": 1, "first": "Mark", "last": "Otto", "handle": "@mdo"},
            {"num": 2, "first": "Jacob", "last": "Thornton", "handle": "@fat"},
            {"num": 3, "first": "Larry", "last": "the Bird", "handle": "@twitter"}
        ]
    }, $("#qunit-fixture"));
    assert.equal(grid.element.text(), "#FirstLastHandle1MarkOtto@mdo2JacobThornton@fat3Larrythe Bird@twitter", "Grid text content");
});

QUnit.test( "Sorted Grid", function( assert ) {
    var data = [
        {"num": 1, "first": "Mark", "last": "Otto", "handle": "@mdo"},
        {"num": 2, "first": "Jacob", "last": "AThornton", "handle": "@fat"},
        {"num": 3, "first": "Larry", "last": "the Bird", "handle": "@twitter"}
    ];
    var datasource = function(callback, opts) {
        if (opts.sort.column) {
            var newData = data.concat().sort(function(a, b) {
                var c = a[opts.sort.column] > b[opts.sort.column];
                return opts.sort.order === "asc" ? c : !c;
            });
            callback(newData);
        } else {
            callback(data);
        }
    };
    var grid = $.sokolui.grid({
        columns: [
            {"id": "num", "title": "#", "sortable": true},
            {"id": "first", "title": "First"},
            {"id": "last", "title": "Last", "sortable": true},
            {"id": "handle", "title": "Handle"}
        ],
        datasource: datasource
    }, $("#qunit-fixture"));

    assert.equal(grid.element.text(), "#FirstLastHandle1MarkOtto@mdo2JacobAThornton@fat3Larrythe Bird@twitter", "Default grid");

    $("th:contains('#')").trigger("click");
    assert.equal(grid.element.text(), "#FirstLastHandle1MarkOtto@mdo2JacobAThornton@fat3Larrythe Bird@twitter", "Sorted by # asc");

    $("th:contains('#')").trigger("click");
    assert.equal(grid.element.text(), "#FirstLastHandle3Larrythe Bird@twitter2JacobAThornton@fat1MarkOtto@mdo", "Sorted by # desc");

    $("th:contains('Last')").trigger("click");
    assert.equal(grid.element.text(), "#FirstLastHandle2JacobAThornton@fat1MarkOtto@mdo3Larrythe Bird@twitter", "Sorted by Last asc");

    $("th:contains('Last')").trigger("click");
    assert.equal(grid.element.text(), "#FirstLastHandle3Larrythe Bird@twitter1MarkOtto@mdo2JacobAThornton@fat", "Sorted by Last desc");

    $("th:contains('Last')").trigger("click");
    assert.equal(grid.element.text(), "#FirstLastHandle1MarkOtto@mdo2JacobAThornton@fat3Larrythe Bird@twitter", "Again default");
});

QUnit.test( "Columns Visibility", function( assert ) {
    var grid = $.sokolui.grid({
        columns: [
            {"id": "num", "title": "#"},
            {"id": "first", "title": "First"},
            {"id": "last", "title": "Last"},
            {"id": "handle", "title": "Handle", "visible": false}
        ],
        data: [
            {"num": 1, "first": "Mark", "last": "Otto", "handle": "@mdo"},
            {"num": 2, "first": "Jacob", "last": "Thornton", "handle": "@fat"},
            {"num": 3, "first": "Larry", "last": "the Bird", "handle": "@twitter"}
        ],
        columnSelector: true
    }, $("#qunit-fixture"));
    assert.equal(grid.element.text(), "Колонки #FirstLastHandle#FirstLast1MarkOtto2JacobThornton3Larrythe Bird", "Last column hidden");

    $(".sokolGridColumnSelector").trigger("click");
    $(".sokolGridColumnSelectItem:contains('First')").trigger("click");
    $("body").trigger("click");
    assert.equal(grid.element.text(), "Колонки #FirstLastHandle#Last1Otto2Thornton3the Bird", "Column with title 'First' are also hidden now");
});