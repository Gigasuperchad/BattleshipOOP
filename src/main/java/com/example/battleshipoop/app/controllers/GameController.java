package com.example.battleshipoop.app.controllers;

import com.example.battleshipoop.app.HelloApplication;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

public class GameController extends BorderPane {
    public GameController() {
        initializeUI();
    }

    private void initializeUI() {
        setBackground(new Background(new BackgroundFill(Color.DARKSLATEGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        HBox topPanel = createTopPanel();
        setTop(topPanel);
        HBox gameArea = createGameArea();
        setCenter(gameArea);
        HBox bottomPanel = createBottomPanel();
        setBottom(bottomPanel);
    }

    private HBox createTopPanel() {
        HBox topPanel = new HBox(20);
        topPanel.setAlignment(Pos.CENTER);
        topPanel.setPadding(new Insets(15));
        topPanel.setStyle("-fx-background-color: #2C3E50;");

        Label playerLabel = new Label("Игрок 1");
        playerLabel.setFont(Font.font("Arial", 20));
        playerLabel.setTextFill(Color.WHITE);

        Label statusLabel = new Label("Ваш ход");
        statusLabel.setFont(Font.font("Arial", 16));
        statusLabel.setTextFill(Color.LIGHTGREEN);

        topPanel.getChildren().addAll(playerLabel, statusLabel);
        return topPanel;
    }

    private HBox createGameArea() {
        HBox gameArea = new HBox(50);
        gameArea.setAlignment(Pos.CENTER);
        gameArea.setPadding(new Insets(20));

        VBox playerField = createGameField("Ваше поле", true);
        VBox enemyField = createGameField("Поле противника", false);
        gameArea.getChildren().addAll(playerField, enemyField);
        return gameArea;
    }

    private VBox createGameField(String title, boolean isPlayer) {
        VBox fieldBox = new VBox(10);
        fieldBox.setAlignment(Pos.CENTER);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", 18));
        titleLabel.setTextFill(Color.WHITE);

        GridPane grid = new GridPane();
        grid.setHgap(2);
        grid.setVgap(2);

        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                Rectangle cell = new Rectangle(35, 35);
                cell.setFill(Color.LIGHTBLUE);
                cell.setStroke(Color.DARKBLUE);

                if (isPlayer) {
                    cell.setOnMouseClicked(e -> cell.setFill(Color.GREEN));
                } else {
                    cell.setOnMouseClicked(e -> cell.setFill(Color.RED));
                }

                grid.add(cell, col, row);
            }
        }

        fieldBox.getChildren().addAll(titleLabel, grid);
        return fieldBox;
    }

    private HBox createBottomPanel() {
        HBox bottomPanel = new HBox(15);
        bottomPanel.setAlignment(Pos.CENTER);
        bottomPanel.setPadding(new Insets(15));

        Button backButton = new Button("В меню");
        backButton.setOnAction(e -> goBack());

        Button restartButton = new Button("Новая игра");
        restartButton.setOnAction(e -> restartGame());

        bottomPanel.getChildren().addAll(backButton, restartButton);
        return bottomPanel;
    }

    private void goBack() {
        HelloApplication app = HelloApplication.getInstance();
        if (app != null && app.getNavigator() != null) {
            app.getNavigator().navigateBack();
        }
    }

    private void restartGame() {
        System.out.println("Новая игра начата");
    }
}