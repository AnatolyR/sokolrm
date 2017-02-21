/*
 * Copyright 2017 Anatolii Rakovskii (rtolik@yandex.ru)
 *
 * Software distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */
package com.kattysoft.core;

import com.kattysoft.core.model.Page;
import com.kattysoft.core.model.Task;
import com.kattysoft.core.model.TasksList;
import com.kattysoft.core.specification.Specification;

import java.util.UUID;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 07.02.2017
 */
public interface TaskService {

    String saveExecutionList(TasksList tasksList);

    TasksList getExecutionList(UUID documentId, String type);

    Page<Task> getTasks(Specification spec);

    Task getTaskById(UUID uuid);

    TasksList getExecutionListById(UUID id);
}
