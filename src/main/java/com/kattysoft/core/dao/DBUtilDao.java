package com.kattysoft.core.dao;

import java.io.InputStream;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 04.10.2017
 */
public interface DBUtilDao {
    boolean isTableExist(String tableName);
    
    void importSQL(String name, InputStream in, Progress progress);

    boolean isSchemaEmpty();

    interface Progress {
        void tell(int i, int n);
    }
}
