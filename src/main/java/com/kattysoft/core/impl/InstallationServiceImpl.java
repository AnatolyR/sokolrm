package com.kattysoft.core.impl;

import com.kattysoft.core.InstallationService;
import com.kattysoft.core.SokolException;
import com.kattysoft.core.dao.DBUtilDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 01.10.2017
 */
public class InstallationServiceImpl implements InstallationService {
    private static final Logger log = LoggerFactory.getLogger(InstallationServiceImpl.class);
    
    private static AtomicInteger progressFixed = new AtomicInteger(0);
    private static volatile AtomicInteger progress = new AtomicInteger(0);
    
    @Autowired
    private DBUtilDao dbUtilDao;
    
    @Override
    public boolean checkAppIsInstalled() {
        return dbUtilDao.isTableExist("configs");
    }

    @Override
    public Thread install(String login, String pass, String[] samples) {
        if (!dbUtilDao.isSchemaEmpty()) {
            throw new SokolException("Target schema not empty");
        }
        Thread thread = new Thread(() -> {
            HashSet<String> modes = new HashSet<>(Arrays.asList(samples));
            if (modes.contains("data")) {
                execute("data.sql", 0.33);
                execute("files.sql", 0.33);
                execute("configs.sql", 0.34);
            } else if (modes.contains("config")) {
                execute("schema.sql", 0.25);
                execute("schema_files.sql", 0.25);
                execute("configs.sql", 0.25);
                execute("admin.sql", 0.25);
            } else {
                execute("schema.sql", 0.25);
                execute("schema_files.sql", 0.25);
                execute("schema_configs.sql", 0.25);
                execute("admin.sql", 0.25);
            }
        }); 
        thread.start();
        return thread;
    }

    private void execute(String name, double partInProgress) {
        log.info("Execute '{}'", name);
        InputStream stream = InstallationServiceImpl.class.getResourceAsStream("/db/initial/" + name);
        dbUtilDao.importSQL(name, stream, (i, n) -> {
            progress.set((int) (progressFixed.get() + (i / n) * partInProgress * 100));
        });
        progressFixed.addAndGet((int) (partInProgress * 100));
    }

    @Override
    public Status getStatus() {
        Status status = new Status();
        status.setProgress(progress.get());
        return status;
    }

    public void setDbUtilDao(DBUtilDao dbUtilDao) {
        this.dbUtilDao = dbUtilDao;
    }
}
