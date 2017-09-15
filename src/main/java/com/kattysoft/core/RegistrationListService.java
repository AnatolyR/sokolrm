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
import com.kattysoft.core.model.RegistrationList;
import com.kattysoft.core.specification.Specification;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 29.03.2017
 */
public interface RegistrationListService {
    Page<RegistrationList> getLists(Specification specification);

    RegistrationList getListById(String id);

    String saveList(RegistrationList list);

    void deleteList(String id);

    String produceNextNumber(String space);
}
