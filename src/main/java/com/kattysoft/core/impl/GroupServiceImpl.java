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
package com.kattysoft.core.impl;

import com.kattysoft.core.*;
import com.kattysoft.core.model.Group;
import com.kattysoft.core.model.Page;
import com.kattysoft.core.repository.GroupRepository;
import com.kattysoft.core.specification.SortOrder;
import com.kattysoft.core.specification.Specification;
import com.kattysoft.core.specification.SpecificationUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.UUID;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 27.01.2017
 */
public class GroupServiceImpl implements GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private AccessRightService accessRightService;

    @Override
    public Page<Group> getGroups(Specification specification) {
        if (!accessRightService.checkRights("_system", "groups", null, AccessRightLevel.LIST)) {
            throw new NoAccessRightsException("No access rights for list group");
        }
        int offset = specification.getOffset();
        int size = specification.getSize();
        int pageNum = offset / size;

        Sort sort;
        if (specification.getSort() != null && specification.getSort().size() > 0) {
            sort = new Sort(specification.getSort().get(0).getOrder() == SortOrder.ASC
                ? Sort.Direction.ASC : Sort.Direction.DESC, specification.getSort().get(0).getField());
        } else {
            sort = new Sort("title");
        }

        PageRequest pageRequest = new PageRequest(pageNum, size, sort);

        org.springframework.data.jpa.domain.Specification<Group> spec = null;
        if (specification.getCondition() != null) {
            spec = SpecificationUtil.conditionToSpringSpecification(specification.getCondition(), Group.class);
        }

        org.springframework.data.domain.Page<Group> repoPage = groupRepository.findAll(spec, pageRequest);

        Page<Group> page = new Page<>(repoPage.getTotalElements(), repoPage.getContent());
        return page;
    }

    @Override
    public Group getGroupById(String id) {
        if (!accessRightService.checkRights("_system", "groups", null, AccessRightLevel.READ)) {
            throw new NoAccessRightsException("No access rights for read group");
        }
        UUID uuid = UUID.fromString(id);
        Group group = groupRepository.findOne(uuid);
        return group;
    }

    @Override
    public String saveGroup(Group group) {
        if (group.getId() == null) {
            if (!accessRightService.checkRights("_system", "groups", null, AccessRightLevel.CREATE)) {
                throw new NoAccessRightsException("No access rights for add group");
            }
            UUID id = UUID.randomUUID();
            group.setId(id);
        } else {
            if (!accessRightService.checkRights("_system", "groups", null, AccessRightLevel.WRITE)) {
                throw new NoAccessRightsException("No access rights for save group");
            }
            Group existGroup = groupRepository.findOne(group.getId());
            if (existGroup == null) {
                throw new SokolException("Group not found");
            } else {
                BeanUtils.copyProperties(group, existGroup, Utils.getNullPropertyNames(group));
                group = existGroup;
            }
        }
        groupRepository.save(group);
        return group.getId().toString();
    }

    @Override
    public void deleteGroup(String id) {
        if (!accessRightService.checkRights("_system", "groups", null, AccessRightLevel.DELETE)) {
            throw new NoAccessRightsException("No access rights for delete group");
        }
        UUID uuid = UUID.fromString(id);
        groupRepository.delete(uuid);
    }

    @Override
    public Group getGroupByTitle(String title) {
        Group group = groupRepository.findOneByTitle(title);
        return group;
    }

    public void setGroupRepository(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public void setAccessRightService(AccessRightService accessRightService) {
        this.accessRightService = accessRightService;
    }
}
