package com.kattysoft.core.specification;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mysema.commons.lang.Assert;
import org.apache.commons.io.IOUtils;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.testng.Assert.*;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 20.01.2017
 */
public class SpecificationUtilTest {
    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testEmptyConditionBlock() throws Exception {
        String condition = IOUtils.toString(SpecificationUtilTest.class.getResourceAsStream("condition.json"), "utf-8");
        ArrayNode conditionNode = (ArrayNode) mapper.readTree(condition);

        Condition conditionObject = SpecificationUtil.read(conditionNode);
        System.out.println(conditionObject);
        assertThat(conditionObject.toString(), equalTo("ContainerCondition{conditions=[ValueCondition{field='documentNumber', operation=GREAT, value=111}, " +
            "ValueCondition{field='status', operation=LIKE, value=222}, ValueCondition{field='author', operation=EQUAL, value=333}, " +
            "ValueCondition{field='documentKind', operation=STARTS, value=444}], operation=AND}"));
    }

    @Test
    public void testAndBlock() throws Exception {
        String condition = IOUtils.toString(SpecificationUtilTest.class.getResourceAsStream("conditionAndBlock.json"), "utf-8");
        ArrayNode conditionNode = (ArrayNode) mapper.readTree(condition);

        Condition conditionObject = SpecificationUtil.read(conditionNode);
        System.out.println(conditionObject);
        assertThat(conditionObject.toString(), equalTo("ContainerCondition{conditions=[ValueCondition{field='documentNumber', operation=GREAT, value=111}, " +
            "ValueCondition{field='status', operation=LIKE, value=222}, ValueCondition{field='author', operation=EQUAL, value=333}, " +
            "ValueCondition{field='documentKind', operation=STARTS, value=444}], operation=AND}"));
    }

    @Test
    public void testAndBlockUnclosed() throws Exception {
        String condition = IOUtils.toString(SpecificationUtilTest.class.getResourceAsStream("conditionAndBlockUnclosed.json"), "utf-8");
        ArrayNode conditionNode = (ArrayNode) mapper.readTree(condition);

        Condition conditionObject = SpecificationUtil.read(conditionNode);
        System.out.println(conditionObject);
        assertThat(conditionObject.toString(), equalTo("ContainerCondition{conditions=[ValueCondition{field='documentNumber', operation=GREAT, value=111}, " +
            "ValueCondition{field='status', operation=LIKE, value=222}, ValueCondition{field='author', operation=EQUAL, value=333}, " +
            "ValueCondition{field='documentKind', operation=STARTS, value=444}], operation=AND}"));
    }

    @Test
    public void testOrBlock() throws Exception {
        String condition = IOUtils.toString(SpecificationUtilTest.class.getResourceAsStream("conditionOrBlock.json"), "utf-8");
        ArrayNode conditionNode = (ArrayNode) mapper.readTree(condition);

        Condition conditionObject = SpecificationUtil.read(conditionNode);
        System.out.println(conditionObject);
        assertThat(conditionObject.toString(), equalTo("ContainerCondition{conditions=[ValueCondition{field='documentNumber', operation=GREAT, value=111}, " +
            "ValueCondition{field='status', operation=LIKE, value=222}, ValueCondition{field='author', operation=EQUAL, value=333}, " +
            "ValueCondition{field='documentKind', operation=STARTS, value=444}], operation=OR}"));
    }

    @Test
    public void testOrBlockUnclosed() throws Exception {
        String condition = IOUtils.toString(SpecificationUtilTest.class.getResourceAsStream("conditionOrBlockUnclosed.json"), "utf-8");
        ArrayNode conditionNode = (ArrayNode) mapper.readTree(condition);

        Condition conditionObject = SpecificationUtil.read(conditionNode);
        System.out.println(conditionObject);
        assertThat(conditionObject.toString(), equalTo("ContainerCondition{conditions=[ValueCondition{field='documentNumber', operation=GREAT, value=111}, " +
            "ValueCondition{field='status', operation=LIKE, value=222}, ValueCondition{field='author', operation=EQUAL, value=333}, " +
            "ValueCondition{field='documentKind', operation=STARTS, value=444}], operation=OR}"));
    }

    @Test
    public void testEmptyWithOr() throws Exception {
        String condition = IOUtils.toString(SpecificationUtilTest.class.getResourceAsStream("conditionEmptyWithOr.json"), "utf-8");
        ArrayNode conditionNode = (ArrayNode) mapper.readTree(condition);

        Condition conditionObject = SpecificationUtil.read(conditionNode);
        System.out.println(conditionObject);
        assertThat(conditionObject.toString(), equalTo("ContainerCondition{" +
            "conditions=[" +
            "ValueCondition{field='documentNumber', operation=GREAT, value=111}, " +
            "ValueCondition{field='status', operation=LIKE, value=222}, " +
            "ContainerCondition{conditions=[" +
                "ValueCondition{field='author', operation=EQUAL, value=333}, " +
                "ValueCondition{field='author', operation=EQUAL, value=444}], " +
                "operation=OR}], " +
            "operation=AND}"));
    }

    @Test
    public void testDoubleOr() throws Exception {
        String condition = IOUtils.toString(SpecificationUtilTest.class.getResourceAsStream("conditionDoubleOrBlocks.json"), "utf-8");
        ArrayNode conditionNode = (ArrayNode) mapper.readTree(condition);

        Condition conditionObject = SpecificationUtil.read(conditionNode);
        System.out.println(conditionObject);
        assertThat(conditionObject.toString(), equalTo("ContainerCondition{conditions=[" +
            "ContainerCondition{conditions=[" +
                "ValueCondition{field='documentNumber', operation=GREAT, value=111}, " +
                "ValueCondition{field='status', operation=LIKE, value=222}], " +
                "operation=OR}, " +
            "ContainerCondition{conditions=[" +
                "ValueCondition{field='author', operation=EQUAL, value=333}, " +
                "ValueCondition{field='documentKind', operation=STARTS, value=444}], " +
                "operation=OR}], " +
            "operation=AND}"));
    }

    @Test
    public void testNested() throws Exception {
        String condition = IOUtils.toString(SpecificationUtilTest.class.getResourceAsStream("conditionNested.json"), "utf-8");
        ArrayNode conditionNode = (ArrayNode) mapper.readTree(condition);

        Condition conditionObject = SpecificationUtil.read(conditionNode);
        System.out.println(conditionObject);
        assertThat(conditionObject.toString(), equalTo("ContainerCondition{conditions=[" +
            "ValueCondition{field='documentNumber', operation=GREAT, value=111}, " +
            "ValueCondition{field='status', operation=LIKE, value=222}, " +
            "ValueCondition{field='author', operation=EQUAL, value=333}, " +
            "ValueCondition{field='documentKind', operation=STARTS, value=444}, " +
            "ContainerCondition{conditions=[" +
                "ValueCondition{field='documentNumber', operation=GREAT, value=111}, " +
                "ValueCondition{field='status', operation=LIKE, value=222}, " +
                "ValueCondition{field='author', operation=EQUAL, value=333}, " +
                "ValueCondition{field='documentKind', operation=STARTS, value=444}, " +
                "ContainerCondition{conditions=[" +
                    "ValueCondition{field='documentNumber', operation=GREAT, value=111}, " +
                    "ValueCondition{field='status', operation=LIKE, value=222}, " +
                    "ValueCondition{field='author', operation=EQUAL, value=333}, " +
                    "ValueCondition{field='documentKind', operation=STARTS, value=444}], " +
                    "operation=OR}], " +
                "operation=AND}], " +
            "operation=OR}"));
    }

    @Test
    public void testEmptyFieldsOneElement() throws Exception {
        String condition = IOUtils.toString(SpecificationUtilTest.class.getResourceAsStream("conditionEmptyFieldsOneElement.json"), "utf-8");
        ArrayNode conditionNode = (ArrayNode) mapper.readTree(condition);

        Condition conditionObject = SpecificationUtil.read(conditionNode);
        System.out.println(conditionObject);
        assertThat(conditionObject, is(nullValue()));
    }

    @Test
    public void testEmptyOneElement() throws Exception {
        String condition = IOUtils.toString(SpecificationUtilTest.class.getResourceAsStream("conditionEmptyOneElement.json"), "utf-8");
        ArrayNode conditionNode = (ArrayNode) mapper.readTree(condition);

        Condition conditionObject = SpecificationUtil.read(conditionNode);
        System.out.println(conditionObject);
        assertThat(conditionObject, is(nullValue()));
    }

    @Test
    public void testEmptyList() throws Exception {
        String condition = IOUtils.toString(SpecificationUtilTest.class.getResourceAsStream("conditionEmptyList.json"), "utf-8");
        ArrayNode conditionNode = (ArrayNode) mapper.readTree(condition);

        Condition conditionObject = SpecificationUtil.read(conditionNode);
        System.out.println(conditionObject);
        assertThat(conditionObject, is(nullValue()));
    }
}