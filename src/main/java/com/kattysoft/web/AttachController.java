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
import com.kattysoft.core.model.*;
import com.kattysoft.core.specification.*;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 18.01.2017
 */
@RestController
public class AttachController {  
    private static final Logger log = LoggerFactory.getLogger(AttachController.class);
    
    public static final Integer DEFAULT_PAGE_SIZE = 20;
    
    @Autowired
    private AttachService attachService;

    @Autowired
    private TitleService titleService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @Autowired
    private ContragentService contragentService;

    @Autowired
    private ReportService reportService;
    
    @Autowired
    private ConfigService configService;

    @Autowired
    private GroupService groupService;
    
    private ObjectMapper mapper = new ObjectMapper();

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    public AttachController() {
        mapper.setDateFormat(dateFormat);
    }

    @RequestMapping(value = "/attaches")
    public List<ObjectNode> getAttaches(String id) {
        List<Attach> attaches = attachService.getAttachesForObject(id);
        List<ObjectNode> attachesNodes = attaches.stream().map(attach -> {
            ObjectNode node = (ObjectNode) mapper.valueToTree(attach);
            Integer iSize = attach.getSize();
            String size = iSize != null ? FileUtils.byteCountToDisplaySize(iSize) : "";
            node.put("size", size);
            return node;
        }).collect(Collectors.toList());
        return attachesNodes;
    }


    @RequestMapping(value = "/deleteAttach")
    public String deleteAttach(String id) {
        if (id == null || id.isEmpty()) {
            throw new SokolException("Attach id is empty");
        }
        attachService.deleteAttach(id);
        return "true";
    }

    @RequestMapping(value = "/searchfiles")
    public ObjectNode getAttaches(Integer offset, Integer size, String conditions, String sort, String sortAsc, String searchtext) throws IOException {
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
            if (searchtext != null && !searchtext.isEmpty()) {
                ContainerCondition and = new ContainerCondition(ContainerOperation.AND, new ValueCondition("searchtext", Operation.FULLTEXTSEARCH, searchtext));
                and.getConditions().add(clientCondition);
                spec.setCondition(and);
            } else {
                spec.setCondition(clientCondition);
            }
        } else {
            if (searchtext != null && !searchtext.isEmpty()) {
                ContainerCondition and = new ContainerCondition(ContainerOperation.AND, new ValueCondition("searchtext", Operation.FULLTEXTSEARCH, searchtext));
                spec.setCondition(and);
            }
        }

        Page<Attach> contragents = attachService.getAttaches(spec);
        contragents.getContent().stream().forEach(contragent -> {
            contragent.setSearchtext("");           
        });
        List<ObjectNode> userNodes = contragents.getContent().stream().map(contragent ->
                (ObjectNode) mapper.valueToTree(contragent)
        ).collect(Collectors.toList());
        userNodes.stream().forEach(n -> {
            String objectId = n.has("objectId") && !n.get("objectId").isNull() ? n.get("objectId").asText() : null;
            String objectType = n.has("objectType") && !n.get("objectType").isNull() ? n.get("objectType").asText() : "";
            String objectTypeTitle = titleService.getTitleNotNull("objectTypes", objectType);
            n.put("objectTypeTitle", objectTypeTitle);

            String objectTitle = getObjectTitle(objectType, objectId);
            n.put("objectTitle", objectTitle);
            
            if ("task".equals(objectType)) { //todo аттачи задач это аттачи документов, чтобы правильна работала ссылка, разобраться
                n.put("objectType", "document");
            }

            if ("report".equals(objectType)) {
                String[] split = objectTitle.split(":");
                n.put("objectId", split[0]);
                n.put("objectTitle", split[1]);
            }
        });

        ObjectNode page = mapper.createObjectNode();
        page.putArray("data").addAll(userNodes);
        page.put("offset", offset);
        page.put("total", contragents.getTotal());

        return page;
    }

    private String getObjectTitle(String objectType, String objectId) {
        if (!Utils.isUUID(objectId)) {
            return "[" + objectId + "]";
        }
        UUID uuid = UUID.fromString(objectId);
        String objectTitle = "";
        try {
            switch (objectType) {
                case "document":
                    Document document = documentService.getDocument(objectId); //todo optimize
                    if (document != null) {
                        objectTitle = document.getTitle();
                    }
                    break;
                case "task":
//                    Task task = taskService.getTaskById(uuid);
//                    if (task != null) {
//                        UUID documentId = task.getDocumentId();
//                        Document taskDocument = documentService.getDocument(documentId.toString());
//                        objectTitle = (taskDocument != null ? taskDocument.getTitle() : "") + " (" + task.getDescription() + ")";
//                    }
                    //todo аттачи для задач это аттачи для документов, разобраться нужны ли отдельные аттачи для заадч
                    Document taskDocument = documentService.getDocument(objectId); //todo optimize
                    if (taskDocument != null) {
                        objectTitle = taskDocument.getTitle();
                    }
                    break;
                case "user":
                    User user = userService.getUserById(objectId);
                    if (user != null) {
                        objectTitle = user.getTitle();
                    }
                    break;
                case "contragent":
                    Contragent contragent = contragentService.getContragentById(objectId);
                    if (contragent != null) {
                        objectTitle = contragent.getTitle();
                    }
                    break;
                case "report":
                    JsonNode config = configService.getConfig2("reports/" + objectId);
                    if (config != null) {
                        String reportId = config.get("id").asText();
                        JsonNode report = configService.getConfig2("reports/" + reportId);
                        if (report != null) {
                            objectTitle = reportId + ":" + report.get("title").asText();
                        }
                    }
                    break;
                case "group":
                    Group group = groupService.getGroupById(objectId);
                    if (group != null) {
                        objectTitle = group.getTitle();
                    }
                    break;

            }
        } catch (Exception e) {
            log.error("Can not get title for object '" + objectId + "'", e);
        }
        return objectTitle;
    }


    public void setAttachService(AttachService attachService) {
        this.attachService = attachService;
    }

    public void setTitleService(TitleService titleService) {
        this.titleService = titleService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setContragentService(ContragentService contragentService) {
        this.contragentService = contragentService;
    }

    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
    }

    public void setGroupService(GroupService groupService) {
        this.groupService = groupService;
    }

    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }
}
