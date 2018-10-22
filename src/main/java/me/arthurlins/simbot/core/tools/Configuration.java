package me.arthurlins.simbot.core.tools;

import java.io.*;
import java.util.Properties;

public class Configuration {

    private static Configuration ourInstance = new Configuration();

    public static Configuration getInstance() {
        return ourInstance;
    }

    private final String cfgFileName = "config.properties";

    private Properties properties;

    private Configuration() {
        this.properties = new Properties();
        if (!new File(cfgFileName).exists()){
            makeConfigFile();
        }
        try {
            properties.load(new FileInputStream(cfgFileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String get(String key){
        return properties.getProperty(key);
    }
    public String get(String key, String def){
        return properties.getProperty(key, def);
    }

    private void makeConfigFile() {
        properties.setProperty("jdbcUrl", "jdbc:mysql://localhost:3306/simbot");
        properties.setProperty("username", "root");
        properties.setProperty("password", "");
        properties.setProperty("contextSize", "3");
        properties.setProperty("differentFactor", "50");


        properties.setProperty("webservice-port", "8080");

        properties.setProperty("webservice-thread-max", "10");
        properties.setProperty("webservice-thread-min", "5");
        properties.setProperty("webservice-idle-timeout", "3000");

        properties.setProperty("webservice-ssl", "false");
        properties.setProperty("webservice-keystore-file-path", "");
        properties.setProperty("webservice-keystore-password", "");
        properties.setProperty("webservice-truststore-file-path", "");
        properties.setProperty("webservice-truststore-password", "");


        try {
            properties.store(new FileOutputStream(cfgFileName), null);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
