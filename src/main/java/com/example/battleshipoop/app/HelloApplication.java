package com.example.battleshipoop.app;

import com.example.battleshipoop.AppTheme;
import com.example.battleshipoop.ViewNavigator;
import com.example.battleshipoop.ViewObject;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    private ViewNavigator navigator;
    private Stage primaryStage;
    private static HelloApplication instance;

    @Override
    public void start(Stage primaryStage) {
        instance = this;
        this.primaryStage = primaryStage;

        StackPane root = new StackPane();
        navigator = new ViewNavigator(root);

        Scene scene = new Scene(root, AppInfo.WINDOW_WIDTH, AppInfo.WINDOW_HEIGHT);
        AppTheme.setScene(scene);

        try {
            primaryStage.getIcons().add(new Image(
                    HelloApplication.class.getResourceAsStream("/images/app-icon.png")
            ));
        } catch (Exception e) {
            System.err.println("Не удалось загрузить иконку приложения: " + e.getMessage());
        }

        primaryStage.setScene(scene);
        primaryStage.setTitle(AppInfo.APP_NAME);

        initializeViews();

        AppSettings settings = AppProperties.getSettings();
        AppTheme.changeTheme(settings.getTheme());

        primaryStage.show();
        navigateToStartScreen();
    }

    public static HelloApplication getInstance() {
        return instance;
    }

    private void initializeViews() {
        navigator.addView(new ViewObject(
                "HelloController",
                "Главное меню",
                false,
                o -> true,
                null
        ));

        // Одиночная игра
        ViewObject aiView = new ViewObject(
                "AIController",
                "Одиночная игра",
                false,
                o -> true,
                null
        );
        navigator.addView(aiView);

        ViewObject hostView = new ViewObject(
                "GameController",
                "Сетевая игра (Хост)",
                false,
                o -> true,
                null
        );
        navigator.addView(hostView);

        ViewObject clientView = new ViewObject(
                "GameController",
                "Сетевая игра (Клиент)",
                false,
                o -> true,
                null
        );
        navigator.addView(clientView);

        navigator.addView(new ViewObject(
                "SettingsController",
                "Настройки",
                true,
                o -> true,
                null
        ));

        navigator.addView(new ViewObject(
                "AboutController",
                "О программе",
                false,
                o -> true,
                null
        ));
    }

    private void navigateToStartScreen() {
        if (!navigator.getViews().isEmpty()) {
            navigator.navigate(navigator.getViews().get(0));
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public ViewNavigator getNavigator() {
        return navigator;
    }

    public static void main(String[] args) {
        System.setProperty("prism.lcdtext", "false");
        System.setProperty("prism.text", "t2k");
        launch(args);
    }
}