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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.hamcrest.MatcherAssert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 10.06.2017
 */
public class DocumentIT {
    private TestService ts;
    private ObjectMapper mapper = new ObjectMapper();

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    SimpleDateFormat dateFormat2 = new SimpleDateFormat("MMddHHmm");
    SimpleDateFormat dateFormat3 = new SimpleDateFormat("EE dd MMMM HH:mm", new Locale("ru"));
    private boolean doAssert = true;

    @DataProvider(name = "flows")
    public Object[][] createData() {
        return new Object[][] {
//            {"incomingNoExecutors"},
            {"incomingTwoSuccessExecutors"}
        };
    }

    @Test
    public void testIncomingDocumentNoExecutors() throws IOException {
        doAssert = true;
        testDocument("incomingNoExecutors");
    }

    @Test
    public void testIncomingDocumentTwoSuccessExecutors() throws IOException {
        doAssert = true;
        testDocument("incomingTwoSuccessExecutors");
    }

    @Test
    public void testIncomingDocumentTwoExecutors() throws IOException {
        doAssert = true;
        testDocument("incomingTwoExecutors");
    }

    //@Test(dataProvider = "flows")
    public void testDocument(String flowName, Integer...  stepNumbers) throws IOException {
        ArrayNode steps = (ArrayNode) mapper.readTree(DocumentIT.class.getResourceAsStream("/" + flowName + ".json"));
        Map<String, Object> objects = new HashMap<>();
        objects.put("testName", flowName);
        final Integer[] currentStepNumber = {0};
        steps.forEach(s -> {
            currentStepNumber[0]++;
            if (stepNumbers.length > 0 && !ArrayUtils.contains(stepNumbers, currentStepNumber[0])) {
                return;
            }
            String action = s.get("action").asText();

            switch (action) {
                case "login":
                    try {
                        loginAction(s);
                    } catch (Exception e) {
                        throw new RuntimeException("Can not login", e);
                    }
                    break;
                case "logout":
                    try {
                        logoutAction(s);
                    } catch (Exception e) {
                        throw new RuntimeException("Can not logout", e);
                    }
                    break;
                case "sleep":
                    try {
                        Integer t = s.get("value").asInt();
                        Thread.sleep(t);
                    } catch (Exception e) {
                        throw new RuntimeException("Can not sleep", e);
                    }
                    break;
                case "close":
                    try {
                        closeAction(s);
                    } catch (Exception e) {
                        throw new RuntimeException("Can not close", e);
                    }
                    break;
                case "newDocument":
                    try {
                        newDocumentAction(s, objects);
                    } catch (Exception e) {
                        throw new RuntimeException("Can not create new document", e);
                    }
                    break;
                case "editDocument":
                    try {
                        editDocumentAction(s, objects);
                    } catch (Exception e) {
                        throw new RuntimeException("Can not edit document", e);
                    }
                    break;
                case "doAction":
                    try {
                        doActionAction(s, objects);
                    } catch (InterruptedException e) {
                        throw new RuntimeException("Can not do action", e);
                    }
                    break;
                case "execute":
                    try {
                        doExecute(s, objects);
                    } catch (InterruptedException e) {
                        throw new RuntimeException("Can not create execute", e);
                    }
                    break;
                case "openList":
                    try {
                        openList(s, objects);
                    } catch (Exception e) {
                        throw new RuntimeException("Can not open list", e);
                    }
                    break;
                case "openObject":
                    try {
                        openObject(s, objects);
                    } catch (Exception e) {
                        throw new RuntimeException("Can not open object", e);
                    }
                    break;
                case "click":
                    try {
                        doClick(s, objects);
                    } catch (Exception e) {
                        throw new RuntimeException("Can not click", e);
                    }
                    break;
                case "executeReport":
                    try {
                        doExecuteReport(s, objects);
                    } catch (Exception e) {
                        throw new RuntimeException("Can not execute report", e);
                    }
                    break;
            }
//            try {
//                if (ts != null) {
//                    ts.screen();
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!doAssert) {
            throw new RuntimeException("doAssert set to false");
        }

        //create document
        //register
        //review
        //a to case
        //b resolution
        //b1 no executors
        //b2 one executor
            //execute
            //not execute
        //b3 two executors
            //all execute
            //one execute
            //all not execute
        //? missed data
        //? missed main executor
        //to case
        //to archive

        //edit execution when executed - сделать сборос исполнености при сменен даты исполнения
        //edit execution when executing

        //просроченное исполнение
        //отдельные списки
        //

        //один исполнитель - успешно
        //два исполнителя успешно
        //без исполнителей

        //один исполнитель неуспешно
        //два исполнителя - один неуспешно
        //два исполнителя - оба неуспешно

        //один исполнитель - просрочено
    }

    private void doExecuteReport(JsonNode step, Map<String, Object> objects) throws InterruptedException {
        String status = step.get("status").asText();
        String comment = step.get("comment").asText();

        fillFirstSelect("result", status);
        fillFirstString("comment", comment);

        ts.click("Сохранить отчет", "executionReportButton", false);
        Thread.sleep(2000);

        checkDocument(step, objects);
    }

    private void doClick(JsonNode step, Map<String, Object> objects) throws InterruptedException {
        String value = step.get("value").asText();
        ts.click(value, null, false);
        Thread.sleep(1000);
    }

    private void openList(JsonNode step, Map<String, Object> objects) throws InterruptedException {
        String listId = step.get("listId").asText();
        String categoryName = "category_" + listId;
        WebElement listsMenuItem = ts.elementByXpath("//*[contains(@name, '" + categoryName + "')]");
        listsMenuItem.click();
        Thread.sleep(2000);
    }

    private void openObject(JsonNode step, Map<String, Object> objects) throws InterruptedException {
        String linkTitle = step.get("value").asText();
        linkTitle = linkTitle.replace("${date3}", objects.get("date3").toString());
        ts.click(linkTitle, null, false);
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
        Thread.sleep(1000);
        checkDocument(step, objects);
    }

    private void doActionAction(JsonNode step, Map<String, Object> objects) throws InterruptedException {
        String name = step.get("name").asText();
        ts.click(name, "documentActionButton", false);
        Thread.sleep(2000);

        ArrayNode data = (ArrayNode) step.get("objects");
        if (data != null) {
            data.forEach(f -> {
                String objectName = f.get("name").asText();
                if (f.has("type") && f.get("type").asText().equals("documentNumber")) {
                    String param = f.get("param").asText();
                    String value = getCurrentNumber(param);
                    objects.put(objectName, value);
                } else {
                    String value = f.get("value").asText();
                    objects.put(objectName, value);
                }
            });
        }

        checkDocument(step, objects);
    }

    private void doExecute(JsonNode step, Map<String, Object> objects) throws InterruptedException {
        String comment = step.get("comment").asText();

        fillString("comment", comment, false);

        ArrayNode executors = (ArrayNode) step.get("executors");
        executors.forEach(e -> {
            try {
                ts.click("Добавить ", "executionFormButtons", false);

                Thread.sleep(500);

                String user = e.get("user").asText();
                String date = e.get("date").asText();
                date = date.replace("${datePlus3d}", dateFormat.format(new Date(new Date().getTime() + 3*24*60*60*1000)));

                fillFirstSelect("userTitle", user);
                fillFirstString("dueDate", date);

            } catch (InterruptedException e1) {
                throw new RuntimeException("Can not add executor", e1);
            }
        });

        ts.click("Сохранить", "executionFormButtons", false);
        Thread.sleep(2000);

        checkDocument(step, objects);
    }

    private void newDocumentAction(JsonNode step, Map<String, Object> objects) throws InterruptedException {
        ts.click("Создать ", null, true);
        Thread.sleep(1000);

        String type = step.get("type").asText();

        ts.click(type, "sokolHeaderMenuItem", false);
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

        editDocumentAction(step, objects);
    }
    private void editDocumentAction(JsonNode step, Map<String, Object> objects) throws InterruptedException {
        Date date = new Date();
        String date3 = dateFormat3.format(date);
        objects.put("date3", date3);
        objects.put("date", dateFormat.format(date));
        objects.put("datePlus3d", dateFormat.format(new Date(date.getTime() + 3*24*60*60*1000)));


        ArrayNode data = (ArrayNode) step.get("data");

        data.forEach(f -> {
            String name = f.get("name").asText();
            String fieldType = f.get("type").asText();
            String value = f.get("value").asText();
            for (Map.Entry<String, Object> entry : objects.entrySet()) {
                value = value.replace("${" + entry.getKey() + "}", entry.getValue().toString());
            }
            boolean clear = f.has("clear") && f.get("clear").asBoolean();

            switch (fieldType) {
                case "string": fillString(name, value, clear); break;
                case "select":
                    try {
                        fillSelect(name, value, clear);
                    } catch (InterruptedException e) {
                        throw new RuntimeException("Can not fill select", e);
                    }
                    break;
            }
        });

        ts.getDriver().executeScript("window.scrollTo(0, 0);");
        Thread.sleep(500);
        ts.click("Сохранить", "btn", true);

        Thread.sleep(1000);
        checkDocument(step, objects);

    }

    private String populate(String text, Map<String, Object> objects, String actualText) {
        for (Map.Entry<String, Object> entry : objects.entrySet()) {
            text = text.replace("${" + entry.getKey() + "}", entry.getValue().toString());
        }

        List<String> ignores = new ArrayList<>();
        if (text.contains("${ignore}")) {
            String[] textLines = text.split("\n");
            String[] actualTextLines = actualText.split("\n");
            for (int i = 0; i < textLines.length; i++) {
                String textLine = textLines[i];
                String actualTextLine = actualTextLines[i];
                if (textLine.contains("${ignore}")) {
                    String[] words = textLine.split(" ");
                    String[] actualWords = actualTextLine.split(" ");
                    for (int j = 0; j < words.length; j++) {
                        String word = words[j];
                        String actualWord = j < actualWords.length ? actualWords[j] : "[no data]";
                        if (word.equals("${ignore}")) {
                            ignores.add(actualWord);
                        }
                    }
                }
            }
        }

        for (String ignore : ignores) {
            text = text.replaceFirst("\\$\\{ignore\\}", ignore);
        }

        return text;
    }

    private void checkDocument(JsonNode step, Map<String, Object> objects) {
        String testName = objects.get("testName").toString();
        if (step.has("header")) {
            String headerFileName = "/" + testName + "/" + step.get("header").asText() + ".txt";
            WebElement headerPanel = ts.elementByXpath("//*[contains(@class, 'sokolHeaderPanel')]");
            System.out.println("\nHeader [" + headerFileName + "]:\n" + headerPanel.getText());
            try {
                String header = IOUtils.toString(DocumentIT.class.getResourceAsStream(headerFileName));
                header = populate(header, objects, headerPanel.getText());
                assertThat(headerPanel.getText(), header);
            } catch (IOException e) {
                throw new RuntimeException("Can not load header file", e);
            }
        }

        if (step.has("sokolExecutionPanel")) {
            String fileName = "/" + testName + "/" + step.get("sokolExecutionPanel").asText() + ".txt";
            checkBlock(fileName, "sokolExecutionPanel", objects);
        }

        if (step.has("sokolExecutionReportPanel")) {
            String fileName = "/" + testName + "/" + step.get("sokolExecutionReportPanel").asText() + ".txt";
            checkBlock(fileName, "sokolExecutionReportPanel", objects);
        }

        if (step.has("sokolHistoryPanel")) {
            String fileName = "/" + testName + "/" + step.get("sokolHistoryPanel").asText() + ".txt";
            checkBlock(fileName, "sokolHistoryPanel", objects);
        }

        if (step.has("mainAttributes")) {
            String mainAttributesFileName = "/" + testName + "/" + step.get("mainAttributes").asText() + ".txt";
            WebElement mainPanel = ts.elementByXpath("//*[contains(@class, 'sokolMainAttributesPanel')]");
            System.out.println("\nMain attributes [" + mainAttributesFileName + "]:\n" + mainPanel.getText());
            try {
                String mainAttributes = IOUtils.toString(DocumentIT.class.getResourceAsStream(mainAttributesFileName));
                mainAttributes = populate(mainAttributes, objects, mainPanel.getText());
                assertThat(mainPanel.getText(), mainAttributes);
            } catch (IOException e) {
                throw new RuntimeException("Can not load mainAttributes file", e);
            }
        }
    }

    private void checkBlock(String fileName, String blockId, Map<String, Object> objects) {
        WebElement mainPanel = ts.elementByXpath("//*[@name = '" + blockId + "']");
        if (mainPanel == null) {
            mainPanel = ts.elementByXpath("//*[contains(@class, '" + blockId + "')]");
        }
        System.out.println("\n[" + fileName + "] <-?->\n" + mainPanel.getText());
        try {
            String mainAttributes = IOUtils.toString(DocumentIT.class.getResourceAsStream(fileName));
            mainAttributes = populate(mainAttributes, objects, mainPanel.getText());
            assertThat(mainPanel.getText(), mainAttributes);
        } catch (IOException e) {
            throw new RuntimeException("Can not load " + fileName + " file", e);
        }
    }

    private void assertThat(String actualText, String textFromFile) {
        if (doAssert) {
            MatcherAssert.assertThat(actualText, equalTo(textFromFile));
        } else {
            if (!textFromFile.equals(actualText)) {
//                System.err.println("java.lang.AssertionError:\n" +
//                    "Expected :" + textFromFile + "\n" +
//                    "Actual   :" + actualText);
                StringBuilder builder = new StringBuilder();
                String[] actualTextLines = actualText.split("\n");
                String[] textFromFileLines = textFromFile.split("\n");

                for (int i = 0; i < textFromFileLines.length; i++) {
                    String textFromFileLine = textFromFileLines[i];
                    String actualTextLine = i < actualTextLines.length ? actualTextLines[i] : "";
                    if (!textFromFileLine.equals(actualTextLine)) {
                        builder.append(textFromFileLine);
                        builder.append(" <-> ");
                        builder.append(actualTextLine);
                        builder.append("\n");
                    }
                }
//                new AssertionError("Expected :" + textFromFile.replace("\n", "\\n") + "\n" +
//                    "Actual   :" + actualText.replace("\n", "\\n")).printStackTrace();
                System.out.println("[!!!] NOT MATCH Expected <-> Actual\n" + builder.toString());
            }
        }
    }

    private void loginAction(JsonNode step) throws InterruptedException, AWTException, MalformedURLException {
        String user = step.get("user").asText();
        String pass = step.get("pass").asText();

        ts = TestService.getInstanceWithoutLogin();
        ts.login(user, pass);
        Thread.sleep(3000);
    }

    private void closeAction(JsonNode step) throws InterruptedException, AWTException, MalformedURLException {
        ts.getDriver().close();
        Thread.sleep(2000);
        Set<String> windowHandles = ts.getDriver().getWindowHandles();
        for (String windowHandle : windowHandles) {
            ts.getDriver().switchTo().window(windowHandle);
            String currentUrl = ts.getDriver().getCurrentUrl();
            if (currentUrl.contains("#lists/documents")) {
                break;
            }
        }
        Thread.sleep(1000);
    }

    private void logoutAction(JsonNode step) throws InterruptedException, AWTException, MalformedURLException {
        if (ts == null) {
            return;
        }
        WebElement headerUserMenu = ts.elementByXpath("//*[contains(@class, 'headerUserMenu')]");

        if (headerUserMenu != null) {
            headerUserMenu.click();
            Thread.sleep(500);
            ts.click("Выход", "sokolHeaderMenuItem", true);
            Thread.sleep(1000);
        }
    }

    public void fillSelect(String selectName, String value, boolean clear) throws InterruptedException {
        WebElement select = ts.elementByXpath("//*[@name = '" + selectName + "']");
        WebElement div = select.findElement(By.xpath(".."));
        WebElement input = div.findElement(By.xpath(".//input"));
        if (clear) {
            input.sendKeys("\b");
        }
        input.sendKeys(value);
        Thread.sleep(1000);
        ts.click(value, "highlight", true);
        Thread.sleep(500);
    }

    public void fillFirstString(String name, String value) {
        WebElement input = ts.elementsByXpath("//*[@name = '" + name + "']").get(0);
        input.sendKeys(value);
    }

    public void fillFirstSelect(String selectName, String value) throws InterruptedException {
        WebElement select = ts.elementsByXpath("//*[@name = '" + selectName + "']").get(0);
        WebElement div = select.findElement(By.xpath(".."));
        WebElement input = div.findElement(By.xpath(".//input"));
        input.sendKeys(value);
        Thread.sleep(1000);
        ts.click(value, "highlight", true);
        Thread.sleep(500);
    }

    public void fillString(String name, String value, boolean clear) {
        WebElement input = ts.elementByXpath("//*[@name = '" + name + "']");
        if (clear) {
            input.clear();
        }
        input.sendKeys(value);
    }

    public String getCurrentNumber(String listTitle) {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/anatolii?currentSchema=sokol",
                "anatolii",
                "");

            pst = con.prepareStatement("SELECT prefix, suffix, \"count\" FROM registrationlists WHERE title = ?");
            pst.setString(1, listTitle);
            rs = pst.executeQuery();

            if (rs.next()) {
                return rs.getString("prefix") + Integer.toString(rs.getInt("count")) + rs.getString("suffix");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                rs.close();
                pst.close();
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "00";
    }
}
