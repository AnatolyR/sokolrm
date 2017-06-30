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
import org.openqa.selenium.Keys;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

import java.awt.*;
import java.io.IOException;
import java.util.Set;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 30.06.2017
 */
public class SearchIT {
    private TestService ts;

    private boolean doAssert = true;

    @AfterTest
    public void afterTest() {
        if (!doAssert) {
            throw new RuntimeException("doAssert set to false");
        }
    }

    @Test
    public void testAttaches() throws InterruptedException, IOException, AWTException {
        doAssert = true;
        ts = TestService.getInstance("ivashov", "123");

        ts.click("Поиск", null, false);
        Thread.sleep(1000);
        String body = ts.elementByXpath("/html/body").getText();
        System.out.println("\nBody [body1]:\n" + body);
        TestService.match(doAssert, body, getContent("body1"));

        ts.fillString("searchtext", "проверка поиска", false).sendKeys(Keys.RETURN);

        Thread.sleep(1000);
        String body2 = ts.elementByXpath("/html/body").getText();
        System.out.println("\nBody [body2]:\n" + body2);
        TestService.match(doAssert, body2, getContent("body2"));

        ts.click("Вложения", "sokolSearchCategoryItem", true);
        Thread.sleep(1000);
        String body3 = ts.elementByXpath("/html/body").getText();
        System.out.println("\nBody [body3]:\n" + body3);
        TestService.match(doAssert, body3, getContent("body3"));
        
        checkLink("Письмо: О графике работы", "document", "bodyD");
        checkLink("Относительно согласования договора", "document", "bodyT");
        checkLink("Зарегистрированные документы", "report", "bodyR");
        checkLink("Луков Б. П.", "user", "bodyU");
        checkLink("НТБ 32", "contargent", "bodyC");
        checkLink("Делопроизводители", "group", "bodyG");
    }

    @Test
    public void testDocument() throws InterruptedException, IOException, AWTException {
        doAssert = true;
        ts = TestService.getInstance("ivashov", "123");

        ts.click("Поиск", null, false);
        Thread.sleep(1000);
        String body = ts.elementByXpath("/html/body").getText();
        System.out.println("\nBody [body1]:\n" + body);
        TestService.match(doAssert, body, getContent("body1"));

        ts.fillString("searchtext", "акт", false).sendKeys(Keys.RETURN);

        Thread.sleep(1000);
        String body4 = ts.elementByXpath("/html/body").getText();
        System.out.println("\nBody [body4]:\n" + body4);
        TestService.match(doAssert, body4, getContent("body4"));

        ts.click("Документы", "sokolSearchCategoryItem", true);
        Thread.sleep(1000);
        String body5 = ts.elementByXpath("/html/body").getText();
        System.out.println("\nBody [body5]:\n" + body5);
        TestService.match(doAssert, body5, getContent("body5"));

        checkLink("О направлении акта сверки расчетов", "document", "body6");
    }

    @Test
    public void testTask() throws InterruptedException, IOException, AWTException {
        doAssert = true;
        ts = TestService.getInstance("ivashov", "123");

        ts.click("Поиск", null, false);
        Thread.sleep(1000);
        String body = ts.elementByXpath("/html/body").getText();
        System.out.println("\nBody [body1]:\n" + body);
        TestService.match(doAssert, body, getContent("body1"));

        ts.fillString("searchtext", "договор", false);
        ts.click("Найти", null, false);

        Thread.sleep(1000);
        String body7 = ts.elementByXpath("/html/body").getText();
        System.out.println("\nBody [body7]:\n" + body7);
        TestService.match(doAssert, body7, getContent("body7"));

        ts.click("Задачи", "sokolSearchCategoryItem", true);
        Thread.sleep(1000);
        String body8 = ts.elementByXpath("/html/body").getText();
        System.out.println("\nBody [body8]:\n" + body8);
        TestService.match(doAssert, body8, getContent("body8"));

        checkLink("О приостановке действия договора", "task", "body9");
    }

    @Test
    public void testUser() throws InterruptedException, IOException, AWTException {
        doAssert = true;
        ts = TestService.getInstance("ivashov", "123");

        ts.click("Поиск", null, false);
        Thread.sleep(1000);
        String body = ts.elementByXpath("/html/body").getText();
        System.out.println("\nBody [body1]:\n" + body);
        TestService.match(doAssert, body, getContent("body1"));

        ts.fillString("searchtext", "Луков", false);
        ts.click("Найти", null, false);

        Thread.sleep(1000);
        String bodyU10 = ts.elementByXpath("/html/body").getText();
        System.out.println("\nBody [bodyU10]:\n" + bodyU10);
        TestService.match(doAssert, bodyU10, getContent("bodyU10"));

        ts.click("Сотрудники", "sokolSearchCategoryItem", true);
        Thread.sleep(1000);
        String bodyU11 = ts.elementByXpath("/html/body").getText();
        System.out.println("\nBody [bodyU11]:\n" + bodyU11);
        TestService.match(doAssert, bodyU11, getContent("bodyU11"));

        checkLink("Луков Б. П.", "user", "bodyU12");
    }

    @Test
    public void testContragents() throws InterruptedException, IOException, AWTException {
        doAssert = true;
        ts = TestService.getInstance("ivashov", "123");

        ts.click("Поиск", null, false);
        Thread.sleep(1000);
        String body = ts.elementByXpath("/html/body").getText();
        System.out.println("\nBody [body1]:\n" + body);
        TestService.match(doAssert, body, getContent("body1"));

        ts.fillString("searchtext", "организация", false);
        ts.click("Найти", null, false);

        Thread.sleep(1000);
        String bodyC13 = ts.elementByXpath("/html/body").getText();
        System.out.println("\nBody [bodyC13]:\n" + bodyC13);
        TestService.match(doAssert, bodyC13, getContent("bodyC13"));

        ts.click("Контрагенты", "sokolSearchCategoryItem", true);
        Thread.sleep(1000);
        String bodyC14 = ts.elementByXpath("/html/body").getText();
        System.out.println("\nBody [bodyC14]:\n" + bodyC14);
        TestService.match(doAssert, bodyC14, getContent("bodyC14"));

        checkLink("КРОВПРОЕКТ, ООО, строительная организация", "contragent", "bodyC15");
    }

    private void checkLink(String link, String type, String fileName) throws InterruptedException, IOException {
        ts.click(link, null, false);
        Thread.sleep(2000);

        RemoteWebDriver driver = ts.getDriver();
        Set<String> windowHandles = driver.getWindowHandles();
        for (String windowHandle : windowHandles) {
            driver.switchTo().window(windowHandle);
            String currentUrl = driver.getCurrentUrl();
            if (currentUrl.contains("#" + type + "/")) {
                break;
            }
        }
        Thread.sleep(1000);

        try {
            String content = type.equals("report") ? ts.elementByXpath("/html/body").getText() : ts.elementByXpath("/html/body/div[contains(@class, 'container')]").getText();
            System.out.println("\n[" + fileName + "]:\n" + content);
            TestService.match(doAssert, content, getContent(fileName));

        } finally {
            ts.getDriver().close();
            Thread.sleep(1000);
            windowHandles = ts.getDriver().getWindowHandles();
            for (String windowHandle : windowHandles) {
                ts.getDriver().switchTo().window(windowHandle);
                String currentUrl = ts.getDriver().getCurrentUrl();
                if (currentUrl.contains("#search")) {
                    break;
                }
            }
            Thread.sleep(1000);
        }
    }

    public String getContent(String name) throws IOException {
        String content = IOUtils.toString(DictionariesIT.class.getResourceAsStream("/search/" + name + ".txt"));
        return content;
    }
}
