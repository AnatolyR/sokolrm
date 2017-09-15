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
package com.kattysoft.core.repository;

import com.kattysoft.core.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
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

    @EntityGraph(value = "User.groups", type = EntityGraph.EntityGraphType.LOAD)
    User findByLoginAndPassword(String login, String password);

    Page<User> findAll(Pageable pageable);

    @Override
    @EntityGraph(value = "User.groups", type = EntityGraph.EntityGraphType.LOAD)
    User findOne(UUID uuid);

    @Override
    @EntityGraph(value = "User.groups", type = EntityGraph.EntityGraphType.LOAD)
    Page<User> findAll(Specification<User> specification, Pageable pageable);

    User findOneByLogin(String login);

    List<User> findAllByLogin(String login);
}
