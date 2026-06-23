package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    private static ConfigReader instance;
    private Properties properties;

    private ConfigReader() {

        properties = new Properties();

        try {
            FileInputStream fis =
                    new FileInputStream(
                            "src/main/resources/config.properties");

            properties.load(fis);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized ConfigReader getInstance() {

        if(instance == null) {
            instance = new ConfigReader();
        }

        return instance;
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

}
