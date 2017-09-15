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

import java.util.Random;
import java.util.UUID;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 17.12.2016
 */
public class UserGenerator {
    public static final String[] names = new String[] {
        "Борис",
        "Бронислав",
        "Густав",
        "Виктор",
        "Глеб",
        "Иван",
        "Карл",
        "Леонид",
        "Никита"
    };
    public static final String[] wNames = new String[] {
        "Антонина",
        "Валентина",
        "Анна",
        "Дарья",
        "Катерина"
    };
    public static final String[] middleNames = new String[] {
        "Алексеевич",
        "Валерьевич",
        "Максимович",
        "Петрович",
        "Вячеславович",
        "Сергеевич",
        "Николаевич",
        "Игоревич"
    };
    public static final String[] wMiddleNames = new String[] {
        "Аркадьевна",
        "Вячеславовна",
        "Георгиевна",
        "Егоровна",
        "Михайловна",
        "Сергеевна"
    };
    public static final String[] lastNames = new String[] {
        "Агапов",
        "Алексеев",
        "Балашов",
        "Беломестов",
        "Болотников",
        "Былинкин",
        "Виноградов",
        "Волков",
        "Гарин",
        "Грибов",
        "Добрынин",
        "Енотин",
        "Захаров",
        "Зверев",
        "Ивашов",
        "Карандашов",
        "Луков",
        "Поляков"
    };
    public static final String[] wLastNames = new String[] {
        "Агапова",
        "Алексеева",
        "Балашова",
        "Беломестова",
        "Болотникова",
        "Былинкина",
        "Виноградова",
        "Волкова",
        "Гарина",
        "Грибова",
        "Добрынина",
        "Енотина",
        "Захарова",
        "Зверева",
        "Ивашова",
        "Карандашова",
        "Лукова",
        "Полякова"
    };

    public static void main(String[] args) {
        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            String name = names[random.nextInt(names.length)];
            String middleName = middleNames[random.nextInt(middleNames.length)];
            String lastName = lastNames[random.nextInt(lastNames.length)];
            String title = lastName + " " + name.substring(0, 1).toUpperCase() + ". " + middleName.substring(0, 1).toUpperCase() + ".";
            String uuid = UUID.randomUUID().toString();

            String insert = "INSERT INTO users (id, title, login, password, firstname, middlename, lastname) VALUES\n" +
                "  ('" + uuid + "', '" + title + "', null, null, '" + name + "', '" + middleName + "', '" + lastName + "');";
            System.out.println(insert);
        }
        for (int i = 0; i < 10; i++) {
            String name = wNames[random.nextInt(wNames.length)];
            String middleName = wMiddleNames[random.nextInt(wMiddleNames.length)];
            String lastName = wLastNames[random.nextInt(wLastNames.length)];
            String title = lastName + " " + name.substring(0, 1).toUpperCase() + ". " + middleName.substring(0, 1).toUpperCase() + ".";
            String uuid = UUID.randomUUID().toString();

            String insert = "INSERT INTO users (id, title, login, password, firstname, middlename, lastname) VALUES\n" +
                "  ('" + uuid + "', '" + title + "', null, null, '" + name + "', '" + middleName + "', '" + lastName + "');";
            System.out.println(insert);
        }
    }
}
