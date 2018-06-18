QUnit.module("Test");

QUnit.test("Simple Test", function (assert) {
    var grid = $.sokolui.test({}, $("#qunit-fixture"));
    assert.equal(grid.element.text(), "TEST", "Test text content");
});