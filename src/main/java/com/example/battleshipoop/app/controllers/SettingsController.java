package com.example.battleshipoop.app.controllers;

import com.example.battleshipoop.app.AppProperties;
import com.example.battleshipoop.app.AppSettings;
import com.example.battleshipoop.app.HelloApplication;
import com.example.battleshipoop.app.utils.FXDesignHelper;
import com.example.battleshipoop.AppTheme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class SettingsController extends BorderPane {
    private AppSettings settings;
    private ToggleGroup themeGroup;

    public SettingsController() {
        settings = AppProperties.getSettings();
        initializeUI();
    }

    private void initializeUI() {
        FXDesignHelper.updateTheme();

        setBackground(FXDesignHelper.createOceanBackground());

        VBox mainContainer = new VBox(30);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(40));

        Label titleLabel = FXDesignHelper.createTitleLabel("НАСТРОЙКИ");

        VBox settingsPanel = new VBox(20);
        settingsPanel.setAlignment(Pos.CENTER_LEFT);
        settingsPanel.setPadding(new Insets(30));
        settingsPanel.setMaxWidth(500);

        Region depthPanel = FXDesignHelper.createDepthPanel();
        StackPane panelContainer = new StackPane();
        panelContainer.getChildren().addAll(depthPanel, settingsPanel);

        Label themeLabel = new Label("Тема интерфейса:");
        themeLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        themeLabel.setTextFill(FXDesignHelper.Colors.TEXT_WHITE);

        themeGroup = new ToggleGroup();

        String currentTheme = settings.getTheme();

        RadioButton darkTheme = createThemeRadioButton("Темная", "dark", currentTheme);
        RadioButton lightTheme = createThemeRadioButton("Светлая", "light", currentTheme);
        RadioButton blueTheme = createThemeRadioButton("Синяя", "blue", currentTheme);

        VBox themeBox = new VBox(10, darkTheme, lightTheme, blueTheme);
        themeBox.setPadding(new Insets(10, 0, 20, 20));

        // Кнопки
        HBox buttonRow = new HBox(20);
        buttonRow.setAlignment(Pos.CENTER);
        buttonRow.setPadding(new Insets(20, 0, 0, 0));

        Button saveButton = createStyledButton(" Сохранить", "success");
        saveButton.setOnAction(e -> saveSettings());

        Button cancelButton = createStyledButton("Отмена", "error");
        cancelButton.setOnAction(e -> goBack());

        buttonRow.getChildren().addAll(saveButton, cancelButton);

        settingsPanel.getChildren().addAll(themeLabel, themeBox, buttonRow);
        mainContainer.getChildren().addAll(titleLabel, panelContainer);
        setCenter(mainContainer);
    }

    private RadioButton createThemeRadioButton(String text, String themeValue, String currentTheme) {
        RadioButton radio = new RadioButton(text);
        radio.setToggleGroup(themeGroup);
        radio.setUserData(themeValue);
        radio.setTextFill(FXDesignHelper.Colors.TEXT_WHITE);
        radio.setFont(Font.font("Segoe UI", 14));

        if (currentTheme.equals(themeValue)) {
            radio.setSelected(true);
        }

        radio.setOnMouseEntered(e -> radio.setTextFill(FXDesignHelper.Colors.LIGHT_BLUE));
        radio.setOnMouseExited(e -> radio.setTextFill(FXDesignHelper.Colors.TEXT_WHITE));

        return radio;
    }

    private Button createStyledButton(String text, String type) {
        Button button = new Button(text);
        button.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        button.setPrefSize(150, 40);

        javafx.scene.paint.Color color;
        switch (type) {
            case "success":
                color = FXDesignHelper.Colors.SUCCESS;
                break;
            case "error":
                color = FXDesignHelper.Colors.ERROR;
                break;
            default:
                color = FXDesignHelper.Colors.BUTTON_BG;
        }

        LinearGradient gradient = new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new javafx.scene.paint.Stop(0, color),
                new javafx.scene.paint.Stop(1, color.darker())
        );

        button.setBackground(new Background(new BackgroundFill(gradient, new CornerRadii(6), null)));
        button.setTextFill(FXDesignHelper.Colors.TEXT_WHITE);

        // Эффекты при наведении
        button.setOnMouseEntered(e -> {
            button.setBackground(new Background(new BackgroundFill(
                    color.brighter(), new CornerRadii(6), null)));
        });

        button.setOnMouseExited(e -> {
            button.setBackground(new Background(new BackgroundFill(
                    gradient, new CornerRadii(6), null)));
        });

        return button;
    }

    private void saveSettings() {
        RadioButton selectedRadio = (RadioButton) themeGroup.getSelectedToggle();
        if (selectedRadio != null) {
            String theme = (String) selectedRadio.getUserData();
            settings.setTheme(theme);
            AppTheme.changeTheme(theme);
            FXDesignHelper.updateTheme();
        }
        goBack();
    }

    private void goBack() {
        HelloApplication app = HelloApplication.getInstance();
        if (app != null && app.getNavigator() != null) {
            app.getNavigator().navigateBack();
        }
    }
}