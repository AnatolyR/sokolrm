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

    void saveProcessAction(String documentId, String actionResult);

    boolean deleteDocument(String documentId);

    ArrayNode getHistory(String documentId);

    UUID addDocumentLink(String docId, String documentNumber, String type);

    void deleteDocumentLinks(List<String> strings);
}
