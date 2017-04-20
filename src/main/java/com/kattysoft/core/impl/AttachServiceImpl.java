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

import com.kattysoft.core.AttachService;
import com.kattysoft.core.dao.AttachesDao;
import com.kattysoft.core.model.Attach;
import com.kattysoft.core.model.User;
import com.kattysoft.core.repository.AttachRepository;
import org.springframework.beans.factory.annotation.Autowired;

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

    public void setAttachRepository(AttachRepository attachRepository) {
        this.attachRepository = attachRepository;
    }

    public void setAttachesDao(AttachesDao attachesDao) {
        this.attachesDao = attachesDao;
    }
}
