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

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 31.01.2017
 */
public class DocumentType {
    private String id;

    private String title;

    private String flow;

    private List<FieldType> fieldsTypes;

    private List<String> actions;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<FieldType> getFieldsTypes() {
        if (fieldsTypes == null) {
            fieldsTypes = new ArrayList<>();
        }
        return fieldsTypes;
    }

    public void setFieldsTypes(List<FieldType> fieldsTypes) {
        this.fieldsTypes = fieldsTypes;
    }

    public List<String> getActions() {
        if (actions == null) {
            actions =  new ArrayList<>();
        }
        return actions;
    }

    public void setActions(List<String> actions) {
        this.actions = actions;
    }

    public String getFlow() {
        return flow;
    }

    public void setFlow(String flow) {
        this.flow = flow;
    }

    @Override
    public String toString() {
        return "DocumentType{" +
            "id='" + id + '\'' +
            ", title='" + title + '\'' +
            ", fieldsTypes=" + fieldsTypes +
            '}';
    }

    public static class FieldType {
        private String id;
        private String title;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return "FieldType{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                '}';
        }
    }
}
