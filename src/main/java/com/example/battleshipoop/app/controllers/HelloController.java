package com.example.battleshipoop.app.controllers;

import com.example.battleshipoop.app.AppInfo;
import com.example.battleshipoop.app.HelloApplication;
import com.example.battleshipoop.app.AppProperties;
import com.example.battleshipoop.Network;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

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
        GridPane centerPane = createCenterPane();
        setCenter(centerPane);
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

    private GridPane createCenterPane() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setPadding(new Insets(40));

        Button startButton = createMenuButton("Начать игру");
        startButton.setOnAction(e -> navigateToGame());

        Button settingsButton = createMenuButton("Настройки");
        settingsButton.setOnAction(e -> navigateToSettings());

        Button aboutButton = createMenuButton("О программе");
        aboutButton.setOnAction(e -> navigateToAbout());

        Button exitButton = createMenuButton("Выход");
        exitButton.setOnAction(e -> exitApplication());

        grid.add(startButton, 0, 0);
        grid.add(settingsButton, 0, 1);
        grid.add(aboutButton, 0, 2);
        grid.add(exitButton, 0, 3);

        return grid;
    }

    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.setPrefSize(200, 50);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        button.setTextFill(Color.WHITE);
        button.setStyle("-fx-background-color: #2E8B57;");

        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #3CB371;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #2E8B57;"));

        return button;
    }

    private HBox createFooter() {
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(15));
        footer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3);");

        Label ipLabel = new Label();
        ipLabel.setFont(Font.font("Arial", 12));
        ipLabel.setTextFill(Color.LIGHTGRAY);

        try {
            String ip = Network.getLocalIPAddress();
            ipLabel.setText("IP: " + ip);
        } catch (Exception e) {
            ipLabel.setText("IP: Недоступен");
        }

        Label versionLabel = new Label("Версия: " + AppInfo.VERSION);
        versionLabel.setFont(Font.font("Arial", 12));
        versionLabel.setTextFill(Color.LIGHTGRAY);

        Label separator = new Label(" | ");
        separator.setTextFill(Color.LIGHTGRAY);

        footer.getChildren().addAll(ipLabel, separator, versionLabel);
        footer.setSpacing(10);

        return footer;
    }

    private void navigateToGame() {
        if (app != null && app.getNavigator() != null && app.getNavigator().getViews().size() > 1) {
            app.getNavigator().navigate(app.getNavigator().getViews().get(1));
        }
    }

    private void navigateToSettings() {
        if (app != null && app.getNavigator() != null && app.getNavigator().getViews().size() > 2) {
            app.getNavigator().navigate(app.getNavigator().getViews().get(2));
        }
    }

    private void navigateToAbout() {
        if (app != null && app.getNavigator() != null && app.getNavigator().getViews().size() > 3) {
            app.getNavigator().navigate(app.getNavigator().getViews().get(3));
        }
    }

    private void exitApplication() {
        if (app != null) {
            app.getPrimaryStage().close();
        }
    }
}