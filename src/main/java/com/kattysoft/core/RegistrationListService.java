/*
 * Copyright 2017 Anatolii Rakovskii (rtolik@yandex.ru)
 *
 * Software distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */
package com.kattysoft.core;

import com.kattysoft.core.model.Group;
import com.kattysoft.core.model.Page;
import com.kattysoft.core.model.RegistrationList;
import com.kattysoft.core.specification.Specification;

import java.util.List;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 29.03.2017
 */
public interface RegistrationListService {
    Page<RegistrationList> getLists(Specification specification);

    RegistrationList getListById(String id);

    String saveList(RegistrationList list);
}
