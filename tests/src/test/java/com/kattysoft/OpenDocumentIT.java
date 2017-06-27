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
import java.util.Date;
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
                "Отчет о закупках",
                "Входящий № ВХ-166 от 31.05.2017 (Отчет)\n" +
                    "Отчет о закупках\n" +
                    "Статус: Рассмотрение",
                "Основные реквизиты\n" +
                    "Заголовок:\n" +
                    "Отчет о закупках\n" +
                    "Вид документа:\n" +
                    "Отчет\n" +
                    "Адресаты:\n" +
                    "Ивашов В. Н.\n" +
                    " Корреспондент:\n" +
                    "АЛАТОО ИТЦ, электромонтажные работы\n" +
                    "Кем подписано:\n" +
                    "Исполнитель:\n" +
                    "Исходящий номер:\n" +
                    "Исходящая дата:\n" +
                    " Номер документа:\n" +
                    "ВХ-166\n" +
                    "Дата регистрации:\n" +
                    "31.05.2017 15:04\n" +
                    "Дата исполнения:\n" +
                    "Количество листов:\n" +
                    "Количество экземпляров:\n" +
                    "Количество приложений:\n" +
                    "Комментарий:\n" +
                    "Пространство:\n" +
                    "Входящие\n" +
                    "Дело:",
                null,
                "Вложения\n" +
                    "Название Размер Дата добавления Кто добавил\n" +
                    "Добавить вложение"
            },
            {
                "Договор о сотрудничестве",
                "Исходящий № - от -\n" +
                    "Договор о сотрудничестве\n" +
                    "Статус: Согласовано",
                "Основные реквизиты\n" +
                    "Заголовок:\n" +
                    "Договор о сотрудничестве\n" +
                    "Вид документа:\n" +
                    " Корреспондент:\n" +
                    "ПРОФИНТЕР ООО, реклама\n" +
                    " Подписант:\n" +
                    "Ивашов В. Н.\n" +
                    "Исполнители:\n" +
                    "Номер документа:\n" +
                    "Дата регистрации:\n" +
                    "Дата согласования:\n" +
                    "Количество листов:\n" +
                    "Количество экземпляров:\n" +
                    "Количество приложений:\n" +
                    "Комментарий:\n" +
                    "Пространство:\n" +
                    "Исходящие\n" +
                    "Дело:",
                null,
                "Вложения\n" +
                    "Название Размер Дата добавления Кто добавил\n" +
                    "Добавить вложение"
            }
        };
    }



    @BeforeClass
    public void before() throws InterruptedException, AWTException, MalformedURLException {
//        ts = TestService.getInstance();
    }

//    @Test(dataProvider = "documents") deprecated
    public void openDocument(String documentTitle, String headerContent, String mainAttributesContent,
                             String agreementContent, String attachesContent) throws InterruptedException, MalformedURLException, AWTException {
        ts = TestService.getInstance();
        System.out.println("===================================");
        System.out.println(Thread.currentThread().getId() + " openDocument " + new Date());
        System.out.println("===================================");
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


        WebElement mainPanel = ts.elementByXpath("//*[contains(@class, 'sokolMainAttributesPanel')]");
        System.out.println("\nMain attributes:\n" + mainPanel.getText());


        List<WebElement> agreementPanelList = ts.elementsByXpath("//*[contains(@class, 'sokolApprovalPanel')]");
        WebElement agreementPanel = agreementPanelList.size() > 0 ? agreementPanelList.get(0) : null;
        if (agreementContent != null) {
            System.out.println("\nAgreement:\n" + (agreementPanel != null ?agreementPanel.getText() : null));
        }

        WebElement attachesPanel = ts.elementByXpath("//*[contains(@class, 'sokolAttachesPanel')]");
        System.out.println("\nAttaches:\n" + attachesPanel.getText());

        assertThat(header.getText(), equalTo(headerContent));
        assertThat(mainPanel.getText(), equalTo(mainAttributesContent));
        if (agreementContent != null) {
            System.out.println("\nAgreement:\n" + (agreementPanel != null ?agreementPanel.getText() : null));
            assertThat(agreementPanel != null ? agreementPanel.getText() : null, equalTo(agreementContent));
        }
        assertThat(attachesPanel.getText(), equalTo(attachesContent));

        ts.getDriver().close();
        Thread.sleep(2000);
        windowHandles = driver.getWindowHandles();
        for (String windowHandle : windowHandles) {
            driver.switchTo().window(windowHandle);
            String currentUrl = driver.getCurrentUrl();
            if (currentUrl.contains("#lists/documents")) {
                break;
            }
        }
        Thread.sleep(1000);
    }
}
