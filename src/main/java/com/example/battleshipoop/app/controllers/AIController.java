package com.example.battleshipoop.app.controllers;

import com.example.battleshipoop.app.HelloApplication;
import com.example.battleshipoop.models.*;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;

import java.util.ArrayList;
import java.util.List;

public class AIController extends BorderPane {
    private Player player;
    private Player aiPlayer;
    private GameBoard aiBoard;
    private GridPane playerGrid;
    private GridPane aiGrid;
    private Label statusLabel;
    private Label playerLabel;
    private boolean isPlayerTurn;
    private boolean gameStarted;
    private boolean gameOver;

    // Для ИИ
    private List<Point> possibleTargets;
    private List<Point> hitTargets;
    private boolean aiHunting;
    private Point lastHit;
    private int directionTries;

    class Point {
        int x, y;
        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Point point = (Point) obj;
            return x == point.x && y == point.y;
        }

        @Override
        public int hashCode() {
            return 31 * x + y;
        }
    }

    public AIController() {
        initializeUI();
        initializeGame();
    }

    private void initializeUI() {
        // Красивый градиентный фон
        LinearGradient gradient = new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#1a2980")),
                new Stop(1, Color.web("#26d0ce"))
        );
        setBackground(new Background(new BackgroundFill(gradient, CornerRadii.EMPTY, Insets.EMPTY)));

        HBox topPanel = createTopPanel();
        setTop(topPanel);

        HBox gameArea = createGameArea();
        setCenter(gameArea);

        HBox bottomPanel = createBottomPanel();
        setBottom(bottomPanel);
    }

    private void initializeGame() {
        player = new Player("Игрок");
        aiPlayer = new Player("Компьютер");
        aiBoard = aiPlayer.getBoard();
        isPlayerTurn = true;
        gameStarted = false;
        gameOver = false;

        // Инициализация ИИ
        possibleTargets = new ArrayList<>();
        hitTargets = new ArrayList<>();
        aiHunting = false;
        lastHit = null;
        directionTries = 0;

        // Автоматическая расстановка кораблей для ИИ
        autoPlaceAIShips();

        autoPlacePlayerShips();

        updatePlayerGrid();
        updateAIGrid();
    }

    private void autoPlaceAIShips() {
        // Сбрасываем ИИ игрока
        aiPlayer = new Player("Компьютер");

        List<Ship> shipsToPlace = new ArrayList<>(aiPlayer.getShips());
        shipsToPlace.sort((s1, s2) -> Integer.compare(s2.getSize(), s1.getSize()));

        for (Ship ship : shipsToPlace) {
            boolean placed = false;
            int attempts = 0;
            int maxAttempts = 200;

            while (!placed && attempts < maxAttempts) {
                int x = (int) (Math.random() * 10);
                int y = (int) (Math.random() * 10);
                ShipDirection direction = Math.random() > 0.5 ? ShipDirection.HORIZONTAL : ShipDirection.VERTICAL;

                if (aiPlayer.placeShip(ship, x, y, direction)) {
                    placed = true;
                }
                attempts++;
            }

            if (!placed) {
                System.err.println("Не удалось разместить корабль ИИ: " + ship.getSize());
            }
        }

        aiBoard = aiPlayer.getBoard();
    }

    private void autoPlacePlayerShips() {
        player = new Player("Игрок");
        placeAllShipsAutomatically();
    }

    private HBox createTopPanel() {
        HBox topPanel = new HBox(20);
        topPanel.setAlignment(Pos.CENTER);
        topPanel.setPadding(new Insets(15));

        topPanel.setStyle("-fx-background-color: linear-gradient(to right, #2C3E50, #4CA1AF); " +
                "-fx-background-radius: 0 0 15 15; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0.5, 0, 2);");

        playerLabel = new Label("🎮 Одиночная игра");
        playerLabel.setFont(Font.font("Arial Bold", 24));
        playerLabel.setTextFill(Color.WHITE);
        playerLabel.setEffect(new DropShadow(5, Color.BLACK));

        statusLabel = new Label("Расставьте корабли");
        statusLabel.setFont(Font.font("Arial", 18));
        statusLabel.setTextFill(Color.web("#F1C40F"));
        statusLabel.setEffect(new InnerShadow(3, Color.BLACK));

        topPanel.getChildren().addAll(playerLabel, statusLabel);
        return topPanel;
    }

    private HBox createGameArea() {
        HBox gameArea = new HBox(30);
        gameArea.setAlignment(Pos.CENTER);
        gameArea.setPadding(new Insets(20));

        VBox playerField = createPlayerField();
        VBox aiField = createAIField();

        gameArea.getChildren().addAll(playerField, aiField);
        return gameArea;
    }

    private VBox createPlayerField() {
        VBox fieldBox = new VBox(10);
        fieldBox.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Ваше поле");
        titleLabel.setFont(Font.font("Arial Bold", 20));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setEffect(new DropShadow(5, Color.BLACK));

        playerGrid = new GridPane();
        playerGrid.setHgap(2);
        playerGrid.setVgap(2);

        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                Rectangle cell = new Rectangle(35, 35);
                cell.setFill(Color.web("#3498DB", 0.7));
                cell.setStroke(Color.web("#2C3E50"));
                cell.setStrokeWidth(1.5);
                cell.setArcWidth(5);
                cell.setArcHeight(5);
                cell.setEffect(new InnerShadow(2, Color.BLACK));

                playerGrid.add(cell, col, row);
            }
        }

        fieldBox.getChildren().addAll(titleLabel, playerGrid);
        return fieldBox;
    }

    private VBox createAIField() {
        VBox fieldBox = new VBox(10);
        fieldBox.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("🤖 Поле компьютера");
        titleLabel.setFont(Font.font("Arial Bold", 20));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setEffect(new DropShadow(5, Color.BLACK));

        aiGrid = new GridPane();
        aiGrid.setHgap(2);
        aiGrid.setVgap(2);

        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                Rectangle cell = new Rectangle(35, 35);
                cell.setFill(Color.web("#E74C3C", 0.7));
                cell.setStroke(Color.web("#2C3E50"));
                cell.setStrokeWidth(1.5);
                cell.setArcWidth(5);
                cell.setArcHeight(5);
                cell.setEffect(new InnerShadow(2, Color.BLACK));

                final int x = col;
                final int y = row;

                cell.setOnMouseClicked(e -> {
                    if (gameStarted && isPlayerTurn && !gameOver) {
                        playerAttack(x, y);
                    }
                });

                aiGrid.add(cell, col, row);
            }
        }

        fieldBox.getChildren().addAll(titleLabel, aiGrid);
        return fieldBox;
    }

    private HBox createBottomPanel() {
        HBox bottomPanel = new HBox(15);
        bottomPanel.setAlignment(Pos.CENTER);
        bottomPanel.setPadding(new Insets(15));

        bottomPanel.setStyle("-fx-background-color: linear-gradient(to right, #2C3E50, #4CA1AF); " +
                "-fx-background-radius: 15 15 0 0; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0.5, 0, -2);");

        Button backButton = createStyledButton("Главное меню", "#E74C3C", "#EC7063");
        backButton.setOnAction(e -> goBack());

        Button restartButton = createStyledButton("Новая игра", "#3498DB", "#2980B9");
        restartButton.setOnAction(e -> restartGame());

        Button autoPlaceButton = createStyledButton("Авторасстановка", "#9B59B6", "#8E44AD");
        autoPlaceButton.setOnAction(e -> {
            autoPlacePlayerShips();
            updatePlayerGrid();
            showInfo("Корабли расставлены автоматически");
        });

        Button startGameButton = createStyledButton("Начать игру", "#27AE60", "#2ECC71");
        startGameButton.setOnAction(e -> {
            if (player.allShipsPlaced() && !gameStarted) {
                startGame();
            } else if (!player.allShipsPlaced()) {
                showInfo("Сначала расставьте все корабли!");
            }
        });

        VBox leftBox = new VBox(5, backButton, restartButton);
        VBox rightBox = new VBox(5, autoPlaceButton, startGameButton);

        bottomPanel.getChildren().addAll(leftBox, rightBox);
        bottomPanel.setSpacing(30);
        return bottomPanel;
    }
    private Button createStyledButton(String text, String color1, String color2) {
        Button button = new Button(text);
        button.setStyle(getButtonStyle(color1, color2));
        button.setFont(Font.font("Arial", 14));
        return button;
    }
    private String getButtonStyle(String color1, String color2) {
        return "-fx-background-color: linear-gradient(to bottom, " + color1 + ", " + color2 + "); " +
                "-fx-background-radius: 10; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 8 15; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 8, 0.5, 0, 2); " +
                "-fx-border-color: rgba(255,255,255,0.2); " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 10;" +
                "-fx-min-width: 120;" +
                "-fx-min-height: 40;";
    }

    private void updatePlayerGrid() {
        if (playerGrid == null || player == null) return;

        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                Rectangle cell = (Rectangle) getNodeFromGridPane(playerGrid, x, y);
                if (cell != null) {
                    GameBoard.CellState state = player.getBoard().getCell(x, y);
                    switch (state) {
                        case EMPTY:
                            cell.setFill(Color.web("#3498DB", 0.7));
                            break;
                        case SHIP:
                            cell.setFill(Color.web("#34495E"));
                            break;
                        case HIT:
                            cell.setFill(Color.web("#E74C3C"));
                            break;
                        case MISS:
                            cell.setFill(Color.web("#ECF0F1"));
                            break;
                        case SUNK:
                            LinearGradient sunkGradient = new LinearGradient(
                                    0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                                    new Stop(0, Color.web("#C0392B")),
                                    new Stop(1, Color.web("#E74C3C"))
                            );
                            cell.setFill(sunkGradient);
                            break;
                    }
                }
            }
        }
    }

    private void updateAIGrid() {
        if (aiGrid == null) return;

        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                Rectangle cell = (Rectangle) getNodeFromGridPane(aiGrid, x, y);
                if (cell != null) {
                    // Не показываем корабли ИИ до попадания
                    cell.setFill(Color.web("#E74C3C", 0.7));
                }
            }
        }
    }

    private void updateAIAttackCell(int x, int y, boolean hit) {
        if (aiGrid == null) return;

        Rectangle cell = (Rectangle) getNodeFromGridPane(aiGrid, x, y);
        if (cell != null) {
            if (hit) {
                cell.setFill(Color.web("#E74C3C"));
            } else {
                cell.setFill(Color.web("#ECF0F1"));
            }
        }
    }

    private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }

    private void playerAttack(int x, int y) {
        if (!isPlayerTurn || gameOver) return;

        Rectangle cell = (Rectangle) getNodeFromGridPane(aiGrid, x, y);
        if (cell != null) {
            Color fill = (Color) cell.getFill();
            if (fill.equals(Color.web("#E74C3C")) || fill.equals(Color.web("#ECF0F1"))) {
                showInfo("Вы уже стреляли в эту клетку!");
                return;
            }
        }

        GameBoard.CellState result = aiBoard.attack(x, y);

        if (result == GameBoard.CellState.HIT || result == GameBoard.CellState.SUNK) {
            updateAIAttackCell(x, y, true);
            statusLabel.setText("Попадание! Стреляйте снова");
            statusLabel.setTextFill(Color.web("#2ECC71"));

            // Проверяем, потоплен ли корабль
            if (result == GameBoard.CellState.SUNK) {
                showInfo("Вы потопили корабль компьютера!");
            }

            // Проверяем победу
            if (checkAILoss()) {
                playerWins();
                return;
            }

            // Игрок продолжает ход при попадании
            isPlayerTurn = true;
        } else {
            updateAIAttackCell(x, y, false);
            statusLabel.setText("Промах. Ход компьютера...");
            statusLabel.setTextFill(Color.web("#F1C40F"));
            isPlayerTurn = false;

            // Ход ИИ после небольшой задержки
            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(e -> aiTurn());
            pause.play();
        }
    }

    private void aiTurn() {
        if (gameOver || isPlayerTurn) return;

        Point target = getAITarget();
        if (target == null) {
            // Если нет целей, выбираем случайную клетку
            target = getRandomTarget();
        }

        if (target != null) {
            GameBoard.CellState result = player.getBoard().attack(target.x, target.y);
            updatePlayerGrid();

            if (result == GameBoard.CellState.HIT || result == GameBoard.CellState.SUNK) {
                hitTargets.add(target);
                lastHit = target;
                aiHunting = true;

                // Добавляем соседние клетки как возможные цели
                addAdjacentTargets(target);

                if (result == GameBoard.CellState.SUNK) {
                    showInfo("Компьютер потопил ваш корабль!");
                    hitTargets.clear();
                    aiHunting = false;
                    lastHit = null;
                }

                // Проверяем поражение
                if (checkPlayerLoss()) {
                    aiWins();
                    return;
                }

                // ИИ продолжает ход при попадании
                isPlayerTurn = false;
                PauseTransition pause = new PauseTransition(Duration.seconds(1));
                pause.setOnFinished(e -> aiTurn());
                pause.play();
            } else {
                // Промах
                isPlayerTurn = true;
                statusLabel.setText("🎮 Ваш ход");
                statusLabel.setTextFill(Color.web("#2ECC71"));
            }
        }
    }

    private Point getAITarget() {
        if (!possibleTargets.isEmpty()) {
            return possibleTargets.remove(0);
        }

        if (aiHunting && lastHit != null) {
            // Продолжаем охоту в определенном направлении
            Point nextTarget = continueHunting();
            if (nextTarget != null) {
                return nextTarget;
            }
        }

        return null;
    }

    private Point getRandomTarget() {
        List<Point> availableTargets = new ArrayList<>();

        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                GameBoard.CellState state = player.getBoard().getCell(x, y);
                if (state == GameBoard.CellState.EMPTY || state == GameBoard.CellState.SHIP) {
                    availableTargets.add(new Point(x, y));
                }
            }
        }

        if (!availableTargets.isEmpty()) {
            return availableTargets.get((int) (Math.random() * availableTargets.size()));
        }

        return null;
    }

    private void addAdjacentTargets(Point point) {
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

        for (int[] dir : directions) {
            int newX = point.x + dir[0];
            int newY = point.y + dir[1];

            if (newX >= 0 && newX < 10 && newY >= 0 && newY < 10) {
                GameBoard.CellState state = player.getBoard().getCell(newX, newY);
                if (state == GameBoard.CellState.EMPTY || state == GameBoard.CellState.SHIP) {
                    Point newPoint = new Point(newX, newY);
                    if (!possibleTargets.contains(newPoint) && !hitTargets.contains(newPoint)) {
                        possibleTargets.add(newPoint);
                    }
                }
            }
        }
    }

    private Point continueHunting() {
        if (lastHit == null) return null;

        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

        // Пробуем разные направления
        for (int i = 0; i < directions.length; i++) {
            int newX = lastHit.x + directions[i][0];
            int newY = lastHit.y + directions[i][1];

            if (newX >= 0 && newX < 10 && newY >= 0 && newY < 10) {
                GameBoard.CellState state = player.getBoard().getCell(newX, newY);
                if (state == GameBoard.CellState.EMPTY || state == GameBoard.CellState.SHIP) {
                    Point newPoint = new Point(newX, newY);
                    if (!possibleTargets.contains(newPoint) && !hitTargets.contains(newPoint)) {
                        return newPoint;
                    }
                }
            }
        }

        return null;
    }

    private void placeAllShipsAutomatically() {
        System.out.println("Начинаем автоматическую расстановку кораблей...");

        player = new Player("Игрок");

        List<Ship> shipsToPlace = new ArrayList<>(player.getShips());
        shipsToPlace.sort((s1, s2) -> Integer.compare(s2.getSize(), s1.getSize()));

        System.out.println("Кораблей для расстановки: " + shipsToPlace.size());

        for (Ship ship : shipsToPlace) {
            System.out.println("Расставляем корабль размером " + ship.getSize() + "...");

            boolean placed = false;
            int attempts = 0;
            int maxAttempts = 200;

            while (!placed && attempts < maxAttempts) {
                int x = (int) (Math.random() * 10);
                int y = (int) (Math.random() * 10);
                ShipDirection direction = Math.random() > 0.5 ? ShipDirection.HORIZONTAL : ShipDirection.VERTICAL;

                if (player.placeShip(ship, x, y, direction)) {
                    placed = true;
                    System.out.println("✓ Корабль размером " + ship.getSize() +
                            " размещен в (" + x + "," + y + ") " +
                            (direction == ShipDirection.HORIZONTAL ? "горизонтально" : "вертикально"));
                }
                attempts++;
            }

            if (!placed) {
                System.out.println("✗ Не удалось разместить корабль размером " + ship.getSize());
                showInfo("Ошибка расстановки кораблей! Попробуйте снова.");
                return;
            }
        }

        updatePlayerGrid();
        showInfo("Все корабли расставлены автоматически!");
    }

    private void startGame() {
        if (player.allShipsPlaced() && !gameStarted) {
            gameStarted = true;
            isPlayerTurn = Math.random() > 0.5; // Случайно определяем, кто ходит первым

            if (isPlayerTurn) {
                statusLabel.setText("🎮 Игра началась! Ваш ход.");
                statusLabel.setTextFill(Color.web("#2ECC71"));
            } else {
                statusLabel.setText("Игра началась! Ход компьютера...");
                statusLabel.setTextFill(Color.web("#F1C40F"));

                // Ход ИИ после небольшой задержки
                PauseTransition pause = new PauseTransition(Duration.seconds(1));
                pause.setOnFinished(e -> aiTurn());
                pause.play();
            }

            showInfo("Игра началась! " + (isPlayerTurn ? "Вы ходите первым." : "Компьютер ходит первым."));
        }
    }

    private boolean checkPlayerLoss() {
        return player.allShipsSunk();
    }

    private boolean checkAILoss() {
        // Проверяем, все ли корабли ИИ потоплены
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                if (aiBoard.getCell(x, y) == GameBoard.CellState.SHIP) {
                    return false;
                }
            }
        }
        return true;
    }

    private void playerWins() {
        gameOver = true;
        statusLabel.setText("ВЫ ВЫИГРАЛИ!");
        statusLabel.setTextFill(Color.web("#2ECC71"));

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Победа!");
            alert.setHeaderText(null);
            alert.setContentText("Поздравляем! Вы потопили все корабли компьютера!");

            // Стилизация алерта
            alert.getDialogPane().setStyle("-fx-background-color: linear-gradient(to bottom, #2C3E50, #4CA1AF);");
            alert.getDialogPane().lookupButton(ButtonType.OK).setStyle(getButtonStyle("#27AE60", "#2ECC71"));
            alert.showAndWait();
        });
    }

    private void aiWins() {
        gameOver = true;
        statusLabel.setText("ВЫ ПРОИГРАЛИ!");
        statusLabel.setTextFill(Color.web("#E74C3C"));

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Поражение");
            alert.setHeaderText(null);
            alert.setContentText("Компьютер потопил все ваши корабли. Попробуйте снова!");

            // Стилизация алерта
            alert.getDialogPane().setStyle("-fx-background-color: linear-gradient(to bottom, #2C3E50, #4CA1AF);");
            alert.getDialogPane().lookupButton(ButtonType.OK).setStyle(getButtonStyle("#E74C3C", "#EC7063"));
            alert.showAndWait();
        });
    }

    private void showInfo(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Информация");
            alert.setHeaderText(null);
            alert.setContentText(message);

            // Стилизация алерта
            alert.getDialogPane().setStyle("-fx-background-color: linear-gradient(to bottom, #2C3E50, #4CA1AF);");
            alert.getDialogPane().lookupButton(ButtonType.OK).setStyle(getButtonStyle("#3498DB", "#2980B9"));
            alert.showAndWait();
        });
    }

    private void restartGame() {
        initializeGame();
        updateAIGrid();
        statusLabel.setText("Расставьте корабли");
        statusLabel.setTextFill(Color.WHITE);
        gameStarted = false;
        gameOver = false;
        showInfo("Новая игра начата! Расставьте корабли.");
    }

    private void goBack() {
        HelloApplication app = HelloApplication.getInstance();
        if (app != null && app.getNavigator() != null) {
            app.getNavigator().navigateBack();
        }
    }
}