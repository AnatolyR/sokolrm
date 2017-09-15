/*
 * Copyright 2016-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
