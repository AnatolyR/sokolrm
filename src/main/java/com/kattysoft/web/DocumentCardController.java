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

import com.kattysoft.core.*;
import com.kattysoft.core.model.Document;
import com.kattysoft.core.model.DocumentType;
import com.kattysoft.core.model.Space;
import com.kattysoft.core.model.User;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 08.12.2016
 */
@RestController
public class DocumentCardController {
    @Autowired
    private DocumentService documentService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private AccessRightService accessRightService;

    @Autowired
    private UserService userService;

    @Autowired
    private TitleService titleService;

    @Autowired
    private SpaceService spaceService;

    @Autowired
    private DocumentTypeService documentTypeService;

    private ObjectMapper mapper = new ObjectMapper();

    private com.fasterxml.jackson.databind.ObjectMapper mapper2 = new com.fasterxml.jackson.databind.ObjectMapper();

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    public DocumentCardController() {
        mapper.setDateFormat(dateFormat);
        mapper2.setDateFormat(dateFormat);
    }

    @RequestMapping(value = "/createdocument")
    public String createDocument(String type) {
        if (type == null || type.isEmpty() || !type.matches("^[a-zA-Z]+$")) {
            throw new SokolException("Wrong document type");
        }
        DocumentType documentType = documentTypeService.getDocumentType(type);
        if (documentType == null) {
            throw new SokolException("Document type not found");
        }
        Document document = new Document();
        document.setType(type);
        document.getFields().put("status", "draft");
        document.getFields().put("created", new Date());
        User currentUser = userService.getCurrentUser();
        document.getFields().put("author", currentUser.getId().toString());
        String id = documentService.saveDocument(document);
        return id;
    }

    @RequestMapping(value = "/card", produces = "application/json; charset=utf-8")
    public String getDocumentCard(String id) {
        Document document = documentService.getDocument(id);
        if (document == null) {
            throw new SokolException("Документ не найден");
        }

        String typeId = document.getType();
        com.fasterxml.jackson.databind.JsonNode typeConfig = configService.getConfig2("types/" + typeId + "Type");
        com.fasterxml.jackson.databind.JsonNode formConfig = configService.getConfig2("forms/" + typeId + "Form");
        Map<String, com.fasterxml.jackson.databind.JsonNode> fieldTypes = new HashMap<>();
        typeConfig.get("fields").forEach(jsonNode -> fieldTypes.put(jsonNode.get("id").asText(), jsonNode));
        Consumer<com.fasterxml.jackson.databind.JsonNode> merge = new Consumer<com.fasterxml.jackson.databind.JsonNode>() {
            @Override
            public void accept(com.fasterxml.jackson.databind.JsonNode jsonNode) {
                if (jsonNode.get("id") != null) {
                    com.fasterxml.jackson.databind.JsonNode fieldType = fieldTypes.get(jsonNode.get("id").asText());
                    if (fieldType == null) {
                        throw new SokolException("Field type '" + jsonNode.get("id").asText() + "' not found for '" + typeId + "'");
                    }
                    ((com.fasterxml.jackson.databind.node.ObjectNode) jsonNode).setAll((com.fasterxml.jackson.databind.node.ObjectNode) fieldType);
                } else if (jsonNode.get("items") != null) {
                    jsonNode.get("items").forEach(this);
                }
            }
        };
        formConfig.get("fields").forEach(merge);

        ((com.fasterxml.jackson.databind.node.ObjectNode) formConfig).set("typeTitle", typeConfig.get("title"));

        addActions(formConfig, typeConfig, document);

        fillTitles(document);

        com.fasterxml.jackson.databind.node.ObjectNode card = mapper2.createObjectNode();
        card.put("form", formConfig);

        com.fasterxml.jackson.databind.node.ObjectNode data = (com.fasterxml.jackson.databind.node.ObjectNode) mapper2.<com.fasterxml.jackson.databind.JsonNode>valueToTree(document);
        com.fasterxml.jackson.databind.node.ObjectNode fields = (com.fasterxml.jackson.databind.node.ObjectNode) data.get("fields");
        data.remove("fields");
        data.setAll(fields);
        card.set("data", data);

        return card.toString();
    }

    private void fillTitles(Document document) {
        String statusId = document.getStatus();
        String statusTitle = titleService.getTitle("status", statusId);
        document.getFields().put("status", statusTitle);

        String spaceId = document.getSpace();
        if (spaceId != null) {
            Space space = spaceService.getSpace(spaceId);
            String spaceTitle = space != null ? space.getTitle() : "[Не найдено]";
            document.getFields().put("spaceTitle", spaceTitle);
        }
    }

    private void addActions(com.fasterxml.jackson.databind.JsonNode formConfig, com.fasterxml.jackson.databind.JsonNode typeConfig, Document document) {
        String typeId = typeConfig.has("flow") ? typeConfig.get("flow").textValue() : null;
        if (typeId != null) {
            com.fasterxml.jackson.databind.JsonNode flow = configService.getConfig2("flows/" + typeId);
            String status = document.getStatus();
            if (status == null || status.isEmpty()) {
                return;
            }
            com.fasterxml.jackson.databind.JsonNode states = flow.get("states");
            com.fasterxml.jackson.databind.JsonNode state = StreamSupport.stream(states.spliterator(), false).filter(s -> status.equals(s.get("id").textValue())).findFirst().orElse(null);
            if (state == null || !state.has("actions")) {
                return;
            }
            com.fasterxml.jackson.databind.node.ArrayNode actions = (com.fasterxml.jackson.databind.node.ArrayNode) state.get("actions");
            com.fasterxml.jackson.databind.node.ArrayNode filteredActions = mapper2.createArrayNode();
            User currentUser = userService.getCurrentUser();
            actions.forEach(a -> {
                String actionId = a.get("id").textValue();
                if (accessRightService.checkDocumentRights(document, "*" + actionId, AccessRightLevel.ALLOW)) {
                    if ("doresolution".equals(actionId)) {
                        List<String> addressee = (List<String>) document.getFields().get("addressee");
                        if (addressee.contains(currentUser.getId().toString())) {
                            filteredActions.add(a);
                        }
                    } else {
                        filteredActions.add(a);
                    }
                }
            });
            ((com.fasterxml.jackson.databind.node.ObjectNode) formConfig).set("actions", filteredActions);
            String currentUserId = userService.getCurrentUser().getId().toString();
            if (accessRightService.checkDocumentRights(document, "", AccessRightLevel.DELETE)
                || ("draft".equals(document.getStatus()) && currentUserId.equals(document.getFields().get("author")))) {
                ((com.fasterxml.jackson.databind.node.ObjectNode) formConfig).put("deleteAction", true);
            }
        }
    }

    @RequestMapping(value = "/savedocument")
    public String saveDocument(Reader reader) {
        try {
            String requestBody = IOUtils.toString(reader);
            ObjectNode data = (ObjectNode) mapper.readTree(requestBody);

            String typeId = data.get("type").asText();
            boolean template = data.has("template");
            JsonNode typeConfig = configService.getConfig("types/" + typeId + "Type");
            Map<String, JsonNode> fieldsInfo = new HashMap<>();
            typeConfig.get("fields").forEach(jsonNode -> fieldsInfo.put(jsonNode.get("id").asText(), jsonNode));

            String id = data.get("id").asText();
            Document document = new Document();
            document.setId(id);
//            document.setType(typeId);
            if (template) {
                document.setStatus("template");
            }

            JsonNode fields = data.get("fields");
            Iterator<String> fieldsNames = fields.getFieldNames();
            Map<String, Object> resultFields = new HashMap<>();
            while (fieldsNames.hasNext()) {
                String name = fieldsNames.next();
                JsonNode value = fields.get(name);
                JsonNode fieldInfo = fieldsInfo.get(name);

                if (fieldInfo != null && value != null) {
                    String type = fieldInfo.get("type").asText();
                    if ("string".equals(type)) {
                        if (value.isTextual()) {
                            resultFields.put(name, value.asText());
                        }
                    } else if ("smallstring".equals(type)) {
                        if (value.isTextual()) {
                            resultFields.put(name, value.asText());
                        }
                    } else if ("text".equals(type)) {
                        if (value.isTextual()) {
                            resultFields.put(name, value.asText());
                        }
                    } else if ("date".equals(type)) {
                        if (value.isTextual()) {
                            String text = value.asText();
                            if (StringUtils.isNotEmpty(text)) {
                                resultFields.put(name, dateFormat.parse(value.asText()));
                            } else {
                                resultFields.put(name, null);
                            }
                        }
                    } else if ("number".equals(type)) {
                        if (value.isTextual()) {
                            String text = value.asText();
                            if (StringUtils.isNotEmpty(text)) {
                                resultFields.put(name, Integer.parseInt(value.asText()));
                            } else {
                                resultFields.put(name, null);
                            }
                        }
                    } else if ("select".equals(type)) {
                        if (value.isTextual()) {
                            resultFields.put(name, value.asText());
                        }
                    } else if ("dictionary".equals(type)) {
                        if (fieldInfo.get("multiple") != null && fieldInfo.get("multiple").asBoolean()) {
                            if (value.isArray()) {
                                List<String> values = new ArrayList<>();
                                value.forEach(node -> {
                                    values.add(node.asText());
                                });
                                resultFields.put(name, values);
                            }
                        } else {
                            if (value.isTextual()) {
                                String text = value.asText();
                                if (StringUtils.isNotEmpty(text)) {
                                    resultFields.put(name, text);
                                } else {
                                    resultFields.put(name, null);
                                }
                            }
                        }
                    }
                }
            }

            document.setFields(resultFields);

            documentService.saveDocument(document);

            return id;
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping(value = "/deletedocument")
    public String deleteDocument(String id) {
        boolean result = documentService.deleteDocument(id);
        return Boolean.toString(result);
    }

    @RequestMapping("addDocumentLink")
    public String addDictionaryValue(String docId, String data) throws IOException {
        if (docId == null || docId.isEmpty()) {
            throw new RuntimeException("docId is null");
        }
        com.fasterxml.jackson.databind.JsonNode node = mapper2.readTree(data);
        String documentNumber = node.get("d.documentNumber").asText();
        String type = node.get("l.linktype").asText();

        if (documentNumber.isEmpty()) {
            throw new RuntimeException("Empty documentNumber");
        }

        documentService.addDocumentLink(docId, documentNumber, type);

        return "reload";
    }

    @RequestMapping("deleteDocumentLinks")
    public String deleteDocumentLinks(String[] ids) {

        documentService.deleteDocumentLinks(Arrays.asList(ids));

        return "reload";
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }

    public static class DocumentCard {
        private String form;
        private Document data;

        public String getForm() {
            return form;
        }

        public void setForm(String form) {
            this.form = form;
        }

        public Document getData() {
            return data;
        }

        public void setData(Document data) {
            this.data = data;
        }
    }

    public void setAccessRightService(AccessRightService accessRightService) {
        this.accessRightService = accessRightService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setTitleService(TitleService titleService) {
        this.titleService = titleService;
    }

    public void setSpaceService(SpaceService spaceService) {
        this.spaceService = spaceService;
    }

    public void setDocumentTypeService(DocumentTypeService documentTypeService) {
        this.documentTypeService = documentTypeService;
    }
}
