import java.io.*;

/**
 * Author: Anatolii Rakovskii (rtolik@yandex.ru)
 * Date: 13.12.2017
 */
public class UnpackCsvToConfigFiles {
    public static void main(String[] args) throws IOException {
        File folder = new File("config");
        File allConfigs = new File("config/allConfigs.csv");

        BufferedReader reader = new BufferedReader(new FileReader(allConfigs));
        
        String line = null;
        while ((line = reader.readLine()) != null) {
            String[] split = line.split("\t");
            String name = split[0];
            String content = split[1];
            
            File file = new File(folder, name);
            File parent = file.getParentFile();
            if (!parent.exists() && !parent.mkdirs()) {
                throw new IllegalStateException("Couldn't create dir: " + parent);
            }
            byte[] bytes = Hex.decodeHex(content.toCharArray());
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(bytes);
            outputStream.close();
        }
        
        reader.close();
    }
}
