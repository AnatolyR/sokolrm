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

import com.kattysoft.core.DocumentTypeService;
import com.kattysoft.core.model.DocumentType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 31.01.2017
 */
public class DocumentTypeServiceImplTest {
    private DocumentTypeService documentTypeService;

    @BeforeClass
    public void setup() {
        ConfigServiceImpl configService = new ConfigServiceImpl();
        configService.setConfigPath("config/");

        documentTypeService = new DocumentTypeServiceImpl();
        ((DocumentTypeServiceImpl) documentTypeService).setConfigService(configService);
    }

    @Test
    public void testGetDocumentTypes() throws Exception {
        List<DocumentType> documentTypes = documentTypeService.getDocumentTypes();
        System.out.println(documentTypes);
        assertThat(documentTypes.size(), equalTo(3));
    }
}