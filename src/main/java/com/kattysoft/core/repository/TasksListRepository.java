/*
 * Copyright 2017 Anatolii Rakovskii (rtolik@yandex.ru)
 *
 * Software distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */
package com.kattysoft.core.repository;

import com.kattysoft.core.model.TasksList;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 14.02.2017
 */
public interface TasksListRepository extends CrudRepository<TasksList, UUID>, JpaSpecificationExecutor<TasksList> {
    TasksList findOneByDocumentIdAndUserIdAndType(UUID documentId, UUID userId, String type);
    TasksList findOneByDocumentIdAndParentIdAndType(UUID documentId, UUID parentId, String type);
    TasksList findOneByParentIdAndUserIdAndType(UUID parentId, UUID userId, String type);
    TasksList findOneByParentTaskId(UUID parentTaskId);
}
