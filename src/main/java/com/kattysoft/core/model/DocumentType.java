/*
 * Copyright 2017 Anatolii Rakovskii (rtolik@yandex.ru)
 *
 * No part of this file can be copied or reproduced without written permission of author.
 *
 * Software distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
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
