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
package com.kattysoft.core;

import com.kattysoft.core.model.Dictionary;
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

    List<Dictionary> getDictionaries();
}
