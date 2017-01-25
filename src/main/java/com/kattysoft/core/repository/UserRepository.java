/*
 * Copyright 2016 Anatolii Rakovskii (rtolik@yandex.ru)
 *
 * No part of this file can be copied or reproduced without written permission of author.
 *
 * Software distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */
package com.kattysoft.core.repository;

import com.kattysoft.core.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 15.12.2016
 */
public interface UserRepository extends CrudRepository<User, UUID>, JpaSpecificationExecutor<User> {
    List<User> findByTitleContaining(String title);

    @Query(value = "select new com.kattysoft.core.model.User(u.id, u.title) from User u where u.title like ?1")
    List<User> findIdAndTitleByTitle(String title);

    User findByLoginAndPassword(String login, String password);

    Page<User> findAll(Pageable pageable);
}
