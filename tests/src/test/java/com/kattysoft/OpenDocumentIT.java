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
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.*;

import java.awt.*;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 22.03.2017
 */
public class OpenDocumentIT {
    private TestService ts;

    @DataProvider(name = "documents")
    public Object[][] createData1() {
        return new Object[][]{
            {
                "Исходящий 1",
                "Исходящий № ИСХ-100500 от 22.03.2017 (Указ)\n" +
                    "Исходящий 1\n" +
                    "Статус: Согласование",
                "Основные реквизиты\n" +
                    "Заголовок:\n" +
                    "Исходящий 1\n" +
                    "Вид документа:\n" +
                    "Указ\n" +
                    " Корреспондент:\n" +
                    "ИСКУССТВО СТИЛЯ, имидж-агентство\n" +
                    " Подписант:\n" +
                    "Поляков И. В.\n" +
                    "Исполнители:\n" +
                    "Луков Б. П., Захаров Н. В.\n" +
                    "Номер документа:\n" +
                    "ИСХ-100500\n" +
                    "Дата регистрации:\n" +
                    "22.03.2017 17:25\n" +
                    "Дата согласования:\n" +
                    "23.03.2017 17:25\n" +
                    "Количество листов:\n" +
                    "1\n" +
                    "Количество экземпляров:\n" +
                    "2\n" +
                    "Количество приложений:\n" +
                    "3\n" +
                    "Комментарий:\n" +
                    "Olololo THIS IS COMMMENT!!!!!",
                "Согласование\n" +
                    "Автор:\n" +
                    "Ивашов В. Н.\n" +
                    "Дата:\n" +
                    "07.03.2017 11:23\n" +
                    "Комментарий:\n" +
                    "111\n" +
                    "Найдено: 4\n" +
                    "Колонки\n" +
                    "Согласующие\n" +
                    "Согласующий Срок Завершено Статус\n" +
                    "Ивашов В. Н. 15.03.2017 00:00\n" +
                    "Волков Б. П. 08.03.2017 00:00 07.03.2017 15:08 Согласовано\n" +
                    "Былинкин Б. Н. 18.03.2017 00:00\n" +
                    "Агапов Г. В. 17.03.2017 00:00\n" +
                    "Редактировать",
                "Вложения\n" +
                    "Название Размер Дата добавления Кто добавил\n" +
                    "71aQHzbTKlL.jpg 128 KB\n" +
                    "Добавить вложение"
            }
        };
    }



    @BeforeClass
    public void before() throws InterruptedException, AWTException, MalformedURLException {
        ts = TestService.getInstance();
    }

    @Test(dataProvider = "documents")
    public void openDocument(String documentTitle, String headerContent, String mainAttributesContent,
                             String agreementContent, String attachesContent) throws InterruptedException {
        ts.click(documentTitle, null, false);
        Thread.sleep(2000);

        RemoteWebDriver driver = ts.getDriver();
        Set<String> windowHandles = driver.getWindowHandles();
        for (String windowHandle : windowHandles) {
            driver.switchTo().window(windowHandle);
            String currentUrl = driver.getCurrentUrl();
            if (currentUrl.contains("#document/")) {
                break;
            }
        }

        WebElement header = ts.elementByXpath("//*[contains(@class, 'sokolHeaderPanel')]");
        System.out.println("\nHeader:\n" + header.getText());
        assertThat(header.getText(), equalTo(headerContent));

        WebElement mainPanel = ts.elementByXpath("//*[contains(@class, 'sokolMainAttributesPanel')]");
        System.out.println("\nMain attributes:\n" + mainPanel.getText());
        assertThat(mainPanel.getText(), equalTo(mainAttributesContent));

        List<WebElement> agreementPanelList = ts.elementsByXpath("//*[contains(@class, 'sokolApprovalPanel')]");
        WebElement agreementPanel = agreementPanelList.size() > 0 ? agreementPanelList.get(0) : null;
        if (agreementContent != null) {
            System.out.println("\nAgreement:\n" + (agreementPanel != null ?agreementPanel.getText() : null));
            assertThat(agreementPanel != null ? agreementPanel.getText() : null, equalTo(agreementContent));
        }

        WebElement attachesPanel = ts.elementByXpath("//*[contains(@class, 'sokolAttachesPanel')]");
        System.out.println("\nAttaches:\n" + attachesPanel.getText());
        assertThat(attachesPanel.getText(), equalTo(attachesContent));
    }
}
