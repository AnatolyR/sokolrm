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
    private Integer size = 20; //default page size
    private Integer offset;
    private String join;
    private String searchText;

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

    public String getJoin() {
        return join;
    }

    public void setJoin(String join) {
        this.join = join;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }
}
