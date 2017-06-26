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

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 26.06.2017
 */
public class Gen1 {
    public static void main(String[] args) throws IOException {
        File file = new File("/Users/anatolii/Documents/sokolsed/config/navigation/main.json");
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        boolean use = false;
        
        File dir = new File("/Users/anatolii/Documents/sokolsed/tests/src/test/resources/lists");
        while ((line = reader.readLine()) != null) {
            if (line.contains("documentTemplates")) {
                use = true;
                continue;
            }
            if (use && line.contains("\"id\":")) {
                String id = line.substring(line.indexOf("\"id\":") + 7, line.indexOf("\","));
                
//                File f = new File(dir, id + ".txt");
//                f.createNewFile();
                
                String id1 = (id.charAt(0) + "").toUpperCase() + id.substring(1);
                System.out.println();
                
                System.out.println("    @Test\n" +
                    "    public void test" + id1 + "List() {\n" +
                    "        listDocuments(\"" + id + "\", 0);\n" +
                    "    }");
            }
        }
    }
}
