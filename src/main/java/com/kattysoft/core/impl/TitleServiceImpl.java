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

import com.kattysoft.core.TitleService;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 22.02.2017
 */
public class TitleServiceImpl implements TitleService {
    public static Map<String, Map<String, String>> values = new HashMap<>();

    static {
        Map<String, String> executionStatus = new HashMap<>();
        executionStatus.put("run", "Выполняется");
        executionStatus.put("complete", "Завершено");
        values.put("executionStatus", executionStatus);

        Map<String, String> executionType = new HashMap<>();
        executionType.put("execution", "Исполнение");
        values.put("executionType", executionType);

        Map<String, String> status = new HashMap<>();
        status.put("draft", "Черновик");
        status.put("registered", "Зарегистрирован");
        status.put("review", "Рассмотрение");
        status.put("resolution", "Исполнение");
        status.put("executed", "Исполнено");
        status.put("tocase", "В дело");
        status.put("archive", "Архив");
        values.put("status", status);
    }

    @Override
    public String getTitle(String space, String name) {
        return values.containsKey(space) ? values.get(space).get(name) : null;
    }

    @Override
    public String getTitleNotNull(String space, String name) {
        String title = getTitle(space, name);
        if (title == null) {
            title = "[" + name + "]";
        }
        return title;
    }

    @Override
    public String getName(String space, String value) {
        return values.containsKey(space) ? getKeyByValue(values.get(space), value) : null;
    }

    @Override
    public String getNameNotNull(String space, String value) {
        String name = getName(space, value);
        if (name == null) {
            name = value;
        }
        return name;
    }

    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        return map.entrySet()
            .stream()
            .filter(entry -> Objects.equals(entry.getValue(), value))
            .map(Map.Entry::getKey)
            .findFirst().orElse(null);
    }
}
