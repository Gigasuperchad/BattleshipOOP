package com.example.battleshipoop;

import javafx.scene.Scene;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AppTheme {
    private static Scene currentScene;
    private static Map<String, String> themes = new HashMap<>();
    private static String currentTheme;

    static {
        // Инициализация доступных тем
        themes.put("Светлая", "/styles/light-theme.css");
        themes.put("Темная", "/styles/dark-theme.css");
        themes.put("Синяя", "/styles/blue-theme.css");
    }

    public static void setScene(Scene scene) {
        currentScene = scene;
    }

    public static void changeTheme(String themeName) {
        if (currentScene == null || !themes.containsKey(themeName)) {
            return;
        }

        String themePath = themes.get(themeName);
        URL themeUrl = AppTheme.class.getResource(themePath);

        if (themeUrl != null) {
            currentScene.getStylesheets().clear();
            currentScene.getStylesheets().add(themeUrl.toExternalForm());
            currentTheme = themeName;
        }
    }

    public static String getCurrentTheme() {
        return currentTheme;
    }

    public static Map<String, String> getAvailableThemes() {
        return new HashMap<>(themes);
    }
}