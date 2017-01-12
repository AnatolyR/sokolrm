/*
 * Copyright 2016 Anatolii Rakovskii (rtolik@yandex.ru)
 *
 * Software distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */
package com.kattysoft.core.repository;

import com.kattysoft.core.model.DictionaryValue;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 28.12.2016
 */
public interface DictionaryValueRepository extends CrudRepository<DictionaryValue, UUID> {
    List<DictionaryValue> findByDictionaryId(String dictionaryId, Sort sort);
    List<DictionaryValue> findByDictionaryIdAndTitle(String dictionaryId, String title);
}
