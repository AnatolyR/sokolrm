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
import com.kattysoft.core.model.Page;
import com.kattysoft.core.model.RegistrationList;
import com.kattysoft.core.model.Space;
import com.kattysoft.core.repository.RegistrationListRepository;
import com.kattysoft.core.specification.SortOrder;
import com.kattysoft.core.specification.Specification;
import com.kattysoft.core.specification.SpecificationUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 29.03.2017
 */
public class RegistrationListServiceImpl implements RegistrationListService {
    @Autowired
    private RegistrationListRepository registrationListRepository;

    @Autowired
    private AccessRightService accessRightService;

    @Autowired
    private SpaceService spaceService;

    @Override
    public Page<RegistrationList> getLists(Specification specification) {
        if (!accessRightService.checkRights("_system", "registrationLists", null, AccessRightLevel.LIST)) {
            throw new NoAccessRightsException("No access rights for list registrationLists");
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

        org.springframework.data.jpa.domain.Specification<RegistrationList> spec = null;
        if (specification.getCondition() != null) {
            spec = SpecificationUtil.conditionToSpringSpecification(specification.getCondition(), RegistrationList.class);
        }

        org.springframework.data.domain.Page<RegistrationList> repoPage = registrationListRepository.findAll(spec, pageRequest);

        Page<RegistrationList> page = new Page<>(repoPage.getTotalElements(), repoPage.getContent());
        return page;
    }

    @Override
    public RegistrationList getListById(String id) {
        if (!accessRightService.checkRights("_system", "registrationLists", null, AccessRightLevel.READ)) {
            throw new NoAccessRightsException("No access rights for read registrationList");
        }
        UUID uuid = UUID.fromString(id);
        RegistrationList list = registrationListRepository.findOne(uuid);

        List<Space> spaces = spaceService.getSpacesByRegistrationListId(list.getId());
        List<UUID> spaceIds = spaces.stream().map(Space::getId).collect(Collectors.toList());
        list.setSpaces(spaceIds);

        return list;
    }

    @Override
    public String saveList(RegistrationList list) {
        List<UUID> spaces = list.getSpaces();
        if (list.getId() == null) {
            if (!accessRightService.checkRights("_system", "registrationLists", null, AccessRightLevel.CREATE)) {
                throw new NoAccessRightsException("No access rights for add registrationList");
            }
            UUID id = UUID.randomUUID();
            list.setId(id);
        } else {
            if (!accessRightService.checkRights("_system", "registrationLists", null, AccessRightLevel.WRITE)) {
                throw new NoAccessRightsException("No access rights for save registrationList");
            }
            RegistrationList existList = registrationListRepository.findOne(list.getId());
            if (existList == null) {
                throw new SokolException("RegistrationList not found");
            } else {
                BeanUtils.copyProperties(list, existList, Utils.getNullPropertyNames(list));
                list = existList;
            }
        }
        registrationListRepository.save(list);

        UUID listId = list.getId();

        List<UUID> existSpaces = spaceService.getSpacesByRegistrationListId(listId).stream().map(Space::getId).collect(Collectors.toList());
        List<UUID> spacesToAdd = spaces == null ? Collections.emptyList() : spaces.stream().filter(s -> !existSpaces.contains(s)).collect(Collectors.toList());
        List<UUID> spacesToDelete = existSpaces.stream().filter(s -> !(spaces != null && spaces.contains(s))).collect(Collectors.toList());

        spacesToAdd.forEach(s -> {
            Space space = spaceService.getSpace(s.toString());
            space.setRegistrationListId(listId);
            spaceService.saveSpace(space);
        });
        spacesToDelete.forEach(s -> {
            Space space = spaceService.getSpace(s.toString());
            space.setRegistrationListId(null);
            spaceService.saveSpace(space);
        });

        return listId.toString();
    }

    @Override
    public void deleteList(String id) {
        if (!accessRightService.checkRights("_system", "registrationLists", null, AccessRightLevel.DELETE)) {
            throw new NoAccessRightsException("No access rights for delete registrationLists");
        }
        UUID uuid = UUID.fromString(id);
        registrationListRepository.delete(uuid);
    }

    public void setRegistrationListRepository(RegistrationListRepository registrationListRepository) {
        this.registrationListRepository = registrationListRepository;
    }

    public void setAccessRightService(AccessRightService accessRightService) {
        this.accessRightService = accessRightService;
    }

    public void setSpaceService(SpaceService spaceService) {
        this.spaceService = spaceService;
    }
}
