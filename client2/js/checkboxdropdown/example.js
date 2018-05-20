examples["checkboxdropdown"] = function() {
    $.sokolui.checkboxdropdown({
        "items": [
            {"id": "1", "title": "One", "checked": true},
            {"id": "2", "title": "Two", "checked": true},
            {"id": "3", "title": "Three", "checked": false}
        ]
    }, $("<div>").addClass("container-fluid example").appendTo("body"));
};