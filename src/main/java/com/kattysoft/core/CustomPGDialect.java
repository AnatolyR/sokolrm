/*
 * Copyright 2015 Anatolii Rakovskii (rtolik@yandex.ru)
 *
 * No part of this file can be copied or reproduced without written permission of author.
 *
 * Software distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
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
