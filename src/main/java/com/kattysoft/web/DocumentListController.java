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
public class DocumentListController {
    public static final Integer DEFAULT_PAGE_SIZE = 50;

    @Autowired
    private DocumentService documentService;

    @RequestMapping(value = "/documents")
    public DocumentsResponse listDocuments(String listId, Integer offset, Integer size) throws IOException {
        if (offset == null) {
            offset = 0;
        }
        if (size == null) {
            size =  DEFAULT_PAGE_SIZE;
        }
        List<Document> documents = documentService.listDocuments(null);
        Integer total = documentService.getTotalCount(null);

        DocumentsResponse response = new DocumentsResponse();
        response.setData(documents);
        response.setOffset(offset);
        response.setTotal(total);
        return response;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public static class DocumentsResponse {
        private List<Document> data;
        private Integer total;
        private Integer offset;

        public List<Document> getData() {
            return data;
        }

        public void setData(List<Document> data) {
            this.data = data;
        }

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }

        public Integer getOffset() {
            return offset;
        }

        public void setOffset(Integer offset) {
            this.offset = offset;
        }
    }
}
