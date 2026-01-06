package com.example.battleshipoop.app;

import java.io.*;
import java.util.Properties;

public class AppSettings {
    private Properties properties;
    private File configFile;

    public AppSettings() {
        properties = new Properties();
        configFile = new File("config.properties");
        loadSettings();
    }

    public void loadSettings() {
        if (configFile.exists()) {
            try (FileInputStream fis = new FileInputStream(configFile)) {
                properties.load(fis);
            } catch (IOException e) {
                System.err.println("Ошибка загрузки настроек: " + e.getMessage());
            }
        }
    }

    public void saveSettings() {
        try (FileOutputStream fos = new FileOutputStream(configFile)) {
            properties.store(fos, "BattleShip Application Settings");
        } catch (IOException e) {
            System.err.println("Ошибка сохранения настроек: " + e.getMessage());
        }
    }

    public String getTheme() {
        return properties.getProperty("theme", "dark");
    }

    public void setTheme(String theme) {
        properties.setProperty("theme", theme);
        saveSettings();
    }

    public String getLanguage() {
        return properties.getProperty("language", "ru");
    }

    public void setLanguage(String language) {
        properties.setProperty("language", language);
        saveSettings();
    }

    public boolean isSoundEnabled() {
        return Boolean.parseBoolean(properties.getProperty("sound_enabled", "true"));
    }

    public void setSoundEnabled(boolean enabled) {
        properties.setProperty("sound_enabled", String.valueOf(enabled));
        saveSettings();
    }
}