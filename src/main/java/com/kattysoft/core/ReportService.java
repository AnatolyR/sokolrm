/*
 * Copyright 2017 Anatolii Rakovskii (rtolik@yandex.ru)
 *
 * Software distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */
package com.kattysoft.core;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Date;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 17.04.2017
 */
public interface ReportService {
    void startReportGeneration(String id, ObjectNode data);

    ReportStatus getReportStatus(String id);

    class ReportStatus {
        private Date started;
        private Date ended;
        private String status;

        public Date getStarted() {
            return started;
        }

        public void setStarted(Date started) {
            this.started = started;
        }

        public Date getEnded() {
            return ended;
        }

        public void setEnded(Date ended) {
            this.ended = ended;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
