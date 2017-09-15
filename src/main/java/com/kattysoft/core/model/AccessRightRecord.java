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
package com.kattysoft.core.model;

import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 01.02.2017
 */
@Entity
@Table(name = "accessrecords")
public class AccessRightRecord {
    @Id
    @Type(type = "pg-uuid")
    private UUID id;

    @Type(type = "pg-uuid")
    private UUID groupId;

    private String space;

    private String element;

    private String subelement;

    private String level;

    public AccessRightRecord() {
    }

    public AccessRightRecord(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getGroupId() {
        return groupId;
    }

    public void setGroupId(UUID groupId) {
        this.groupId = groupId;
    }

    public String getSpace() {
        return space;
    }

    public void setSpace(String space) {
        this.space = space;
    }

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public String getSubelement() {
        return subelement;
    }

    public void setSubelement(String subelement) {
        this.subelement = subelement;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "AccessRightRecord{" +
            "id=" + id +
            ", groupId=" + groupId +
            ", space='" + space + '\'' +
            ", element='" + element + '\'' +
            ", subelement='" + subelement + '\'' +
            ", level='" + level + '\'' +
            '}';
    }
}
