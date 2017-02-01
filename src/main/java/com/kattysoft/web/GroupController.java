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
import com.kattysoft.core.model.Dictionary;
import com.kattysoft.core.specification.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 27.01.2017
 */
@RestController
public class GroupController {
    public static final Integer DEFAULT_PAGE_SIZE = 20;

    @Autowired
    private GroupService groupService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private SpaceService spaceService;

    @Autowired
    private DocumentTypeService documentTypeService;

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private AccessRightService accessRightService;

    private ObjectMapper mapper = new ObjectMapper();

    @RequestMapping(value = "/groups")
    public ObjectNode getGroups(Integer offset, Integer size, String conditions, String sort, String sortAsc) throws IOException {
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

        Page<Group> contragents = groupService.getGroups(spec);
        List<ObjectNode> userNodes = contragents.getContent().stream().map(contragent ->
                (ObjectNode) mapper.valueToTree(contragent)
        ).collect(Collectors.toList());

        ObjectNode page = mapper.createObjectNode();
        page.putArray("data").addAll(userNodes);
        page.put("offset", offset);
        page.put("total", contragents.getTotal());

        return page;
    }

    @RequestMapping(value = "/groupcard")
    public ObjectNode getUserCard(String id) {
        if (id == null || id.isEmpty()) {
            throw new SokolException("Empty contragent id");
        }
        Group group = id.equals("new/group") ? new Group() : groupService.getGroupById(id);
        if (group == null) {
            throw new SokolException("Карточка группы не найдена");
        }

        JsonNode formConfig = configService.getConfig2("forms/groupForm");

        ObjectNode card = mapper.createObjectNode();
        card.set("form", formConfig);
        ObjectNode data = (ObjectNode) mapper.<JsonNode>valueToTree(group);
        card.set("data", data);
        card.put("containerType", "group");

        if (group.getId() != null) {
            ArrayNode subforms = mapper.createArrayNode();

            ObjectNode subformCard = mapper.createObjectNode();
            ObjectNode systemForm = mapper.createObjectNode();
            systemForm.put("id", "accessRightsGrid");
            subformCard.set("form", systemForm);

            subforms.add(subformCard);

            card.set("subforms", subforms);
        }

        return card;
    }

    @RequestMapping(value = "getAccessRightsElements")
    public ObjectNode getAccessRightsElements() {
        ObjectNode settings = mapper.createObjectNode();

        List<ObjectNode> spaces = spaceService.getSpaces().stream().map(s ->
            (ObjectNode) mapper.valueToTree(s)
        ).collect(Collectors.toList());
        ObjectNode spaceSystem = mapper.createObjectNode();
        spaceSystem.put("id", "_system");
        spaceSystem.put("title", "[Системные объекты]");
        spaces.add(spaceSystem);
        ObjectNode spaceDictionaries = mapper.createObjectNode();
        spaceDictionaries.put("id", "_dictionaries");
        spaceDictionaries.put("title", "[Справочники]");
        spaces.add(spaceDictionaries);
        settings.putArray("spaces").addAll(spaces);

        List<DocumentType> documentTypes = documentTypeService.getDocumentTypes();
        JsonNode documentTypesNode = mapper.valueToTree(documentTypes);
        ObjectNode anyDocumentType = mapper.createObjectNode();
        anyDocumentType.put("id", "_document");
        anyDocumentType.put("title", "[Документ]");
        ArrayNode fieldsTypes = anyDocumentType.putArray("fieldsTypes");
        Set<String> fieldsTypesIdsSet = new HashSet<>();
        documentTypesNode.forEach(node -> {
            node.get("fieldsTypes").forEach(f -> {
                String id = f.get("id").asText();
                if (!fieldsTypesIdsSet.contains(id)) {
                    fieldsTypesIdsSet.add(id);
                    ObjectNode fieldType = mapper.createObjectNode();
                    fieldType.put("id", id);
                    fieldType.put("title", f.get("title").asText());
                    fieldsTypes.add(fieldType);
                }
            });
        });

        ((ArrayNode) documentTypesNode).insert(0, anyDocumentType);
        settings.set("documentTypes", documentTypesNode);


        ArrayNode systemObjects = settings.putArray("systemObjects");
        ObjectNode usersSystemObject = mapper.createObjectNode();
        usersSystemObject.put("id", "users");
        usersSystemObject.put("title", "Пользователи");
        systemObjects.add(usersSystemObject);
        ObjectNode groupsSystemObject = mapper.createObjectNode();
        groupsSystemObject.put("id", "groups");
        groupsSystemObject.put("title", "Группы");
        systemObjects.add(groupsSystemObject);
        ObjectNode spacesSystemObject = mapper.createObjectNode();
        spacesSystemObject.put("id", "spaces");
        spacesSystemObject.put("title", "Пространства");
        systemObjects.add(spacesSystemObject);

        List<Dictionary> dictionaries = dictionaryService.getDictionaries();
        JsonNode dictionariesNode = mapper.valueToTree(dictionaries);
        dictionariesNode.forEach(n -> {
            String id = n.get("dictionaryId").asText();
            ((ObjectNode) n).put("id", id);
            ((ObjectNode) n).remove("dictionaryId");
        });
        settings.set("dictionaries", dictionariesNode);

        ArrayNode ac = settings.putArray("ac");
        ac.addAll(Arrays.asList("NONE", "READ", "WRITE", "CREATE", "ADD", "DELETE", "LIST").stream().map(s -> (JsonNode) mapper.valueToTree(s)).collect(Collectors.toList()));

        return settings;
    }

    @RequestMapping(value = "getAccessRightsRecordsForGroup")
    public ArrayNode getAccessRightsRecordsForGroup(String groupId) {
        if (groupId == null || groupId.isEmpty()) {
            throw new SokolException("GroupId is empty");
        }
        List<AccessRightRecord> records = accessRightService.getAccessRightsForGroup(groupId);
        ArrayNode recordsNodes = mapper.valueToTree(records);

        fillTitles(recordsNodes);

        return recordsNodes;
    }

    @RequestMapping("addAccessRightRecord")
    public ObjectNode addAccessRightRecord(String groupId, String data) throws IOException {
        if (groupId == null || groupId.isEmpty()) {
            throw new RuntimeException("groupId is null");
        }

        AccessRightRecord record = mapper.readValue(data, AccessRightRecord.class);
        record.setGroupId(UUID.fromString(groupId));

        String id = accessRightService.addRecord(record);
        AccessRightRecord reloadedValue = accessRightService.getRecord(id);
        ObjectNode reloadedValueNode = mapper.valueToTree(reloadedValue);
        fillTitles(reloadedValueNode);
        return reloadedValueNode;
    }

    private void fillTitles(JsonNode node) {

        Map<String, String> spaceTitles = new HashMap<>();
        spaceTitles.putAll(spaceService.getSpaces().stream().collect(Collectors.toMap(s -> s.getId().toString(), Space::getTitle)));
        spaceTitles.put("_system", "[Системные объекты]");
        spaceTitles.put("_dictionaries", "[Справочники]");

        Map<String, String> elementTitles = new HashMap<>();
        List<DocumentType> documentTypes = documentTypeService.getDocumentTypes();
        elementTitles.putAll(documentTypes.stream().collect(Collectors.toMap(DocumentType::getId, DocumentType::getTitle)));
        elementTitles.putAll(dictionaryService.getDictionaries().stream().collect(Collectors.toMap(Dictionary::getDictionaryId, Dictionary::getTitle)));
        elementTitles.put("spaces", "Пространства");
        elementTitles.put("groups", "Группы");
        elementTitles.put("users", "Пользователи");
        elementTitles.put("_document", "[Документ]");

        Map<String, String> subelementTitles = new HashMap<>();
        documentTypes.forEach(dt -> {
            dt.getFieldsTypes().forEach(ft -> {
                if (!subelementTitles.containsKey(ft.getId())) {
                    subelementTitles.put(ft.getId(), ft.getTitle());
                }
            });
        });

        Consumer<JsonNode> fillTitle = (rn -> {
            String space = rn.get("space").asText();
            String spaceTitle = spaceTitles.get(space);
            ((ObjectNode) rn).put("spaceTitle", spaceTitle);

            String element = rn.get("element").asText();
            String elementTitle = elementTitles.get(element);
            ((ObjectNode) rn).put("elementTitle", elementTitle);

            String subelement = rn.has("subelement") ? rn.get("subelement").asText() : null;
            String subelementTitle = subelement != null ? subelementTitles.get(subelement) : null;
            ((ObjectNode) rn).put("subelementTitle", subelementTitle);
        });

        if (node.isArray()) {
            node.forEach(fillTitle);
        } else {
            fillTitle.accept(node);
        }
    }

    public void setGroupService(GroupService groupService) {
        this.groupService = groupService;
    }

    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }

    public void setSpaceService(SpaceService spaceService) {
        this.spaceService = spaceService;
    }

    public void setDocumentTypeService(DocumentTypeService documentTypeService) {
        this.documentTypeService = documentTypeService;
    }

    public void setDictionaryService(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    public void setAccessRightService(AccessRightService accessRightService) {
        this.accessRightService = accessRightService;
    }
}
