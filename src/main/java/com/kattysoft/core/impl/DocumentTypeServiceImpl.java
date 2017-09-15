/*
 * Copyright 2016-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
