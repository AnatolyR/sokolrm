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
package com.kattysoft.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kattysoft.core.ActionService;
import com.kattysoft.core.SokolException;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.Reader;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 28.02.2017
 */
@RestController
public class ActionController {

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private ActionService actionService;

    @RequestMapping(value = "doAction")
    public String doAction(Reader reader) throws IOException {
        String requestBody = IOUtils.toString(reader);
        ObjectNode data = (ObjectNode) mapper.readTree(requestBody);
        String actionId = data.has("action") ? data.get("action").textValue() : null;
        if (actionId == null) {
            throw new SokolException("Action is null");
        }

        String documentId = data.get("documentId").textValue();
        if (documentId == null) {
            throw new SokolException("DocumentId is null");
        }
        actionService.doAction(documentId, actionId);
        return "true";
    }

    public void setActionService(ActionService actionService) {
        this.actionService = actionService;
    }
}
