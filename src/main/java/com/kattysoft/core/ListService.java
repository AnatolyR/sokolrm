package com.kattysoft.core;

import com.kattysoft.core.model.Page;
import com.kattysoft.core.specification.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 16.12.2017
 */
public interface ListService {
    <R extends JpaSpecificationExecutor<T>,T> Page<T> getEntities(Class<R> repositoryClass, Class<T> entityClass, String acSpace, String acType, Specification specification);
}
