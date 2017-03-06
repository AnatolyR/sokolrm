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

import com.kattysoft.core.AccessRightLevel;
import com.kattysoft.core.AccessRightService;
import com.kattysoft.core.SokolException;
import com.kattysoft.core.UserService;
import com.kattysoft.core.model.AccessRightRecord;
import com.kattysoft.core.model.Document;
import com.kattysoft.core.model.User;
import com.kattysoft.core.repository.AccessRightRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 01.02.2017
 */
public class AccessRightServiceImpl implements AccessRightService {
    private static final Logger log = LoggerFactory.getLogger(AccessRightServiceImpl.class);
    @Autowired
    private AccessRightRecordRepository accessRightRecordRepository;

    @Autowired
    private UserService userService;

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

    @Override
    public boolean checkRights(String space, String element, String subelement, AccessRightLevel level) {
        User user = userService.getCurrentUser();
        log.debug("Check rights for user {}: {}.{}.{}.{}", user.getTitle() + " (" + user.getId() + ")", space, element, subelement, level);
        List<UUID> groups = user.getGroups();
        boolean result = false;
        boolean generalResult = false;
        boolean generalNone = false;
        if (log.isDebugEnabled()) {
            groups.forEach(groupId -> {
                log.debug("User groups {}: {}", user.getTitle() + " (" + user.getId() + ")", String.join(", ", groups.stream().map(UUID::toString).collect(Collectors.toList())));
            });
        }
        for (UUID groupId : groups) {
            List<AccessRightRecord> records = accessRightRecordRepository.findAllByGroupIdAndSpaceAndElementAndSubelement(groupId, space, element, subelement);
            if (log.isDebugEnabled()) {
                records.forEach(r -> log.debug("Record : {}", r));
            }
            List<String> levels = records.stream().map(AccessRightRecord::getLevel).collect(Collectors.toList());
            if (levels.contains(AccessRightLevel.DENY.toString())) {
                return false;
            }
            if (levels.contains(level.toString())) {
                result = true;
            }
            List<AccessRightRecord> generalRecords = accessRightRecordRepository.findAllByGroupIdAndSpaceAndElementAndSubelementAndLevel(groupId, space, element, "", level.toString());
            if (log.isDebugEnabled()) {
                generalRecords.forEach(r -> log.debug("General record : {}", r));
            }
            List<String> generalLevels = generalRecords.stream().map(AccessRightRecord::getLevel).collect(Collectors.toList());
            if (generalLevels.contains(level.toString())) {
                generalResult = true;
            }
            if (generalLevels.contains(AccessRightLevel.DENY.toString())) {
                generalNone = true;
            }
        }
        return result || (generalResult && !generalNone);
    }

    public boolean hasRights(String space, String element, String subelement, AccessRightLevel level) {
        User user = userService.getCurrentUser();
        log.debug("Has rights for user {}: {}.{}.{}.{}", user.getTitle() + " (" + user.getId() + ")", space, element, subelement, level);
        List<UUID> groups = user.getGroups();
        if (log.isDebugEnabled()) {
            groups.forEach(groupId -> {
                log.debug("User groups {}: {}", user.getTitle() + " (" + user.getId() + ")", String.join(", ", groups.stream().map(UUID::toString).collect(Collectors.toList())));
            });
        }
        for (UUID groupId : groups) {

            List<AccessRightRecord> records = accessRightRecordRepository.findAllByGroupIdAndSpaceAndElementAndSubelementAndLevel(groupId, space, element, subelement, level.toString());
            if (log.isDebugEnabled()) {
                records.forEach(r -> log.debug("Record : {}", r));
            }
            List<String> generalLevels = records.stream().map(AccessRightRecord::getLevel).collect(Collectors.toList());
            if (generalLevels.contains(level.toString())) {
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean checkDocumentRights(Document document, String subelement, AccessRightLevel level) {
        if (this.hasRights(document.getSpace(), document.getType(), subelement, AccessRightLevel.DENY)) {
            return false;
        }
        if (this.hasRights(document.getSpace(), document.getType(), subelement, level)) {
            return true;
        }
        if (this.hasRights(document.getSpace(), "_document", subelement, AccessRightLevel.DENY)) {
            return false;
        }
        if (this.hasRights(document.getSpace(), "_document", subelement, level)) {
            return true;
        }
        if (this.hasRights("_space", "_document", subelement, AccessRightLevel.DENY)) {
            return false;
        }
        if (this.hasRights("_space", "_document", subelement, level)) {
            return true;
        }
        return false;
    }

    @Override
    public List<AccessRightLevel> getRights(String space, String element, String subelement) {
        return Arrays.asList(AccessRightLevel.DENY, AccessRightLevel.READ, AccessRightLevel.WRITE,
            AccessRightLevel.CREATE, AccessRightLevel.ADD, AccessRightLevel.DELETE, AccessRightLevel.LIST);
    }

    public void setAccessRightRecordRepository(AccessRightRecordRepository accessRightRecordRepository) {
        this.accessRightRecordRepository = accessRightRecordRepository;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
