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
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 14.12.2016
 */
@NamedEntityGraph(
    name = "User.groups",
    attributeNodes={@NamedAttributeNode("groups")}
)
@Entity
@Table(name = "users")
public class User {
    @Id
    @Type(type = "pg-uuid")
    private UUID id;

    private String login;

    @JsonIgnore
    @Basic(fetch = FetchType.LAZY)
    private String password;

    private String title;

    private String firstName;

    private String middleName;

    private String lastName;
    
    private String email;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name="user_groups", joinColumns=@JoinColumn(name="userid"))
    @Column(name="groupid")
    @JsonIgnore
    private List<UUID> groups;
    
    private String appConfigFile;
    
    private String navigationConfigFile;

    public User() {
    }

    public User(UUID id, String title) {
        this.id = id;
        this.title = title;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<UUID> getGroups() {
        return groups;
    }

    public void setGroups(List<UUID> groups) {
        this.groups = groups;
    }

    public String getAppConfigFile() {
        return appConfigFile;
    }

    public void setAppConfigFile(String appConfigFile) {
        this.appConfigFile = appConfigFile;
    }

    public String getNavigationConfigFile() {
        return navigationConfigFile;
    }

    public void setNavigationConfigFile(String navigationConfigFile) {
        this.navigationConfigFile = navigationConfigFile;
    }
}
