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
import com.kattysoft.core.ConfigService;
import com.kattysoft.core.DictionaryService;
import com.kattysoft.core.model.Dictionary;
import com.kattysoft.core.model.DictionaryValue;
import com.kattysoft.core.repository.DictionaryValueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 28.12.2016
 */
public class DictionaryServiceImpl implements DictionaryService {
    @Autowired
    private ConfigService configService;

    @Autowired
    private DictionaryValueRepository dictionaryValueRepository;

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
}
