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
import com.kattysoft.core.ConfigService;
import com.kattysoft.core.RegistrationListService;
import com.kattysoft.core.SokolException;
import com.kattysoft.core.SpaceService;
import com.kattysoft.core.model.*;
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
 * Date: 29.03.2017
 */
@RestController
public class RegistrationListController {
    private static final Logger log = LoggerFactory.getLogger(RegistrationListController.class);

    public static final Integer DEFAULT_PAGE_SIZE = 20;

    @Autowired
    private RegistrationListService registrationListService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private SpaceService spaceService;

    private ObjectMapper mapper = new ObjectMapper();

    @RequestMapping(value = "/registrationLists")
    public ObjectNode getRegistrationLists(Integer offset, Integer size, String conditions, String sort, String sortAsc) throws IOException, IOException {
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

        Page<RegistrationList> groups = registrationListService.getLists(spec);
        List<ObjectNode> groupNodes = groups.getContent().stream().map(group ->
                (ObjectNode) mapper.valueToTree(group)
        ).collect(Collectors.toList());

        ObjectNode page = mapper.createObjectNode();
        page.putArray("data").addAll(groupNodes);
        page.put("offset", offset);
        page.put("total", groups.getTotal());

        return page;
    }

    public void fillTitle(ObjectNode node) {
        ArrayNode groupsTitle = node.putArray("spacesTitle");
        node.get("spaces").forEach(g -> {
            Space space = null;
            try {
                space = spaceService.getSpace(g.asText());
            } catch (Exception e) {
                log.error("Can not read user from groups user field");
            }
            groupsTitle.add(space != null ? space.getTitle() : "[Пространство отсутствует]");
        });
    }

    @RequestMapping(value = "/deleteRegistrationList")
    public String deleteRegistrationList(String id) {
        if (id == null || id.isEmpty()) {
            throw new SokolException("Registration list id is empty");
        }
        registrationListService.deleteList(id);
        return "true";
    }

    @RequestMapping(value = "/registrationlistcard")
    public ObjectNode getCard(String id) {
        if (id == null || id.isEmpty()) {
            throw new SokolException("Empty registrationList id");
        }
        RegistrationList list = id.equals("new/registrationlist") ? new RegistrationList() : registrationListService.getListById(id);
        if (list == null) {
            throw new SokolException("Карточка журнала регистрации не найдена");
        }

        JsonNode formConfig = configService.getConfig2("forms/registrationListForm");

        ObjectNode card = mapper.createObjectNode();
        card.set("form", formConfig);
        ObjectNode data = (ObjectNode) mapper.<JsonNode>valueToTree(list);
        fillTitle(data);
        card.set("data", data);
        card.put("containerType", "registrationlist");

        return card;
    }

    @RequestMapping(value = "/saveRegistrationList")
    public String saveGroup(Reader reader) throws IOException {
        String requestBody = IOUtils.toString(reader);
        ObjectNode data = (ObjectNode) mapper.readTree(requestBody);
        UUID uuid = data.has("id") && !data.get("id").asText().isEmpty() && !data.get("id").isNull() ? UUID.fromString(data.get("id").asText()) : null;
        ObjectNode fields = (ObjectNode) data.get("fields");

        RegistrationList list = mapper.treeToValue(fields, RegistrationList.class);
        list.setId(uuid);

        String id = registrationListService.saveList(list);

        return id;
    }

    public void setRegistrationListService(RegistrationListService registrationListService) {
        this.registrationListService = registrationListService;
    }

    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }

    public void setSpaceService(SpaceService spaceService) {
        this.spaceService = spaceService;
    }
}
