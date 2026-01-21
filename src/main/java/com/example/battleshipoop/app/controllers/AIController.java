package com.example.battleshipoop.app.controllers;

import com.example.battleshipoop.app.utils.FXDesignHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class AIController extends BorderPane {

    public AIController() {
        initializeUI();
    }

    private void initializeUI() {
        setBackground(FXDesignHelper.createOceanBackground());

        VBox topPanel = new VBox(10);
        topPanel.setAlignment(Pos.CENTER);
        topPanel.setPadding(new Insets(20));

        Label titleLabel = FXDesignHelper.createTitleLabel("ОДИНОЧНАЯ ИГРА");
        Label subtitleLabel = FXDesignHelper.createSubtitleLabel("Против искусственного интеллекта");

        topPanel.getChildren().addAll(titleLabel, subtitleLabel);
        setTop(topPanel);

        HBox centerPanel = new HBox(40);
        centerPanel.setAlignment(Pos.CENTER);
        centerPanel.setPadding(new Insets(20));

        VBox playerField = createGameField("ВАШЕ ПОЛЕ", true);
        VBox aiField = createGameField("ПОЛЕ ИИ", false);

        centerPanel.getChildren().addAll(playerField, aiField);
        setCenter(centerPanel);

        HBox bottomPanel = new HBox(20);
        bottomPanel.setAlignment(Pos.CENTER);
        bottomPanel.setPadding(new Insets(20));

        Button autoPlaceBtn = FXDesignHelper.createActionButton("⚡  Авторасстановка", Color.rgb(255, 193, 7));
        Button startBtn = FXDesignHelper.createActionButton("▶  Начать игру", Color.rgb(40, 167, 69));
        Button backBtn = FXDesignHelper.createActionButton("◀  Назад", Color.rgb(108, 117, 125));

        bottomPanel.getChildren().addAll(autoPlaceBtn, startBtn, backBtn);
        setBottom(bottomPanel);
    }

    private VBox createGameField(String title, boolean isPlayer) {
        VBox fieldContainer = new VBox(15);
        fieldContainer.setAlignment(Pos.CENTER);
        fieldContainer.setPadding(new Insets(20));

        Region depthPanel = FXDesignHelper.createDepthPanel();
        StackPane container = new StackPane();
        container.getChildren().addAll(depthPanel, fieldContainer);

        Label titleLabel = new Label(title);
        titleLabel.setFont(javafx.scene.text.Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 18));
        titleLabel.setTextFill(isPlayer ? FXDesignHelper.LIGHT_BLUE : Color.rgb(255, 107, 107));

        GridPane grid = new GridPane();
        grid.setHgap(2);
        grid.setVgap(2);
        grid.setAlignment(Pos.CENTER);

        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                Region cell = FXDesignHelper.createGameCell(isPlayer);
                grid.add(cell, col, row);
            }
        }

        fieldContainer.getChildren().addAll(titleLabel, grid);
        return fieldContainer;
    }
}