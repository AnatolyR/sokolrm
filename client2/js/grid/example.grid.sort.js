examples["grid.sort"] = function() {
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
    $.sokolui.grid({
        columns: [
            {"id": "num", "title": "#", "sortable": true},
            {"id": "first", "title": "First"},
            {"id": "last", "title": "Last", "sortable": true},
            {"id": "handle", "title": "Handle"}
        ],
        datasource: datasource
    }, $("<div>").addClass("container-fluid example").appendTo("body"));  
};