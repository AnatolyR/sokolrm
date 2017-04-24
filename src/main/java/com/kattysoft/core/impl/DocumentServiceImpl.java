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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kattysoft.core.*;
import com.kattysoft.core.dao.DocumentDao;
import com.kattysoft.core.model.Document;
import com.kattysoft.core.model.User;
import com.kattysoft.core.specification.Specification;
import org.codehaus.jackson.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
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

    @Autowired
    private TitleService titleService;

    private ObjectMapper mapper = new ObjectMapper();

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

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
        Document existDocument = documentDao.getDocument(document.getId(), new ArrayList<String>(document.getFields().keySet()));
        if (document.getId() != null && existDocument == null) {
            throw new SokolException("Document not found");
        }
        if ("template".equals(document.getStatus())) {
            if ("draft".equals(existDocument.getStatus())) {
                document.getFields().put("status", "template");
            } else {
                throw new SokolException("Only draft document can be saved as template");
            }
        }

        fillTitleFields(document, existDocument != null ? existDocument.getType() : document.getType());

        String id = documentDao.saveDocument(document);

        if (existDocument == null) {
            existDocument = new Document();
            existDocument.setId(id);
        }
        saveHistory(existDocument, document);

        return id;
    }

    private void saveHistory(Document existDocument, Document document) {
        if (existDocument == null) {
            return;
        }
        String documentId = document.getId();
        String historyJson = documentDao.getHistory(documentId);
        ArrayNode history;
        if (historyJson == null) {
            history = mapper.createArrayNode();
        } else {
            try {
                history = (ArrayNode) mapper.readTree(historyJson);
            } catch (IOException e) {
                throw new SokolException("Can not read document '" + documentId + "' history", e);
            }
        }

        ObjectNode item = mapper.createObjectNode();

        item.put("date", dateFormat.format(new Date()));
        User user = userService.getCurrentUser();
        item.put("user", user.getId().toString());
        item.put("userTitle", user.getTitle());

        ArrayNode fieldDiffs = mapper.createArrayNode();
        document.getFields().entrySet().stream().forEach(e -> {
            String fieldName = e.getKey();
            Object newValue = e.getValue() != null ? e.getValue() : "";
            Object oldValue = existDocument.getFields().get(fieldName);
            if (oldValue == null) {
                oldValue = "";
            }
            if (!newValue.equals(oldValue)) {
                ObjectNode fieldDiff = mapper.createObjectNode();
                fieldDiff.put("field", fieldName);
                fieldDiff.put("oldValue", oldValue.toString());
                fieldDiff.put("newValue", newValue.toString());
                fieldDiffs.add(fieldDiff);
            }
        });
        item.set("fields", fieldDiffs);

        if (document.getFields().get("status") != null) {
            String status = document.getFields().get("status").toString();
            if ("draft".equals(status)) {
                item.put("type", "Создание");
            } else {
                String statusTitle = titleService.getTitle("status", status);
                item.put("type", statusTitle);
            }
        } else if (document.getFields() == null) {
            item.put("type", "Создание");
        } else {
            item.put("type", "Редактирование");
        }

        history.add(item);

        documentDao.saveHistory(documentId, history.toString());
    }

    private void fillTitleFields(Document document, String typeId) {
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
            if (fieldInfo == null) {
                return;
            }
            String type = fieldInfo.get("type").asText();
            if ("dictionary".equals(type)) {
                String dictionaryName = fieldInfo.get("dictionary") != null ? fieldInfo.get("dictionary").asText() : null;
                if ("organizationPersons".equals(dictionaryName) || "correspondent".equals(dictionaryName)) {
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
            }
        });
        document.getFields().putAll(titleFields);
    }

    @Override
    public boolean deleteDocument(String documentId) {
        boolean result = documentDao.deleteDocument(documentId);
        return result;
    }

    @Override
    public ArrayNode getHistory(String documentId) {
        String historyJson = documentDao.getHistory(documentId);
        ArrayNode history;
        if (historyJson == null) {
            history = mapper.createArrayNode();
        } else {
            try {
                history = (ArrayNode) mapper.readTree(historyJson);
            } catch (IOException e) {
                throw new SokolException("Can not read document '" + documentId + "' history", e);
            }
        }
        return history;
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

    public void setTitleService(TitleService titleService) {
        this.titleService = titleService;
    }
}
