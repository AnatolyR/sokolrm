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
package com.kattysoft.core.impl;

import com.kattysoft.core.SpaceService;
import com.kattysoft.core.model.Space;
import com.kattysoft.core.repository.SpaceRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 30.01.2017
 */
public class SpaceServiceImpl implements SpaceService {
    @Autowired
    private SpaceRepository spaceRepository;

    @Override
    public List<Space> getSpaces() {
        List<Space> result = new ArrayList<>();
        spaceRepository.findAll().forEach(result::add);
        return result;
    }

    @Override
    public boolean isValueExist(String title) {
        return spaceRepository.findByTitle(title).size() > 0;
    }

    @Override
    public void deleteSpaces(List<String> ids) {
        List<Space> spaces = ids.stream().map(id -> new Space(UUID.fromString(id))).collect(Collectors.toList());
        spaceRepository.delete(spaces);
    }

    @Override
    public String addSpace(Space space) {
        UUID id = UUID.randomUUID();
        space.setId(id);

        spaceRepository.save(space);
        return id.toString();
    }

    @Override
    public Space getSpace(String id) {
        return spaceRepository.findOne(UUID.fromString(id));
    }

    @Override
    public List<Space> getSpacesByRegistrationListId(UUID registrationListId) {
        return spaceRepository.findAllByRegistrationListId(registrationListId);
    }

    @Override
    public void saveSpace(Space space) {
        spaceRepository.save(space);
    }

    public void setSpaceRepository(SpaceRepository spaceRepository) {
        this.spaceRepository = spaceRepository;
    }
}
