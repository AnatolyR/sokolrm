package com.kattysoft.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kattysoft.core.ConfigService;
import com.kattysoft.core.ListService;
import com.kattysoft.core.model.Page;
import com.kattysoft.core.specification.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 16.12.2017
 */
@RestController
public class ListController {
    public static final Integer DEFAULT_PAGE_SIZE = 20;
    
    @Autowired
    private ListService listService;

    @Autowired
    private ConfigService configService;

    private ObjectMapper mapper = new ObjectMapper();

    @RequestMapping(value = "/list")
    public ObjectNode getList(String listId, Integer offset, Integer size, String conditions, String sort, String sortAsc) throws IOException {
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

        JsonNode config = configService.getConfig2("lists/" + listId);
        String repositoryClassName = config.get("repositoryClassName").asText();
        String entityClassName = config.get("entityClassName").asText();
        String sortField = config.has("sortField") ? config.get("sortField").asText() : null;
        String acType = config.get("acType").asText();
        String acSpace = config.get("acSpace").asText();
        
        Specification spec = new Specification();
        if (sort != null && !sort.isEmpty()) {
            Sort sortObject = new Sort();
            sortObject.setField(sort);
            sortObject.setOrder("true".equals(sortAsc) ? SortOrder.ASC : SortOrder.DESC);
            spec.setSort(Collections.singletonList(sortObject));
        } else if (sortField != null) {
            Sort sortObject = new Sort();
            sortObject.setField(sortField);
            sortObject.setOrder("true".equals(sortAsc) ? SortOrder.ASC : SortOrder.DESC);
            spec.setSort(Collections.singletonList(sortObject));
        }

        spec.setOffset(offset);
        spec.setSize(size);

        if (clientCondition != null) {
            spec.setCondition(clientCondition);
        }
        
        Class repositoryClass = null;
        try {
            repositoryClass = Class.forName(repositoryClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        Class entityClass = null;
        try {
            entityClass = Class.forName(entityClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        Page<Object> entities = listService.getEntities(repositoryClass, entityClass, acSpace, acType, spec);
        List<ObjectNode> entitiesNodes = entities.getContent().stream().map(entity
            -> (ObjectNode) mapper.valueToTree(entity)).collect(Collectors.toList());

        ObjectNode page = mapper.createObjectNode();
        page.putArray("data").addAll(entitiesNodes);
        page.put("offset", offset);
        page.put("total", entities.getTotal());

        return page;
    }

    public void setListService(ListService listService) {
        this.listService = listService;
    }

    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }
}
