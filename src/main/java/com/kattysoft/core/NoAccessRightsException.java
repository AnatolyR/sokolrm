/*
 * Copyright 2017 Anatolii Rakovskii (rtolik@yandex.ru)
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
 * Date: 06.02.2017
 */
public class NoAccessRightsException extends RuntimeException {
    public NoAccessRightsException(String message) {
        super(message);
    }
}
