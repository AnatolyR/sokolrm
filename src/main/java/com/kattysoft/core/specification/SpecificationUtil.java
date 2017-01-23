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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 20.01.2017
 */
public class SpecificationUtil {
    public static Condition read(ArrayNode node) {
        Iterator<JsonNode> iterator = node.iterator();
        Condition condition = toCondition(null, iterator);
        return condition;
    }

    public static ContainerCondition toCondition(ContainerOperation operation, Iterator<JsonNode> iterator) {
        List<Condition> conditions = new ArrayList<>();
        while (iterator.hasNext()) {
            ObjectNode conditionNode = (ObjectNode) iterator.next();
            String conditionValue = conditionNode.get("condition") != null ? conditionNode.get("condition").asText() : "";
            if ("".equals(conditionValue)) {
                Condition c = toCondition(conditionNode);
                if (c != null) {
                    conditions.add(c);
                }
            } else if ("and_block".equals(conditionValue)) {
                ContainerCondition c = toCondition(ContainerOperation.AND, iterator);
                if (c != null && c.getConditions().size() > 0) {
                    conditions.add(c);
                }
            } else if ("or_block".equals(conditionValue)) {
                ContainerCondition c = toCondition(ContainerOperation.OR, iterator);
                if (c != null && c.getConditions().size() > 0) {
                    conditions.add(c);
                }
            } else if ("end_block".equals(conditionValue)) {
                break;
            }
        }
        if (conditions.size() == 1 && conditions.get(0) instanceof ContainerCondition) {
            return (ContainerCondition) conditions.get(0);
        } else if (conditions.size() > 0) {
            ContainerCondition condition = new ContainerCondition();
            condition.setOperation(operation != null ? operation : ContainerOperation.AND);
            condition.setConditions(conditions);
            return condition;
        } else {
            return null;
        }
    }

    public static Condition toCondition(ObjectNode conditionNode) {
        if (conditionNode.get("column") != null && !conditionNode.get("column").asText().isEmpty()
            && conditionNode.get("operation") != null && !conditionNode.get("operation").asText().isEmpty()) {
            ValueCondition condition = new ValueCondition();

            condition.setField(conditionNode.get("column").asText());
            condition.setOperation(Operation.valueOf(conditionNode.get("operation").asText()));
            condition.setValue(conditionNode.get("value") != null ? conditionNode.get("value").asText() : null);
            return condition;
        } else {
            return null;
        }
    }
}
