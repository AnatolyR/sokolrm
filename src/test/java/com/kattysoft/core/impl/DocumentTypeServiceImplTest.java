package com.kattysoft.core.impl;

import com.kattysoft.core.DocumentTypeService;
import com.kattysoft.core.model.DocumentType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.testng.Assert.*;

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