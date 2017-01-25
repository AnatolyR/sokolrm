/*
 * Copyright 2016 Anatolii Rakovskii (rtolik@yandex.ru)
 *
 * No part of this file can be copied or reproduced without written permission of author.
 *
 * Software distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */
package com.kattysoft.core.impl;

import com.kattysoft.core.ContragentService;
import com.kattysoft.core.SokolException;
import com.kattysoft.core.model.Contragent;
import com.kattysoft.core.model.Page;
import com.kattysoft.core.repository.ContragentRepository;
import com.kattysoft.core.specification.SortOrder;
import com.kattysoft.core.specification.Specification;
import com.kattysoft.core.specification.SpecificationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.UUID;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 19.12.2016
 */
public class ContragentServiceImpl implements ContragentService {

    @Autowired
    private ContragentRepository contragentRepository;

    @Override
    public List<Contragent> getContragentsByTitle(String title) {
        List<Contragent> contragents = contragentRepository.findByTitleContaining(title, new PageRequest(0, 10));
        return contragents;
    }

    @Override
    public String getContragentTitleById(String contragentId) {
        Contragent contragent = contragentRepository.findOne(UUID.fromString(contragentId));
        return contragent != null ? contragent.getTitle() : null;
    }

    public Page<Contragent> getContragents(Specification specification) {
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

        org.springframework.data.jpa.domain.Specification<Contragent> spec = null;
        if (specification.getCondition() != null) {
            spec = SpecificationUtil.conditionToSpringSpecification(specification.getCondition(), Contragent.class);
        }

        org.springframework.data.domain.Page<Contragent> repoPage = contragentRepository.findAll(spec, pageRequest);

        Page<Contragent> page = new Page<>(repoPage.getTotalElements(), repoPage.getContent());
        return page;
    }

    public Contragent getContragentById(String id) {
        UUID uuid = UUID.fromString(id);
        Contragent contragent = contragentRepository.findOne(uuid);
        return contragent;
    }

    public String saveContragent(Contragent contragent) {
        if (contragent.getId() == null) {
            UUID id = UUID.randomUUID();
            contragent.setId(id);
        } else {
            if (contragentRepository.findOne(contragent.getId()) == null) {
                throw new SokolException("Contragent not found");
            }
        }
        contragentRepository.save(contragent);
        return contragent.getId().toString();
    }

    public void deleteContragent(String id) {
        UUID uuid = UUID.fromString(id);
        contragentRepository.delete(uuid);
    }

    public void setContragentRepository(ContragentRepository contragentRepository) {
        this.contragentRepository = contragentRepository;
    }
}
