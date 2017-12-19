package com.kattysoft.core.impl;

import com.kattysoft.core.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.CrudRepository;

import java.lang.reflect.Field;
import java.util.UUID;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 17.12.2017
 */
public class EntityServiceImpl implements EntityService {
    @Autowired
    private AccessRightService accessRightService;

    @Autowired
    private ApplicationContext applicationContext;
    
    @Override
    public <R extends CrudRepository<T, UUID>, T> T getEntity(Class<R> repositoryClass, Class<T> entityClass, String type, String id) {
        if (!accessRightService.checkRights("_system", type, null, AccessRightLevel.READ)) {
            throw new NoAccessRightsException("No access rights for read " + type);
        }
        UUID uuid = UUID.fromString(id);
        R repository = applicationContext.getBean(repositoryClass, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
        T entity = repository.findOne(uuid);

        return entity;
    }

    @Override
    public <R extends CrudRepository<T, UUID>, T> String saveEntity(Class<R> repositoryClass, Class<T> entityClass, String acSpace, String acType, UUID id, Object entity) {
        R repository = applicationContext.getBean(repositoryClass, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
        if (id == null) {
            if (!accessRightService.checkRights(acSpace, acType, null, AccessRightLevel.CREATE)) {
                throw new NoAccessRightsException("No rights to create entity");
            }
            id = UUID.randomUUID();
            try {
                Field idField = entityClass.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(entity, id);
            } catch (NoSuchFieldException | RuntimeException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } else {
            if (!accessRightService.checkRights(acSpace, acType, null, AccessRightLevel.WRITE)) {
                throw new NoAccessRightsException("No rights to save entity");
            }
            
            Object existEntity = repository.findOne(id);
            if (existEntity == null) {
                throw new SokolException("Entity not found");
            } else {
                BeanUtils.copyProperties(entity, existEntity, Utils.getNullPropertyNames(entity));
                entity = existEntity;
            }
        }

        repository.save((T) entity);
        return id.toString();
    }

    @Override
    public <R extends CrudRepository<T, UUID>, T> void deleteEntity(Class<R> repositoryClass, Class<T> entityClass, String acSpace, String acType, String id) {
        if (!accessRightService.checkRights(acSpace, acType, null, AccessRightLevel.DELETE)) {
            throw new NoAccessRightsException("No access rights for delete entity");
        }
        UUID uuid = UUID.fromString(id);

        R repository = applicationContext.getBean(repositoryClass, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
        repository.delete(uuid);
    }

    public void setAccessRightService(AccessRightService accessRightService) {
        this.accessRightService = accessRightService;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
