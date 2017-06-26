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

import org.openqa.selenium.*;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.List;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 22.03.2017
 */
public class TestService {
    private static final Logger log = LoggerFactory.getLogger(TestService.class);
    private static TestService instance;

    private Robot robot;
    private RemoteWebDriver driver;

//    public static String screenPath = "/Users/anatolii/Documents/sokolsed/tests/";
    public static String screenPath = "/Users/anatolii/Desktop/tests";

    public static TestService getInstance() throws InterruptedException, AWTException, MalformedURLException {
        if (instance == null) {
            instance = new TestService();
            instance.create();
            instance.login("ivashov", "123");
            Thread.sleep(3000);
        } else {
            instance.open();
        }
        return instance;
    }

    public static TestService getInstanceWithoutLogin() throws InterruptedException, AWTException, MalformedURLException {
        if (instance == null) {
            instance = new TestService();
            instance.create();
        } else {
            instance.open();
        }
        return instance;
    }

    public static TestService getInstance(String user, String pass) throws InterruptedException, AWTException, MalformedURLException {
        if (instance == null) {
            instance = new TestService();
            instance.create();
            instance.login(user, pass);
            Thread.sleep(3000);
        } else {
            instance.open();
        }
        return instance;
    }

    private void create() throws InterruptedException, AWTException, MalformedURLException {
        System.setProperty("apple.awt.UIElement", "true");

        ChromeOptions options = new ChromeOptions();
//        options.addArguments("user-data-dir=" + profilePath);
//        options.addArguments("--start-maximized");
        options.addArguments("disable-infobars");
        options.addArguments("enable-automation");

        Map<String, Object> prefs = new LinkedHashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        options.setExperimentalOption("prefs", prefs);

        DesiredCapabilities capability = DesiredCapabilities.chrome();

        capability.setBrowserName("chrome");
        capability.setCapability(ChromeOptions.CAPABILITY, options);

        driver = new RemoteWebDriver(new URL("http://127.0.0.1:9515/"), capability);
        driver.manage().window().setPosition(new Point(100, 100));
        driver.manage().window().setSize(new Dimension(1400, 900));
        Thread.sleep(1000);

        robot = new Robot();
        altTab();

        open();

//        screen(robot, driver);
        //Thread.sleep(4000);

//        click("Исходящий 1", null, false);
    }

    private void open() throws InterruptedException {
//        Set<String> windowHandles = driver.getWindowHandles();
//        boolean moreThanOneList
//        for (String windowHandle : windowHandles) {
//            driver.switchTo().window(windowHandle);
//            String currentUrl = driver.getCurrentUrl();
//            if (currentUrl.contains("#lists/documents")) {
//                driver.close();
//            }
//        }
        driver.get("http://localhost:8080/sokol");
//        driver.navigate().refresh();
        Thread.sleep(2000);
        Set<String> windowHandles = driver.getWindowHandles();
        for (String windowHandle : windowHandles) {
            driver.switchTo().window(windowHandle);
            String currentUrl = driver.getCurrentUrl();
            if (currentUrl.contains("#lists/documents")) {
                break;
            }
        }
        Thread.sleep(1000);
    }

    public void login(String userData, String passwordData) {
        WebElement username = driver.findElement(By.name("user"));
        WebElement password = driver.findElement(By.name("password"));
        WebElement submitButton = driver.findElement(By.id("submitButton"));

        username.sendKeys(userData);
        password.sendKeys(passwordData);

        submitButton.click();
    }

    public boolean click(String text, String clazzWanted, boolean contains) throws InterruptedException {
        text = text.trim();
        log.info("Click '" + text + "'");
        java.util.List<WebElement> elements = contains ?
            driver.findElements(By.xpath("//*[contains(text(), '" + text + "')]"))
            : driver.findElements(By.xpath("//*[text() = '" + text + "']"));
        WebElement clickable = null;
        for (WebElement element : elements) {
            if (element.isDisplayed()) {
                logElement("Finding to click ", element);
                if (clazzWanted == null) {
                    clickable = element;
                    break;
                } else {
                    List<String> clazz = element.getAttribute("class") != null ? Arrays.asList(element.getAttribute("class").split(" ")) : Collections.<String>emptyList();
                    if (clazz.contains(clazzWanted)) {
                        clickable = element;
                        break;
                    }
                }
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
                //clickable = element;
            }
        }
//        if (clickable == null) {
//            for (WebElement element : elements) {
//                driver.executeScript("arguments[0].scrollIntoView(true);", element);
//                Thread.sleep(500);
//                driver.executeScript("window.scrollBy(0, -60);");
//                Thread.sleep(500);
//                logElement("Finding to click ", element);
//                if (clazzWanted == null) {
//                    clickable = element;
//                    break;
//                } else {
//                    List<String> clazz = element.getAttribute("class") != null ? Arrays.asList(element.getAttribute("class").split(" ")) : Collections.<String>emptyList();
//                    if (clazz.contains(clazzWanted)) {
//                        clickable = element;
//                        break;
//                    }
//                }
//            }
//        }
        logElement("Clickable ", clickable);
        if (clickable != null) {
            clickable.click();
            return true;
        }
        return false;
    }

    public WebElement elementByXpath(String xpath) {
        return driver.findElement(By.xpath(xpath));
    }

    public java.util.List<WebElement> elementsByXpath(String xpath) {
        return driver.findElements(By.xpath(xpath));
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
            log.info("element is null");
        } else {
            String clazz = element.getAttribute("class");
            String id = element.getAttribute("id");
            String tag = element.getTagName();
            log.info(message + "\n<" + tag + " id=\"" + id + "\" class=\"" + clazz + "\">");
        }
    }

    public void altTab() throws AWTException {
        robot.keyPress(KeyEvent.VK_META);
        robot.keyPress(KeyEvent.VK_SHIFT);
        robot.keyPress(KeyEvent.VK_TAB);
        robot.delay(200);
        robot.keyRelease(KeyEvent.VK_TAB);
        robot.keyRelease(KeyEvent.VK_SHIFT);
        robot.keyRelease(KeyEvent.VK_META);
    }

    public void screen() throws InterruptedException, IOException {
        Thread.sleep(2000);

        WebDriver.Window window = driver.manage().window();
        BufferedImage bufferedImage = robot.createScreenCapture(new Rectangle(window.getPosition().getX(), window.getPosition().getY(), window.getSize().getWidth(), window.getSize().getHeight()));

        File file = new File(screenPath, UUID.randomUUID().toString() + ".png");
        ImageIO.write(bufferedImage, "png", new FileOutputStream(file));
        log.info("Save screen to '{}'", file.getAbsolutePath());
    }

    public RemoteWebDriver getDriver() {
        return driver;
    }

    public void addAttach() {
        robot.mouseMove(690, 248);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        robot.mouseMove(1106, 532);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
    }

    public void fillSelect(String selectName, String value, boolean clear) throws InterruptedException {
        WebElement select = this.elementByXpath("//*[@name = '" + selectName + "']");
        WebElement div = select.findElement(By.xpath(".."));
        WebElement input = div.findElement(By.xpath(".//input"));
        if (clear) {
            input.sendKeys("\b");
        }
        input.sendKeys(value);
        Thread.sleep(1000);
        this.click(value, "highlight", true);
        Thread.sleep(500);
    }

    public void fillSimpleSelect(String selectName, String value) throws InterruptedException {
        WebElement select = this.elementByXpath("//*[@name = '" + selectName + "']");
        WebElement div = select.findElement(By.xpath(".."));
        WebElement button = div.findElement(By.xpath(".//button"));
        
        button.click();
        
        this.click(value, "text", false);
        
        Thread.sleep(1000);
    }

    public void fillNSimpleSelect(String selectName, int n, String value) throws InterruptedException {
        List<WebElement> selects = this.elementsByXpath("//*[@name = '" + selectName + "']");
        WebElement select = selects.get(n);
        WebElement div = select.findElement(By.xpath(".."));
        WebElement button = div.findElement(By.xpath(".//button"));

        button.click();

        this.click(value, "text", false);

        Thread.sleep(1000);
    }

    public void fillFirstString(String name, String value) {
        WebElement input = this.elementsByXpath("//*[@name = '" + name + "']").get(0);
        input.sendKeys(value);
    }

    public void fillFirstSelect(String selectName, String value) throws InterruptedException {
        WebElement select = this.elementsByXpath("//*[@name = '" + selectName + "']").get(0);
        WebElement div = select.findElement(By.xpath(".."));
        WebElement input = div.findElement(By.xpath(".//input"));
        input.sendKeys(value);
        Thread.sleep(1000);
        this.click(value, "highlight", true);
        Thread.sleep(500);
    }

    public void fillString(String name, String value, boolean clear) {
        WebElement input = this.elementByXpath("//*[@name = '" + name + "']");
        if (clear) {
            input.clear();
        }
        input.sendKeys(value);
    }

    public void fillNString(String name, int n, String value, boolean clear) {
        List<WebElement> inputs = this.elementsByXpath("//*[@name = '" + name + "']");
        WebElement input = inputs.get(n);
        if (clear) {
            input.clear();
        }
        input.sendKeys(value);
    }
}
