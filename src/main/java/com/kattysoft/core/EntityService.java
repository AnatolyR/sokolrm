package com.kattysoft.core;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 17.12.2017
 */
public interface EntityService {
    <R extends CrudRepository<T, UUID>,T> T getEntity(Class<R> repositoryClass, Class<T> entityClass, String type, String id);

    <R extends CrudRepository<T, UUID>,T> String saveEntity(Class<R> repositoryClass, Class<T> entityClass, String acSpace, String acType, UUID id, Object entity);

    <R extends CrudRepository<T, UUID>,T> void deleteEntity(Class<R> repositoryClass, Class<T> entityClass, String acSpace, String acType, String id);
}
