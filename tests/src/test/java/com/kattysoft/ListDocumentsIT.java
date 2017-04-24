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
                null,
                "Документы\n" +
                    "Номер Тип Статус Заголовок Дата регистрации\n" +
                    "Входящий [Заголовок не указан] 13.04.2016 22:03\n" +
                    "Входящий jksdf lsdhjf jksd gfjg 13.04.2016 22:44\n" +
                    "[test] Test document 2 09.04.2016 15:43\n" +
                    "sd23235 Входящий 1235678+ 12.11.2016 21:51\n" +
                    "Входящий В дело Супер тест 1\n" +
                    "1122 Входящий Исполнение 6tu7 13.04.2016 22:45\n" +
                    "Исходящий Согласование Исходящий 2\n" +
                    "Входящий Черновик [Заголовок не указан]\n" +
                    "ИСХ-100500 Исходящий Согласование Исходящий 1 22.03.2017 17:25\n" +
                    "ИСХ-012 Исходящий Черновик Все поля 21.03.2017 17:33\n" +
                    "Внутренний [Заголовок не указан]\n" +
                    "Исходящий Черновик [Заголовок не указан]\n" +
                    "Внутренний [Заголовок не указан]\n" +
                    "ВН-110 Внутренний Все поля 21.03.2017 17:45\n" +
                    "Входящий Рассмотрение 2222222\n" +
                    "Входящий Test document 1rtyr2355777 23.04.2016 23:32\n" +
                    "Входящий Рассмотрение Проверка Рассмотрение\n" +
                    "Входящий Зарегистрирован Проверка Зарегистрирован\n" +
                    "Входящий Черновик Проверка новых полей"
            }
        };
    }

    @BeforeClass
    public void before() throws InterruptedException, AWTException, MalformedURLException {
//        ts = TestService.getInstance();
    }

    @Test(dataProvider = "documents")
    public void listDocuments(String listId, String listContent) throws InterruptedException, MalformedURLException, AWTException {
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

        WebElement tablePanel = ts.elementByXpath("//*[contains(@name, 'tablePanel')]");
        System.out.println("\nTable:\n" + tablePanel.getText());
        assertThat(tablePanel.getText(), equalTo(listContent));
    }
}
