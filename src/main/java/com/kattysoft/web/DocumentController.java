/*
 * Copyright 2016 Anatolii Rakovskii (rtolik@yandex.ru)
 *
 * No part of this file can be copied or reproduced without written permission of author.
 *
 * Software distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */
package com.kattysoft.web;

import com.kattysoft.core.DocumentService;
import com.kattysoft.core.model.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 20.07.2016
 */
@RestController
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @RequestMapping(value = "/documents")
    public List<Document> listDocuments() throws IOException {
        List<Document> documents = documentService.listDocuments(null);
        return documents;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }
}
