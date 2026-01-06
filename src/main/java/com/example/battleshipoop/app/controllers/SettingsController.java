package com.example.battleshipoop.app.controllers;

import com.example.battleshipoop.app.AppProperties;
import com.example.battleshipoop.app.AppSettings;
import com.example.battleshipoop.app.HelloApplication;
import com.example.battleshipoop.AppTheme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class SettingsController extends BorderPane {
    private AppSettings settings;

    public SettingsController() {
        settings = AppProperties.getSettings();
        initializeUI();
    }

    private void initializeUI() {
        setBackground(new Background(new BackgroundFill(Color.DARKSLATEGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        VBox centerBox = new VBox(20);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(40));

        Label themeLabel = new Label("Тема:");
        themeLabel.setFont(Font.font("Arial", 16));
        themeLabel.setTextFill(Color.WHITE);

        ComboBox<String> themeCombo = new ComboBox<>();
        themeCombo.getItems().addAll("Темная", "Светлая", "Синяя");
        themeCombo.setValue(settings.getTheme());

        Label soundLabel = new Label("Звук:");
        soundLabel.setFont(Font.font("Arial", 16));
        soundLabel.setTextFill(Color.WHITE);

        CheckBox soundCheckbox = new CheckBox("Включить звук");
        soundCheckbox.setSelected(settings.isSoundEnabled());
        soundCheckbox.setTextFill(Color.WHITE);

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);

        Button saveButton = new Button("Сохранить");
        saveButton.setOnAction(e -> {
            settings.setTheme(themeCombo.getValue());
            settings.setSoundEnabled(soundCheckbox.isSelected());
            AppTheme.changeTheme(settings.getTheme());
            goBack();
        });

        Button cancelButton = new Button("Отмена");
        cancelButton.setOnAction(e -> goBack());

        buttonBox.getChildren().addAll(saveButton, cancelButton);
        centerBox.getChildren().addAll(themeLabel, themeCombo, soundLabel, soundCheckbox, buttonBox);
        setCenter(centerBox);
    }

    private void goBack() {
        HelloApplication app = HelloApplication.getInstance();
        if (app != null && app.getNavigator() != null) {
            app.getNavigator().navigateBack();
        }
    }
}