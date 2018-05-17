examples["grid.columns"] = function() {
    $.sokolui.grid({
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
        ]
    }, $("<div>").addClass("container-fluid example").appendTo("body"));
};