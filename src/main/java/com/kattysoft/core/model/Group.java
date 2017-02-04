/*
 * Copyright 2016 Anatolii Rakovskii (rtolik@yandex.ru)
 *
 * No part of this file can be copied or reproduced without written permission of author.
 *
 * Software distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */
package com.kattysoft.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 27.01.2017
 */
@Entity
@Table(name = "groups")
@TypeDefs({@TypeDef( name= "JsonObject", typeClass = JsonType.class)})
public class Group {
    @Id
    @Type(type = "pg-uuid")
    private UUID id;

    private String title;

    @JsonIgnore
    @Column(name = "ardata")
    @Type(type = "JsonObject")
    private ObjectNode data;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name="user_groups", joinColumns=@JoinColumn(name="groupid"))
    @Column(name="userid")
    private List<UUID> users;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ObjectNode getData() {
        return data;
    }

    public void setData(ObjectNode data) {
        this.data = data;
    }

    public List<UUID> getUsers() {
        return users;
    }

    public void setUsers(List<UUID> users) {
        this.users = users;
    }
}
