/*
 * Copyright 2016 Anatolii Rakovskii (rtolik@yandex.ru)
 *
 * Software distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */
package com.kattysoft.core;

import com.kattysoft.core.model.Attach;

import java.util.List;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 18.01.2017
 */
public interface AttachService {
    List<Attach> getAttachesForObject(String objectId);

    void deleteAttach(String id);
}
