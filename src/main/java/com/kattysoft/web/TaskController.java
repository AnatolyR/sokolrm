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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kattysoft.core.SokolException;
import com.kattysoft.core.TaskService;
import com.kattysoft.core.UserService;
import com.kattysoft.core.Utils;
import com.kattysoft.core.model.Task;
import com.kattysoft.core.model.TasksList;
import com.kattysoft.core.model.User;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 14.02.2017
 */
@RestController
public class TaskController {

    @Autowired
    private TaskService taskService;

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/saveExecutionList")
    public String saveExecution(Reader reader) throws IOException {
        String requestBody = IOUtils.toString(reader);
        ObjectNode data = (ObjectNode) mapper.readTree(requestBody);
        UUID uuid = data.has("id") && !data.get("id").asText().isEmpty() && !data.get("id").isNull() ? UUID.fromString(data.get("id").asText()) : null;
        ObjectNode fields = (ObjectNode) data.get("fields");

        TasksList tasksList = new TasksList();
        tasksList.setId(uuid);

        UUID documentId = UUID.fromString(data.get("documentId").asText());
        tasksList.setDocumentId(documentId);

        String comment = fields.get("comment").asText();
        tasksList.setComment(comment);

        tasksList.setType("resolution");

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

    public static List<String> types = Arrays.asList("resolution");
    @RequestMapping(value = "/getExecutionList")
    public ObjectNode getExecutionList(String documentId, String type) {
        if (documentId == null || documentId.isEmpty()) {
            throw new SokolException("Empty documentId");
        }

        if (type == null || type.isEmpty() || !types.contains(type)) {
            throw new SokolException("Wrong executionlist type");
        }

        TasksList taskList = taskService.getExecutionList(UUID.fromString(documentId), type);
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

            listNode.get("tasks").forEach(t -> {
                if (t.has("userId") && !t.get("userId").isNull() &&!t.get("userId").asText().isEmpty()) {
                    String taskUserId = t.get("userId").asText();
                    User taskUser = userService.getUserById(taskUserId);
                    ((ObjectNode) t).put("userTitle", taskUser.getTitle());
                } else {
                    ((ObjectNode) t).put("userTitle", "[Пользователь не найден]");
                }
            });

            return listNode;
        }

        return null;
    }

    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
