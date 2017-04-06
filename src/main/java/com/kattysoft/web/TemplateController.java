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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kattysoft.core.DocumentService;
import com.kattysoft.core.SokolException;
import com.kattysoft.core.UserService;
import com.kattysoft.core.model.Document;
import com.kattysoft.core.model.User;
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
 * Date: 06.04.2017
 */
@RestController
public class TemplateController {
    @Autowired
    private DocumentService documentService;

    @Autowired
    private UserService userService;

    private ObjectMapper mapper = new ObjectMapper();

    @RequestMapping(value = "/documentTemplates")
    public Object listDocumentTemplates(String type) throws IOException {
        if (type == null || !type.matches("^[a-zA-Z]+$")) {
            throw new SokolException("Empty or wrong document type");
        }

        Specification spec = new Specification();

        Sort sortObject = new Sort();
        sortObject.setField("title");
        sortObject.setOrder(SortOrder.ASC);
        spec.setSort(Collections.singletonList(sortObject));

        spec.setOffset(0);
        spec.setSize(20);
        List<String> fields = Collections.singletonList("title");
        spec.setFields(fields);

        ContainerCondition condition = new ContainerCondition();
        String listConditionSql = "author = '${currentUser}' and status='template' and type='" + type + "'";
        if (listConditionSql.contains("${currentUser}")) {
            User currentUser = userService.getCurrentUser();
            listConditionSql = listConditionSql.replaceAll("\\$\\{currentUser\\}", currentUser.getId().toString());
        }
        condition.setOperation(ContainerOperation.AND);
        Condition listCondition = new SqlCondition(listConditionSql);
        condition.getConditions().add(listCondition);

        spec.setCondition(condition);
        List<Document> documents = documentService.listDocuments(spec);
        Integer total = documentService.getTotalCount(spec);

        ObjectNode page = mapper.createObjectNode();
        List<ObjectNode> documentsNodes = documents.stream().map(doc -> {
            ObjectNode document = mapper.valueToTree(doc);
            ObjectNode docFields = (ObjectNode) document.get("fields");
            document.remove("fields");
            document.setAll(docFields);

            return document;
        }).collect(Collectors.toList());
        page.putArray("data").addAll(documentsNodes);
        page.put("offset", 0);
        page.put("total", total);

        return page;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
