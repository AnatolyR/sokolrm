/*
 * Copyright 2016 Anatolii Rakovskii (rtolik@yandex.ru)
 *
 * Software distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */
package com.kattysoft.core;

import com.kattysoft.core.model.Page;
import com.kattysoft.core.model.User;
import com.kattysoft.core.specification.Specification;

import java.util.List;
import java.util.UUID;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 14.12.2016
 */
public interface UserService {
    List<User> getUsersByShortTitle(String title);

    String getUserTitleById(String userId);

    User getUserByLoginAndPassword(String login, String password);

    void setCurrentUser(User user);

    User getCurrentUser();

    Page<User> getUsers(Specification specification);

    User getUserById(String id);

    String saveUser(User user);

    void deleteUser(String id);

    String resetPassword(String id);
}
