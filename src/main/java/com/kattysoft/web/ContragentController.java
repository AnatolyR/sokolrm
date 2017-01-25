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
import com.kattysoft.core.ContragentService;
import com.kattysoft.core.SokolException;
import com.kattysoft.core.model.Contragent;
import com.kattysoft.core.model.Page;
import com.kattysoft.core.specification.*;
import org.apache.commons.io.IOUtils;
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
 * Date: 18.01.2017
 */
@RestController
public class ContragentController {
    public static final Integer DEFAULT_PAGE_SIZE = 20;

    @Autowired
    private ContragentService contragentService;

    @Autowired
    private ConfigService configService;

    private ObjectMapper mapper = new ObjectMapper();

    @RequestMapping(value = "/contragents")
    public ObjectNode getContragents(Integer offset, Integer size, String conditions, String sort, String sortAsc) throws IOException {
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

        Page<Contragent> contragents = contragentService.getContragents(spec);
        List<ObjectNode> userNodes = contragents.getContent().stream().map(contragent ->
                (ObjectNode) mapper.valueToTree(contragent)
        ).collect(Collectors.toList());

        ObjectNode page = mapper.createObjectNode();
        page.putArray("data").addAll(userNodes);
        page.put("offset", offset);
        page.put("total", contragents.getTotal());

        return page;
    }

    @RequestMapping(value = "/contragentcard")
    public ObjectNode getUserCard(String id) {
        if (id == null || id.isEmpty()) {
            throw new SokolException("Empty contragent id");
        }
        Contragent contragent = id.equals("new/contragent") ? new Contragent() : contragentService.getContragentById(id);
        if (contragent == null) {
            throw new SokolException("Карточка контрагента не найдена");
        }

        JsonNode formConfig = configService.getConfig2("forms/contragentForm");

        ObjectNode card = mapper.createObjectNode();
        card.set("form", formConfig);
        ObjectNode data = (ObjectNode) mapper.<JsonNode>valueToTree(contragent);
        card.set("data", data);
        card.put("containerType", "contragent");

        return card;
    }

    @RequestMapping(value = "/savecontragent")
    public String saveUser(Reader reader) throws IOException {
        String requestBody = IOUtils.toString(reader);
        ObjectNode data = (ObjectNode) mapper.readTree(requestBody);
        UUID uuid = data.has("id") && !data.get("id").asText().isEmpty() && !data.get("id").isNull() ? UUID.fromString(data.get("id").asText()) : null;
        ObjectNode fields = (ObjectNode) data.get("fields");

        Contragent contragent = mapper.treeToValue(fields, Contragent.class);
        contragent.setId(uuid);

        String id = contragentService.saveContragent(contragent);

        return id;
    }

    @RequestMapping(value = "/deletecontragent")
    public String deleteContragent(String id) {
        if (id == null || id.isEmpty()) {
            throw new SokolException("Contragent id is empty");
        }
        contragentService.deleteContragent(id);
        return "true";
    }

    public void setContragentService(ContragentService contragentService) {
        this.contragentService = contragentService;
    }

    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }
}
