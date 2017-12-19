package com.kattysoft.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kattysoft.core.*;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 17.12.2017
 */
@RestController
public class EntityController {
    @Autowired
    private ConfigService configService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private AccessRightService accessRightService;
    
    private ObjectMapper mapper = new ObjectMapper();
    
    @RequestMapping(value = "/entity")
    public <T> ObjectNode getEntity(String type, String id) {
        if (id == null || id.isEmpty()) {
            throw new SokolException("Empty entity id");
        }

        JsonNode formConfig = configService.getConfig2("forms/" + type);
        String repositoryClassName = formConfig.get("repositoryClassName").asText();
        String entityClassName = formConfig.get("entityClassName").asText();
        String acType = formConfig.get("acType").asText();
        String acSpace = formConfig.get("acSpace").asText();

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

        Object entity = null;
        try {
            entity = id.equals("new/" + type) ? entityClass.getConstructor().newInstance() : entityService.getEntity(repositoryClass, entityClass, acType, id);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        if (entity == null) {
            throw new SokolException("Карточка не найдена");
        }

        if (accessRightService.checkRights(acSpace, acType, "", AccessRightLevel.DELETE)) {
            ((com.fasterxml.jackson.databind.node.ObjectNode) formConfig).put("deleteAction", true);
        }
        if (accessRightService.checkRights(acSpace, acType, "", AccessRightLevel.WRITE)) {
            ((com.fasterxml.jackson.databind.node.ObjectNode) formConfig).put("editAction", true);
        }
        
        ObjectNode card = mapper.createObjectNode();
        card.set("form", formConfig);
        ObjectNode data = (ObjectNode) mapper.<JsonNode>valueToTree(entity);
        card.set("data", data);
        card.put("containerType", type);

        JsonNode subforms = formConfig.get("subforms");
        if (subforms != null && !subforms.isNull() && subforms.isArray()) {
            card.set("subforms", subforms);
            ((ObjectNode) formConfig).remove("subforms");
        }

        return card;
    }

    @RequestMapping(value = "/saveEntity")
    public String saveEntity(Reader reader) throws IOException {
        String requestBody = IOUtils.toString(reader);
        ObjectNode data = (ObjectNode) mapper.readTree(requestBody);
        String type = data.get("saveType").asText();
        UUID uuid = data.has("id") && !data.get("id").asText().isEmpty() && !data.get("id").isNull() ? UUID.fromString(data.get("id").asText()) : null;
        ObjectNode fields = (ObjectNode) data.get("fields");

        JsonNode formConfig = configService.getConfig2("forms/" + type);
        String repositoryClassName = formConfig.get("repositoryClassName").asText();
        String entityClassName = formConfig.get("entityClassName").asText();
        String acType = formConfig.get("acType").asText();
        String acSpace = formConfig.get("acSpace").asText();

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
        
        Object entity = mapper.treeToValue(fields, entityClass);
        try {
            Field idField = entityClass.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, uuid);
        } catch (NoSuchFieldException | RuntimeException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } 

        String id = entityService.saveEntity(repositoryClass, entityClass, acSpace, acType, uuid, entity);

        return id;
    }

    @RequestMapping(value = "/deleteEntity")
    public String deleteEntity(String type, String id) {
        if (id == null || id.isEmpty()) {
            throw new SokolException("Entity id is empty");
        }

        JsonNode formConfig = configService.getConfig2("forms/" + type);
        String repositoryClassName = formConfig.get("repositoryClassName").asText();
        String entityClassName = formConfig.get("entityClassName").asText();
        String acType = formConfig.get("acType").asText();
        String acSpace = formConfig.get("acSpace").asText();

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
        
        entityService.deleteEntity(repositoryClass, entityClass, acSpace, acType, id);
        return "true";
    }

    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }

    public void setEntityService(EntityService entityService) {
        this.entityService = entityService;
    }

    public void setAccessRightService(AccessRightService accessRightService) {
        this.accessRightService = accessRightService;
    }
}
