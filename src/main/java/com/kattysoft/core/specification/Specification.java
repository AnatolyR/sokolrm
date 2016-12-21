/*
 * Copyright 2016 Anatolii Rakovskii (rtolik@yandex.ru)
 *
 * No part of this file can be copied or reproduced without written permission of author.
 *
 * Software distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */
package com.kattysoft.core.specification;

import java.util.ArrayList;
import java.util.List;

/*
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 19.07.2016
 */

/**
 * Class to hold parameters required to specify criteria for object lists.
 * Instead of different numbers of argument we can use instances of this class
 * and extend if need in future without changing signature. Firstly there was
 * idea to use something like Querydsl Predicate or Expression or JPA Criteria
 * API classes but that class to complex to easily handle so it was decided to
 * create own, may be in future it will be replaced to some common largely use alt.
 */
public class Specification {
    private List<String> fields;
    private Condition condition;
    private List<Sort> sort;
    private Integer size;
    private Integer offset;

    public List<String> getFields() {
        if (fields == null) {
            fields = new ArrayList<>();
        }
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public List<Sort> getSort() {
        return sort;
    }

    public void setSort(List<Sort> sort) {
        this.sort = sort;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }
}
