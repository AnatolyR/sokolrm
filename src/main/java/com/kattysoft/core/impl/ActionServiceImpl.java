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
import org.springframework.beans.factory.annotation.Autowired;

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
    private RegistrationListService registrationListService;

    @Override
    public void doAction(String documentId, String actionId) {
        Document document = documentService.getDocument(documentId);
        if (document == null) {
            throw new SokolException("Document not found");
        }
        if (actionId == null) {
            throw new SokolException("Action is empty");
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
                Document holder = new Document();
                holder.setId(documentId);
                holder.getFields().put("status", endState);
                documentService.saveDocument(holder);
            }
        } else {
            throw new SokolException("No end state");
        }
    }

    private void registrDocument(String documentId, String endState, String space) {
        String documentNumber = registrationListService.produceNextNumber(space);

        Document holder = new Document();
        holder.setId(documentId);
        holder.getFields().put("status", endState);
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
}
