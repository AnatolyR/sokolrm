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

    TasksList getExecutionList(UUID parentListId, String type);

    TasksList getMainExecutionList(UUID documentId, String type);

    Page<Task> getTasks(Specification spec);

    Task getTaskById(UUID uuid);

    TasksList getExecutionListById(UUID id);

    void completeTask(Task task);

    TasksList getTaskExecutionList(UUID taskId);

    void clearApprovalTasksState(String documentId);
}
