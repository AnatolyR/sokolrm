/*
 * Copyright 2016 Anatolii Rakovskii (rtolik@yandex.ru)
 *
 * Software distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */
package com.kattysoft.core;

import com.kattysoft.core.model.DictionaryValue;

import java.util.List;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 28.12.2016
 */
public interface DictionaryService {
    List<String> getValuesTitlesForDictionaryId(String dictionaryId);

    List<DictionaryValue> getValuesForDictionaryId(String dictionaryId);

    void deleteDictionaryValues(List<String> ids);

    boolean isValueExist(String dictionaryId, String title);

    String addDictionaryValue(DictionaryValue value);

    DictionaryValue getDictionaryValue(String id);
}
