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

        Map<String, String> executionResult = new HashMap<>();
        executionResult.put("done", "Исполнено");
        executionResult.put("not_done", "Не исполнено");
        executionResult.put("agreed", "Согласовано");
        executionResult.put("not_agreed", "Не согласовано");
        executionResult.put("agreed_with_note", "Согласовано с замечаниями");
        values.put("executionResult", executionResult);

        Map<String, String> executionType = new HashMap<>();
        executionType.put("execution", "Исполнение");
        executionType.put("approval", "Согласование");
        values.put("executionType", executionType);

        Map<String, String> status = new HashMap<>();
        status.put("draft", "Черновик");
        status.put("registered", "Зарегистрирован");
        status.put("review", "Рассмотрение");
        status.put("execution", "Исполнение");
        status.put("executed", "Исполнено");
        status.put("tocase", "В дело");
        status.put("archive", "Архив");

        status.put("project", "Проект");
        status.put("approval", "Согласование");
        status.put("agreed", "Согласовано");
        status.put("not_agreed", "Не согласовано");
        status.put("sign", "На подписании");
        status.put("signed", "Подписано");
        status.put("sent", "Отправлено");
        values.put("status", status);

        Map<String, String> actionTitles = new HashMap<>();
        actionTitles.put("doregistration", "Зарегистрировать");
        actionTitles.put("toreview", "На рассмотрение");
        actionTitles.put("doresolution", "Резолюция");
        actionTitles.put("tocase", "В дело");
        actionTitles.put("toarchive", "В архив");
        actionTitles.put("toproject", "Опуликовать");
        actionTitles.put("toapproval", "На согласование");
        actionTitles.put("tosign", "На подписание");
        actionTitles.put("sign", "Подписать");
        actionTitles.put("reject", "Вернуть");
        actionTitles.put("send", "Отправлено");
        values.put("action", actionTitles);
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
