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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kattysoft.core.ConfigService;
import com.kattysoft.core.DocumentService;
import com.kattysoft.core.model.Document;
import com.kattysoft.core.specification.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 20.07.2016
 */
@RestController
public class DocumentListController {
    public static final Integer DEFAULT_PAGE_SIZE = 50;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private ConfigService configService;

    private ObjectMapper mapper = new ObjectMapper();

    @RequestMapping(value = "/documents")
    public Object listDocuments(String listId, Integer offset, Integer size) throws IOException {
        if (offset == null) {
            offset = 0;
        }
        if (size == null) {
            size =  DEFAULT_PAGE_SIZE;
        }

        JsonNode config = configService.getConfig2("lists/" + listId + "List");

        Specification spec = new Specification();
        spec.setOffset(offset);
        spec.setSize(size);
        List<String> fields = StreamSupport.stream(config.get("gridConfig").get("columns").spliterator(), false)
            .map(node -> node.get("id").asText()).collect(Collectors.toList());
        spec.setFields(fields);

        Map<String, String> fieldsRenders = new HashMap<>();
        config.get("gridConfig").get("columns").forEach(field -> {
            if (field.get("render") != null) {
                fieldsRenders.put(field.get("id").asText(), field.get("render").asText());
            }
        });
        final Map<String, String> typeTitleCash = new HashMap<>();

        ContainerCondition condition = new ContainerCondition();
        String listConditionSql = config.get("condition").asText();
        condition.setOperation(ContainerOperation.AND);
        Condition listCondition = new SqlCondition(listConditionSql);
        condition.getConditions().add(listCondition);
        spec.setCondition(condition);

        if (config.get("join") != null) {
            String join = config.get("join").asText();
            spec.setJoin(join);
        }

        List<Document> documents = documentService.listDocuments(spec);
        Integer total = documentService.getTotalCount(spec);

        ObjectNode page = mapper.createObjectNode();
        List<ObjectNode> documentsNodes = documents.stream().map(doc -> {
            ObjectNode document = mapper.valueToTree(doc);
            ObjectNode docFields = (ObjectNode) document.get("fields");
            document.remove("fields");
            document.setAll(docFields);

            processServerRenders(document, fieldsRenders, typeTitleCash);

            return document;
        }).collect(Collectors.toList());
        page.putArray("data").addAll(documentsNodes);
        page.put("offset", offset);
        page.put("total", total);

        return page;
    }

    protected void processServerRenders(ObjectNode document, Map<String, String> fieldsRenders, final Map<String, String> typeTitleCash) {
        fieldsRenders.entrySet().forEach(e -> {
            String name = e.getKey();
            String renderer = e.getValue();
            if (document.get(name) != null) {
                String value = document.get(name).asText();
                if ("doctype".equals(renderer)) {
                    String type = value;
                    if (!typeTitleCash.containsKey(type)) {
                        JsonNode typeConfig = configService.getConfig2("types/" + type + "Type");
                        String typeTitle = typeConfig != null ? typeConfig.get("title").asText() : "[" + type + "]";
                        typeTitleCash.put(type, typeTitle);
                    }
                    String typeTitle = typeTitleCash.get(type);
                    document.put(name, typeTitle);
                }
            }
        });
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }
}
