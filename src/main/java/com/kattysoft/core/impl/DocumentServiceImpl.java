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
import com.kattysoft.core.model.DocumentLink;
import com.kattysoft.core.model.User;
import com.kattysoft.core.repository.DocumentLinkRepository;
import com.kattysoft.core.specification.*;
import org.codehaus.jackson.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

    @Autowired
    private AccessRightService accessRightService;

    @Autowired
    private DocumentLinkRepository documentLinkRepository;

    private ObjectMapper mapper = new ObjectMapper();

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    private SimpleDateFormat dateFormatSs = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

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
        String author = document.getFields().containsKey("author") ? document.getFields().get("author").toString() : null;
        boolean systemAuthor = false;
        if ("system".equals(author)) {
            systemAuthor = true;
            document.getFields().remove("author");
        }

        if (document.getId() == null && !accessRightService.checkDocumentRights(document, "", AccessRightLevel.CREATE)) {
            throw new SokolException("Not enough access rights to create document");
        }
        
        //todo проверка прав на поля
        Document existDocument = documentDao.getDocument(document.getId(), new ArrayList<String>(document.getFields().keySet()));
        if (document.getId() != null && existDocument == null) {
            throw new SokolException("Document not found");
        }
        
        if (existDocument!= null && !checkAccessRights(document, existDocument)) {
            throw new SokolException("Not enough access rights to write document");
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
        saveHistory(existDocument, document, systemAuthor);

        return id;
    }

    private boolean checkAccessRights(Document document, Document existDocument) {
        User currentUser = userService.getCurrentUser();
        
        if (accessRightService.checkDocumentRights(existDocument, "", AccessRightLevel.WRITE)) {
            return true;
        }
        if ("draft".equals(existDocument.getStatus()) && currentUser.getId().toString().equals(existDocument.getFields().get("author"))) {
            return true;
        }
        if (document.getFields().size() == 1 && document.getFields().containsKey("status")) {
            return true;
        }
        
        return false;
    }

    @Override
    public void saveProcessAction(String documentId, String actionResult) {
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

        item.put("date", dateFormatSs.format(new Date()));
        User user = userService.getCurrentUser();
        item.put("user", user.getId().toString());
        item.put("userTitle", user.getTitle());

        ArrayNode fieldDiffs = mapper.createArrayNode();
        item.set("fields", fieldDiffs);

        item.put("type", actionResult);

        history.add(item);

        documentDao.saveHistory(documentId, history.toString());
    }

    private void saveHistory(Document existDocument, Document document, boolean systemAuthor) {
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

        Date date = new Date();
        String dateStr = dateFormatSs.format(new Date());
        if (StreamSupport.stream(history.spliterator(), false).filter(n -> n.get("date").asText().equals(dateStr)).findAny().isPresent()) {
            item.put("date", dateFormatSs.format(new Date(date.getTime() + 1000)));
        } else {
            item.put("date", dateStr);
        }

        User user = userService.getCurrentUser();
        item.put("user", systemAuthor ? "c8f30782-2daf-48d3-a4a5-f5aeeb31efb0" : user.getId().toString());
        item.put("userTitle", systemAuthor ? "Система" : user.getTitle());

        ArrayNode fieldDiffs = mapper.createArrayNode();
        document.getFields().entrySet().stream().forEach(e -> {
            String fieldName = e.getKey();
            Object newValue = e.getValue() != null ? e.getValue() : "";
            Object oldValue = existDocument != null ? existDocument.getFields().get(fieldName) : null;
            if (oldValue == null) {
                oldValue = "";
            }
            if (!newValue.equals(oldValue)) {
                ObjectNode fieldDiff = mapper.createObjectNode();

                String fieldTitle = titleService.getTitleNotNull("fieldsTitles", fieldName);
                fieldDiff.put("field", fieldTitle + " (" + fieldName + ")");

                fieldDiff.put("oldValue", oldValue.toString());
                fieldDiff.put("newValue", newValue.toString());
                fieldDiffs.add(fieldDiff);
            }
        });
        item.set("fields", fieldDiffs);

        if (document.getFields() == null) {
            item.put("type", "Создание");
        } else if (document.getFields().get("status") != null) {
            String status = document.getFields().get("status").toString();
            if ("draft".equals(status)) {
                item.put("type", "Создание");
            } else {
                String statusTitle = titleService.getTitle("status", status);
                item.put("type", statusTitle);
            }
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
        Document existDocument = documentDao.getDocument(documentId, null);
        if (documentId != null && existDocument == null) {
            throw new SokolException("Document not found");
        }
        if (!accessRightService.checkDocumentRights(existDocument, "", AccessRightLevel.DELETE)) {
            throw new NoAccessRightsException("No access rights for delete document");
        }
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

    @Override
    public UUID addDocumentLink(String docId, String documentNumber, String type) {

        UUID documentId = UUID.fromString(docId);
        UUID linkId = null;
        if (Utils.isUUID(documentNumber)) {
            linkId = UUID.fromString(documentNumber);
        } else {
            Document document = getDocumentByNumber(documentNumber);
            if (document != null) {
                linkId = UUID.fromString(document.getId());
            }
            //todo search by documentNumber
        }
        if (linkId != null) {
            DocumentLink link = new DocumentLink();
            UUID id = UUID.randomUUID();
            link.setId(id);
            link.setDocId(documentId);
            link.setLinkId(linkId);
            link.setLinkType(type);

            documentLinkRepository.save(link);
            return id;
        } else {
            throw new SokolException("Document to link not found");
        }
    }

    private Document getDocumentByNumber(String documentNumber) {
        Specification spec = new Specification();
        spec.setCondition(new ContainerCondition(ContainerOperation.AND, new ValueCondition("documentNumber", Operation.EQUAL, documentNumber)));
        List<Document> documentsList = documentDao.getDocumentsList(spec);
        if (documentsList.size() > 0) {
            return documentsList.get(0);
        }
        return null;
    }

    @Override
    public void deleteDocumentLinks(List<String> ids) {
        List<DocumentLink> values = ids.stream().map(id -> new DocumentLink(UUID.fromString(id))).collect(Collectors.toList());
        documentLinkRepository.delete(values);
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

    public DocumentLinkRepository getDocumentLinkRepository() {
        return documentLinkRepository;
    }

    public void setDocumentLinkRepository(DocumentLinkRepository documentLinkRepository) {
        this.documentLinkRepository = documentLinkRepository;
    }

    public void setAccessRightService(AccessRightService accessRightService) {
        this.accessRightService = accessRightService;
    }
}
