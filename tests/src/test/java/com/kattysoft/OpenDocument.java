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
import java.util.Set;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 22.03.2017
 */
public class OpenDocument {
    private TestService ts;

    @BeforeClass
    public void before() throws InterruptedException, AWTException, MalformedURLException {
        ts = TestService.getInstance();
    }

    @Test
    public void openDocument() throws InterruptedException {
        ts.click("Исходящий 1", null, false);
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

        System.out.println(">>>> " + driver.getCurrentUrl());
        WebElement header = ts.elementByXpath("//*[contains(@class, 'sokolHeaderPanel')]");
        System.out.println(">>> header >>>> " + header.getText());


        WebElement mainPanel = ts.elementByXpath("//*[contains(@class, 'sokolMainAttributesPanel')]");
        System.out.println(">>> mainPanel >>>> " + mainPanel.getText());

    }
}
