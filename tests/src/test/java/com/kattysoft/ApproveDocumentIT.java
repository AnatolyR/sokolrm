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

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.awt.*;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 24.03.2017
 */
public class ApproveDocumentIT {
    private TestService ts;

    @BeforeClass
    public void before() throws InterruptedException, AWTException, MalformedURLException {
//        ts = TestService.getInstance();
    }

//    @Test deprecated
    public void testApprove() throws InterruptedException, MalformedURLException, AWTException {
        ts = TestService.getInstance();
        System.out.println("===================================");
        System.out.println(Thread.currentThread().getId() + " approveDocument " + new Date());
        System.out.println("===================================");
        ts.click("Создать ", null, true);
        Thread.sleep(1000);

        ts.click("Входящий", "sokolHeaderMenuItem", false);
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

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("MMddHHmm");
        SimpleDateFormat dateFormat3 = new SimpleDateFormat("EE dd MMMM HH:mm", new Locale("ru"));
        Date date = new Date();

        fillString("title", "Входящий " + dateFormat3.format(date));

        fillSelect("documentKind", "Письмо");

        fillSelect("addressee", "Беломестов");

        fillSelect("correspondent", "ПРОФИНТЕР");

        fillString("externalSigner", "Заваляшин И. К.");

        fillString("externalExecutor", "Тимошев Б. В.");

        fillString("externalDate", dateFormat.format(new Date(date.getTime() - 259200000L)));

        fillString("externalNumber", "ИСХ-" + dateFormat2.format(date));

        fillString("documentNumber", "ВХ-" + dateFormat2.format(date));

        fillString("registrationDate", dateFormat.format(date));

        fillString("executionDate", dateFormat.format(new Date(date.getTime() + 259200000L)));

        fillString("pagesQuantity", "3");

        fillString("itemQuantity", "1");

        fillString("appQuantity", "2");

        fillString("comment", "Автоматически созданный входящий документ " + dateFormat3.format(date));

        ts.getDriver().executeScript("window.scrollTo(0, 0);");
        Thread.sleep(500);
        ts.click("Сохранить", "btn", true);
    }

    public void fillSelect(String selectName, String value) throws InterruptedException {
        WebElement select = ts.elementByXpath("//*[@name = '" + selectName + "']");
        WebElement div = select.findElement(By.xpath(".."));
        WebElement input = div.findElement(By.xpath(".//input"));
        input.sendKeys(value);
        Thread.sleep(1000);
        ts.click(value, "highlight", true);
        Thread.sleep(500);
    }

    public void fillString(String name, String value) {
        WebElement input = ts.elementByXpath("//*[@name = '" + name + "']");
        input.sendKeys(value);
    }
}
