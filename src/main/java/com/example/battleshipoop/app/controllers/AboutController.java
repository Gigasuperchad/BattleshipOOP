package com.example.battleshipoop.app.controllers;

import com.example.battleshipoop.app.AppInfo;
import com.example.battleshipoop.app.HelloApplication;
import com.example.battleshipoop.app.utils.FXDesignHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class AboutController extends BorderPane {
    public AboutController() {
        initializeUI();
    }

    private void initializeUI() {
        setBackground(FXDesignHelper.createOceanBackground());

        VBox mainContainer = new VBox(30);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(40));

        Label titleLabel = FXDesignHelper.createTitleLabel("О ПРОГРАММЕ");

        VBox infoPanel = new VBox(20);
        infoPanel.setAlignment(Pos.CENTER);
        infoPanel.setPadding(new Insets(30));
        infoPanel.setMaxWidth(600);

        Region depthPanel = FXDesignHelper.createDepthPanel();
        StackPane panelContainer = new StackPane();
        panelContainer.getChildren().addAll(depthPanel, infoPanel);

        Label appNameLabel = new Label(AppInfo.APP_NAME);
        appNameLabel.setFont(javafx.scene.text.Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 28));
        appNameLabel.setTextFill(FXDesignHelper.GOLD);

        Label versionLabel = new Label("Версия: " + AppInfo.VERSION);
        versionLabel.setFont(javafx.scene.text.Font.font("Segoe UI", 16));
        versionLabel.setTextFill(FXDesignHelper.LIGHT_BLUE);

        Text description = new Text(
                "Классическая игра 'Морской бой' с современным интерфейсом.\n\n" +
                        "Особенности:\n" +
                        "• Игра против компьютера с ИИ\n" +
                        "• Сетевая игра по локальной сети\n" +
                        "• Несколько тем оформления\n" +
                        "• Автоматическая расстановка кораблей\n" +
                        "• Встроенный игровой чат\n" +
                        "• Подробная статистика игр\n\n" +
                        "Цель игры: первым потопить все корабли противника."
        );
        description.setFont(javafx.scene.text.Font.font("Segoe UI", 14));
        description.setFill(Color.WHITE);
        description.setTextAlignment(TextAlignment.CENTER);
        description.setWrappingWidth(550);

        Region separator = new Region();
        separator.setPrefHeight(2);
        separator.setBackground(new Background(new BackgroundFill(
                FXDesignHelper.WAVE_BLUE,
                CornerRadii.EMPTY,
                null
        )));
        separator.setMaxWidth(400);

        VBox devBox = new VBox(10);
        devBox.setAlignment(Pos.CENTER);

        Label devTitle = new Label("Разработка:");
        devTitle.setFont(javafx.scene.text.Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 16));
        devTitle.setTextFill(FXDesignHelper.LIGHT_BLUE);

        Label devInfo = new Label("BattleShip\nJavaFX проект 2026");
        devInfo.setFont(javafx.scene.text.Font.font("Segoe UI", 12));
        devInfo.setTextFill(Color.LIGHTGRAY);
        devInfo.setTextAlignment(TextAlignment.CENTER);

        devBox.getChildren().addAll(devTitle, devInfo);

        Button backButton = FXDesignHelper.createActionButton("◀  Назад", Color.rgb(108, 117, 125));
        backButton.setPrefSize(180, 45);
        backButton.setOnAction(e -> goBack());

        infoPanel.getChildren().addAll(
                appNameLabel,
                versionLabel,
                description,
                separator,
                devBox
        );

        mainContainer.getChildren().addAll(titleLabel, panelContainer, backButton);
        setCenter(mainContainer);
    }

    private void goBack() {
        HelloApplication app = HelloApplication.getInstance();
        if (app != null && app.getNavigator() != null) {
            app.getNavigator().navigateBack();
        }
    }
}