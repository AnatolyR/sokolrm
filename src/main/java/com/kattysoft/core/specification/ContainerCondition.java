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
import java.util.Arrays;
import java.util.List;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 20.07.2016
 */
public class ContainerCondition extends Condition {
    private List<Condition> conditions;
    private ContainerOperation operation;

    public ContainerCondition() {
    }

    public ContainerCondition(ContainerOperation operation, Condition... condition) {
        this.operation = operation;
        this.conditions = new ArrayList<>(Arrays.asList(condition));
    }

    public List<Condition> getConditions() {
        if (conditions == null) {
            conditions = new ArrayList<>();
        }
        return conditions;
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }

    public ContainerOperation getOperation() {
        return operation;
    }

    public void setOperation(ContainerOperation operation) {
        this.operation = operation;
    }

    @Override
    public String toString() {
        return "ContainerCondition{" +
            "conditions=" + conditions +
            ", operation=" + operation +
            '}';
    }
}
