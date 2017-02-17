/*
 * Copyright 2017 Anatolii Rakovskii (rtolik@yandex.ru)
 *
 * No part of this file can be copied or reproduced without written permission of author.
 *
 * Software distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */
package com.kattysoft.core.impl;

import com.kattysoft.core.SokolException;
import com.kattysoft.core.TaskService;
import com.kattysoft.core.UserService;
import com.kattysoft.core.Utils;
import com.kattysoft.core.model.Task;
import com.kattysoft.core.model.TasksList;
import com.kattysoft.core.model.User;
import com.kattysoft.core.repository.TaskRepository;
import com.kattysoft.core.repository.TasksListRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 14.02.2017
 */
public class TaskServiceImpl implements TaskService {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TasksListRepository tasksListRepository;

    @Autowired
    private UserService userService;

    @Override
    public String saveExecutionList(TasksList tasksList) {
        TasksList existList = getExecutionList(tasksList.getDocumentId(), tasksList.getType());

        List<Task> tasks = tasksList.getTasks();

        if (tasksList.getId() == null && existList == null) {
            UUID id = UUID.randomUUID();
            tasksList.setId(id);

            User currentUser = userService.getCurrentUser();
            tasksList.setUserId(currentUser.getId());

            Date date = new Date();
            tasksList.setCreated(date);
        } else {
            if (existList == null) {
                existList = tasksListRepository.findOne(tasksList.getId());
            }
            if (existList == null) {
                throw new SokolException("TasksList not found");
            } else {
                BeanUtils.copyProperties(tasksList, existList, Utils.getNullPropertyNames(tasksList));
                tasksList = existList;
            }
        }

        tasksListRepository.save(tasksList);
        saveTasks(tasks, tasksList);
        deleteNotMoreExistsTasks(tasks, tasksList);

        return tasksList.getId().toString();
    }

    private void deleteNotMoreExistsTasks(List<Task> tasks, TasksList tasksList) {
        List<UUID> users = tasks.stream().map(Task::getUserId).collect(Collectors.toList());
        List<Task> existsTasks = taskRepository.findAllByListId(tasksList.getId());
        List<Task> toDelete = existsTasks.stream().filter(t -> !users.contains(t.getUserId())).collect(Collectors.toList());
        taskRepository.delete(toDelete);
    }

    private void saveTasks(List<Task> tasks, TasksList tasksList) {
        for (Task task : tasks) {
            saveTask(task, tasksList);
        }
    }

    private void saveTask(Task task, TasksList tasksList) {
        if (task.getUserId() == null) {
            return;
        }
        Task existTask = taskRepository.findOneByListIdAndUserId(tasksList.getId(), task.getUserId());
        if (task.getId() == null && existTask == null) {
            UUID id = UUID.randomUUID();
            task.setId(id);

            task.setDocumentId(tasksList.getDocumentId());

            task.setListId(tasksList.getId());

            task.setType(tasksList.getType());

            task.setDescription(tasksList.getComment());

            task.setStatus("run");

            task.setCreated(new Date());

            task.setAuthor(tasksList.getUserId());
        } else {
            if (existTask == null) {
                existTask = taskRepository.findOne(task.getId());
            }
            if (existTask == null) {
                throw new SokolException("Task not found");
            } else {
                BeanUtils.copyProperties(task, existTask, Utils.getNullPropertyNames(task));
                task = existTask;
            }
        }
        taskRepository.save(task);
    }

    @Override
    public TasksList getExecutionList(UUID documentId, String type) {
        User currentUser = userService.getCurrentUser();
        TasksList tasksList = tasksListRepository.findOneByDocumentIdAndUserIdAndType(documentId, currentUser.getId(), type);

        if (tasksList != null) {
            List<Task> tasks = taskRepository.findAllByListId(tasksList.getId());
            tasksList.setTasks(tasks);
        }

        return tasksList;
    }

    public void setTaskRepository(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void setTasksListRepository(TasksListRepository tasksListRepository) {
        this.tasksListRepository = tasksListRepository;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
