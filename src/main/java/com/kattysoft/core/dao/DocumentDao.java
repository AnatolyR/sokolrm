/*
 * Copyright 2016 Anatolii Rakovskii (rtolik@yandex.ru)
 *
 * Software distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */
package com.kattysoft.core.dao;

import com.kattysoft.core.model.Document;
import com.kattysoft.core.specification.Specification;

import java.util.List;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 17.07.2016
 */
public interface DocumentDao {
    Document getDocument(String documentId, List<String> fieldsNames);

    List<Document> getDocumentsList(Specification specification);

    String saveDocument(Document document);

    Document deleteDocument(String documentId);

    Integer getTotalCount(Specification specification);
}
