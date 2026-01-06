package com.example.battleshipoop.app;

import java.util.Locale;
import java.util.ResourceBundle;

public class AppProperties {
    private static ResourceBundle resources;
    private static AppSettings settings;

    static {
        // Загрузка ресурсов
        try {
            resources = ResourceBundle.getBundle("bundles.strings", Locale.getDefault());
        } catch (Exception e) {
            System.err.println("Не удалось загрузить ресурсы: " + e.getMessage());
        }

        // Инициализация настроек
        settings = new AppSettings();
    }

    public static String getString(String key) {
        try {
            return resources.getString(key);
        } catch (Exception e) {
            return key;
        }
    }

    public static AppSettings getSettings() {
        return settings;
    }
}