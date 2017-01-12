/*
 * Copyright 2016 Anatolii Rakovskii (rtolik@yandex.ru)
 *
 * No part of this file can be copied or reproduced without written permission of author.
 *
 * Software distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */
package com.kattysoft.core;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 12.01.2017
 */
public class SokolException extends RuntimeException {
    public SokolException(String message) {
        super(message);
    }

    public SokolException(String message, Throwable cause) {
        super(message, cause);
    }
}
