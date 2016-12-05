/*
 * Copyright 2016 Anatolii Rakovskii (rtolik@yandex.ru)
 *
 * No part of this file can be copied or reproduced without written permission of author.
 *
 * Software distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */
package com.kattysoft.core.impl;

import com.kattysoft.core.DocumentService;
import com.kattysoft.core.dao.DocumentDao;
import com.kattysoft.core.model.Document;
import com.kattysoft.core.specification.Specification;

import java.util.List;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 20.07.2016
 */
public class DocumentServiceImpl implements DocumentService {
    private DocumentDao documentDao;

    public List<Document> listDocuments(Specification specification) {
        List<Document> documentsList = documentDao.getDocumentsList(specification);
        return documentsList;
    }

    public void setDocumentDao(DocumentDao documentDao) {
        this.documentDao = documentDao;
    }
}
