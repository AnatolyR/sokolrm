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

import com.kattysoft.core.DictionaryService;
import com.kattysoft.core.model.DictionaryValue;
import com.kattysoft.core.repository.DictionaryValueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 28.12.2016
 */
public class DictionaryServiceImpl implements DictionaryService {
    @Autowired
    private DictionaryValueRepository dictionaryValueRepository;

    @Override
    public List<String> getValuesTitlesForDictionaryId(String dictionaryId) {
        List<DictionaryValue> dictionaryValues = dictionaryValueRepository.findByDictionaryId(dictionaryId, new Sort("title"));
        List<String> titles = dictionaryValues.stream().map(DictionaryValue::getTitle).collect(Collectors.toList());
        return titles;
    }

    public void setDictionaryValueRepository(DictionaryValueRepository dictionaryValueRepository) {
        this.dictionaryValueRepository = dictionaryValueRepository;
    }
}
