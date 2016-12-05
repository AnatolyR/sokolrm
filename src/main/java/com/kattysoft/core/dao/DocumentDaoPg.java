/*
 * Copyright 2016 Anatolii Rakovskii (rtolik@yandex.ru)
 *
 * No part of this file can be copied or reproduced without written permission of author.
 *
 * Software distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */
package com.kattysoft.core.dao;

import com.kattysoft.core.model.Document;
import com.kattysoft.core.specification.Specification;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 17.07.2016
 */
public class DocumentDaoPg implements DocumentDao {
    @Autowired
    private DataSource dataSource;

    public Document getDocument(String documentId, List<String> fieldsNames) {
        return null;
    }

    public List<Document> getDocumentsList(Specification specification) {
        try {
            List<Document> documents = new ArrayList<Document>();
            Connection connection = dataSource.getConnection();
            ResultSet resultSet = connection.createStatement().executeQuery("select * from documents;");
            while (resultSet.next()) {
                Document document = new Document();
                String id = resultSet.getString("id");
                document.setId(id);
                String title = resultSet.getString("title");
                document.setTitle(title);
                documents.add(document);
            }
            return documents;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Document saveDocument(Document document) {
        return null;
    }

    public Document deleteDocument(String documentId) {
        return null;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
