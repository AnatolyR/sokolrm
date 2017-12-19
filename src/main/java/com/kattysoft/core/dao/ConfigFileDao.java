package com.kattysoft.core.dao;

import java.util.UUID;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 13.12.2017
 */
public interface ConfigFileDao {
    byte[] getContent(String path);

    byte[] getContent(UUID id);

    void saveContent(UUID uuid, byte[] content);
}
