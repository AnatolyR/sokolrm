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

import org.apache.commons.io.IOUtils;
import org.hamcrest.MatcherAssert;
import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.HashMap;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 24.03.2017
 */
public class DictionariesIT {
    private TestService ts;
    
    private boolean doAssert = true;

    @BeforeClass
    public void before() throws InterruptedException, AWTException, MalformedURLException {
//        ts = TestService.getInstance();
    }

    @AfterTest
    public void afterTest() {
        if (!doAssert) {
            throw new RuntimeException("doAssert set to false");
        }
    }
    
    @Test
    public void testDictionaries() throws InterruptedException, IOException, AWTException {
        doAssert = true;
        ts = TestService.getInstance("ivashov", "123");

        ts.click("Справочники", null, false);
        Thread.sleep(1000);
        String body = ts.elementByXpath("/html/body").getText();
        System.out.println("\nBody:\n" + body);
        TestService.match(doAssert, body, getContent("body1"));

        ts.click("Сотрудники", "sokolDictionaryListItem", false);
        Thread.sleep(1000);
        checkList("users");

        ts.click("Контрагенты", "sokolDictionaryListItem", false);
        Thread.sleep(1000);
        checkList("contragents");

        ts.click("Виды документов", "sokolDictionaryListItem", false);
        Thread.sleep(1000);
        checkList("documentKinds");

        ts.click("Заголовки документов", "sokolDictionaryListItem", false);
        Thread.sleep(1000);
        checkList("documentTitles");

        ts.click("Статусы отчета по исполнению", "sokolDictionaryListItem", false);
        Thread.sleep(1000);
        checkList("executionReportStatus");

        ts.click("Типы связей документов", "sokolDictionaryListItem", false);
        Thread.sleep(1000);
        checkList("documentLinkTypes");
    }

    @Test
    public void testCanNotCreateUser() throws InterruptedException, IOException, AWTException {
        doAssert = true;
        ts = TestService.getInstance("lukov", "123");

        ts.click("Справочники", null, false);
        Thread.sleep(1000);

        ts.click("Сотрудники", "sokolDictionaryListItem", false);
        Thread.sleep(1000);

        checkButtons("buttons2", "Найдено: 41 Предыдущая \n" +
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
            "Фильтр");

        checkList("users");
    }

    private void checkButtons(String id, String buttons) {
        WebElement tableButtons = ts.elementByXpath("//*[contains(@name, 'tableButtons')]");
        System.out.println("\nTable buttons [" + id + "]:\n" + tableButtons.getText());
        buttons = ts.populate(buttons, new HashMap<>(), tableButtons.getText());
        TestService.match(doAssert, tableButtons.getText(), buttons);
    }

    public String getContent(String name) throws IOException {
        String content = IOUtils.toString(DictionariesIT.class.getResourceAsStream("/dictionaries/" + name + ".txt"));
        return content;
    }
    
    public void listDocuments(String listId, int listSize) {
        try {
            String content = IOUtils.toString(DictionariesIT.class.getResourceAsStream("/lists/" + listId + ".txt"));
            listDocuments(listId, listSize, content);
        } catch (IOException | AWTException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    //@Test(dataProvider = "documents")
    public void listDocuments(String listId, int listSize, String listContent) throws InterruptedException, MalformedURLException, AWTException {
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
        Thread.sleep(2000);
        String categoryName = "category_" + listId;
        WebElement listsMenuItem = ts.elementByXpath("//*[contains(@name, '" + categoryName + "')]");
        listsMenuItem.click();
        Thread.sleep(2000);

        WebElement tableButtons = ts.elementByXpath("//*[contains(@name, 'tableButtons')]");
//        System.out.println("\nTable buttons [" + listId + "]:\n" + tableButtons.getText());
//        assertThat(tableButtons.getText(), equalTo(listButtons));
        String size = tableButtons.getText().split("\\n")[0].split(" ")[1];
        System.out.println("\nTable size [" + listId + "]:\n" + size);
        assertThat(size, equalTo(Integer.toString(listSize)));

        WebElement tablePanel = ts.elementByXpath("//*[contains(@name, 'tablePanel')]");
        System.out.println("\nTable [" + listId + "]:\n" + tablePanel.getText());
        assertThat(tablePanel.getText(), equalTo(listContent));
    }

    private void checkList(String listFile) throws IOException {
        String textFromFile = IOUtils.toString(ListDocumentsFeaturesIT.class.getResourceAsStream("/dictionaries/" + listFile + ".txt"));

        WebElement tablePanel = ts.elementByXpath("//*[contains(@name, 'tablePanel')]");
        System.out.println("\nTable [" + listFile + "]:\n" + tablePanel.getText());
        String actualText = tablePanel.getText();

        TestService.match(doAssert, actualText, textFromFile);
    }
    
    
}
