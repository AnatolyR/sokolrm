/*
 * Copyright 2017 Anatolii Rakovskii (rtolik@yandex.ru)
 *
 * No part of this file can be copied or reproduced without written permission of author.
 *
 * Software distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */
package com.kattysoft;

import java.io.*;
import java.util.stream.Collectors;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 24.04.2017
 */
public class AddMetaCharsetToHtml {
    public static void main(String[] args) throws IOException {
        FileInputStream inputStream = new FileInputStream(args[0]);
        String result = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));
        inputStream.close();
        result = result.replace("<head>", "<head><meta charset=\"UTF-8\">");

        FileOutputStream outputStream = new FileOutputStream(args[0]);
        outputStream.write(result.getBytes());
        outputStream.close();
    }
}
