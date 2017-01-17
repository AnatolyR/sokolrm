var modules = modules || {};

modules["dictionaries"] = (function() {
    var options = {

    };

    var testComponent = null;

    var testAll = function(completeCallback) {

        var steps = [];

        steps.push({
            step: function renderDictionaries() {
                testComponent = $.sokol.dictionaries({id: 'dictionaries/documentKinds'}, $("<div></div>").appendTo("body"));
            },
            wait: 500
        });
        steps.push({
            step: function checkDictionaries() {
                assertEquals('СотрудникиКонтрагентыВиды документовЗаголовки документовНайдено: 32УдалитьДобавитьВид документаЗнчениеАктУказОтвет на запросЖалобаЗапросЗаявкаПисьмоПриказРешениеСправкаИсковое заявлениеПовесткаПротоколПриговор судаСудебная повесткаУказание МинтрансаИзвещениеПоручениеПретензияТелеграммаТребованиеОпределениеПредписаниеПриглашениеУведомлениеРаспоряжениеКассационная жалобаПредставлениеАпелляционная жалобаПредупреждениеТелефонограммаИсполнительный лист', testComponent.element.text());
            },
            wait: 500
        });
        steps.push({
            step: function clean() {
                testComponent.destroy();
            },
            wait: 500
        });
        steps.push({
            step: function renderOrgPersons() {
                testComponent = $.sokol.dictionaries({id: 'dictionaries/organizationPersons'}, $("<div></div>").appendTo("body"));
            },
            wait: 500
        });
        steps.push({
            step: function checkOrgPersons() {
                assertEquals('СотрудникиКонтрагентыВиды документовЗаголовки документовНайдено: 55 Предыдущая 1 / 3 1 / 32 / 33 / 31 / 32 / 33 / 3 Следующая  Отображать 20 20501002050100 Колонки  Сокращенное имя Фамилия Имя ОтчествоСоздатьСотрудники организацииСокращенное имяФамилияИмяОтчествоФамилияю И. О. 1Фамилия 1Имя 1Отчество 1Фамилияю И. О. 2Фамилия 2Имя 2Отчество 2Фамилияю И. О. 3Фамилия 3Имя 3Отчество 3Фамилияю И. О. 4Фамилия 4Имя 4Отчество 4Фамилияю И. О. 5Фамилия 5Имя 5Отчество 5Фамилияю И. О. 6Фамилия 6Имя 6Отчество 6Фамилияю И. О. 7Фамилия 7Имя 7Отчество 7Фамилияю И. О. 8Фамилия 8Имя 8Отчество 8Фамилияю И. О. 9Фамилия 9Имя 9Отчество 9Фамилияю И. О. 10Фамилия 10Имя 10Отчество 10Фамилияю И. О. 11Фамилия 11Имя 11Отчество 11Фамилияю И. О. 12Фамилия 12Имя 12Отчество 12Фамилияю И. О. 13Фамилия 13Имя 13Отчество 13Фамилияю И. О. 14Фамилия 14Имя 14Отчество 14Фамилияю И. О. 15Фамилия 15Имя 15Отчество 15Фамилияю И. О. 16Фамилия 16Имя 16Отчество 16Фамилияю И. О. 17Фамилия 17Имя 17Отчество 17Фамилияю И. О. 18Фамилия 18Имя 18Отчество 18Фамилияю И. О. 19Фамилия 19Имя 19Отчество 19Фамилияю И. О. 20Фамилия 20Имя 20Отчество 20Найдено: 55 Предыдущая 1 / 3 1 / 32 / 33 / 31 / 32 / 33 / 3 Следующая  Отображать 20 20501002050100 ', testComponent.element.text());
            },
            wait: 500
        });

        doTest({destroy: function() { testComponent.destroy() }}, steps, completeCallback);
    };

    return {
        testAll: {
            title: "Тестировать все",
            action: function(completeCallback) {
                testAll(completeCallback);
            }
        },
        show: {
            title: "Отобразить",
            action: function() {
                $.sokol.dictionaries(options, $("<div></div>").appendTo("body"));
            }
        }
    }
}());

