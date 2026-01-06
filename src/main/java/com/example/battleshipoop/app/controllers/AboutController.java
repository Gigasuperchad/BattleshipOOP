package com.example.battleshipoop.app.controllers;

import com.example.battleshipoop.app.AppInfo;
import com.example.battleshipoop.app.HelloApplication;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class AboutController extends BorderPane {
    public AboutController() {
        initializeUI();
    }

    private void initializeUI() {
        setBackground(new Background(new BackgroundFill(Color.DARKSLATEGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        VBox centerBox = new VBox(20);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(40));

        Label titleLabel = new Label("О программе");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.WHITE);

        Label nameLabel = new Label(AppInfo.APP_NAME);
        nameLabel.setFont(Font.font("Arial", 18));
        nameLabel.setTextFill(Color.LIGHTGRAY);

        Label versionLabel = new Label("Версия: " + AppInfo.VERSION);
        versionLabel.setFont(Font.font("Arial", 14));
        versionLabel.setTextFill(Color.LIGHTGRAY);


        Label descriptionLabel = new Label("Классическая игра в морской бой");
        descriptionLabel.setFont(Font.font("Arial", 14));
        descriptionLabel.setTextFill(Color.LIGHTGRAY);

        Button backButton = new Button("Назад");
        backButton.setOnAction(e -> goBack());

        centerBox.getChildren().addAll(titleLabel, nameLabel, versionLabel, descriptionLabel, backButton);
        setCenter(centerBox);
    }

    private void goBack() {
        HelloApplication app = HelloApplication.getInstance();
        if (app != null && app.getNavigator() != null) {
            app.getNavigator().navigateBack();
        }
    }
}