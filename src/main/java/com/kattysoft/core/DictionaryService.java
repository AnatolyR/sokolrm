/*
 * Copyright 2016 Anatolii Rakovskii (rtolik@yandex.ru)
 *
 * Software distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */
package com.kattysoft.core;

import java.util.List;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 28.12.2016
 */
public interface DictionaryService {
    List<String> getValuesTitlesForDictionaryId(String dictionaryId);
}