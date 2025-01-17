/*
 * Copyright 2016 Anatolii Rakovskii (rtolik@yandex.ru)
 *
 * Software distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */
package com.kattysoft.core;

import com.kattysoft.core.model.Contragent;
import com.kattysoft.core.model.Page;
import com.kattysoft.core.specification.Specification;

import java.util.List;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 19.12.2016
 */
public interface ContragentService {
    List<Contragent> getContragentsByTitle(String title);

    String getContragentTitleById(String contragentId);

    Page<Contragent> getContragents(Specification spec);

    Contragent getContragentById(String id);

    String saveContragent(Contragent contragent);

    void deleteContragent(String id);
}
