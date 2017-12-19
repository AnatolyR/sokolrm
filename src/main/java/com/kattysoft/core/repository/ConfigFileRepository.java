package com.kattysoft.core.repository;

import com.kattysoft.core.model.ConfigFile;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 16.12.2017
 */
public interface ConfigFileRepository extends CrudRepository<ConfigFile, UUID>, JpaSpecificationExecutor<ConfigFile> {
}
