/*
 * Copyright 2016 Anatolii Rakovskii (rtolik@yandex.ru)
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
import com.kattysoft.core.model.Dictionary;
import com.kattysoft.core.model.DictionaryValue;
import com.kattysoft.core.repository.DictionaryValueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 28.12.2016
 */
public class DictionaryServiceImpl implements DictionaryService {
    @Autowired
    private ConfigService configService;

    @Autowired
    private DictionaryValueRepository dictionaryValueRepository;

    @Autowired
    private AccessRightService accessRightService;

    @Override
    public List<String> getValuesTitlesForDictionaryId(String dictionaryId) {
        List<DictionaryValue> dictionaryValues = dictionaryValueRepository.findByDictionaryId(dictionaryId, new Sort("title"));
        List<String> titles = dictionaryValues.stream().map(DictionaryValue::getTitle).collect(Collectors.toList());
        return titles;
    }

    @Override
    public List<DictionaryValue> getValuesForDictionaryId(String dictionaryId) {
        List<DictionaryValue> dictionaryValues = dictionaryValueRepository.findByDictionaryId(dictionaryId, new Sort("title"));
        return dictionaryValues;
    }

    @Override
    public void deleteDictionaryValues(List<String> ids) {
        List<UUID> uuids = ids.stream().map(UUID::fromString).collect(Collectors.toList());
        Iterable<DictionaryValue> all = dictionaryValueRepository.findAll(uuids);
        Set<String> dictionaries = new HashSet<>();
        StreamSupport.stream(all.spliterator(), false).forEach(d -> dictionaries.add(d.getDictionaryId()));
        for (String dictionary : dictionaries) {
            if (!accessRightService.checkRights("_dictionaries", dictionary, "", AccessRightLevel.DELETE)) {
                throw new NoAccessRightsException("No rights to delete dictionary value");
            }
        }
        
        List<DictionaryValue> values = ids.stream().map(id -> new DictionaryValue(UUID.fromString(id))).collect(Collectors.toList());
        dictionaryValueRepository.delete(values);
    }

    @Override
    public boolean isValueExist(String dictionaryId, String title) {
        if (dictionaryId == null || dictionaryId.isEmpty()) {
            throw new RuntimeException("dictionaryId is null");
        }
        return dictionaryValueRepository.findByDictionaryIdAndTitle(dictionaryId, title).size() > 0;
    }

    @Override
    public String addDictionaryValue(DictionaryValue value) {
        if (value.getDictionaryId() == null || value.getDictionaryId().isEmpty()) {
            throw new RuntimeException("dictionaryId is null");
        }
        if (!accessRightService.checkRights("_dictionaries", value.getDictionaryId(), "", AccessRightLevel.ADD)) {
            throw new NoAccessRightsException("No rights to add dictionary value");
        }
        UUID id = UUID.randomUUID();
        value.setId(id);

        dictionaryValueRepository.save(value);
        return id.toString();
    }

    @Override
    public List<Dictionary> getDictionaries() {
        JsonNode documentTypes = configService.getConfig2("dictionaries");
        List<Dictionary> dictionaries = new ArrayList<>();

        documentTypes.forEach(jsonNode -> {
            String dictionaryName = jsonNode.asText();
            JsonNode typeNode = configService.getConfig2("dictionaries/" + dictionaryName);
            String title = typeNode.get("gridConfig").get("title").asText();

            Dictionary dictionary = new Dictionary();
            dictionary.setDictionaryId(dictionaryName);
            dictionary.setTitle(title);

            dictionaries.add(dictionary);
        });

        return dictionaries;
    }

    @Override
    public DictionaryValue getDictionaryValue(String id) {
        return dictionaryValueRepository.findOne(UUID.fromString(id));
    }

    public void setDictionaryValueRepository(DictionaryValueRepository dictionaryValueRepository) {
        this.dictionaryValueRepository = dictionaryValueRepository;
    }

    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }

    public void setAccessRightService(AccessRightService accessRightService) {
        this.accessRightService = accessRightService;
    }
}
