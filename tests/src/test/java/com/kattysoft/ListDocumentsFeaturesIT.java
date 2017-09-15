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
import org.hamcrest.MatcherAssert;
import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;

import static org.hamcrest.CoreMatchers.equalTo;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 24.03.2017
 */
public class ListDocumentsFeaturesIT {
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
    public void testPaging() throws InterruptedException, AWTException, IOException {
        doAssert = true;
        
        ts = TestService.getInstance("ivashov", "123");
        String listId = "documents";
        Thread.sleep(2000);
        String categoryName = "category_" + listId;
        WebElement listsMenuItem = ts.elementByXpath("//*[contains(@name, '" + categoryName + "')]");
        listsMenuItem.click();
        Thread.sleep(2000);
        
        checkList("list1");
        
        ts.click("Следующая", "btn", true);
        Thread.sleep(1000);
        
        checkList("list2AfterNext");

        ts.click("Предыдущая", "btn", true);
        Thread.sleep(1000);

        checkList("list3AfterPrev");

        checkButtons("list4After50", "Найдено: 41 Предыдущая \n" +
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
        
        ts.click("20", "dropdown-toggle", true);
        Thread.sleep(100);
        ts.click("50", null, false);
        Thread.sleep(1000);

        checkList("list4After50");
        checkButtons("list4After50", "Найдено: 41 \n" +
            "  Отображать\n" +
            "50\n" +
            " 5\n" +
            "20\n" +
            "50\n" +
            "100\n" +
            " Колонки\n" +
            "Фильтр");
        
    }

    @Test
    public void testColumns() throws InterruptedException, AWTException, IOException {
        doAssert = true;

        ts = TestService.getInstance("ivashov", "123");
        String listId = "documents";
        Thread.sleep(2000);
        String categoryName = "category_" + listId;
        WebElement listsMenuItem = ts.elementByXpath("//*[contains(@name, '" + categoryName + "')]");
        listsMenuItem.click();
        Thread.sleep(2000);

        checkList("list1");

        ts.click("Колонки", "btn", true);
        Thread.sleep(100);
        ts.click("Адресаты", "sokolListColumnSelectItem", false);
        Thread.sleep(100);
        ts.click("Корреспондент", "sokolListColumnSelectItem", false);
        Thread.sleep(100);
        ts.click("Колонки", "btn", true);
        Thread.sleep(100);
        
        Thread.sleep(2000);
        checkList("listBAfterColumnSelect");
    }

    @Test
    public void testFilter() throws InterruptedException, AWTException, IOException {
        doAssert = true;

        ts = TestService.getInstance("ivashov", "123");
        String listId = "documents";
        Thread.sleep(2000);
        String categoryName = "category_" + listId;
        WebElement listsMenuItem = ts.elementByXpath("//*[contains(@name, '" + categoryName + "')]");
        listsMenuItem.click();
        Thread.sleep(2000);

        checkList("list1");

        ts.click("Фильтр", "btn", false);
        Thread.sleep(1000);
        ts.click("Добавить условие", "btn", false);
        Thread.sleep(1000);

        WebElement plusButton = ts.elementByXpath("//*[contains(@title, 'Добавить элемент условия')]");
        plusButton.click();
        plusButton.click();

        ts.fillNSimpleSelect("conditionSelector", 0, "ИЛИ (");
        
        ts.fillNSimpleSelect("columnSelector", 1, "Статус");
        ts.fillNSimpleSelect("operationSelector", 1, "равно");
        ts.fillNString("valueBox", 1, "Согласование", false);
        
        ts.fillNSimpleSelect("columnSelector", 2, "Статус");
        ts.fillNSimpleSelect("operationSelector", 2, "равно");
        ts.fillNString("valueBox", 2, "Рассмотрение", false);
        
        ts.click("Применить", "btn", false);

        checkList("listCFiltered");
    }
    
    private void checkButtons(String listId, String listButtons) {
        WebElement tableButtons = ts.elementByXpath("//*[contains(@name, 'tableButtons')]");
        System.out.println("\nTable buttons [" + listId + "]:\n" + tableButtons.getText());
//        assertThat(tableButtons.getText(), equalTo(listButtons));
        match(tableButtons.getText(), listButtons);
    }

    private void checkList(String listFile) throws IOException {
        String textFromFile = IOUtils.toString(ListDocumentsFeaturesIT.class.getResourceAsStream("/listfeatures/" + listFile + ".txt"));

        WebElement tablePanel = ts.elementByXpath("//*[contains(@name, 'tablePanel')]");
        System.out.println("\nTable [" + listFile + "]:\n" + tablePanel.getText());
        String actualText = tablePanel.getText();
        
        match(actualText, textFromFile);
    }

    private void match(String actualText, String textFromFile) {
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
}
