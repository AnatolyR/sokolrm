/*
 * Copyright 2017 Anatolii Rakovskii (rtolik@yandex.ru)
 *
 * Software distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */
package com.kattysoft.core;

import com.kattysoft.core.model.AccessRightRecord;

import java.util.List;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 01.02.2017
 */
public interface AccessRightService {
    List<AccessRightRecord> getAccessRightsForGroup(String groupId);

    String addRecord(AccessRightRecord record);

    AccessRightRecord getRecord(String id);

    void deleteRecords(List<String> strings);

    boolean checkRights(String space, String element, String subelement, AccessRightLevel level);

    List<AccessRightLevel> getRights(String space, String element, String subelement);
}
