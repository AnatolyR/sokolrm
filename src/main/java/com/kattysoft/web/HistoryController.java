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
import com.kattysoft.core.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 21.04.2017
 */
@RestController
public class HistoryController {
    private static final Logger log = LoggerFactory.getLogger(HistoryController.class);

    private ObjectMapper mapper = new ObjectMapper();

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    private SimpleDateFormat dateFormatSs = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    @Autowired
    private DocumentService documentService;

    public HistoryController() {
        mapper.setDateFormat(dateFormat);
    }

    @RequestMapping(value = "/history", produces = "application/json; charset=utf-8")
    public ObjectNode getHistory(String listId, Integer offset, Integer size, String conditions, String sort, String sortAsc, String searchtext) {

        if (offset == null) {
            offset = 0;
        }
        if (size == null) {
            size = 5;
        }

        ArrayNode history = documentService.getHistory(listId);

        List<JsonNode> sortedItems = StreamSupport.stream(history.spliterator(), false).sorted((o1, o2) -> {
            try {
                Date date1 = dateFormatSs.parse(o1.get("date").asText());
                Date date2 = dateFormatSs.parse(o2.get("date").asText());
                return -date1.compareTo(date2);
            } catch (Exception e) {
                log.error("History processing error", e);
                return 0;
            }
        }).collect(Collectors.toList());

        ArrayList<JsonNode> items = new ArrayList<>();
        if (offset < sortedItems.size()) {
            for (int i = offset; i < sortedItems.size() && i < offset + size; i++) {
                items.add(sortedItems.get(i));
            }
        }

        ObjectNode page = mapper.createObjectNode();
        page.putArray("data").addAll(items);
        page.put("offset", offset);
        page.put("total", sortedItems.size());

        return page;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }
}
