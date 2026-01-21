package com.example.battleshipoop.app.utils;

import com.example.battleshipoop.app.AppProperties;
import com.example.battleshipoop.app.AppSettings;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class FXDesignHelper {
    public static Paint GOLD;
    public static Paint LIGHT_BLUE;
    public static Paint WAVE_BLUE;
    private static String currentTheme = "dark";

    static {
        updateTheme();
    }

    public static class Colors {
        public static final Color OCEAN_BG = Color.rgb(0, 77, 153);
        public static final Color DEEP_BLUE = Color.rgb(0, 51, 102);
        public static final Color LIGHT_BLUE = Color.rgb(173, 216, 230);
        public static final Color WAVE_BLUE = Color.rgb(100, 149, 237);
        public static final Color PANEL_BG = Color.rgb(30, 40, 60, 0.9);
        public static final Color TEXT_WHITE = Color.WHITE;
        public static final Color TEXT_GOLD = Color.rgb(255, 193, 7);
        public static final Color CELL_BG = Color.rgb(135, 206, 250, 0.8);
        public static final Color CELL_BORDER = Color.rgb(30, 144, 255);
        public static final Color BUTTON_BG = Color.rgb(0, 123, 255);
        public static final Color BUTTON_HOVER = Color.rgb(23, 162, 184);
        public static final Color SUCCESS = Color.rgb(40, 167, 69);
        public static final Color WARNING = Color.rgb(255, 193, 7);
        public static final Color ERROR = Color.rgb(220, 53, 69);
        public static final Color INFO = Color.rgb(23, 162, 184);
    }

    public static class LightColors {
        public static final Color OCEAN_BG = Color.rgb(240, 248, 255); // AliceBlue
        public static final Color DEEP_BLUE = Color.rgb(176, 224, 230); // PowderBlue
        public static final Color LIGHT_BLUE = Color.rgb(70, 130, 180); // SteelBlue
        public static final Color WAVE_BLUE = Color.rgb(100, 149, 237);
        public static final Color PANEL_BG = Color.rgb(255, 255, 255, 0.9);
        public static final Color TEXT_WHITE = Color.rgb(33, 37, 41); // Темный текст
        public static final Color TEXT_GOLD = Color.rgb(220, 120, 0);
        public static final Color CELL_BG = Color.rgb(240, 248, 255, 0.8);
        public static final Color CELL_BORDER = Color.rgb(70, 130, 180);
        public static final Color BUTTON_BG = Color.rgb(13, 110, 253);
        public static final Color BUTTON_HOVER = Color.rgb(10, 88, 202);
        public static final Color SUCCESS = Color.rgb(25, 135, 84);
        public static final Color WARNING = Color.rgb(255, 193, 7);
        public static final Color ERROR = Color.rgb(220, 53, 69);
        public static final Color INFO = Color.rgb(23, 162, 184);
    }

    public static class BlueColors {
        public static final Color OCEAN_BG = Color.rgb(0, 47, 94);
        public static final Color DEEP_BLUE = Color.rgb(0, 32, 64);
        public static final Color LIGHT_BLUE = Color.rgb(100, 200, 255);
        public static final Color WAVE_BLUE = Color.rgb(0, 150, 255);
        public static final Color PANEL_BG = Color.rgb(20, 50, 80, 0.9);
        public static final Color TEXT_WHITE = Color.rgb(220, 240, 255);
        public static final Color TEXT_GOLD = Color.rgb(255, 215, 0);
        public static final Color CELL_BG = Color.rgb(100, 180, 255, 0.8);
        public static final Color CELL_BORDER = Color.rgb(0, 150, 255);
        public static final Color BUTTON_BG = Color.rgb(0, 150, 255);
        public static final Color BUTTON_HOVER = Color.rgb(0, 180, 255);
        public static final Color SUCCESS = Color.rgb(0, 200, 150);
        public static final Color WARNING = Color.rgb(255, 165, 0);
        public static final Color ERROR = Color.rgb(255, 50, 50);
        public static final Color INFO = Color.rgb(23, 162, 184);
    }

    public static void updateTheme() {
        try {
            AppSettings settings = AppProperties.getSettings();
            currentTheme = settings.getTheme();
        } catch (Exception e) {
            currentTheme = "dark";
        }
    }

    public static String getCurrentTheme() {
        return currentTheme;
    }

    public static Color getColor(String colorType) {
        if ("light".equals(currentTheme)) {
            switch (colorType) {
                case "ocean_bg": return LightColors.OCEAN_BG;
                case "deep_blue": return LightColors.DEEP_BLUE;
                case "light_blue": return LightColors.LIGHT_BLUE;
                case "wave_blue": return LightColors.WAVE_BLUE;
                case "panel_bg": return LightColors.PANEL_BG;
                case "text_white": return LightColors.TEXT_WHITE;
                case "text_gold": return LightColors.TEXT_GOLD;
                case "cell_bg": return LightColors.CELL_BG;
                case "cell_border": return LightColors.CELL_BORDER;
                case "button_bg": return LightColors.BUTTON_BG;
                case "button_hover": return LightColors.BUTTON_HOVER;
                case "success": return LightColors.SUCCESS;
                case "warning": return LightColors.WARNING;
                case "error": return LightColors.ERROR;
                case "info": return LightColors.INFO;
            }
        } else if ("blue".equals(currentTheme)) {
            switch (colorType) {
                case "ocean_bg": return BlueColors.OCEAN_BG;
                case "deep_blue": return BlueColors.DEEP_BLUE;
                case "light_blue": return BlueColors.LIGHT_BLUE;
                case "wave_blue": return BlueColors.WAVE_BLUE;
                case "panel_bg": return BlueColors.PANEL_BG;
                case "text_white": return BlueColors.TEXT_WHITE;
                case "text_gold": return BlueColors.TEXT_GOLD;
                case "cell_bg": return BlueColors.CELL_BG;
                case "cell_border": return BlueColors.CELL_BORDER;
                case "button_bg": return BlueColors.BUTTON_BG;
                case "button_hover": return BlueColors.BUTTON_HOVER;
                case "success": return BlueColors.SUCCESS;
                case "warning": return BlueColors.WARNING;
                case "error": return BlueColors.ERROR;
                case "info": return BlueColors.INFO;
            }
        }
        switch (colorType) {
            case "ocean_bg": return Colors.OCEAN_BG;
            case "deep_blue": return Colors.DEEP_BLUE;
            case "light_blue": return Colors.LIGHT_BLUE;
            case "wave_blue": return Colors.WAVE_BLUE;
            case "panel_bg": return Colors.PANEL_BG;
            case "text_white": return Colors.TEXT_WHITE;
            case "text_gold": return Colors.TEXT_GOLD;
            case "cell_bg": return Colors.CELL_BG;
            case "cell_border": return Colors.CELL_BORDER;
            case "button_bg": return Colors.BUTTON_BG;
            case "button_hover": return Colors.BUTTON_HOVER;
            case "success": return Colors.SUCCESS;
            case "warning": return Colors.WARNING;
            case "error": return Colors.ERROR;
            case "info": return Colors.INFO;
            default: return Colors.OCEAN_BG;
        }
    }

    public static Background createOceanBackground() {
        Color color = getColor("ocean_bg");
        LinearGradient gradient = new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, color.darker()),
                new Stop(0.5, color),
                new Stop(1, color.brighter())
        );
        return new Background(new BackgroundFill(gradient, CornerRadii.EMPTY, null));
    }

    public static Background createWaveBackground() {
        Color color = getColor("wave_blue");
        LinearGradient gradient = new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, color.deriveColor(0, 1, 0.9, 1)),
                new Stop(1, color.deriveColor(0, 1, 0.7, 1))
        );
        return new Background(new BackgroundFill(gradient, new CornerRadii(10), null));
    }

    public static Background createPanelBackground() {
        Color color = getColor("panel_bg");
        LinearGradient gradient = new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, color),
                new Stop(1, color.darker())
        );
        return new Background(new BackgroundFill(gradient, new CornerRadii(15), new Insets(0)));
    }

    public static Button createNavButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        button.setPrefSize(250, 50);

        Color buttonBg = getColor("button_bg");
        Color buttonHover = getColor("button_hover");

        LinearGradient gradient = new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, buttonBg),
                new Stop(1, buttonBg.darker())
        );

        LinearGradient hoverGradient = new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, buttonHover),
                new Stop(1, buttonHover.darker())
        );

        button.setBackground(new Background(new BackgroundFill(gradient, new CornerRadii(8), null)));
        button.setTextFill(getColor("text_white"));
        button.setBorder(new Border(new BorderStroke(
                buttonBg.darker(),
                BorderStrokeStyle.SOLID,
                new CornerRadii(8),
                new BorderWidths(2)
        )));

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.3));
        shadow.setRadius(5);
        button.setEffect(shadow);

        button.setOnMouseEntered(e -> {
            button.setBackground(new Background(new BackgroundFill(hoverGradient, new CornerRadii(8), null)));
            button.setTranslateY(-2);
            button.setEffect(new DropShadow(10, buttonBg.deriveColor(0, 1, 1, 0.5)));
        });

        button.setOnMouseExited(e -> {
            button.setBackground(new Background(new BackgroundFill(gradient, new CornerRadii(8), null)));
            button.setTranslateY(0);
            button.setEffect(shadow);
        });

        button.setOnMousePressed(e -> {
            button.setBackground(new Background(new BackgroundFill(
                    buttonBg.darker(), new CornerRadii(8), null)));
        });

        return button;
    }

    public static Button createActionButton(String text, Color color) {
        Button button = new Button(text);
        button.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        button.setPrefSize(180, 40);

        LinearGradient gradient = new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, color),
                new Stop(1, color.darker())
        );

        button.setBackground(new Background(new BackgroundFill(gradient, new CornerRadii(6), null)));
        button.setTextFill(getColor("text_white"));

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.2));
        shadow.setRadius(3);
        button.setEffect(shadow);

        button.setOnMouseEntered(e -> {
            button.setBackground(new Background(new BackgroundFill(color.brighter(), new CornerRadii(6), null)));
            button.setEffect(new DropShadow(5, color.brighter()));
        });

        button.setOnMouseExited(e -> {
            button.setBackground(new Background(new BackgroundFill(gradient, new CornerRadii(6), null)));
            button.setEffect(shadow);
        });

        return button;
    }

    public static Label createTitleLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 36));
        label.setTextFill(getColor("text_white"));

        DropShadow glow = new DropShadow();
        glow.setColor(getColor("wave_blue").deriveColor(0, 1, 1, 0.7));
        glow.setRadius(15);
        glow.setSpread(0.3);

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.5));
        shadow.setRadius(5);
        shadow.setInput(glow);

        label.setEffect(shadow);
        return label;
    }

    public static Label createSubtitleLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        label.setTextFill(getColor("text_gold"));

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.3));
        shadow.setRadius(3);
        label.setEffect(shadow);

        return label;
    }

    public static Label createInfoLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Segoe UI", 14));
        label.setTextFill(getColor("text_white"));
        return label;
    }

    public static Label createStatusLabel(String text, Color color) {
        Label label = new Label(text);
        label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        label.setTextFill(color);

        DropShadow shadow = new DropShadow();
        shadow.setColor(color.deriveColor(0, 1, 1, 0.5));
        shadow.setRadius(10);
        label.setEffect(shadow);

        return label;
    }

    public static Region createDepthPanel() {
        Region panel = new Region();
        panel.setBackground(createPanelBackground());

        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setColor(Color.rgb(0, 0, 0, 0.4));
        innerShadow.setRadius(10);
        innerShadow.setOffsetX(2);
        innerShadow.setOffsetY(2);

        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.3));
        dropShadow.setRadius(15);
        dropShadow.setOffsetX(5);
        dropShadow.setOffsetY(5);

        panel.setEffect(dropShadow);
        panel.setBorder(new Border(new BorderStroke(
                getColor("wave_blue").deriveColor(0, 1, 1, 0.5),
                BorderStrokeStyle.SOLID,
                new CornerRadii(15),
                new BorderWidths(2)
        )));

        return panel;
    }

    public static Region createGameCell(boolean isClickable) {
        Region cell = new Region();
        cell.setPrefSize(35, 35);

        Color cellBg = getColor("cell_bg");
        Color cellBorder = getColor("cell_border");

        LinearGradient cellGradient = new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, cellBg),
                new Stop(1, cellBg.darker())
        );

        cell.setBackground(new Background(new BackgroundFill(
                cellGradient,
                new CornerRadii(3),
                null
        )));

        cell.setBorder(new Border(new BorderStroke(
                cellBorder,
                BorderStrokeStyle.SOLID,
                new CornerRadii(3),
                new BorderWidths(1.5)
        )));

        if (isClickable) {
            cell.setOnMouseEntered(e -> {
                cell.setBackground(new Background(new BackgroundFill(
                        cellBorder.deriveColor(0, 1, 1, 0.6),
                        new CornerRadii(3),
                        null
                )));
                cell.setScaleX(1.05);
                cell.setScaleY(1.05);
            });

            cell.setOnMouseExited(e -> {
                cell.setBackground(new Background(new BackgroundFill(
                        cellGradient,
                        new CornerRadii(3),
                        null
                )));
                cell.setScaleX(1.0);
                cell.setScaleY(1.0);
            });
        }

        return cell;
    }

    public static VBox createChatPanel() {
        VBox chatPanel = new VBox(10);
        chatPanel.setPrefWidth(300);
        chatPanel.setPadding(new Insets(15));

        Color panelBg = getColor("panel_bg");
        Color waveBlue = getColor("wave_blue");

        LinearGradient chatGradient = new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, panelBg),
                new Stop(1, panelBg.darker())
        );

        chatPanel.setBackground(new Background(new BackgroundFill(
                chatGradient,
                new CornerRadii(10),
                null
        )));

        chatPanel.setBorder(new Border(new BorderStroke(
                waveBlue,
                BorderStrokeStyle.SOLID,
                new CornerRadii(10),
                new BorderWidths(2)
        )));


        DropShadow glow = new DropShadow();
        glow.setColor(waveBlue.deriveColor(0, 1, 1, 0.3));
        glow.setRadius(10);
        chatPanel.setEffect(glow);

        return chatPanel;
    }
}