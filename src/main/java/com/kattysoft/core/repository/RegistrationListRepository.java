/*
 * Copyright 2017 Anatolii Rakovskii (rtolik@yandex.ru)
 *
 * Software distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */
package com.kattysoft.core.repository;

import com.kattysoft.core.model.RegistrationList;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 29.03.2017
 */
public interface RegistrationListRepository extends CrudRepository<RegistrationList, UUID>, JpaSpecificationExecutor<RegistrationList> {
//    @Modifying(clearAutomatically = true)
//    @Query("update RegistrationList list set list.count =:isRead where feedEntry.id =:entryId")
//    void markEntryAsRead(@Param("entryId") Long rssFeedEntryId, @Param("isRead") boolean isRead);
}
