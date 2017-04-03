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
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 27.01.2017
 */
@RestController
public class GroupController {
    private static final Logger log = LoggerFactory.getLogger(GroupController.class);

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

    @Autowired
    private TitleService titleService;

    @Autowired
    private UserService userService;

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

        Page<Group> groups = groupService.getGroups(spec);
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
        ArrayNode groupsTitle = node.putArray("usersTitle");
        node.get("users").forEach(g -> {
            User user = null;
            try {
                user = userService.getUserById(g.asText());
            } catch (Exception e) {
                log.error("Can not read user from groups user field");
            }
            groupsTitle.add(user != null ? user.getTitle() : "[Пользовтель отсутствует]");
        });
    }

    @RequestMapping(value = "/groupcard")
    public ObjectNode getCard(String id) {
        if (id == null || id.isEmpty()) {
            throw new SokolException("Empty group id");
        }
        Group group = id.equals("new/group") ? new Group() : groupService.getGroupById(id);
        if (group == null) {
            throw new SokolException("Карточка группы не найдена");
        }

        JsonNode formConfig = configService.getConfig2("forms/groupForm");

        ObjectNode card = mapper.createObjectNode();
        card.set("form", formConfig);
        ObjectNode data = (ObjectNode) mapper.<JsonNode>valueToTree(group);
        fillTitle(data);
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
        ObjectNode spaceDocuments = mapper.createObjectNode();
        spaceDocuments.put("id", "_space");
        spaceDocuments.put("title", "[Документы]");
        spaces.add(spaceDocuments);
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

        JsonNode accessRightsElements = configService.getConfig2("accessRightsElements");

        addActions(documentTypesNode, anyDocumentType);

        ((ArrayNode) documentTypesNode).insert(0, anyDocumentType);
        settings.set("documentTypes", documentTypesNode);

        settings.set("systemObjects", accessRightsElements.get(0).get("elements"));

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

    private void addActions(JsonNode documentTypesNode, ObjectNode anyDocumentType) {
        ArrayNode anyTypeActions = anyDocumentType.putArray("actions");
        Set<String> anyTypeActionIdSet = new HashSet<>();
        documentTypesNode.forEach(type -> {
            ArrayNode typeActions = mapper.createArrayNode();
            if (type.has("flow") && !type.get("flow").isNull()) {
                String flowId = type.get("flow").textValue();
                JsonNode flow = configService.getConfig2("flows/" + flowId);
                Set<String> typeActionIdSet = new HashSet<>();
                flow.get("states").forEach(s -> {
                    if (s.has("actions")) {
                        s.get("actions").forEach(a -> {
                            String actionId = a.get("id").textValue();
                            if (!typeActionIdSet.contains(actionId)) {
                                typeActionIdSet.add(actionId);
                                ObjectNode action = mapper.createObjectNode();
                                action.put("id", actionId);
                                String actionTitle = titleService.getTitleNotNull("action", actionId);
                                action.put("title", actionTitle);
                                typeActions.add(action);
                                if (!anyTypeActionIdSet.contains(actionId)) {
                                    anyTypeActionIdSet.add(actionId);
                                    anyTypeActions.add(action);
                                }
                            }
                        });
                    }
                });
            }
            ((ObjectNode) type).set("actions", typeActions);
        });
    }

    @RequestMapping(value = "getAccessRightsRecordsForGroup")
    public List<JsonNode> getAccessRightsRecordsForGroup(String groupId) {
        if (groupId == null || groupId.isEmpty()) {
            throw new SokolException("GroupId is empty");
        }
        List<AccessRightRecord> records = accessRightService.getAccessRightsForGroup(groupId);
        ArrayNode recordsNodes = mapper.valueToTree(records);

        fillTitles(recordsNodes);

        List<JsonNode> nodesList = new ArrayList<>();
        recordsNodes.forEach(nodesList::add);
        Collections.sort(nodesList, this::compareAR);

        return nodesList;
    }

    public static final String[] sortFields = {"spaceTitle", "elementTitle", "subelementTitle", "level"};
    private int compareAR(JsonNode o1, JsonNode o2) {
        for (String field : sortFields) {
            String val1 = o1.has(field) ? o1.get(field).asText() : "";
            String val2 = o2.has(field) ? o2.get(field).asText() : "";
            int compare = val1.compareTo(val2);
            if (compare != 0) {
                return compare;
            }
        }
        return 0;
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

    @RequestMapping("deleteAccessRightRecord")
    public String deleteAccessRightRecord(String[] ids) {
        accessRightService.deleteRecords(Arrays.asList(ids));
        return "true";
    }

    @RequestMapping(value = "/saveGroup")
    public String saveGroup(Reader reader) throws IOException {
        String requestBody = IOUtils.toString(reader);
        ObjectNode data = (ObjectNode) mapper.readTree(requestBody);
        UUID uuid = data.has("id") && !data.get("id").asText().isEmpty() && !data.get("id").isNull() ? UUID.fromString(data.get("id").asText()) : null;
        ObjectNode fields = (ObjectNode) data.get("fields");

        Group group = mapper.treeToValue(fields, Group.class);
        group.setId(uuid);

        String id = groupService.saveGroup(group);

        return id;
    }

    @RequestMapping(value = "/deleteGroup")
    public String deleteGroup(String id) {
        if (id == null || id.isEmpty()) {
            throw new SokolException("Group id is empty");
        }
        groupService.deleteGroup(id);
        return "true";
    }

    private void fillTitles(JsonNode node) {

        Map<String, String> spaceTitles = new HashMap<>();
        spaceTitles.putAll(spaceService.getSpaces().stream().collect(Collectors.toMap(s -> s.getId().toString(), Space::getTitle)));
        spaceTitles.put("_system", "[Системные объекты]");
        spaceTitles.put("_dictionaries", "[Справочники]");
        spaceTitles.put("_space", "[Документы]");

        Map<String, String> elementTitles = new HashMap<>();
        List<DocumentType> documentTypes = documentTypeService.getDocumentTypes();
        elementTitles.putAll(documentTypes.stream().collect(Collectors.toMap(DocumentType::getId, DocumentType::getTitle)));
        elementTitles.putAll(dictionaryService.getDictionaries().stream().collect(Collectors.toMap(Dictionary::getDictionaryId, Dictionary::getTitle)));
        elementTitles.put("spaces", "Пространства");

        JsonNode accessRightsElements = configService.getConfig2("accessRightsElements");
        JsonNode systemElements = accessRightsElements.get(0).get("elements");
        systemElements.forEach(n -> elementTitles.put(n.get("id").asText(), n.get("title").asText()));

        elementTitles.put("_document", "[Документ]");

        Map<String, String> subelementTitles = new HashMap<>();
        JsonNode documentActions = accessRightsElements.get(1).get("actions");
        documentActions.forEach(n -> subelementTitles.put(n.get("id").asText(), "{" + n.get("title").asText() + "}"));

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
            String subelementTitle;
            if (subelement != null && subelement.startsWith("*")) {
                subelementTitle = titleService.getTitleNotNull("action", subelement.substring(1));
            } else {
                subelementTitle = subelement != null ? subelementTitles.get(subelement) : null;
            }
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

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setTitleService(TitleService titleService) {
        this.titleService = titleService;
    }
}
