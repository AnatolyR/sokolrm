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
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 24.03.2017
 */
public class ListDocumentsIT {
    private TestService ts;

    @BeforeClass
    public void before() throws InterruptedException, AWTException, MalformedURLException {
//        ts = TestService.getInstance();
    }
    
    @Test
    public void testDocumentsList() {
        listDocuments("documents", 41);
    }

    @Test
    public void testIncomingDocumentsList() {
        listDocuments("incomingDocuments", 31);
    }

    @Test
    public void testOutgoingDocumentsList() {
        listDocuments("outgoingDocuments", 10);
    }

    @Test
    public void testInternalDocumentsList() {
        listDocuments("internalDocuments", 0);
    }

    @Test
    public void testReviewDocumentsList() {
        listDocuments("reviewDocuments", 4);
    }

    @Test
    public void testSignDocumentsList() {
        listDocuments("signDocuments", 1);
    }

    @Test
    public void testMyDocumentsList() {
        listDocuments("myDocuments", 88);
    }

    @Test
    public void testDocumentTemplatesList() {
        listDocuments("documentTemplates", 4);
    }

    @Test
    public void testIncomingTasksList() {
        listDocuments("incomingTasks", 3);
    }

    @Test
    public void testIncomingExecutionTasksList() {
        listDocuments("incomingExecutionTasks", 2);
    }

    @Test
    public void testIncomingApprovalTasksList() {
        listDocuments("incomingApprovalTasks", 1);
    }

    @Test
    public void testIncomingAcquaintanceTasksList() {
        listDocuments("incomingAcquaintanceTasks", 0);
    }

    @Test
    public void testArchivedIncomingTasksList() {
        listDocuments("archivedIncomingTasks", 1);
    }

    @Test
    public void testOutgoingTasksList() {
        listDocuments("outgoingTasks", 8);
    }

    @Test
    public void testOutgoingExecutionTasksList() {
        listDocuments("outgoingExecutionTasks", 5);
    }

    @Test
    public void testOutgoingApprovalTasksList() {
        listDocuments("outgoingApprovalTasks", 3);
    }

    @Test
    public void testOutgoingAcquaintanceTasksList() {
        listDocuments("outgoingAcquaintanceTasks", 0);
    }

    @Test
    public void testArchivedOutgoingTasksList() {
        listDocuments("archivedOutgoingTasks", 16);
    }

    @Test
    public void testArchivedDocumentsList() {
        listDocuments("archivedDocuments", 18);
    }
    
    public void listDocuments(String listId, int listSize) {
        try {
            String content = IOUtils.toString(ListDocumentsIT.class.getResourceAsStream("/lists/" + listId + ".txt"));
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
