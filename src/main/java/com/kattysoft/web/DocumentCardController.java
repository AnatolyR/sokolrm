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
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
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

    private ObjectMapper mapper = new ObjectMapper();

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    public DocumentCardController() {
        mapper.setDateFormat(dateFormat);
    }

    @RequestMapping(value = "/createdocument")
    public String createDocument(String type) {
        Document document = new Document();
        document.setType(type);
        document.getFields().put("status", "Черновик");
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
        JsonNode typeConfig = configService.getConfig("types/" + typeId + "Type");
        JsonNode formConfig = configService.getConfig("forms/" + typeId + "Form");
        Map<String, JsonNode> fieldTypes = new HashMap<>();
        typeConfig.get("fields").forEach(jsonNode -> fieldTypes.put(jsonNode.get("id").asText(), jsonNode));
        Consumer<JsonNode> merge = new Consumer<JsonNode>() {
            @Override
            public void accept(JsonNode jsonNode) {
                if (jsonNode.get("id") != null) {
                    JsonNode fieldType = fieldTypes.get(jsonNode.get("id").asText());
                    ((ObjectNode) jsonNode).putAll((ObjectNode) fieldType);
                } else if (jsonNode.get("items") != null) {
                    jsonNode.get("items").forEach(this);
                }
            }
        };
        formConfig.get("fields").forEach(merge);

        ((ObjectNode) formConfig).put("typeTitle", typeConfig.get("title"));
        if (formConfig.has("actions")) {
            ArrayNode filteredActions = mapper.createArrayNode();
            formConfig.get("actions").forEach(a -> {
                if (accessRightService.checkDocumentRights(document, a.asText(), AccessRightLevel.CREATE)) {
                    filteredActions.add(a);
                }
            });
            ((ObjectNode) formConfig).put("actions", filteredActions);
        }

        ObjectNode card = mapper.createObjectNode();
        card.put("form", formConfig);

        ObjectNode data = (ObjectNode) mapper.<JsonNode>valueToTree(document);
        ObjectNode fields = (ObjectNode) data.get("fields");
        data.remove("fields");
        data.putAll(fields);
        card.put("data", data);

        return card.toString();
    }

    @RequestMapping(value = "/savedocument")
    public String saveDocument(Reader reader) {
        try {
            String requestBody = IOUtils.toString(reader);
            ObjectNode data = (ObjectNode) mapper.readTree(requestBody);

            String typeId = data.get("type").asText();
            JsonNode typeConfig = configService.getConfig("types/" + typeId + "Type");
            Map<String, JsonNode> fieldsInfo = new HashMap<>();
            typeConfig.get("fields").forEach(jsonNode -> fieldsInfo.put(jsonNode.get("id").asText(), jsonNode));

            String id = data.get("id").asText();
            Document document = new Document();
            document.setId(id);
            document.setType(typeId);

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
}
