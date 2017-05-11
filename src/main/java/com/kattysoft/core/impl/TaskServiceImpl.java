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

import com.kattysoft.core.*;
import com.kattysoft.core.model.*;
import com.kattysoft.core.repository.TaskRepository;
import com.kattysoft.core.repository.TasksListRepository;
import com.kattysoft.core.specification.SortOrder;
import com.kattysoft.core.specification.Specification;
import com.kattysoft.core.specification.SpecificationUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

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
    private DocumentService documentService;

    @Autowired
    private UserService userService;

    @Override
    public String saveExecutionList(TasksList tasksList) {
//        TasksList existList = getExecutionList(tasksList.getDocumentId(), tasksList.getType());

        List<Task> tasks = tasksList.getTasks();

        if (tasksList.getId() == null) {
            UUID id = UUID.randomUUID();
            tasksList.setId(id);

            User currentUser = userService.getCurrentUser();
            tasksList.setUserId(currentUser.getId());

            Date date = new Date();
            tasksList.setCreated(date);
        } else {
            TasksList existList = tasksListRepository.findOne(tasksList.getId());

            if (existList == null) {
                throw new SokolException("TasksList not found");
            } else {
                User currentUser = userService.getCurrentUser();
                if (!currentUser.getId().equals(existList.getUserId())) {
                    throw new NoAccessRightsException("Only process author can edit process");
                }

                BeanUtils.copyProperties(tasksList, existList, Utils.getNullPropertyNames(tasksList));
                tasksList = existList;
            }
        }

        tasksListRepository.save(tasksList);
        saveTasks(tasks, tasksList);
        deleteNotMoreExistsTasks(tasks, tasksList);

        updateDocumentStatus(tasksList.getId(), tasksList.getType(), tasksList.getDocumentId().toString());

        return tasksList.getId().toString();
    }

    private void updateDocumentStatus(UUID id, String type, String documentId) {
        TasksList mainExecutionList = getMainExecutionList(UUID.fromString(documentId), type);
        if (!mainExecutionList.getId().equals(id)) {
            return;
        }
        List<Task> tasks = taskRepository.findAllByListId(id);
        Document document = documentService.getDocument(documentId);
        boolean runningTasks = tasks.stream().filter(t -> "run".equals(t.getStatus())).findFirst().isPresent();
        if ("execution".equals(type)) {
            if (runningTasks) {
                if ("executed".equals(document.getStatus())) {
                    updateDocumentStatus(documentId, "execution");
                }
            } else {
                if ("execution".equals(document.getStatus())) {
                    updateDocumentStatus(documentId, "executed");
                }
            }
        } else if ("approval".equals(type)) {
            if (runningTasks) {
                if ("project".equals(document.getStatus()) || "agreed".equals(document.getStatus()) || "not_agreed".equals(document.getStatus())) {
                    updateDocumentStatus(documentId, "approval");
                }
            } else {
                boolean done = !tasks.stream().filter(t -> !"agreed".equals(t.getResult())).findFirst().isPresent();
                if (done) {
                    if ("approval".equals(document.getStatus())) {
                        updateDocumentStatus(documentId, "agreed");
                    }
                } else {
                    if ("approval".equals(document.getStatus())) {
                        updateDocumentStatus(documentId, "not_agreed");
                    }
                }
            }
        }
    }

    private void updateDocumentStatus(String documentId, String status) {
        Document holder = new Document();
        holder.setId(documentId);
        holder.getFields().put("status", status);
        documentService.saveDocument(holder);
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
                task.setDescription(tasksList.getComment());
            }
        }
        taskRepository.save(task);
    }

    @Override
    public TasksList getExecutionList(UUID parentListId, String type) {
        User currentUser = userService.getCurrentUser();
        TasksList tasksList = tasksListRepository.findOneByParentIdAndUserIdAndType(parentListId, currentUser.getId(), type);

        if (tasksList != null) {
            List<Task> tasks = taskRepository.findAllByListId(tasksList.getId());
            tasksList.setTasks(tasks);
        }

        return tasksList;
    }

    @Override
    public TasksList getTaskExecutionList(UUID taskId) {
        TasksList tasksList = tasksListRepository.findOneByParentTaskId(taskId);

        if (tasksList != null) {
            List<Task> tasks = taskRepository.findAllByListId(tasksList.getId());
            tasksList.setTasks(tasks);
        }

        return tasksList;
    }

    @Override
    public void clearApprovalTasksState(String documentId) {
        List<Task> tasks = taskRepository.findAllByDocumentIdAndType(UUID.fromString(documentId), "approval");
        tasks.stream().forEach(t -> {
            //todo save task history SOKOL-90
            t.setStatus("run");
            t.setExecutedDate(null);
            t.setComment("");
            t.setResult(null);

            taskRepository.save(t);
        });
    }

    @Override
    public TasksList getMainExecutionList(UUID documentId, String type) {
        TasksList tasksList = tasksListRepository.findOneByDocumentIdAndParentIdAndType(documentId, null, type);

        if (tasksList != null) {
            List<Task> tasks = taskRepository.findAllByListId(tasksList.getId());
            tasksList.setTasks(tasks);
        }

        return tasksList;
    }

    @Override
    public Page<Task> getTasks(Specification spec) {
        int offset = spec.getOffset();
        int size = spec.getSize();
        int pageNum = offset / size;

        Sort sort;
        if (spec.getSort() != null && spec.getSort().size() > 0) {
            sort = new Sort(spec.getSort().get(0).getOrder() == SortOrder.ASC
                ? Sort.Direction.ASC : Sort.Direction.DESC, spec.getSort().get(0).getField());
        } else {
            sort = new Sort("created");
        }

        PageRequest pageRequest = new PageRequest(pageNum, size, sort);

        org.springframework.data.jpa.domain.Specification<Task> specification = null;
        if (spec.getCondition() != null) {
            specification = SpecificationUtil.conditionToSpringSpecification(spec.getCondition(), Task.class);
        }

        org.springframework.data.domain.Page<Task> repoPage = taskRepository.findAll(specification, pageRequest);

        Page<Task> page = new Page<>(repoPage.getTotalElements(), repoPage.getContent());
        return page;
    }

    @Override
    public Task getTaskById(UUID uuid) {
        Task task = taskRepository.findOne(uuid);
        return task;
    }

    @Override
    public TasksList getExecutionListById(UUID id) {
        TasksList tasksList = tasksListRepository.findOne(id);

        if (tasksList != null) {
            List<Task> tasks = taskRepository.findAllByListId(tasksList.getId());
            tasksList.setTasks(tasks);
        }

        return tasksList;
    }

    @Override
    public void completeTask(Task task) {
        Task existedTask = taskRepository.findOne(task.getId());
        if (existedTask == null) {
            throw new SokolException("Task for complete not exist");
        }
        if ("complete".equals(existedTask.getStage())) {
            throw new SokolException("Task already completed");
        }
        existedTask.setStatus("complete");
        existedTask.setResult(task.getResult());
        existedTask.setComment(task.getComment());
        existedTask.setExecutedDate(new Date());
        User currentUser = userService.getCurrentUser();
        existedTask.setExecutedByUser(currentUser.getId());
        taskRepository.save(existedTask);

        updateDocumentStatus(existedTask.getListId(), existedTask.getType(), existedTask.getDocumentId().toString());

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

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }
}
