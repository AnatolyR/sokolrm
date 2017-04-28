/*
 * Copyright 2016 Anatolii Rakovskii (rtolik@yandex.ru)
 *
 * Software distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */
package com.kattysoft.core;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.kattysoft.core.model.Document;
import com.kattysoft.core.specification.Specification;

import java.util.List;
import java.util.UUID;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 12.07.2016
 */
public interface DocumentService {
    List<Document> listDocuments(Specification specification);

    Integer getTotalCount(Specification specification);

    Document getDocument(String id);

    String saveDocument(Document document);

    boolean deleteDocument(String documentId);

    ArrayNode getHistory(String documentId);

    UUID addDocumentLink(String docId, String documentNumber, String type);

    void deleteDocumentLinks(List<String> strings);
}
