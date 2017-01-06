/*
 * Copyright 2016 Anatolii Rakovskii (rtolik@yandex.ru)
 *
 * No part of this file can be copied or reproduced without written permission of author.
 *
 * Software distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

import java.util.UUID;
import java.util.stream.Stream;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 28.12.2016
 */
public class DocumengKindGenerator {
    private static String[] data = new String[]{
        "Акт",
        "Апелляционная жалоба",
        "Жалоба",
        "Запрос",
        "Заявка",
        "Извещение",
        "Исковое заявление",
        "Исполнительный лист",
        "Кассационная жалоба",
        "Определение",
        "Ответ на запрос",
        "Отчет",
        "Письмо",
        "Повестка",
        "Поручение",
        "Постановление",
        "Предписание",
        "Представление",
        "Предупреждение",
        "Претензия",
        "Приглашение",
        "Приговор суда",
        "Приказ",
        "Протокол",
        "Распоряжение",
        "Решение",
        "Справка",
        "Судебная повестка",
        "Телеграмма",
        "Телефонограмма",
        "Требование",
        "Уведомление",
        "Указ",
        "Указание Минтранса"
    };

    public static void main(String[] args) {
        Stream.of(data).forEach(v -> {
            String dictionaryId = "documentKind";
            String id = UUID.randomUUID().toString();
            String insert = "INSERT INTO dictionaryValues (id, dictionaryId, title) VALUES ('$1', '$2', '$3');";
            insert = insert.replace("$1", id).replace("$2", dictionaryId).replace("$3", v);
            System.out.println(insert);
        });
    }
}
