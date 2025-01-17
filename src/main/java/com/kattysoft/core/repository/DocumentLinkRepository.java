/*
 * Copyright 2017 Anatolii Rakovskii (rtolik@yandex.ru)
 *
 * Software distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */
package com.kattysoft.core.repository;

import com.kattysoft.core.model.DocumentLink;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 28.04.2017
 */
public interface DocumentLinkRepository extends CrudRepository<DocumentLink, UUID> {
}
