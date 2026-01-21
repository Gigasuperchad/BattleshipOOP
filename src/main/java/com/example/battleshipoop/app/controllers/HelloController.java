package com.example.battleshipoop.app.controllers;

import com.example.battleshipoop.app.AppInfo;
import com.example.battleshipoop.app.HelloApplication;
import com.example.battleshipoop.app.utils.FXDesignHelper;
import com.example.battleshipoop.Network;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class HelloController extends BorderPane {
    private HelloApplication app;

    public HelloController() {
        initializeUI();
    }

    private void initializeUI() {
        setBackground(FXDesignHelper.createOceanBackground());
        this.app = HelloApplication.getInstance();

        VBox header = createHeader();
        setTop(header);

        VBox centerBox = createCenterBox();
        setCenter(centerBox);

        HBox footer = createFooter();
        setBottom(footer);
    }

    private VBox createHeader() {
        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(30, 20, 20, 20));
        header.setBackground(Background.EMPTY);

        Label titleLabel = FXDesignHelper.createTitleLabel(AppInfo.APP_NAME);

        Label subtitleLabel = FXDesignHelper.createSubtitleLabel("КЛАССИЧЕСКАЯ МОРСКАЯ БИТВА");

        Region line = new Region();
        line.setPrefHeight(3);
        line.setBackground(new Background(new BackgroundFill(
                FXDesignHelper.WAVE_BLUE,
                CornerRadii.EMPTY,
                null
        )));
        line.setMaxWidth(400);

        header.getChildren().addAll(titleLabel, subtitleLabel, line);
        return header;
    }

    private VBox createCenterBox() {
        VBox centerBox = new VBox(20);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(40));
        centerBox.setBackground(Background.EMPTY);

        VBox buttonPanel = new VBox(15);
        buttonPanel.setAlignment(Pos.CENTER);
        buttonPanel.setPadding(new Insets(30));
        buttonPanel.setMaxWidth(500);

        Region depthPanel = FXDesignHelper.createDepthPanel();
        StackPane panelContainer = new StackPane();
        panelContainer.getChildren().addAll(depthPanel, buttonPanel);

        Button singlePlayerBtn = FXDesignHelper.createNavButton("⚔  ОДИНОЧНАЯ ИГРА");
        singlePlayerBtn.setOnAction(e -> startSinglePlayerGame());

        Button hostGameBtn = FXDesignHelper.createNavButton("🌐  СОЗДАТЬ ИГРУ (ХОСТ)");
        hostGameBtn.setOnAction(e -> startMultiplayerAsHost());

        Button connectGameBtn = FXDesignHelper.createNavButton("🔗  ПОДКЛЮЧИТЬСЯ К ИГРЕ");
        connectGameBtn.setOnAction(e -> connectToExistingGame());

        buttonPanel.getChildren().addAll(singlePlayerBtn, hostGameBtn, connectGameBtn);

        try {
            String ip = Network.getLocalIPAddress();
            HBox ipBox = new HBox(10);
            ipBox.setAlignment(Pos.CENTER);
            ipBox.setPadding(new Insets(20, 0, 0, 0));

            Label ipIcon = new Label("🌐");
            ipIcon.setFont(Font.font(20));
            ipIcon.setTextFill(FXDesignHelper.LIGHT_BLUE);

            Label ipLabel = new Label("Ваш IP: " + ip);
            ipLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
            ipLabel.setTextFill(FXDesignHelper.GOLD);

            ipBox.getChildren().addAll(ipIcon, ipLabel);
            centerBox.getChildren().addAll(panelContainer, ipBox);
        } catch (Exception e) {
            centerBox.getChildren().add(panelContainer);
        }

        return centerBox;
    }

    private HBox createFooter() {
        HBox footer = new HBox(20);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(15));
        footer.setBackground(new Background(new BackgroundFill(
                Color.rgb(0, 0, 0, 0.3),
                CornerRadii.EMPTY,
                null
        )));

        Label versionLabel = new Label("Версия: " + AppInfo.VERSION);
        versionLabel.setFont(Font.font("Segoe UI", 12));
        versionLabel.setTextFill(Color.LIGHTGRAY);

        Button settingsButton = createFooterButton("⚙  Настройки");
        settingsButton.setOnAction(e -> navigateToSettings());

        Button aboutButton = createFooterButton("ℹ  О программе");
        aboutButton.setOnAction(e -> navigateToAbout());

        Button exitButton = createFooterButton("🚪  Выход");
        exitButton.setOnAction(e -> exitApplication());

        footer.getChildren().addAll(versionLabel, settingsButton, aboutButton, exitButton);
        return footer;
    }

    private Button createFooterButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Segoe UI", 12));
        button.setTextFill(Color.LIGHTGRAY);
        button.setBackground(Background.EMPTY);
        button.setBorder(Border.EMPTY);
        button.setPadding(new Insets(5, 10, 5, 10));

        button.setOnMouseEntered(e -> {
            button.setTextFill(Color.WHITE);
            button.setBackground(new Background(new BackgroundFill(
                    Color.rgb(255, 255, 255, 0.1),
                    new CornerRadii(5),
                    null
            )));
        });

        button.setOnMouseExited(e -> {
            button.setTextFill(Color.LIGHTGRAY);
            button.setBackground(Background.EMPTY);
        });

        return button;
    }

    private void startSinglePlayerGame() {
        AIController aiController = new AIController();
        Scene scene = new Scene(aiController, 1200, 800);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Морской бой - Одиночная игра");
        stage.show();
        app.getPrimaryStage().hide();
    }

    private void startMultiplayerAsHost() {
        GameController gameController = new GameController("host");
        Scene scene = new Scene(gameController, 1200, 800);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Морской бой - Создание игры");
        stage.show();
        app.getPrimaryStage().hide();
    }

    private void connectToExistingGame() {
        GameController gameController = new GameController("client");
        Scene scene = new Scene(gameController, 1200, 800);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Морской бой - Подключение к игре");
        stage.show();
        app.getPrimaryStage().hide();
    }

    private void navigateToSettings() {
        if (app != null && app.getNavigator() != null) {
            app.getNavigator().navigateToController("SettingsController");
        }
    }

    private void navigateToAbout() {
        if (app != null && app.getNavigator() != null) {
            app.getNavigator().navigateToController("AboutController");
        }
    }

    private void exitApplication() {
        if (app != null) {
            app.getPrimaryStage().close();
        }
    }
}