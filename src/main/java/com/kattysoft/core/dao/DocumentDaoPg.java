/*
 * Copyright 2016 Anatolii Rakovskii (rtolik@yandex.ru)
 *
 * No part of this file can be copied or reproduced without written permission of author.
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
public class DocumentDaoPg implements DocumentDao {


    public Document getDocument(String documentId, List<String> fieldsNames) {
        return null;
    }

    public List<Document> getDocumentsList(Specification specification) {
        return null;
    }

    public Document saveDocument(Document document) {
        return null;
    }

    public Document deleteDocument(String documentId) {
        return null;
    }
}
