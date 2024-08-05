package ru.otus.basic.yampolskiy.webserver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static Properties properties = new Properties();

    static {
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                System.out.println("Файл application.properties не найден");
                // Обработка ошибки отсутствия файла настроек
            } else {
                properties.load(input);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            // Обработка ошибки загрузки файла настроек
        }
    }

    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public static int getIntProperty(String key, int defaultValue) {
        String value = getProperty(key, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
