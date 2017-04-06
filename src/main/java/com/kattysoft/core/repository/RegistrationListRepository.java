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
//    @org.springframework.transaction.annotation.Transactional
//    @Modifying(clearAutomatically = true)
//    @Query(value = "UPDATE registrationlists r SET r.count = LAST_INSERT_ID(r.count + 1) WHERE r.id = ?1", nativeQuery = true)
//    int updateCounterByName(UUID listId);
//
//    @Query(value = "SELECT LAST_INSERT_ID()", nativeQuery = true)
//    int getLastInsertId();
}
