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

import com.kattysoft.core.*;
import com.kattysoft.core.dao.DocumentDao;
import com.kattysoft.core.model.Document;
import com.kattysoft.core.specification.Specification;
import org.codehaus.jackson.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 20.07.2016
 */
public class DocumentServiceImpl implements DocumentService {
    private DocumentDao documentDao;

    @Autowired
    private ConfigService configService;

    @Autowired
    private UserService userService;

    @Autowired
    private ContragentService contragentService;

    public List<Document> listDocuments(Specification specification) {
        //todo проверка прав на поля
        List<Document> documentsList = documentDao.getDocumentsList(specification);
        return documentsList;
    }

    public Integer getTotalCount(Specification specification) {
        Integer total = documentDao.getTotalCount(specification);
        return total;
    }

    public Document getDocument(String id) {
        //todo проверка прав на поля
        Document document = documentDao.getDocument(id, null);
        return document;
    }

    @Override
    public String saveDocument(Document document) {
        //todo проверка прав на поля
        if (document.getId() != null && documentDao.getDocument(document.getId(), null) == null) {
            throw new SokolException("Document not found");
        }

        fillTitleFields(document);

        return documentDao.saveDocument(document);
    }

    private void fillTitleFields(Document document) {
        String typeId = document.getType();
        if (typeId == null) {
            return;
        }
        JsonNode typeConfig = configService.getConfig("types/" + typeId + "Type");
        Map<String, JsonNode> fieldsInfo = new HashMap<>();
        typeConfig.get("fields").forEach(jsonNode -> fieldsInfo.put(jsonNode.get("id").asText(), jsonNode));

        Map<String, Object> titleFields = new HashMap<>();
        document.getFields().entrySet().stream().forEach(e -> {
            String name = e.getKey();
            Object value = e.getValue();
            JsonNode fieldInfo = fieldsInfo.get(name);
            String type = fieldInfo.get("type").asText();
            if ("dictionary".equals(type)) {
                String dictionaryName = fieldInfo.get("dictionary").asText();
                if (fieldInfo.get("multiple") != null && fieldInfo.get("multiple").asBoolean()) {
                    List<String> titles = ((List<String>) value).stream().map(val -> {
                        if ("organizationPersons".equals(dictionaryName)) {
                            String title = userService.getUserTitleById(val);
                            return title;
                        } if ("correspondent".equals(dictionaryName)) {
                            String title = contragentService.getContragentTitleById(val);
                            return title;
                        }
                        return "[нет значения]";
                    }).collect(Collectors.toList());
                    titleFields.put(name + "Title", titles);
                } else {
                    if (value != null) {
                        String title = null;
                        if ("organizationPersons".equals(dictionaryName)) {
                            title = userService.getUserTitleById((String) value);
                        } if ("correspondent".equals(dictionaryName)) {
                            title = contragentService.getContragentTitleById((String) value);
                        }
                        titleFields.put(name + "Title", title != null ? title : "[нет значения]");
                    } else {
                        titleFields.put(name + "Title", null);
                    }
                }
            }
        });
        document.getFields().putAll(titleFields);
    }

    @Override
    public boolean deleteDocument(String documentId) {
        boolean result = documentDao.deleteDocument(documentId);
        return result;
    }

    public void setDocumentDao(DocumentDao documentDao) {
        this.documentDao = documentDao;
    }

    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setContragentService(ContragentService contragentService) {
        this.contragentService = contragentService;
    }
}
