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
import com.kattysoft.core.*;
import com.kattysoft.core.model.Document;
import com.kattysoft.core.model.Space;
import com.kattysoft.core.model.User;
import com.kattysoft.core.specification.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.SimpleDateFormat;
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

    @Autowired
    private TitleService titleService;

    @Autowired
    private SpaceService spaceService;

    private ObjectMapper mapper = new ObjectMapper();

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    public DocumentListController() {
        mapper.setDateFormat(dateFormat);
    }

    @RequestMapping(value = "/documents")
    public Object listDocuments(String listId, Integer offset, Integer size, String conditions, String sort, String sortAsc, String searchtext, String docId) throws IOException {
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
                        if ("executionType".equals(render) || "executionStatus".equals(render) || "status".equals(render)) {
                            String name = titleService.getName(render, value);
                            ((ObjectNode) jsonNode).put("value", name);
                        } else if ("executionResult".equals(render)) {
                            String name = titleService.getName("executionResult", value);
                            ((ObjectNode) jsonNode).put("value", name);
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

        if (searchtext != null && !searchtext.isEmpty()) {
            spec.setSearchText(searchtext);
        }

        spec.setOffset(offset);
        spec.setSize(size);
        List<String> fields = StreamSupport.stream(config.get("gridConfig").get("columns").spliterator(), false)
            .map(node -> node.get("id").asText()).collect(Collectors.toList());
        if (fields.contains("textheadline")) {
            fields.remove("textheadline");
        }
        spec.setFields(fields);

        final Map<String, String> typeTitleCash = new HashMap<>();
        final Map<String, Map<String, String>> flowStatusTitleCash = new HashMap<>();

        ContainerCondition condition = new ContainerCondition();
        String listConditionSql = config.get("condition").asText();
        if (listConditionSql.contains("${currentUser}")) {
            User currentUser = userService.getCurrentUser();
            listConditionSql = listConditionSql.replaceAll("\\$\\{currentUser\\}", currentUser.getId().toString());
        }
        if (docId != null && !docId.isEmpty()) {
            if (listConditionSql.contains("${docId}")) {
                UUID docIdUuid = UUID.fromString(docId);
                listConditionSql = listConditionSql.replaceAll("\\$\\{docId\\}", docIdUuid.toString());
            }
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
            if (document.has("d.type")) {
                document.set("type", document.get("d.type"));
            }

            processServerRenders(document, fieldsRenders, typeTitleCash, flowStatusTitleCash);

            return document;
        }).collect(Collectors.toList());
        page.putArray("data").addAll(documentsNodes);
        page.put("offset", offset);
        page.put("total", total);

        return page;
    }

    protected void processServerRenders(ObjectNode document, Map<String, String> fieldsRenders, final Map<String, String> typeTitleCash, Map<String, Map<String, String>> flowStatusTitleCash) {
        String typeId = document.has("type") ? document.get("type").textValue() : null;
        fieldsRenders.entrySet().forEach(e -> {
            String name = e.getKey();
            String renderer = e.getValue();
            if (document.get(name) != null) {
                String value = document.has(name) && !document.get(name).isNull() ? document.get(name).asText() : "";
                if ("doctype".equals(renderer)) {
                    String type = value;
                    cacheTypeData(type, typeTitleCash, flowStatusTitleCash);
                    String typeTitle = typeTitleCash.get(type);
                    document.put(name, typeTitle);
                } else if ("status".equals(renderer)) {
//                    cacheTypeData(typeId, typeTitleCash, flowStatusTitleCash);
//                    String status = value;
//                    String statusTitle = status != null && flowStatusTitleCash.containsKey(typeId) ? flowStatusTitleCash.get(typeId).get(status) : "";
                    String titleValue = titleService.getTitleNotNull(renderer, value);
                    document.put(name, titleValue);
                } else if ("executionType".equals(renderer) || "executionStatus".equals(renderer)) {
                    String titleValue = titleService.getTitleNotNull(renderer, value);
                    document.put(name, titleValue);
                } else if ("executionResult".equals(renderer)) {
                    String titleValue = titleService.getTitleNotNull(renderer, value);
                    document.put(name, titleValue);
                } else if ("space".equals(renderer)) {
                    if (value == null || value.isEmpty()) {
                        document.put(name, "");
                    } else if (Utils.isUUID(value)) {
                        Space space = spaceService.getSpace(value);
                        document.put(name, space.getTitle());
                    } else {
                        document.put(name, "[" + value + "]");
                    }
                } else if ("user".equals(renderer)) {
                    if (value == null || value.isEmpty()) {
                        document.put(name, "");
                    } else if (Utils.isUUID(value)) {
                        User user = userService.getUserById(value);
                        document.put(name, user.getTitle());
                    } else {
                        document.put(name, "[" + value + "]");
                    }
                }
            }
        });
    }

    private void cacheTypeData(String type, Map<String, String> typeTitleCash, Map<String, Map<String, String>> flowStatusTitleCash) {
        if (!typeTitleCash.containsKey(type)) {
            JsonNode typeConfig = configService.getConfig2("types/" + type + "Type");
            String typeTitle = typeConfig != null ? typeConfig.get("title").asText() : "[" + type + "]";
            typeTitleCash.put(type, typeTitle);

            String flowId = typeConfig != null && typeConfig.has("flow") ? typeConfig.get("flow").textValue() : null;
            if (flowId != null) {
                JsonNode flow = configService.getConfig2("flows/" + flowId);
                JsonNode states = flow.get("states");
                states.forEach(state -> {
                    String stateId = state.get("id").textValue();
                    String stateTitle = state.get("title").textValue();
                    if (!flowStatusTitleCash.containsKey(type)) {
                        flowStatusTitleCash.put(type, new HashMap<>());
                    }
                    flowStatusTitleCash.get(type).put(stateId, stateTitle);
                });
            }
        }
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

    public void setTitleService(TitleService titleService) {
        this.titleService = titleService;
    }

    public void setSpaceService(SpaceService spaceService) {
        this.spaceService = spaceService;
    }
}
