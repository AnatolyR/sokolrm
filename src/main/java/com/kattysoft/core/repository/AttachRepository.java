/*
 * Copyright 2016 Anatolii Rakovskii (rtolik@yandex.ru)
 *
 * Software distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */
package com.kattysoft.core.repository;

import com.kattysoft.core.model.Attach;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 18.01.2017
 */
public interface AttachRepository extends CrudRepository<Attach, UUID> {
    List<Attach> findAllByObjectId(UUID objectId);
}
