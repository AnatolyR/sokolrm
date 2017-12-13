import java.io.*;
import java.util.UUID;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 13.12.2017
 */
public class GenerateSqlFromConfigFolder {
    private Writer writer;

    public GenerateSqlFromConfigFolder(File outputFile) {
        try {
            writer = new FileWriter(outputFile);
            writer.write("SET search_path TO sokol;\n");
            writer.write("DELETE FROM configs;\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        File configFolder = new File("config");
        
        GenerateSqlFromConfigFolder generate = new GenerateSqlFromConfigFolder(new File("db/importConfigs.sql"));

        generate.processFolder(configFolder, "");
        
        generate.close();
    }
    
    private void processFolder(File folder, String name) {
        File[] files = folder.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                processFolder(file, name + "/" + file.getName());
            } else if (file.getName().endsWith(".json")) {
                processFile(file, name + "/" + file.getName());
            }
        }
    }
    
    private void processFile(File file, String fullName) {
        try {
            writer.append("\\set content `cat config" + fullName + "`\n");
            String id = UUID.randomUUID().toString();
            writer.append("INSERT INTO configs(id, path, content) VALUES('" + id + "', '" + fullName + "', :'content');\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private void close() {
        try {
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
