/*
 * Copyright 2017 Anatolii Rakovskii (rtolik@yandex.ru)
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
import com.kattysoft.core.model.*;
import com.kattysoft.core.specification.*;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 14.02.2017
 */
@RestController
public class TaskController {
    public static final Integer DEFAULT_PAGE_SIZE = 20;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private TitleService titleService;

    @Autowired
    private AccessRightService accessRightService;

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private UserService userService;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    public TaskController() {
        mapper.setDateFormat(dateFormat);
    }

    @RequestMapping(value = "/saveExecutionList")
    public String saveExecution(Reader reader) throws IOException {
        String requestBody = IOUtils.toString(reader);
        ObjectNode data = (ObjectNode) mapper.readTree(requestBody);
        UUID uuid = data.has("id") && !data.get("id").asText().isEmpty() && !data.get("id").isNull() ? UUID.fromString(data.get("id").asText()) : null;
        ObjectNode fields = (ObjectNode) data.get("fields");
        String type = data.get("type").textValue();
        String documentId = data.get("documentId").asText();

        if (documentId == null || documentId.isEmpty()) {
            throw new SokolException("Empty documentId");
        }
        Document document = documentService.getDocument(documentId);
        if (!accessRightService.checkDocumentRights(document, "", AccessRightLevel.READ)) {
            throw new NoAccessRightsException("Not access rights to read document");
        }
        if ("resolution".equals(type)) {
            if (!accessRightService.checkDocumentRights(document, "*doresolution", AccessRightLevel.ALLOW)) {
                throw new NoAccessRightsException("Not access rights to start execution document");
            }
        } else if ("approval".equals(type)) {
            if (!accessRightService.checkDocumentRights(document, "*toapproval", AccessRightLevel.ALLOW)) {
                throw new NoAccessRightsException("Not access rights to start approval document");
            }
        } else if ("acquaintance".equals(type)) {
            if (!accessRightService.checkDocumentRights(document, "*toacquaintance", AccessRightLevel.ALLOW)) {
                throw new NoAccessRightsException("Not access rights to start acquaintance document");
            }
        } else {
            throw new SokolException("Wrong task list type");
        }

        TasksList tasksList = new TasksList();
        tasksList.setId(uuid);

        tasksList.setDocumentId(UUID.fromString(documentId));

        String comment = fields.get("comment").asText();
        tasksList.setComment(comment);

        if ("resolution".equals(type)) {
            tasksList.setType("execution");
        } else if ("approval".equals(type) || "acquaintance".equals(type)) {
            tasksList.setType(type);
        } else {
            throw new SokolException("Wrong task list type");
        }

        ArrayNode executors = (ArrayNode) data.get("executors");
        executors.forEach(e -> {
            Task task = new Task();

            UUID executorId = e.has("userId") && !e.get("userId").asText().isEmpty() && !e.get("userId").isNull() ? UUID.fromString(e.get("userId").asText()) : null;
            task.setUserId(executorId);

            Date dueDate = Utils.parseDateShort(e.get("dueDate").asText());
            task.setDueDate(dueDate);

            if (executorId != null && dueDate != null) {
                tasksList.getTasks().add(task);
            }
        });

        String id = taskService.saveExecutionList(tasksList);

        return id;
    }

    public static List<String> types = Arrays.asList("execution", "approval");
    @RequestMapping(value = "/getExecutionList")
    public ObjectNode getExecutionList(String documentId, String type) {
        if (documentId == null || documentId.isEmpty()) {
            throw new SokolException("Empty documentId");
        }
        Document document = documentService.getDocument(documentId);
        if (!accessRightService.checkDocumentRights(document, "", AccessRightLevel.READ)) {
            throw new NoAccessRightsException("Not access rights to read document");
        }

        if (type == null || type.isEmpty() || !types.contains(type)) {
            throw new SokolException("Wrong executionlist type");
        }

        TasksList taskList = taskService.getMainExecutionList(UUID.fromString(documentId), type);
        if (taskList != null) {
            ObjectNode listNode = mapper.valueToTree(taskList);
            UUID userId = taskList.getUserId();
            User user = userService.getUserById(userId.toString());
            if (user != null) {
                listNode.put("userIdTitle", user.getTitle());
            } else {
                listNode.put("userIdTitle", "[Пользователь не найден]");
            }
            listNode.put("created", Utils.formatDate(taskList.getCreated()));

            User currentUser = userService.getCurrentUser();
            listNode.put("editable", currentUser.getId().equals(userId));

            listNode.get("tasks").forEach(t -> {
                if (t.has("userId") && !t.get("userId").isNull() &&!t.get("userId").asText().isEmpty()) {
                    String taskUserId = t.get("userId").asText();
                    User taskUser = userService.getUserById(taskUserId);
                    ((ObjectNode) t).put("userTitle", taskUser.getTitle());
                } else {
                    ((ObjectNode) t).put("userTitle", "[Пользователь не найден]");
                }
                if (t.has("status") && !t.get("status").isNull() &&!t.get("status").asText().isEmpty()) {
                    String statusTitle = titleService.getTitleNotNull("executionStatus", t.get("status").asText());
                    ((ObjectNode) t).put("status", statusTitle);
                }
                if (t.has("result") && !t.get("result").isNull() &&!t.get("result").asText().isEmpty()) {
                    String resultTitle = titleService.getTitleNotNull("executionResult", t.get("result").asText());
                    ((ObjectNode) t).put("result", resultTitle);
                }
            });

            return listNode;
        }

        return null;
    }

    @RequestMapping(value = "/tasks")
    public ObjectNode getTasks(Integer offset, Integer size, String conditions, String sort, String sortAsc) throws IOException {
        if (offset == null) {
            offset = 0;
        }
        if (size == null) {
            size =  DEFAULT_PAGE_SIZE;
        }

        if (conditions == null || conditions.isEmpty()) {
            conditions = "[]";
        }
        JsonNode clientConditionsNode = mapper.readTree(conditions);

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

        if (clientCondition != null) {
            spec.setCondition(clientCondition);
        }

        Page<Task> tasks = taskService.getTasks(spec);
        List<ObjectNode> taskNodes = tasks.getContent().stream().map(task
            -> (ObjectNode) mapper.valueToTree(task)).collect(Collectors.toList());

        ObjectNode page = mapper.createObjectNode();
        page.putArray("data").addAll(taskNodes);
        page.put("offset", offset);
        page.put("total", tasks.getTotal());

        return page;
    }

    @RequestMapping(value = "/taskcard", produces = "application/json; charset=utf-8")
    public String getTaskCard(String id) {
        if (id == null || !Utils.isUUID(id)) {
            throw new SokolException("Task id wrong");
        }
        Task task = taskService.getTaskById(UUID.fromString(id));
        User currentUser = userService.getCurrentUser();
        if (!task.getUserId().equals(currentUser.getId()) && !task.getAuthor().equals(currentUser.getId())) {
            throw new NoAccessRightsException("Current user not executor or author of this task");
        }
        UUID documentId = task.getDocumentId();

        Document document = documentService.getDocument(documentId.toString());
        if (document == null) {
            throw new SokolException("Документ не найден");
        }

        String typeId = document.getType();
        JsonNode typeConfig = configService.getConfig2("types/" + typeId + "Type");
        JsonNode formConfig = configService.getConfig2("forms/" + typeId + "Form");
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

        ((ObjectNode) formConfig).remove("actions");
        fillDocumentTitles(document);

        ObjectNode card = mapper.createObjectNode();
        card.put("form", formConfig);

        ObjectNode data = (ObjectNode) mapper.<JsonNode>valueToTree(document);
        ObjectNode fields = (ObjectNode) data.get("fields");
        data.remove("fields");
        data.putAll(fields);
        card.put("data", data);

        ArrayNode subforms = mapper.createArrayNode();
        ObjectNode subformCard = mapper.createObjectNode();
        ObjectNode taskForm = mapper.createObjectNode();
        taskForm.put("id", "task");
        subformCard.set("form", taskForm);
        ObjectNode taskData = (ObjectNode) mapper.<JsonNode>valueToTree(task);
        if (taskData.has("author") && !taskData.get("author").isNull()) {
            String author = taskData.get("author").asText();
            if (Utils.isUUID(author)) {
                User user = userService.getUserById(author);
                taskData.put("authorTitle", user.getTitle());
            }
        }

        TasksList executionList = taskService.getExecutionListById(task.getListId());
        List<String> executors = executionList.getTasks().stream()
            .map(Task::getUserId).map(UUID::toString).collect(Collectors.toList());
        List<String> executorsTitle = executors.stream()
            .map(u -> userService.getUserById(u).getTitle()).collect(Collectors.toList());

        taskData.set("executors", mapper.valueToTree(executors));
        taskData.set("executorsTitle", mapper.valueToTree(executorsTitle));

        if (task.getUserId().equals(currentUser.getId())) {
            taskData.put("editable", true);
        }

        subformCard.set("data", taskData);
        subforms.add(subformCard);
        card.set("subforms", subforms);

        return card.toString();
    }

    private void fillDocumentTitles(Document document) {
        String statusId = document.getStatus();
        String statusTitle = titleService.getTitle("status", statusId);
        document.getFields().put("status", statusTitle);
    }

    @RequestMapping(value = "/saveTaskReport")
    public Task saveTaskReport(Reader reader) throws IOException {
        String requestBody = IOUtils.toString(reader);
        ObjectNode data = (ObjectNode) mapper.readTree(requestBody);
        UUID uuid = data.has("id") && !data.get("id").asText().isEmpty() && !data.get("id").isNull() ? UUID.fromString(data.get("id").asText()) : null;

        if (uuid == null) {
            throw new SokolException("Task id is null");
        }

        Task existTask = taskService.getTaskById(uuid);
        User currentUser = userService.getCurrentUser();
        if (!existTask.getUserId().equals(currentUser.getId())) {
            throw new NoAccessRightsException("Current user not executor of this task");
        }

        ObjectNode fields = (ObjectNode) data.get("fields");

        Task task = new Task();
        task.setId(uuid);

        String comment = fields.has("comment") ? fields.get("comment").asText() : "";
        task.setComment(comment);

        String result = fields.get("result").asText();
        task.setResult(result);

        taskService.completeTask(task);
        Task savedTask = taskService.getTaskById(uuid);

        return savedTask;
    }

    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setTitleService(TitleService titleService) {
        this.titleService = titleService;
    }

    public void setAccessRightService(AccessRightService accessRightService) {
        this.accessRightService = accessRightService;
    }
}
