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
package com.kattysoft.core;

import com.kattysoft.core.repository.PostgreSQLFullTextSearchFunction;
import org.hibernate.dialect.PostgreSQL9Dialect;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 15.04.2015.
 */
public class CustomPGDialect extends PostgreSQL9Dialect {
    public static final int JSON_TYPE = 3001;
    public CustomPGDialect() {
        super();
        registerColumnType(JSON_TYPE, "jsonb");
        registerFunction("fts", new PostgreSQLFullTextSearchFunction());
    }
}
