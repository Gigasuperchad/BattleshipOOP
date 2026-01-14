package com.example.battleshipoop.app.controllers;

import com.example.battleshipoop.app.AppInfo;
import com.example.battleshipoop.app.HelloApplication;
import com.example.battleshipoop.app.AppProperties;
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
        setBackground(new Background(new BackgroundFill(Color.DARKSLATEGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
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
        header.setPadding(new Insets(20));

        Label titleLabel = new Label(AppInfo.APP_NAME);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        titleLabel.setTextFill(Color.WHITE);

        Label subtitleLabel = new Label("Classic Naval Battle Game");
        subtitleLabel.setFont(Font.font("Arial", 18));
        subtitleLabel.setTextFill(Color.LIGHTGRAY);

        header.getChildren().addAll(titleLabel, subtitleLabel);
        return header;
    }

    private VBox createCenterBox() {
        VBox centerBox = new VBox(30);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(40));

        // Одиночная игра
        Button singlePlayerBtn = createMenuButton("Одиночная игра", Color.web("#2E8B57"));
        singlePlayerBtn.setOnAction(e -> startSinglePlayerGame());

        // Создание игры (Хост)
        Button hostGameBtn = createMenuButton("Создать сетевую игру", Color.web("#3498DB"));
        hostGameBtn.setOnAction(e -> startMultiplayerAsHost());

        // Подключение к игре
        Button connectGameBtn = createMenuButton("Подключиться к игре", Color.web("#9B59B6"));
        connectGameBtn.setOnAction(e -> connectToExistingGame());

        VBox buttonBox = new VBox(20, singlePlayerBtn, hostGameBtn, connectGameBtn);
        buttonBox.setAlignment(Pos.CENTER);

        // Информация для сетевой игры
        try {
            String ip = Network.getLocalIPAddress();
            Label ipLabel = new Label("Ваш IP-адрес для подключения: " + ip);
            ipLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12)); // Было: Font.font("Arial", 12, FontWeight.BOLD)
            ipLabel.setTextFill(Color.LIGHTGREEN);
            ipLabel.setPadding(new Insets(20, 0, 0, 0));

            centerBox.getChildren().addAll(buttonBox, ipLabel);
        } catch (Exception e) {
            centerBox.getChildren().add(buttonBox);
        }

        return centerBox;
    }

    private Button createMenuButton(String text, Color color) {
        Button button = new Button(text);
        button.setPrefSize(300, 50);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        button.setTextFill(Color.WHITE);
        button.setStyle(String.format(
                "-fx-background-color: %s; -fx-background-radius: 10; -fx-border-radius: 10;",
                color.toString().replace("0x", "#")
        ));

        button.setOnMouseEntered(e -> button.setStyle(
                String.format("-fx-background-color: %s; -fx-background-radius: 10; -fx-border-radius: 10; -fx-scale-x: 1.05; -fx-scale-y: 1.05;",
                        color.brighter().toString().replace("0x", "#"))
        ));

        button.setOnMouseExited(e -> button.setStyle(
                String.format("-fx-background-color: %s; -fx-background-radius: 10; -fx-border-radius: 10; -fx-scale-x: 1.0; -fx-scale-y: 1.0;",
                        color.toString().replace("0x", "#"))
        ));

        return button;
    }

    private HBox createFooter() {
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(15));
        footer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3);");

        Label versionLabel = new Label("Версия: " + AppInfo.VERSION);
        versionLabel.setFont(Font.font("Arial", 12));
        versionLabel.setTextFill(Color.LIGHTGRAY);

        Button settingsButton = new Button("Настройки");
        settingsButton.setStyle("-fx-background-color: transparent; -fx-text-fill: lightgray; -fx-border-color: transparent;");
        settingsButton.setOnAction(e -> navigateToSettings());

        Button aboutButton = new Button("О программе");
        aboutButton.setStyle("-fx-background-color: transparent; -fx-text-fill: lightgray; -fx-border-color: transparent;");
        aboutButton.setOnAction(e -> navigateToAbout());

        Button exitButton = new Button("Выход");
        exitButton.setStyle("-fx-background-color: transparent; -fx-text-fill: lightgray; -fx-border-color: transparent;");
        exitButton.setOnAction(e -> exitApplication());

        footer.getChildren().addAll(versionLabel, createSeparator(), settingsButton,
                createSeparator(), aboutButton, createSeparator(), exitButton);
        footer.setSpacing(10);

        return footer;
    }

    private Label createSeparator() {
        Label separator = new Label("|");
        separator.setTextFill(Color.LIGHTGRAY);
        return separator;
    }

    private void startSinglePlayerGame() {
        // Просто создаем и показываем AIController
        AIController aiController = new AIController();
        Scene scene = new Scene(aiController, 1200, 800);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Морской бой - Одиночная игра");
        stage.show();

        // Закрываем главное меню
        app.getPrimaryStage().hide();
    }

    private void startMultiplayerAsHost() {
        // Создаем GameController в режиме хоста
        GameController gameController = new GameController("host");
        Scene scene = new Scene(gameController, 1200, 800);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Морской бой - Создание игры");
        stage.show();

        // Закрываем главное меню
        app.getPrimaryStage().hide();
    }

    private void connectToExistingGame() {
        // Создаем GameController в режиме клиента
        GameController gameController = new GameController("client");
        Scene scene = new Scene(gameController, 1200, 800);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Морской бой - Подключение к игре");
        stage.show();

        // Закрываем главное меню
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