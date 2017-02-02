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

import com.kattysoft.core.AccessRightService;
import com.kattysoft.core.SokolException;
import com.kattysoft.core.model.AccessRightRecord;
import com.kattysoft.core.repository.AccessRightRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 01.02.2017
 */
public class AccessRightServiceImpl implements AccessRightService {
    @Autowired
    private AccessRightRecordRepository accessRightRecordRepository;

    @Override
    public List<AccessRightRecord> getAccessRightsForGroup(String groupId) {
        if (groupId == null || groupId.isEmpty()) {
            throw new SokolException("GroupId is empty");
        }
        UUID uuid = UUID.fromString(groupId);
        List<AccessRightRecord> records = accessRightRecordRepository.findAllByGroupId(uuid);
        return records;
    }

    @Override
    public String addRecord(AccessRightRecord record) {
        UUID uuid = UUID.randomUUID();
        record.setId(uuid);

        accessRightRecordRepository.save(record);
        return uuid.toString();
    }

    @Override
    public AccessRightRecord getRecord(String id) {
        if (id == null || id.isEmpty()) {
            throw new SokolException("id is empty");
        }
        UUID uuid = UUID.fromString(id);
        AccessRightRecord record = accessRightRecordRepository.findOne(uuid);
        return record;
    }

    @Override
    public void deleteRecords(List<String> ids) {
        List<AccessRightRecord> values = ids.stream().map(id -> new AccessRightRecord(UUID.fromString(id))).collect(Collectors.toList());
        accessRightRecordRepository.delete(values);
    }

    public void setAccessRightRecordRepository(AccessRightRecordRepository accessRightRecordRepository) {
        this.accessRightRecordRepository = accessRightRecordRepository;
    }
}
