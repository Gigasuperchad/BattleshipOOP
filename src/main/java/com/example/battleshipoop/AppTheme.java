package com.example.battleshipoop;

import com.example.battleshipoop.app.utils.FXDesignHelper;
import javafx.scene.Scene;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AppTheme {
    private static Scene currentScene;
    private static Map<String, String> themes = new HashMap<>();
    private static String currentTheme;

    static {
        themes.put("Темная", "dark");
        themes.put("Светлая", "light");
        themes.put("Синяя", "blue");
    }

    public static void setScene(Scene scene) {
        currentScene = scene;
    }

    public static void changeTheme(String themeName) {
        if (currentScene == null || !themes.containsKey(themeName)) {
            return;
        }

        String themeKey = themes.get(themeName);
        currentTheme = themeName;

        FXDesignHelper.updateTheme();

        applyThemeToScene(themeKey);
    }

    private static void applyThemeToScene(String themeKey) {
        if (currentScene == null) return;

        try {
            currentScene.getStylesheets().clear();

            String cssFile;
            switch (themeKey) {
                case "light":
                    cssFile = "/styles/light-theme.css";
                    break;
                case "blue":
                    cssFile = "/styles/blue-theme.css";
                    break;
                default:
                    cssFile = "/styles/dark-theme.css";
                    break;
            }

            URL themeUrl = AppTheme.class.getResource(cssFile);
            if (themeUrl != null) {
                currentScene.getStylesheets().add(themeUrl.toExternalForm());
            }

        } catch (Exception e) {
            System.err.println("Ошибка загрузки темы: " + e.getMessage());
        }
    }

    public static String getCurrentTheme() {
        return currentTheme;
    }

    public static Map<String, String> getAvailableThemes() {
        return new HashMap<>(themes);
    }
}