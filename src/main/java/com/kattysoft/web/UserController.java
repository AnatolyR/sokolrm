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
import com.kattysoft.core.model.Group;
import com.kattysoft.core.model.Page;
import com.kattysoft.core.model.User;
import com.kattysoft.core.specification.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 15.01.2017
 */
@RestController
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    public static final Integer DEFAULT_PAGE_SIZE = 20;

    @Autowired
    private UserService userService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private GroupService groupService;
    
    @Autowired
    private AccessRightService accessRightService;

    private ObjectMapper mapper = new ObjectMapper();

    @RequestMapping(value = "/users")
    public ObjectNode getUsers(Integer offset, Integer size, String conditions, String sort, String sortAsc, String searchtext) throws IOException {
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
        clientConditionsNode.forEach(c -> {
            if ("groupsTitle".equals(c.get("column").asText())) {
                String value = c.has("value") ? c.get("value").asText() : "";
                Group group = groupService.getGroupByTitle(value);
                if (group != null) {
                    ((ObjectNode) c).put("column", "groups");
                    ((ObjectNode) c).put("value", group.getId().toString());
                } else {
                    ((ObjectNode) c).put("column", "");
                }
            }
        });

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
            if (searchtext != null && !searchtext.isEmpty()) {
                ContainerCondition and = new ContainerCondition(ContainerOperation.AND, new ValueCondition("title", Operation.FULLTEXTSEARCH, searchtext));
                and.getConditions().add(clientCondition);
                spec.setCondition(and);
            } else {
                spec.setCondition(clientCondition);
            }
        } else {
            if (searchtext != null && !searchtext.isEmpty()) {
                ContainerCondition and = new ContainerCondition(ContainerOperation.AND, new ValueCondition("title", Operation.FULLTEXTSEARCH, searchtext));
                spec.setCondition(and);
            }
        }

        Page<User> users = userService.getUsers(spec);
        List<ObjectNode> userNodes = users.getContent().stream().map(user -> {
            ObjectNode node = (ObjectNode) mapper.valueToTree(user);
            ArrayNode groups = mapper.valueToTree(user.getGroups());
            node.set("groups", groups);
            return node;
        }).collect(Collectors.toList());

        userNodes.forEach(this::fillTitle);

        ObjectNode page = mapper.createObjectNode();
        page.putArray("data").addAll(userNodes);
        page.put("offset", offset);
        page.put("total", users.getTotal());

        return page;
    }

    public void fillTitle(ObjectNode node) {
        ArrayNode groupsTitle = node.putArray("groupsTitle");
        if (node.has("groups")) {
            node.get("groups").forEach(g -> {
                Group group = null;
                try {
                    group = groupService.getGroupById(g.asText());
                } catch (Exception e) {
                    log.error("Can not read group from user groups field");
                }
                groupsTitle.add(group != null ? group.getTitle() : "[Группа отсутствует]");
            });
        }
    }

    @RequestMapping(value = "/usercard")
    public ObjectNode getUserCard(String id) {
        if (id == null || id.isEmpty()) {
            throw new SokolException("Empty user id");
        }
        User user = id.equals("new/user") ? new User() : userService.getUserById(id);
        if (user == null) {
            throw new SokolException("Карточка пользователя не найдена");
        }

        JsonNode formConfig = configService.getConfig2("forms/userForm");

        if (accessRightService.checkRights("_system", "users", "", AccessRightLevel.DELETE)) {
            ((com.fasterxml.jackson.databind.node.ObjectNode) formConfig).put("deleteAction", true);
        }
        if (accessRightService.checkRights("_system", "users", "", AccessRightLevel.WRITE)) {
            ((com.fasterxml.jackson.databind.node.ObjectNode) formConfig).put("editAction", true);
        }

        ObjectNode card = mapper.createObjectNode();
        card.set("form", formConfig);
        ObjectNode data = (ObjectNode) mapper.<JsonNode>valueToTree(user);
        ArrayNode groups = mapper.valueToTree(user.getGroups());
        data.set("groups", groups);

        fillTitle(data);

        card.set("data", data);
        card.put("containerType", "user");

        if (user.getId() != null) {
            ArrayNode subforms = mapper.createArrayNode();

            ObjectNode subformCard = mapper.createObjectNode();
            JsonNode systemForm = configService.getConfig2("forms/userSystemForm");
            subformCard.set("form", systemForm);

            subforms.add(subformCard);

            card.set("subforms", subforms);
        }

        return card;
    }

    @RequestMapping(value = "/saveuser")
    public String saveUser(Reader reader) throws IOException {
        String requestBody = IOUtils.toString(reader);
        ObjectNode data = (ObjectNode) mapper.readTree(requestBody);
        UUID uuid = data.has("id") && !data.get("id").asText().isEmpty() && !data.get("id").isNull() ? UUID.fromString(data.get("id").asText()) : null;
        ObjectNode fields = (ObjectNode) data.get("fields");

        User user = mapper.treeToValue(fields, User.class);
        user.setId(uuid);

        JsonNode systemUserForm = data.get("userSystem");
        if (systemUserForm != null) {
            JsonNode systemFields = systemUserForm.get("fields");
            String login = systemFields.get("login").asText();
            String email = systemFields.get("email").asText();
            user.setLogin(login);

            ArrayNode groups = (ArrayNode) systemFields.get("groups");
            groups.forEach(g -> user.getGroups().add(UUID.fromString(g.asText())));
        }

        String id = userService.saveUser(user);

        return id;
    }

    @RequestMapping(value = "/saveProfile")
    public String saveProfile(Reader reader) throws IOException {
        String requestBody = IOUtils.toString(reader);
        ObjectNode data = (ObjectNode) mapper.readTree(requestBody);

        ObjectNode fields = (ObjectNode) data.get("fields");

        String oldPassword = fields.get("oldPassword").asText();
        String newPassword = fields.get("newPassword").asText();
        String newPasswordConfirm = fields.get("newPasswordConfirm").asText();
        if (!newPassword.equals(newPasswordConfirm)) {
            throw new SokolException("Подтверждение пароля некорректное");
        }

        User currentUser = userService.getCurrentUser();
        User userByLoginAndPassword = userService.getUserByLoginAndPassword(currentUser.getLogin(), oldPassword);
        if (userByLoginAndPassword == null) {
            throw new SokolException("Неверный старый пароль");
        }

        //todo check password for rules
        String id = userService.savePassword(currentUser.getId().toString(), newPassword);

        return id;
    }

    @RequestMapping(value = "/deleteuser")
    public String deleteUser(String id) {
        if (id == null || id.isEmpty()) {
            throw new SokolException("User id is empty");
        }
        userService.deleteUser(id);
        return "true";
    }

    @RequestMapping(value = "/resetPassword")
    public String resetPassword(String id) {
        if (id == null || id.isEmpty()) {
            throw new SokolException("User id is empty");
        }
        String password = userService.resetPassword(id);
        return password;
    }


    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }

    public void setGroupService(GroupService groupService) {
        this.groupService = groupService;
    }

    public void setAccessRightService(AccessRightService accessRightService) {
        this.accessRightService = accessRightService;
    }
}
