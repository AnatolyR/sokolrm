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

    $("button:contains('Колонки')").trigger("click");
    $("a:contains('First')").trigger("click");
    $("body").trigger("click");
    assert.equal(grid.element.text(), "Колонки #FirstLastHandle#Last1Otto2Thornton3the Bird", "Column with title 'First' are also hidden now");
});

QUnit.test( "Paging", function( assert ) {
    var datasource = function(callback, opts) {
        var size = opts.size,
            offset = opts.offset,
            totalSize = 100;
        var data = Array.apply(null, {length: size}).map(function(e, i) {var id = 1 + i + offset; return {num: id, first: "First " + id, last: "Last " + id, handle: "@hndl" + id}});
        callback(data, totalSize);
    };
    var grid = $.sokolui.grid({
        columns: [
            {"id": "num", "title": "#"},
            {"id": "first", "title": "First"},
            {"id": "last", "title": "Last"},
            {"id": "handle", "title": "Handle"}
        ],
        datasource: datasource,
        pageSize: 10,
        page: 1,
        pageSelector: true
    }, $("#qunit-fixture"));
    assert.equal(grid.element.text(), "Найдено: 100 Предыдущая 1 / 10 1 / 102 / 103 / 104 / 105 / 106 / 107 / 108 / 109 / 1010 / 101 / 102 / 103 / 104 / 105 / 106 / 107 / 108 / 109 / 1010 / 10 Следующая  Отображать 10 102050100102050100 #FirstLastHandle1First 1Last 1@hndl12First 2Last 2@hndl23First 3Last 3@hndl34First 4Last 4@hndl45First 5Last 5@hndl56First 6Last 6@hndl67First 7Last 7@hndl78First 8Last 8@hndl89First 9Last 9@hndl910First 10Last 10@hndl10", 
        "First page");
    var $grid = $(grid.element);
    assert.ok($grid.find("button[name='preview']").prop("disabled"), "Preview button disabled");
    assert.ok(!$grid.find("button[name='next']").prop("disabled"), "Next button enabled");

    $grid.find("button[name='next']").trigger("click");
    console.log("Second page actual", grid.element.text());
    assert.equal(grid.element.text(), "Найдено: 100 Предыдущая 2 / 10 1 / 102 / 103 / 104 / 105 / 106 / 107 / 108 / 109 / 1010 / 101 / 102 / 103 / 104 / 105 / 106 / 107 / 108 / 109 / 1010 / 10 Следующая  Отображать 10 102050100102050100 #FirstLastHandle11First 11Last 11@hndl1112First 12Last 12@hndl1213First 13Last 13@hndl1314First 14Last 14@hndl1415First 15Last 15@hndl1516First 16Last 16@hndl1617First 17Last 17@hndl1718First 18Last 18@hndl1819First 19Last 19@hndl1920First 20Last 20@hndl20",
        "Second page");
    assert.ok(!$grid.find("button[name='preview']").prop("disabled"), "Preview button enabled");
    assert.ok(!$grid.find("button[name='next']").prop("disabled"), "Next button enabled");

    $grid.find("button[name='pageSelector']").trigger("click");
    $("a:contains('4 / 10')").trigger("click");
    console.log("Forth page actual", grid.element.text());
    assert.equal(grid.element.text(), "Найдено: 100 Предыдущая 4 / 10 1 / 102 / 103 / 104 / 105 / 106 / 107 / 108 / 109 / 1010 / 101 / 102 / 103 / 104 / 105 / 106 / 107 / 108 / 109 / 1010 / 10 Следующая  Отображать 10 102050100102050100 #FirstLastHandle31First 31Last 31@hndl3132First 32Last 32@hndl3233First 33Last 33@hndl3334First 34Last 34@hndl3435First 35Last 35@hndl3536First 36Last 36@hndl3637First 37Last 37@hndl3738First 38Last 38@hndl3839First 39Last 39@hndl3940First 40Last 40@hndl40",
        "Forth page");
    assert.ok(!$grid.find("button[name='preview']").prop("disabled"), "Preview button enabled");
    assert.ok(!$grid.find("button[name='next']").prop("disabled"), "Next button enabled");

    $grid.find("button[name='preview']").trigger("click");
    console.log("Third page actual", grid.element.text());
    assert.equal(grid.element.text(), "Найдено: 100 Предыдущая 3 / 10 1 / 102 / 103 / 104 / 105 / 106 / 107 / 108 / 109 / 1010 / 101 / 102 / 103 / 104 / 105 / 106 / 107 / 108 / 109 / 1010 / 10 Следующая  Отображать 10 102050100102050100 #FirstLastHandle21First 21Last 21@hndl2122First 22Last 22@hndl2223First 23Last 23@hndl2324First 24Last 24@hndl2425First 25Last 25@hndl2526First 26Last 26@hndl2627First 27Last 27@hndl2728First 28Last 28@hndl2829First 29Last 29@hndl2930First 30Last 30@hndl30",
        "Third page");
    assert.ok(!$grid.find("button[name='preview']").prop("disabled"), "Preview button enabled");
    assert.ok(!$grid.find("button[name='next']").prop("disabled"), "Next button enabled");
    
    $grid.find("button[name='pageSelector']").trigger("click");
    $("a:contains('10 / 10')").trigger("click");
    console.log("Last page actual", grid.element.text());
    assert.equal(grid.element.text(), "Найдено: 100 Предыдущая 10 / 10 1 / 102 / 103 / 104 / 105 / 106 / 107 / 108 / 109 / 1010 / 101 / 102 / 103 / 104 / 105 / 106 / 107 / 108 / 109 / 1010 / 10 Следующая  Отображать 10 102050100102050100 #FirstLastHandle91First 91Last 91@hndl9192First 92Last 92@hndl9293First 93Last 93@hndl9394First 94Last 94@hndl9495First 95Last 95@hndl9596First 96Last 96@hndl9697First 97Last 97@hndl9798First 98Last 98@hndl9899First 99Last 99@hndl99100First 100Last 100@hndl100",
        "Last page");
    assert.ok(!$grid.find("button[name='preview']").prop("disabled"), "Preview button enabled");
    assert.ok($grid.find("button[name='next']").prop("disabled"), "Next button enabled");
});