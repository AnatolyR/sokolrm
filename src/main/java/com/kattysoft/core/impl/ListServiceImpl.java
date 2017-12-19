package com.kattysoft.core.impl;

import com.kattysoft.core.AccessRightLevel;
import com.kattysoft.core.AccessRightService;
import com.kattysoft.core.ListService;
import com.kattysoft.core.NoAccessRightsException;
import com.kattysoft.core.model.Page;
import com.kattysoft.core.specification.SortOrder;
import com.kattysoft.core.specification.Specification;
import com.kattysoft.core.specification.SpecificationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 16.12.2017
 */
public class ListServiceImpl implements ListService {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private AccessRightService accessRightService;
    
    @Override
    public <R extends JpaSpecificationExecutor<T>, T> Page<T> getEntities(Class<R> repositoryClass, Class<T> entityClass, String acSpace, String acType, Specification specification) {
        if (!accessRightService.checkRights(acSpace, acType, null, AccessRightLevel.LIST)) {
            throw new NoAccessRightsException("No access rights for list entity");
        }
        int offset = specification.getOffset();
        int size = specification.getSize();
        int pageNum = offset / size;

        Sort sort;
        if (specification.getSort() != null && specification.getSort().size() > 0) {
            sort = new Sort(specification.getSort().get(0).getOrder() == SortOrder.ASC
                ? Sort.Direction.ASC : Sort.Direction.DESC, specification.getSort().get(0).getField());
        } else {
            sort = new Sort("title");
        }

        PageRequest pageRequest = new PageRequest(pageNum, size, sort);

        org.springframework.data.jpa.domain.Specification<T> spec = null;
        if (specification.getCondition() != null) {
            spec = SpecificationUtil.conditionToSpringSpecification(specification.getCondition(), entityClass);
        }

        R repository = applicationContext.getBean(repositoryClass, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);

        org.springframework.data.domain.Page<T> repoPage = repository.findAll(spec, pageRequest);

        Page<T> page = new Page<>(repoPage.getTotalElements(), repoPage.getContent());
        return page;
    }

    public void setAccessRightService(AccessRightService accessRightService) {
        this.accessRightService = accessRightService;
    }
}
