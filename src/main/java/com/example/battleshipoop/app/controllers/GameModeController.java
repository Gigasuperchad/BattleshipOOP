package com.example.battleshipoop.app.controllers;

import com.example.battleshipoop.app.HelloApplication;
import com.example.battleshipoop.ViewObject;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class GameModeController extends BorderPane {

    public GameModeController() {
        initializeUI();
    }

    private void initializeUI() {
        setBackground(new Background(new BackgroundFill(Color.DARKSLATEGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

        VBox centerBox = new VBox(20);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(40));

        Label titleLabel = new Label("Выберите режим игры");
        titleLabel.setFont(Font.font("Arial", Font.font("Arial").getSize() * 1.5));
        titleLabel.setTextFill(Color.WHITE);

        Button singlePlayerButton = new Button("Одиночная игра");
        singlePlayerButton.setPrefSize(200, 50);
        singlePlayerButton.setOnAction(e -> startSinglePlayer());

        Button multiplayerButton = new Button("Сетевая игра");
        multiplayerButton.setPrefSize(200, 50);
        multiplayerButton.setOnAction(e -> startMultiplayer());

        Button backButton = new Button("Назад");
        backButton.setPrefSize(200, 50);
        backButton.setOnAction(e -> goBack());

        centerBox.getChildren().addAll(titleLabel, singlePlayerButton, multiplayerButton, backButton);
        setCenter(centerBox);
    }

    private void startSinglePlayer() {
        // Создаем ViewObject для AIController
        ViewObject aiView = new ViewObject(
                "AIController",
                "Игра против ИИ",
                false,
                o -> true,
                null
        );

        // Устанавливаем действие при навигации
        aiView.setOnNavigate(() -> {
            HelloApplication app = HelloApplication.getInstance();
            if (app != null && app.getNavigator() != null) {
                app.getNavigator().navigate(aiView);
            }
        });

        // Выполняем навигацию
        aiView.navigate();
    }

    private void startMultiplayer() {
        // Создаем ViewObject для GameController
        ViewObject gameView = new ViewObject(
                "GameController",
                "Сетевая игра",
                false,
                o -> true,
                null
        );

        // Устанавливаем действие при навигации
        gameView.setOnNavigate(() -> {
            HelloApplication app = HelloApplication.getInstance();
            if (app != null && app.getNavigator() != null) {
                app.getNavigator().navigate(gameView);
            }
        });

        // Выполняем навигацию
        gameView.navigate();
    }

    private void goBack() {
        HelloApplication app = HelloApplication.getInstance();
        if (app != null && app.getNavigator() != null) {
            app.getNavigator().navigateBack();
        }
    }
}