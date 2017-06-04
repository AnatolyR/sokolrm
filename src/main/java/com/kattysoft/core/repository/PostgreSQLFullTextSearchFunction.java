/*
 * Copyright 2017 Anatolii Rakovskii (rtolik@yandex.ru)
 *
 * No part of this file can be copied or reproduced without written permission of author.
 *
 * Software distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */
package com.kattysoft.core.repository;

import org.hibernate.QueryException;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.BooleanType;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;

import java.util.List;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 30.05.2017
 */
public class PostgreSQLFullTextSearchFunction implements SQLFunction {
    @Override
    public Type getReturnType(Type columnType, Mapping mapping) throws QueryException {
        return StandardBasicTypes.BOOLEAN;
    }

    @Override
    public boolean hasArguments() {
        return true;
    }

    @Override
    public boolean hasParenthesesIfNoArguments() {
        return false;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public String render(Type firstArgumentType, List args, SessionFactoryImplementor factory)
        throws QueryException {
        if (args != null && args.size() < 2) {
            throw new IllegalArgumentException(
                "The function must be passed 2 arguments");
        }

        String fragment = null;
        String ftsConfig = null;
        String field = null;
        String value = null;
        if (args.size() == 3) {
            ftsConfig = (String) args.get(0);
            field = (String) args.get(1);
            value = (String) args.get(2);
//            fragment = "to_tsvector('russian', " + field + ") @@ to_tsquery(" + ftsConfig + ", " + value + ")";
            fragment = field + " @@ to_tsquery('russian', " + ftsConfig + ", " + value + ")";
        } else {
            field = (String) args.get(0);
            value = (String) args.get(1);
            fragment = "(to_tsvector('russian', " + field + ") @@ to_tsquery('russian', " + value + "))";
//            fragment = "(" + field + " @@ to_tsquery('russian', " + value + "))";
        }
        return fragment;
    }
}
