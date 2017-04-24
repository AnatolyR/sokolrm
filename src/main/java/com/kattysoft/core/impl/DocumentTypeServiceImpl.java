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
import com.kattysoft.core.ConfigService;
import com.kattysoft.core.DocumentTypeService;
import com.kattysoft.core.model.DocumentType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 31.01.2017
 */
public class DocumentTypeServiceImpl implements DocumentTypeService {
    @Autowired
    private ConfigService configService;

    @Override
    public List<DocumentType> getDocumentTypes() {
        JsonNode documentTypes = configService.getConfig2("documentTypes");
        List<DocumentType> types = new ArrayList<>();

        documentTypes.forEach(jsonNode -> {
            String typeName = jsonNode.asText();
            DocumentType documentType = getDocumentType(typeName);

            types.add(documentType);
        });

        return types;
    }

    @Override
    public DocumentType getDocumentType(String typeName) {
        JsonNode typeNode = configService.getConfig2("types/" + typeName + "Type");
        String typeTitle = typeNode.get("title").asText();
        List<DocumentType.FieldType> fieldsTypes = new ArrayList<DocumentType.FieldType>();
        typeNode.get("fields").forEach(fieldNode -> {
            String fieldId = fieldNode.get("id").asText();
            String fieldTitle = fieldNode.has("title") ? fieldNode.get("title").asText() : null;
            if (fieldTitle != null) {
                DocumentType.FieldType fieldType = new DocumentType.FieldType();
                fieldType.setId(fieldId);
                fieldType.setTitle(fieldTitle);
                fieldsTypes.add(fieldType);
            }
        });
        DocumentType documentType = new DocumentType();
        documentType.setId(typeName);
        documentType.setTitle(typeTitle);
        String flowId = typeNode.has("flow") ? typeNode.get("flow").textValue() : null;
        documentType.setFlow(flowId);
        documentType.setFieldsTypes(fieldsTypes);

        if (typeNode.has("actions")) {
            typeNode.get("actions").forEach(a -> documentType.getActions().add(a.asText()));
        }
        return documentType;
    }

    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }
}
