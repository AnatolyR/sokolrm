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

import org.openqa.selenium.*;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.UUID;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 22.03.2017
 */
public class WebTest {
    private static final Logger log = LoggerFactory.getLogger(WebTest.class);

    /*
    НЕОБХОДИМ ЗАПУЩЕННЫЙ chromedriver
     */
    public static void main(String[] args) throws IOException, AWTException, InterruptedException {
        System.setProperty("apple.awt.UIElement", "true");
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();

        RemoteWebDriver driver = new RemoteWebDriver(new URL("http://127.0.0.1:9515/"), capabilities);
        driver.manage().window().setPosition(new Point(100, 100));
        driver.manage().window().setSize(new Dimension(1400, 900));
        Thread.sleep(1000);

        Robot robot = new Robot();
        altTab(robot);

        driver.get("http://localhost:8080/sokol");
        driver.navigate().refresh();
        Thread.sleep(2000);

        WebElement username = driver.findElement(By.name("user"));
        WebElement password = driver.findElement(By.name("password"));
        WebElement submitButton = driver.findElement(By.id("submitButton"));

        username.sendKeys("test");
        password.sendKeys("123");

        submitButton.click();

//        screen(robot, driver);
        Thread.sleep(4000);

        click(driver, "Исходящий 1", null, false);


//
//        (new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
//            public Boolean apply(WebDriver d) {
//                System.out.println(d.getTitle());
//                return d.getTitle().startsWith("Документы связанные со мной");
//            }
//        });
//
//        System.out.println("Page title is: " + driver.getTitle());
//        driver.quit();
    }

    private static void click(RemoteWebDriver driver, String text, String clazzWanted, boolean contains) {
        text = text.trim();
        log.info("Click '${text}'");
//        java.util.List<WebElement> elements = driver.findElements(By.xpath("//*[contains(text(), '" + text + "')]"));
        java.util.List<WebElement> elements = contains ?
            driver.findElements(By.xpath("//*[contains(text(), '" + text + "')]"))
            : driver.findElements(By.xpath("//*[text() = '" + text + "']"));
        WebElement clickable = null;
        for (WebElement element : elements) {
            if (element.isDisplayed()) {
                logElement("Finding to click ", element);
//                String clazz = element.getAttribute("class");
//                if (clazz.contains("dijitMenuItemLabel") && (clazzWanted == null || clazz.contains(clazzWanted))) {
//                    clickable = getParent(element, "dijitMenuItem");
//                    break;
//                } else if (clazz.contains("dijitButtonText") && (clazzWanted == null || clazz.contains(clazzWanted))) {
//                    clickable = getParent(element, "dijitButtonContents", "dijitButtonNode", "dijitDropDownButton | dijitButton");
//                } else if (clazz.contains("dijitButtonContents") && (clazzWanted == null || clazz.contains(clazzWanted))) {
//                    clickable = getParent(element, "dijitButtonNode", "dijitDropDownButton | dijitButton");
//                } else if (clazz.contains("dijitComboBoxHighlightMatch") && (clazzWanted == null || clazz.contains(clazzWanted))) {
//                    clickable = getParent(element, "dijitMenuItem");
//                }
                clickable = element;
            }
        }
        logElement("Clickable ", clickable);
        if (clickable != null) {
            clickable.click();
        }
    }

    private static WebElement getParent(WebElement element, String... c) {
        WebElement parent = element.findElement(By.xpath(".."));
        logElement("Parent ", parent);
        String clazz = parent.getAttribute("class");
        String c0 = c[0];
        String[] cc = c0.split("|");
        if (containsAnyOf(clazz, cc)) {
            if (c.length == 1) {
                return parent;
            } else {
                String[] cs = Arrays.copyOfRange(c, 1, c.length);
                return getParent(parent, cs);
            }
        }
        return null;
    }

    private static boolean containsAnyOf(String clazz, String[] cc) {
        for (String s : cc) {
            if (clazz.contains(s.trim())) {
                return true;
            }
        }
        return false;
    }

    private static void logElement(String message, WebElement element) {
        if (element == null) {
            log.debug("element is null");
        } else {
            String clazz = element.getAttribute("class");
            String id = element.getAttribute("id");
            String tag = element.getTagName();
            log.debug(message + "\n<" + tag + " id=\"" + id + "\" class=\"" + clazz + "\">");
        }
    }

    public static void altTab(Robot robot) throws AWTException {
        robot.keyPress(KeyEvent.VK_META);
        robot.keyPress(KeyEvent.VK_SHIFT);
        robot.keyPress(KeyEvent.VK_TAB);
        robot.delay(200);
        robot.keyRelease(KeyEvent.VK_TAB);
        robot.keyRelease(KeyEvent.VK_SHIFT);
        robot.keyRelease(KeyEvent.VK_META);
    }

    public static final String screenPath = "/Users/anatolii/Documents/sokolsed/tests/";

    private static void screen(Robot robot, RemoteWebDriver driver) throws InterruptedException, IOException {
        if (screenPath == null) {
            log.debug("Screen path is null");
            return;
        }

        Thread.sleep(2000);

        WebDriver.Window window = driver.manage().window();
        BufferedImage bufferedImage = robot.createScreenCapture(new Rectangle(window.getPosition().getX(), window.getPosition().getY(), window.getSize().getWidth(), window.getSize().getHeight()));

        File file = new File(screenPath, UUID.randomUUID().toString() + ".png");
        ImageIO.write(bufferedImage, "png", new FileOutputStream(file));
        log.info("Save screen to '{}'", file.getAbsolutePath());
    }
}
