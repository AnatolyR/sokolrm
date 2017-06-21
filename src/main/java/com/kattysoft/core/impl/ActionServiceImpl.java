/*
 * Copyright 2017 Anatolii Rakovskii (rtolik@yandex.ru)
 *
 * No part of this file can be copied or reproduced without written permission of author.
 *
 * Software distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */
package com.kattysoft.core.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.kattysoft.core.*;
import com.kattysoft.core.model.Document;
import com.kattysoft.core.model.Task;
import com.kattysoft.core.model.User;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.stream.StreamSupport;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 28.02.2017
 */
public class ActionServiceImpl implements ActionService {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RegistrationListService registrationListService;
    
    @Autowired
    private AccessRightService accessRightService;
    
    @Autowired
    private UserService userService;

    @Override
    public void doAction(String documentId, String actionId) {
        Document document = documentService.getDocument(documentId);
        if (document == null) {
            throw new SokolException("Document not found");
        }
        if (actionId == null) {
            throw new SokolException("Action is empty");
        }

        User currentUser = userService.getCurrentUser();
        if (accessRightService.checkDocumentRights(document, "*" + actionId, AccessRightLevel.ALLOW)) {
            if ("doresolution".equals(actionId)) {
                List<String> addressee = (List<String>) document.getFields().get("addressee");
                if (addressee.contains(currentUser.getId().toString())) {
                    throw new NoAccessRightsException("Only addressee can create resolution");
                }
            } else if ("toapproval".equals(actionId) || "tosign".equals(actionId)) {
                if (!currentUser.getId().toString().equals(document.getFields().get("author"))) {
                    throw new NoAccessRightsException("Only author can toapproval or tosign");
                }
            } else if ("sign".equals(actionId) || "reject".equals(actionId)) {
                String signerId = (String) document.getFields().get("signer");
                if (!currentUser.getId().toString().equals(signerId)) {
                    throw new NoAccessRightsException("Only signer can sign or reject");
                }
            } 
        }
        
        String typeId = document.getType();
        JsonNode type = configService.getConfig2("types/" + typeId + "Type");
        String flowId = type.get("flow").textValue();
        JsonNode flow = configService.getConfig2("flows/" + flowId);
        String status = document.getStatus();
        String space = document.getSpace();
        JsonNode state = StreamSupport.stream(flow.get("states").spliterator(), false).filter(s -> status.equals(s.get("id").textValue())).findFirst().orElse(null);
        JsonNode action = StreamSupport.stream(state.get("actions").spliterator(), false).filter(a -> actionId.equals(a.get("id").textValue())).findFirst().orElse(null);
        String endState = action.has("state") ? action.get("state").textValue() : null;
        if (endState != null) {
            if (actionId.equals("doregistration")) {
                registrDocument(documentId, endState, space);
            } else {
                if ("reject".equals(actionId)) {
                    resetApprovalTasks(documentId);
                }
                Document holder = new Document();
                holder.setId(documentId);
                holder.getFields().put("status", endState);
                documentService.saveDocument(holder);
            }
        } else {
            throw new SokolException("No end state");
        }
    }

    private void resetApprovalTasks(String documentId) {
        taskService.clearApprovalTasksState(documentId);
    }

    private void registrDocument(String documentId, String endState, String space) {
        String documentNumber = registrationListService.produceNextNumber(space);

        Document holder = new Document();
        holder.setId(documentId);
        holder.getFields().put("status", endState);
        holder.getFields().put("registrationDate", new Date());
        if (documentNumber != null) {
            holder.getFields().put("documentNumber", documentNumber);
        }
        documentService.saveDocument(holder);
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }

    public void setRegistrationListService(RegistrationListService registrationListService) {
        this.registrationListService = registrationListService;
    }

    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    public void setAccessRightService(AccessRightService accessRightService) {
        this.accessRightService = accessRightService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
