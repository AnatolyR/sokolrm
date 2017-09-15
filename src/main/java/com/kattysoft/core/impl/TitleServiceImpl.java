/*
 * Copyright 2016-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kattysoft.core.impl;

import com.kattysoft.core.TitleService;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 22.02.2017
 */
public class TitleServiceImpl implements TitleService {
    public static Map<String, Map<String, String>> values = new HashMap<>();

    static {
        Map<String, String> objectTypes = new HashMap<>();
        objectTypes.put("document", "Документ");
        objectTypes.put("task", "Задача");
        objectTypes.put("user", "Пользователь");
        objectTypes.put("contragent", "Контрагент");
        objectTypes.put("report", "Отчет");
        objectTypes.put("group", "Группа");
        values.put("objectTypes", objectTypes);
        
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
        status.put("template", "Шаблон");
        status.put("registered", "Зарегистрирован");
        status.put("review", "Рассмотрение");
        status.put("execution", "Исполнение");
        status.put("executed", "Исполнено");
        status.put("not_executed", "Не исполнено");
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

        Map<String, String> fieldsTitles = new HashMap<>();
        fieldsTitles.put("title", "Заголовок");
        fieldsTitles.put("status", "Статус");
        fieldsTitles.put("documentKind", "Вид документа");
        fieldsTitles.put("addressee", "Адресаты");
        fieldsTitles.put("correspondent", "Корреспондент");
        fieldsTitles.put("externalSigner", "Кем подписано");
        fieldsTitles.put("externalExecutor", "Исполнитель");
        fieldsTitles.put("externalNumber", "Исходящий номер");
        fieldsTitles.put("externalDate", "Исходящая дата");
        fieldsTitles.put("documentNumber", "Номер документа");
        fieldsTitles.put("registrationDate", "Дата регистрации");
        fieldsTitles.put("executionDate", "Дата исполнения");
        fieldsTitles.put("pagesQuantity", "Количество листов");
        fieldsTitles.put("itemQuantity", "Количество экземпляров");
        fieldsTitles.put("appQuantity", "Количество приложений");
        fieldsTitles.put("comment", "Комментарий");
        fieldsTitles.put("space", "Пространство");
        fieldsTitles.put("archivecase", "Дело");
        fieldsTitles.put("signer", "Подписант");
        fieldsTitles.put("executors", "Исполнители");
        values.put("fieldsTitles", fieldsTitles);

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
