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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kattysoft.core.AttachService;
import com.kattysoft.core.SokolException;
import com.kattysoft.core.model.Attach;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 18.01.2017
 */
@RestController
public class AttachController {

    @Autowired
    private AttachService attachService;

    private ObjectMapper mapper = new ObjectMapper();

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

    @RequestMapping(value = "/searchfiles", produces = "application/json; charset=utf-8")
    public ObjectNode getDictionaryValues(String searchText) {
        List<ObjectNode> userNodes = new ArrayList<>();

        ObjectNode page = mapper.createObjectNode();
        page.putArray("data").addAll(userNodes);
        page.put("offset", 0);
        page.put("total", 0);

        return page;
    }


    public void setAttachService(AttachService attachService) {
        this.attachService = attachService;
    }
}
