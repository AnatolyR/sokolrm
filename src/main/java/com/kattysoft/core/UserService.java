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
package com.kattysoft.core;

import com.kattysoft.core.model.Page;
import com.kattysoft.core.model.User;
import com.kattysoft.core.specification.Specification;

import java.util.List;

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

    String savePassword(String id, String password);
}
