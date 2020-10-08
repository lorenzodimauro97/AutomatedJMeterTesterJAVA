package com.scippo97enterpriseproductions;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    InputStream inputStream;

    public Config() {
    }

    public String Parse(String value) throws IOException {
        String result = null;

        try {
            Properties prop = new Properties();
            String propFileName = "config.properties";
            this.inputStream = this.getClass().getClassLoader().getResourceAsStream(propFileName);
            if (this.inputStream == null) {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }

            prop.load(this.inputStream);
            result = prop.getProperty(value);
        } catch (Exception var8) {
            System.out.println("Exception: " + var8);
        } finally {
            assert this.inputStream != null;
            this.inputStream.close();
        }

        return result;
    }
}
