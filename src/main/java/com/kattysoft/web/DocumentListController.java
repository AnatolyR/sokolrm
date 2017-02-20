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
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kattysoft.core.ConfigService;
import com.kattysoft.core.DocumentService;
import com.kattysoft.core.UserService;
import com.kattysoft.core.Utils;
import com.kattysoft.core.model.Document;
import com.kattysoft.core.model.User;
import com.kattysoft.core.specification.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
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

    @Autowired
    private UserService userService;

    private ObjectMapper mapper = new ObjectMapper();

    @RequestMapping(value = "/documents")
    public Object listDocuments(String listId, Integer offset, Integer size, String conditions, String sort, String sortAsc) throws IOException {
        if (offset == null) {
            offset = 0;
        }
        if (size == null) {
            size =  DEFAULT_PAGE_SIZE;
        }

        JsonNode config = configService.getConfig2("lists/" + listId + "List");

        if (conditions == null || conditions.isEmpty()) {
            conditions = "[]";
        }

        Map<String, String> fieldsRenders = new HashMap<>();
        config.get("gridConfig").get("columns").forEach(field -> {
            if (field.get("render") != null) {
                fieldsRenders.put(field.get("id").asText(), field.get("render").asText());
            }
        });

        JsonNode clientConditionsNode = mapper.readTree(conditions);

        Consumer fixCustomRendersSearch = new Consumer<JsonNode>() {
            @Override
            public void accept(JsonNode jsonNode) {
                if (jsonNode.has("column") && !jsonNode.get("column").isNull()) {
                    String column = jsonNode.get("column").asText();
                    String value = jsonNode.get("value").asText();
                    String render = fieldsRenders.get(column);
                    if (render != null) {
                        if ("executionType".equals(render)) {
                            if ("Исполнение".equals(value)) {
                                ((ObjectNode) jsonNode).put("value", "execution");
                            }
                        } else if ("executionStatus".equals(render)) {
                            if ("Выполняется".equals(value)) {
                                ((ObjectNode) jsonNode).put("value", "run");
                            } else if ("Завершено".equals(value)) {
                                ((ObjectNode) jsonNode).put("value", "complete");
                            }
                        } else if ("user".equals(render)) {
                            if (!Utils.isUUID(value)) {
                                List<User> users = userService.getUsersByShortTitle(value);
                                if (users.size() > 0) {
                                    ((ObjectNode) jsonNode).put("value", users.get(0).getId().toString());
                                }
                            }
                        }
                    }
                }
            }
        };

        clientConditionsNode.forEach(fixCustomRendersSearch);

        Condition clientCondition = SpecificationUtil.read((ArrayNode) clientConditionsNode);


        Specification spec = new Specification();
        if (sort != null && !sort.isEmpty()) {
            Sort sortObject = new Sort();
            sortObject.setField(sort);
            sortObject.setOrder("true".equals(sortAsc) ? SortOrder.ASC : SortOrder.DESC);
            spec.setSort(Collections.singletonList(sortObject));
        }

        spec.setOffset(offset);
        spec.setSize(size);
        List<String> fields = StreamSupport.stream(config.get("gridConfig").get("columns").spliterator(), false)
            .map(node -> node.get("id").asText()).collect(Collectors.toList());
        spec.setFields(fields);

        final Map<String, String> typeTitleCash = new HashMap<>();

        ContainerCondition condition = new ContainerCondition();
        String listConditionSql = config.get("condition").asText();
        if (listConditionSql.contains("${currentUser}")) {
            User currentUser = userService.getCurrentUser();
            listConditionSql = listConditionSql.replaceAll("\\$\\{currentUser\\}", currentUser.getId().toString());
        }
        condition.setOperation(ContainerOperation.AND);
        Condition listCondition = new SqlCondition(listConditionSql);
        condition.getConditions().add(listCondition);
        if (clientCondition != null) {
            condition.getConditions().add(clientCondition);
        }
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
                } else if ("executionType".equals(renderer)) {
                    if ("execution".equals(value)) {
                        value = "Исполнение";
                    } else {
                        value = "[" + value + "]";
                    }
                    document.put(name, value);
                } else if ("executionStatus".equals(renderer)) {
                    if ("run".equals(value)) {
                        value = "Выполняется";
                    } else if ("complete".equals(value)){
                        value = "Завершено";
                    } else {
                        value = "[" + value + "]";
                    }
                    document.put(name, value);
                } else if ("user".equals(renderer)) {
                    if (Utils.isUUID(value)) {
                        User user = userService.getUserById(value);
                        document.put(name, user.getTitle());
                    } else {
                        document.put(name, "[" + value + "]");
                    }
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

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
