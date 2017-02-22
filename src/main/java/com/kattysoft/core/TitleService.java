/*
 * Copyright 2017 Anatolii Rakovskii (rtolik@yandex.ru)
 *
 * Software distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */
package com.kattysoft.core;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 22.02.2017
 */
public interface TitleService {
    String getTitle(String space, String name);

    String getTitleNotNull(String space, String name);

    String getName(String space, String value);

    String getNameNotNull(String space, String value);
}
