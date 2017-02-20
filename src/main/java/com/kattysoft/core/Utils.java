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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 26.01.2017
 */
public class Utils {
    private static final Logger log = LoggerFactory.getLogger(Utils.class);
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    private static SimpleDateFormat dateFormatShort = new SimpleDateFormat("dd.MM.yyyy");

    public static String[] getNullPropertyNames (Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for(java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    public static Date parseDate(String dateStr) {
        try {
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            log.error("Can not parse date '" + dateStr + "'", e);
        }
        return null;
    }

    public static String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        return dateFormat.format(date);
    }

    public static Date parseDateShort(String dateStr) {
        try {
            return dateFormatShort.parse(dateStr);
        } catch (ParseException e) {
            log.error("Can not parse date '" + dateStr + "'", e);
        }
        return null;
    }

    private static Pattern uuidPattern = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    public static boolean isUUID(String uuid) {
        return uuid != null && uuidPattern.matcher(uuid).matches();
    }
}
