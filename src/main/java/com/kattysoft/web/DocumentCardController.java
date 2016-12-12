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

import com.kattysoft.core.ConfigService;
import com.kattysoft.core.DocumentService;
import com.kattysoft.core.model.Document;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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

    private ObjectMapper mapper = new ObjectMapper();

    @RequestMapping(value = "/card", produces = "application/json; charset=utf-8")
    public String getDocumentCard(String id) {
        Document document = documentService.getDocument(id);

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

            Iterator<JsonNode> fields = data.get("fields").getElements();
            Map<String, Object> resultFields = new HashMap<>();
            while (fields.hasNext()) {
                JsonNode field = fields.next();
                String name = field.get("name").asText();
                JsonNode value = field.get("value");
                JsonNode fieldInfo = fieldsInfo.get(name);

                if (fieldInfo != null && "string".equals(fieldInfo.get("type").asText())) {
                    if (value.isTextual()) {
                        resultFields.put(name, value.asText());
                    }
                }

            }
            document.setFields(resultFields);

            documentService.saveDocument(document);

            return id;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
}
