/*
 * Copyright 2017 Anatolii Rakovskii (rtolik@yandex.ru)
 *
 * No part of this file can be copied or reproduced without written permission of author.
 *
 * Software distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */
package com.kattysoft;

import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.awt.*;
import java.net.MalformedURLException;
import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 24.03.2017
 */
public class ListDocumentsIT {
    private TestService ts;

    @DataProvider(name = "documents")
    public Object[][] createData1() {
        return new Object[][]{
            {
                "documents",
                "Найдено: 41 Предыдущая \n" +
                    "1 / 3\n" +
                    " 1 / 3\n" +
                    "2 / 3\n" +
                    "3 / 3\n" +
                    " Следующая  Отображать\n" +
                    "20\n" +
                    " 5\n" +
                    "20\n" +
                    "50\n" +
                    "100\n" +
                    " Колонки\n" +
                    "Фильтр",
                "Документы\n" +
                    "Номер Тип Статус Заголовок Дата регистрации\n" +
                    "ВХ-166 Входящий Рассмотрение Отчет о закупках 31.05.2017 15:04\n" +
                    "Исходящий Согласовано Договор о сотрудничестве\n" +
                    "ВХ-164 Входящий Рассмотрение Относительно стоимости 29.05.2017 17:04\n" +
                    "ВХ-163 Входящий Зарегистрирован Об оформлении стенда 29.05.2017 16:04\n" +
                    "ВХ-162 Входящий Зарегистрирован Архивная справка 29.05.2017 12:03\n" +
                    "ВХ-161 Входящий Зарегистрирован Предложения по сотрудничеству 28.05.2017 14:05\n" +
                    "Исходящий Согласование Об оформлении разовых пропусков\n" +
                    "ВХ-160 Входящий Исполнение О результатах проверки 26.05.2017 18:07\n" +
                    "ВХ-159 Входящий Зарегистрирован Письмо: О графике работы 26.05.2017 15:03\n" +
                    "ВХ-158 Входящий Зарегистрирован О направлении отчета 25.05.2017 18:08\n" +
                    "ВХ-167 Входящий Исполнение Относительно согласования договора 31.05.2017 15:27\n" +
                    "ВХ-157 Входящий Исполнение Счет-фактура за обслуживание 25.05.2017 17:08\n" +
                    "Исходящий На подписании О разрешении прохода\n" +
                    "ВХ-155 Входящий Исполнено О приостановке действия договора 24.05.2017 18:08\n" +
                    "ВХ-156 Входящий Зарегистрирован Запрос данных для выполнения работ 24.05.2017 19:16\n" +
                    "ВХ-154 Входящий В дело Исполненный акт выполненных работ 23.05.2017 16:03\n" +
                    "ВХ-153 Входящий Рассмотрение Подтверждение юридического и почтового адресов 23.05.2017 15:18\n" +
                    "ВХ-152 Входящий Зарегистрирован Об объемах работ выполняемых по Договору №557 от 23.04.2017 23.05.2017 11:18\n" +
                    "Исходящий Согласование Об оформлении документов\n" +
                    "ВХ-151 Входящий Зарегистрирован О стоимости оборудования 22.05.2017 17:04"
            },
            {
                "incomingDocument",
                "Найдено: 31 Предыдущая \n" +
                    "1 / 2\n" +
                    " 1 / 2\n" +
                    "2 / 2\n" +
                    " Следующая  Отображать\n" +
                    "20\n" +
                    " 5\n" +
                    "20\n" +
                    "50\n" +
                    "100\n" +
                    " Колонки\n" +
                    "Фильтр",
                "Входящие документы\n" +
                    "Номер Тип Статус Заголовок Дата регистрации\n" +
                    "ВХ-149 Входящий Зарегистрирован Отчет об оказанных услугах №10 19.05.2017 11:02\n" +
                    "ВХ-150 Входящий Зарегистрирован Разрешение на провоз 19.05.2017 22:18\n" +
                    "ВХ-137 Входящий Зарегистрирован О направлении информации 05.05.2017 11:02\n" +
                    "ВХ-140 Входящий Зарегистрирован Отчет субподрядчика 08.05.2017 11:03\n" +
                    "ВХ-162 Входящий Зарегистрирован Архивная справка 29.05.2017 12:03\n" +
                    "ВХ-141 Входящий Зарегистрирован О датах изготовления печатных материалов 11.05.2017 12:04\n" +
                    "ВХ-151 Входящий Зарегистрирован О стоимости оборудования 22.05.2017 17:04\n" +
                    "ВХ-160 Входящий Исполнение О результатах проверки 26.05.2017 18:07\n" +
                    "ВХ-167 Входящий Исполнение Относительно согласования договора 31.05.2017 15:27\n" +
                    "ВХ-141 Входящий Зарегистрирован О реализации оборудования 11.05.2017 14:02\n" +
                    "ВХ-156 Входящий Зарегистрирован Запрос данных для выполнения работ 24.05.2017 19:16\n" +
                    "ВХ-165 Входящий Рассмотрение Отчет о закупках 31.05.2017 14:54\n" +
                    "ВХ-159 Входящий Зарегистрирован Письмо: О графике работы 26.05.2017 15:03\n" +
                    "ВХ-154 Входящий В дело Исполненный акт выполненных работ 23.05.2017 16:03\n" +
                    "ВХ-161 Входящий Зарегистрирован Предложения по сотрудничеству 28.05.2017 14:05\n" +
                    "ВХ-163 Входящий Зарегистрирован Об оформлении стенда 29.05.2017 16:04\n" +
                    "ВХ-143 Входящий Зарегистрирован О направлении копий документов 12.05.2017 13:04\n" +
                    "ВХ-164 Входящий Рассмотрение Относительно стоимости 29.05.2017 17:04\n" +
                    "ВХ-158 Входящий Зарегистрирован О направлении отчета 25.05.2017 18:08\n" +
                    "ВХ-138 Входящий Зарегистрирован Отчет об оказанных услугах 05.05.2017 17:08"
            },
            {
                "outgoingDocuments",
                "Найдено: 10 \n" +
                    "  Отображать\n" +
                    "20\n" +
                    " 5\n" +
                    "20\n" +
                    "50\n" +
                    "100\n" +
                    " Колонки\n" +
                    "Фильтр",
                "Исходящие документы\n" +
                    "Номер Тип Статус Заголовок Дата регистрации\n" +
                    "Исходящий Согласование Об оформлении документов\n" +
                    "Исходящий Проект Результаты проверки отчетов\n" +
                    "Исходящий Проект О направлении счет-фактуры\n" +
                    "Исходящий Проект О выписке счета\n" +
                    "Исходящий Проект О направлении документов\n" +
                    "Исходящий Проект Запрос дополнительных документов\n" +
                    "Исходящий Проект Заявка: Выдача пропуска\n" +
                    "Исходящий На подписании О разрешении прохода\n" +
                    "Исходящий Согласование Об оформлении разовых пропусков\n" +
                    "Исходящий Согласовано Договор о сотрудничестве"
            },
            {
                "internalDocuments",
                "Найдено: 0 \n" +
                    "  Отображать\n" +
                    "20\n" +
                    " 5\n" +
                    "20\n" +
                    "50\n" +
                    "100\n" +
                    " Колонки\n" +
                    "Фильтр",
                "Внутренние документы\n" +
                    "Номер Тип Статус Заголовок Дата регистрации"
            }
        };
    }

    @BeforeClass
    public void before() throws InterruptedException, AWTException, MalformedURLException {
//        ts = TestService.getInstance();
    }

    @Test(dataProvider = "documents")
    public void listDocuments(String listId, String listButtons, String listContent) throws InterruptedException, MalformedURLException, AWTException {
        ts = TestService.getInstance();
        System.out.println("===================================");
        System.out.println(Thread.currentThread().getId() + " listDocument " + new Date());
        System.out.println("===================================");
//        ts.click(documentTitle, null, false);
//        Thread.sleep(2000);

//        RemoteWebDriver driver = ts.getDriver();
//        Set<String> windowHandles = driver.getWindowHandles();
//        for (String windowHandle : windowHandles) {
//            driver.switchTo().window(windowHandle);
//            String currentUrl = driver.getCurrentUrl();
//            if (currentUrl.contains("#document/")) {
//                break;
//            }
//        }

        String categoryName = "category_" + listId;
        WebElement listsMenuItem = ts.elementByXpath("//*[contains(@name, '" + categoryName + "')]");
        listsMenuItem.click();

        WebElement tableButtons = ts.elementByXpath("//*[contains(@name, 'tableButtons')]");
        System.out.println("\nTable buttons [" + listId + "]:\n" + tableButtons.getText());
        assertThat(tableButtons.getText(), equalTo(listButtons));

        WebElement tablePanel = ts.elementByXpath("//*[contains(@name, 'tablePanel')]");
        System.out.println("\nTable [" + listId + "]:\n" + tablePanel.getText());
        assertThat(tablePanel.getText(), equalTo(listContent));
    }
}
