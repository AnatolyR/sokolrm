package com.kattysoft.core.dao;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 13.12.2017
 */
public interface ConfigFileDao {
    byte[] getContent(String path);
}
