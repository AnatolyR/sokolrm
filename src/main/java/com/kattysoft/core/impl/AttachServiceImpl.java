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

import com.kattysoft.core.AttachService;
import com.kattysoft.core.dao.AttachesDao;
import com.kattysoft.core.model.Attach;
import com.kattysoft.core.model.Page;
import com.kattysoft.core.model.User;
import com.kattysoft.core.repository.AttachRepository;
import com.kattysoft.core.specification.SortOrder;
import com.kattysoft.core.specification.Specification;
import com.kattysoft.core.specification.SpecificationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 18.01.2017
 */
public class AttachServiceImpl implements AttachService {

    @Autowired
    private AttachRepository attachRepository;

    @Autowired
    private AttachesDao attachesDao;

    @Override
    public List<Attach> getAttachesForObject(String objectId) {
        UUID uuid = UUID.fromString(objectId);
        List<Attach> attaches = attachRepository.findAllByObjectIdOrderByCreatedDesc(uuid);
        return attaches;
    }

    public void deleteAttach(String id) {
        UUID uuid = UUID.fromString(id);
        attachRepository.delete(uuid);
    }

    @Override
    public byte[] getContent(String id) {
        return attachesDao.getContent(id);
    }

    @Override
    public void addContent(String reportObjectId, String name, User user, Date date, byte[] bytes) {
        attachesDao.addContent(reportObjectId, name, user, date, bytes);
    }

    @Override
    public Page<Attach> getAttaches(Specification specification) {
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

        org.springframework.data.jpa.domain.Specification<Attach> spec = null;
        if (specification.getCondition() != null) {
            spec = SpecificationUtil.conditionToSpringSpecification(specification.getCondition(), Attach.class);
        }

        org.springframework.data.domain.Page<Attach> repoPage = attachRepository.findAll(spec, pageRequest);

        Page<Attach> page = new Page<>(repoPage.getTotalElements(), repoPage.getContent());
        return page;
    }

    public void setAttachRepository(AttachRepository attachRepository) {
        this.attachRepository = attachRepository;
    }

    public void setAttachesDao(AttachesDao attachesDao) {
        this.attachesDao = attachesDao;
    }
}
