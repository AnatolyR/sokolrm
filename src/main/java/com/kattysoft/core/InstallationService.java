package com.kattysoft.core;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 01.10.2017
 */
public interface InstallationService {
    boolean checkAppIsInstalled();

    Thread install(String login, String pass, String[] samples);

    Status getStatus();
    
    class Status {
        private Integer progress;

        public Integer getProgress() {
            return progress;
        }

        public void setProgress(Integer progress) {
            this.progress = progress;
        }
    }
}
