examples["grid.paging"] = function() {
    var datasource = function(callback, opts) {
        var size = opts.size,
            offset = opts.offset,
            totalSize = 1000;
        var data = Array.apply(null, {length: size}).map(function(e, i) {var id = 1 + i + offset; return {num: id, first: "First " + id, last: "Last " + id, handle: "@hndl" + id}});
        callback(data, totalSize);
    };
    $.sokolui.grid({
        columns: [
            {"id": "num", "title": "#"},
            {"id": "first", "title": "First"},
            {"id": "last", "title": "Last"},
            {"id": "handle", "title": "Handle"}
        ],
        datasource: datasource,
        pageSize: 5,
        page: 1,
        pageSelector: true
    }, $("<div>").addClass("container-fluid example").appendTo("body"));  
};