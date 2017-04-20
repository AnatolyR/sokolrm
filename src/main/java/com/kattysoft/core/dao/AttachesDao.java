/*
 * Copyright 2017 Anatolii Rakovskii (rtolik@yandex.ru)
 *
 * Software distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */
package com.kattysoft.core.dao;

import com.kattysoft.core.model.User;

import java.util.Date;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 17.04.2017
 */
public interface AttachesDao {
    byte[] getContent(String id);

    void addContent(String reportObjectId, String name, User user, Date date, byte[] bytes);
}
