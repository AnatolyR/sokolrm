/*
 * Copyright 2016-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kattysoft;

import org.apache.commons.io.IOUtils;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 24.03.2017
 */
public class DictionaryIT {
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
    public void testReadUser1() throws InterruptedException, AWTException, IOException {
        doAssert = true;
        testReadUser("ivashov", "123", "user");
    }
    
    @Test
    public void testReadUser2() throws InterruptedException, AWTException, IOException {
        doAssert = true;
        testReadUser("lukov", "123", "user2");
    }

    @Test
    public void testReadContragent1() throws InterruptedException, AWTException, IOException {
        doAssert = true;
        testReadContragent("ivashov", "123", "contragent1");
    }

    @Test
    public void testReadContragent2() throws InterruptedException, AWTException, IOException {
        doAssert = true;
        testReadContragent("lukov", "123", "contragent2");
    }

    public void testReadContragent(String login, String pass, String contentName) throws InterruptedException, IOException, AWTException {
        ts = TestService.getInstance(login, pass);

        ts.click("Справочники", null, false);
        Thread.sleep(1000);

        ts.click("Контрагенты", "sokolDictionaryListItem", false);
        Thread.sleep(1000);

        String linkTitle = "СТРИМ, автострахование";
        if (!ts.click(linkTitle, null, false)) {
            throw new AssertionError("No object to open '" + linkTitle + "'");
        }
        Thread.sleep(2000);

        RemoteWebDriver driver = ts.getDriver();
        Set<String> windowHandles = driver.getWindowHandles();
        for (String windowHandle : windowHandles) {
            driver.switchTo().window(windowHandle);
            String currentUrl = driver.getCurrentUrl();
            if (currentUrl.contains("#contragent/")) {
                break;
            }
        }
        Thread.sleep(1000);

        try {
            String contragent = ts.elementByXpath("/html/body/div[contains(@class, 'container')]").getText();
            System.out.println("\nContragent [" + contentName + "]:\n" + contragent);
            TestService.match(doAssert, contragent, getContent(contentName));
        } finally {
            ts.getDriver().close();
            Thread.sleep(2000);
            windowHandles = ts.getDriver().getWindowHandles();
            for (String windowHandle : windowHandles) {
                ts.getDriver().switchTo().window(windowHandle);
                String currentUrl = ts.getDriver().getCurrentUrl();
                if (currentUrl.contains("#dictionaries/contragents")) {
                    break;
                }
            }
            Thread.sleep(1000);
        }
    }
    
    public void testReadUser(String login, String pass, String contentName) throws InterruptedException, IOException, AWTException {
        ts = TestService.getInstance(login, pass);
        
        ts.click("Справочники", null, false);
        Thread.sleep(1000);

        ts.click("Сотрудники", "sokolDictionaryListItem", false);
        Thread.sleep(1000);
    
        String linkTitle = "Луков В. А.";
        if (!ts.click(linkTitle, null, false)) {
            throw new AssertionError("No object to open '" + linkTitle + "'");
        }
        Thread.sleep(2000);

        RemoteWebDriver driver = ts.getDriver();
        Set<String> windowHandles = driver.getWindowHandles();
        for (String windowHandle : windowHandles) {
            driver.switchTo().window(windowHandle);
            String currentUrl = driver.getCurrentUrl();
            if (currentUrl.contains("#user/")) {
                break;
            }
        }
        Thread.sleep(1000);

        try {
            String user = ts.elementByXpath("/html/body").getText();
            System.out.println("\nUser [" + contentName + "]:\n" + user);
            TestService.match(doAssert, user, getContent(contentName));
        } finally {
            ts.getDriver().close();
            Thread.sleep(2000);
            windowHandles = ts.getDriver().getWindowHandles();
            for (String windowHandle : windowHandles) {
                ts.getDriver().switchTo().window(windowHandle);
                String currentUrl = ts.getDriver().getCurrentUrl();
                if (currentUrl.contains("#dictionaries/organizationPersons")) {
                    break;
                }
            }
            Thread.sleep(1000);
        }
    }
    
    @Test
    public void testCreateUser() throws InterruptedException, IOException, AWTException {
        doAssert = true;
        ts = TestService.getInstance("ivashov", "123");

        ts.click("Справочники", null, false);
        Thread.sleep(1000);

        ts.click("Сотрудники", "sokolDictionaryListItem", false);
        Thread.sleep(1000);

        ts.click("Создать", "btn", false);
        Thread.sleep(1000);

        RemoteWebDriver driver = ts.getDriver();
        Set<String> windowHandles = driver.getWindowHandles();
        for (String windowHandle : windowHandles) {
            driver.switchTo().window(windowHandle);
            String currentUrl = driver.getCurrentUrl();
            if (currentUrl.contains("#user/")) {
                break;
            }
        }
        Thread.sleep(1000);
        
        try {

            String user = ts.elementByXpath("/html/body/div[contains(@class, 'container')]").getText();
            System.out.println("\n[userNew]:\n" + user);
            TestService.match(doAssert, user, getContent("userNew"));

            String userTitle = "User " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddhhmmss"));

            ts.fillString("title", userTitle, false);
            ts.fillString("lastName", "Lastname", false);
            ts.fillString("firstName", "Firstname", false);
            ts.fillString("middleName", "Middlename", false);

            ts.click("Сохранить", "btn", false);
            Thread.sleep(3000);

            String testUser = ts.elementByXpath("/html/body/div[contains(@class, 'container')]").getText();
            System.out.println("\n[userTest]:\n" + testUser);
            TestService.match(doAssert, testUser, getContent("userTest").replace("Test Title", userTitle));

            if (!ts.click("Редактировать", "btn", false)) {
                throw new RuntimeException("Can not click");
            }
            Thread.sleep(1000);

            String userLogin = "testUser" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddhhmmss"));
            ts.fillString("login", userLogin, false);
            ts.fillSelect("groups", "Пользователи", false);
            Thread.sleep(1000);
            ts.getDriver().executeScript("window.scrollTo(0, 0);");
            Thread.sleep(500);
            ts.click("Сохранить", "btn", false);
            Thread.sleep(2000);
            String testUser2 = ts.elementByXpath("/html/body/div[contains(@class, 'container')]").getText();
            System.out.println("\n[userTest2]:\n" + testUser2);
            TestService.match(doAssert, testUser2, getContent("userTest2").replace("testUser", userLogin).replace("Test Title", userTitle));
        } finally {
            ts.getDriver().close();
            Thread.sleep(3000);
            windowHandles = ts.getDriver().getWindowHandles();
            for (String windowHandle : windowHandles) {
                ts.getDriver().switchTo().window(windowHandle);
                String currentUrl = ts.getDriver().getCurrentUrl();
                if (currentUrl.contains("#dictionaries/organizationPersons")) {
                    break;
                }
            }
            Thread.sleep(1000);
        }
    }

    @Test
    public void testCreateContragent() throws InterruptedException, IOException, AWTException {
        doAssert = true;
        ts = TestService.getInstance("ivashov", "123");

        ts.click("Справочники", null, false);
        Thread.sleep(1000);

        ts.click("Контрагенты", "sokolDictionaryListItem", false);
        Thread.sleep(1000);

        ts.click("Создать", "btn", false);
        Thread.sleep(1000);

        RemoteWebDriver driver = ts.getDriver();
        Set<String> windowHandles = driver.getWindowHandles();
        for (String windowHandle : windowHandles) {
            driver.switchTo().window(windowHandle);
            String currentUrl = driver.getCurrentUrl();
            if (currentUrl.contains("#contragent/")) {
                break;
            }
        }
        Thread.sleep(1000);

        try {

            String user = ts.elementByXpath("/html/body/div[contains(@class, 'container')]").getText();
            System.out.println("\n[contragentNew]:\n" + user);
            TestService.match(doAssert, user, getContent("contragentNew"));

            String contragentTitle = "Company Title" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddhhmmss"));

            ts.fillString("title", contragentTitle, false);
            ts.fillString("fullName", "Test Company", false);
            ts.fillString("address", "Russia, Moscow", false);
            ts.fillString("phone", "8(495)2003535", false);

            ts.click("Сохранить", "btn", false);
            Thread.sleep(3000);

            String testUser = ts.elementByXpath("/html/body/div[contains(@class, 'container')]").getText();
            System.out.println("\n[contragentTest]:\n" + testUser);
            TestService.match(doAssert, testUser, getContent("contragentTest").replace("Company Title", contragentTitle));

            if (!ts.click("Редактировать", "btn", false)) {
                throw new RuntimeException("Can not click");
            }
            Thread.sleep(1000);

            ts.fillString("address", ", pr. Lenina 121, o. 405", false);
            ts.getDriver().executeScript("window.scrollTo(0, 0);");
            Thread.sleep(500);
            ts.click("Сохранить", "btn", false);
            Thread.sleep(2000);
            String testUser2 = ts.elementByXpath("/html/body/div[contains(@class, 'container')]").getText();
            System.out.println("\n[contragentTest2]:\n" + testUser2);
            TestService.match(doAssert, testUser2, getContent("contragentTest2").replace("Company Title", contragentTitle));
        } finally {
            ts.getDriver().close();
            Thread.sleep(3000);
            windowHandles = ts.getDriver().getWindowHandles();
            for (String windowHandle : windowHandles) {
                ts.getDriver().switchTo().window(windowHandle);
                String currentUrl = ts.getDriver().getCurrentUrl();
                if (currentUrl.contains("#dictionaries/contragents")) {
                    break;
                }
            }
            Thread.sleep(1000);
        }
    }

    @Test
    public void testCreateDocumentKind() throws InterruptedException, IOException, AWTException {
        doAssert = true;
        ts = TestService.getInstance("ivashov", "123");

        ts.click("Справочники", null, false);
        Thread.sleep(1000);

        ts.click("Виды документов", "sokolDictionaryListItem", false);
        Thread.sleep(1000);

        ts.checkList(doAssert, "dictionaries/beforeAddDocumentKind");
        
        ts.click("Добавить", "btn", false);
        Thread.sleep(1000);

        ts.fillString("title", "Test Test Test", false).sendKeys(Keys.RETURN);

        ts.click("Виды документов", "sokolDictionaryListItem", false);
        Thread.sleep(1000);
        ts.checkList(doAssert, "dictionaries/afterAddDocumentKind");

        ts.elementByXpath("//input[@type='checkbox']").click();
        ts.click("Удалить", "btn", false);
        Thread.sleep(500);
        ts.click("Удалить", "confirmButton", false);
        Thread.sleep(500);
        ts.checkList(doAssert, "dictionaries/afterDeleteAddDocumentKind");
        ts.click("Виды документов", "sokolDictionaryListItem", false);
        Thread.sleep(1000);
        ts.checkList(doAssert, "dictionaries/afterDeleteAddDocumentKind");
    }

    private void checkButtons(String id, String buttons) {
        WebElement tableButtons = ts.elementByXpath("//*[contains(@name, 'tableButtons')]");
        System.out.println("\nTable buttons [" + id + "]:\n" + tableButtons.getText());
        buttons = ts.populate(buttons, new HashMap<>(), tableButtons.getText());
        TestService.match(doAssert, tableButtons.getText(), buttons);
    }

    public String getContent(String name) throws IOException {
        String content = IOUtils.toString(DictionaryIT.class.getResourceAsStream("/dictionaries/" + name + ".txt"));
        return content;
    }
    
    public void listDocuments(String listId, int listSize) {
        try {
            String content = IOUtils.toString(DictionaryIT.class.getResourceAsStream("/lists/" + listId + ".txt"));
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
}
