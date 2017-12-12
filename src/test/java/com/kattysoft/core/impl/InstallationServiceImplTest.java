package com.kattysoft.core.impl;

import com.kattysoft.core.dao.DBUtilDao;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.InputStream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 01.12.2017
 */
public class InstallationServiceImplTest {
    
    @Mock
    DBUtilDao utilDao;
    
    @InjectMocks
    private InstallationServiceImpl installationService;
    
    @BeforeClass
    public void setup() {
        installationService = new InstallationServiceImpl();
        MockitoAnnotations.initMocks(this);
        doAnswer((invocation) -> {
            Object[] args = invocation.getArguments();
            DBUtilDao.Progress progress = (DBUtilDao.Progress) args[2];
            progress.tell(10, 20);
            progress.tell(15, 20);
            progress.tell(20, 20);
            return null;
        }).when(utilDao).importSQL(any(String.class), any(InputStream.class), any(DBUtilDao.Progress.class));
        when(utilDao.isSchemaEmpty()).thenReturn(true);
    }

    @Test
    public void testInstall() throws Exception {
        Thread thread = installationService.install(null, null, new String[]{"data"});
        thread.join(3000);
        if (thread.isAlive()) {
            throw new RuntimeException("Thread not completed");
        }
        assertThat(installationService.getStatus().getProgress(), equalTo(100));
    }
}