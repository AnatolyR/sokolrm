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
import com.kattysoft.core.model.Contragent;
import com.kattysoft.core.repository.ContragentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

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

    public void setContragentRepository(ContragentRepository contragentRepository) {
        this.contragentRepository = contragentRepository;
    }
}
