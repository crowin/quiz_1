package ru.home.qa.app;

import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author astolnikov: 18.02.2020
 */
public class ConfigProperties {
    public Properties apiProps;

    private static ConfigProperties instance = null;

    public static ConfigProperties getInstance() {
        if (instance == null) {
            synchronized (ConfigProperties.class) {
                if (instance == null) {
                    instance = new ConfigProperties();
                }
            }
        }
        return instance;
    }

    public Properties getTestProperties() {
        return apiProps;
    }

    public String getProperty(String prop) {
        return apiProps.getProperty(prop);
    }

    // Use only for singleton
    private  ConfigProperties() {
        apiProps = readFromFile("config.properties");
    }

    private Properties readFromFile(String fileName) {
        InputStream reader;
        Properties properties = new Properties();
        try {
            reader = getClass().getClassLoader().getResourceAsStream(fileName);
            properties = new Properties();
            properties.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return SerializationUtils.clone(properties);
    }

}
