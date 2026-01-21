package com.example.battleshipoop.app.controllers;

import com.example.battleshipoop.app.HelloApplication;
import com.example.battleshipoop.app.utils.FXDesignHelper;
import com.example.battleshipoop.models.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GameController extends BorderPane {
    // Игровые компоненты
    private Player player;
    private Player enemy;
    private GridPane playerGrid;
    private GridPane enemyGrid;

    // UI элементы
    private Label statusLabel;
    private Label playerLabel;
    private Label turnIndicator;
    private Label playerShipsLabel;
    private Label enemyShipsLabel;

    // Чат компоненты
    private VBox chatPanel;
    private TextArea chatArea;
    private TextField chatInput;
    private Button chatSendButton;
    private boolean chatInitialized = false;

    // Игровое состояние
    private boolean isMyTurn = false;
    private boolean gameStarted = false;
    private boolean iAmReady = false;
    private boolean opponentReady = false;
    private String gameMode = "single";
    private int enemyHits = 0;
    private final int totalEnemyCells = 20;
    private boolean connectionDialogShown = false;
    private String connectionType = ""; // "host" или "client"

    // Цвета для клеток
    private Color hitColor = Color.rgb(220, 53, 69);
    private Color missColor = Color.rgb(248, 249, 250);
    private Color shipColor = Color.rgb(169, 169, 169);
    private Color sunkColor = Color.rgb(139, 0, 0);
    private Color emptyColor = Color.rgb(173, 216, 230, 0.8);

    // Конструкторы
    public GameController(String gameMode) {
        this.gameMode = gameMode;
        System.out.println("Инициализация GameController в режиме: " + gameMode);

        // Обновляем тему
        FXDesignHelper.updateTheme();

        if (gameMode.equals("host")) {
            connectionType = "host";
        } else if (gameMode.equals("client")) {
            connectionType = "client";
        }

        initializeUI(); // Сначала инициализируем UI
        initializeGame(); // Затем инициализируем игровые объекты
        initializeForMode(); // Настраиваем для режима

        // Обновляем счетчики после инициализации
        Platform.runLater(() -> updateShipCounters());
    }

    public GameController() {
        this("single");
    }

    // Инициализация игры
    private void initializeGame() {
        player = new Player("Вы");
        enemy = new Player("Противник");

        // Автоматическая расстановка кораблей
        placeAllShipsAutomatically();
    }

    // Инициализация UI
    private void initializeUI() {
        // Устанавливаем фон
        setBackground(FXDesignHelper.createOceanBackground());

        // Создаем верхнюю панель
        VBox topPanel = createTopPanel();
        setTop(topPanel);

        // Создаем центральную игровую область
        HBox centerArea = createCenterArea();
        setCenter(centerArea);

        // Создаем нижнюю панель управления
        HBox bottomPanel = createBottomPanel();
        setBottom(bottomPanel);
    }

    // Создание верхней панели
    private VBox createTopPanel() {
        VBox topPanel = new VBox(10);
        topPanel.setAlignment(Pos.CENTER);
        topPanel.setPadding(new Insets(20));
        topPanel.setBackground(Background.EMPTY);

        // Заголовок в зависимости от режима
        String title = "";
        if (gameMode.equals("host")) {
            title = "🌐  СОЗДАНИЕ ИГРЫ (ХОСТ)";
        } else if (gameMode.equals("client")) {
            title = "🔗  ПОДКЛЮЧЕНИЕ К ИГРЕ";
        } else {
            title = "⚔  ОДИНОЧНАЯ ИГРА";
        }

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        titleLabel.setTextFill(FXDesignHelper.Colors.TEXT_WHITE);

        // Эффект свечения для заголовка
        javafx.scene.effect.DropShadow glow = new javafx.scene.effect.DropShadow();
        glow.setColor(FXDesignHelper.Colors.WAVE_BLUE);
        glow.setRadius(15);
        glow.setSpread(0.3);
        titleLabel.setEffect(glow);

        // Панель состояния
        HBox statusPanel = new HBox(20);
        statusPanel.setAlignment(Pos.CENTER);
        statusPanel.setPadding(new Insets(10, 0, 0, 0));

        playerLabel = new Label("Игрок: Вы");
        playerLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        playerLabel.setTextFill(FXDesignHelper.Colors.LIGHT_BLUE);

        turnIndicator = new Label("⚓  Расставьте корабли");
        turnIndicator.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        turnIndicator.setTextFill(FXDesignHelper.Colors.SUCCESS);

        statusLabel = new Label("");
        statusLabel.setFont(Font.font("Segoe UI", 14));
        statusLabel.setTextFill(FXDesignHelper.Colors.TEXT_GOLD);

        statusPanel.getChildren().addAll(playerLabel, turnIndicator, statusLabel);

        // Информация о кораблях
        HBox shipsInfo = new HBox(30);
        shipsInfo.setAlignment(Pos.CENTER);
        shipsInfo.setPadding(new Insets(10, 0, 0, 0));

        playerShipsLabel = createShipInfoLabel("Ваши корабли: 10/10", FXDesignHelper.Colors.LIGHT_BLUE);
        enemyShipsLabel = createShipInfoLabel("Корабли противника: 10/10", Color.rgb(255, 107, 107));

        shipsInfo.getChildren().addAll(playerShipsLabel, enemyShipsLabel);

        topPanel.getChildren().addAll(titleLabel, statusPanel, shipsInfo);
        return topPanel;
    }

    private Label createShipInfoLabel(String text, Color color) {
        Label label = new Label(text);
        label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        label.setTextFill(color);
        label.setPadding(new Insets(5, 15, 5, 15));
        label.setBackground(new Background(new BackgroundFill(
                color.deriveColor(0, 1, 1, 0.1),
                new CornerRadii(5),
                null
        )));
        return label;
    }

    // Создание центральной области
    private HBox createCenterArea() {
        HBox centerArea = new HBox(30);
        centerArea.setAlignment(Pos.CENTER);
        centerArea.setPadding(new Insets(20));

        // Игровые поля
        VBox playerField = createGameField("🚢  ВАШЕ ПОЛЕ", true);
        VBox enemyField = createGameField("🎯  ПОЛЕ ПРОТИВНИКА", false);

        // Для сетевой игры добавляем чат
        if (gameMode.equals("host") || gameMode.equals("client")) {
            chatPanel = createChatPanel();
            centerArea.getChildren().addAll(playerField, enemyField, chatPanel);
        } else {
            centerArea.getChildren().addAll(playerField, enemyField);
        }

        return centerArea;
    }

    // Создание панели чата
    private VBox createChatPanel() {
        VBox chatPanel = new VBox(10);
        chatPanel.setPrefWidth(300);
        chatPanel.setPadding(new Insets(15));

        // Фон панели чата
        LinearGradient chatGradient = new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(25, 35, 55, 0.95)),
                new Stop(1, Color.rgb(15, 25, 45, 0.95))
        );

        chatPanel.setBackground(new Background(new BackgroundFill(
                chatGradient,
                new CornerRadii(10),
                null
        )));

        chatPanel.setBorder(new Border(new BorderStroke(
                FXDesignHelper.Colors.WAVE_BLUE,
                BorderStrokeStyle.SOLID,
                new CornerRadii(10),
                new BorderWidths(2)
        )));

        // Эффект свечения
        javafx.scene.effect.DropShadow glow = new javafx.scene.effect.DropShadow();
        glow.setColor(FXDesignHelper.Colors.WAVE_BLUE.deriveColor(0, 1, 1, 0.3));
        glow.setRadius(10);
        chatPanel.setEffect(glow);

        // Заголовок чата
        Label chatTitle = new Label("💬  ИГРОВОЙ ЧАТ");
        chatTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        chatTitle.setTextFill(FXDesignHelper.Colors.WAVE_BLUE);

        // Область сообщений
        chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setWrapText(true);
        chatArea.setPrefHeight(400);
        chatArea.setStyle(
                "-fx-control-inner-background: #2C3E50; " +
                        "-fx-text-fill: #ECF0F1; " +
                        "-fx-font-family: 'Segoe UI'; " +
                        "-fx-font-size: 12px; " +
                        "-fx-border-color: #4CAF50; " +
                        "-fx-border-radius: 5;"
        );

        // Панель ввода
        HBox inputBox = new HBox(5);
        inputBox.setPadding(new Insets(5, 0, 0, 0));

        chatInput = new TextField();
        chatInput.setPromptText("Введите сообщение...");
        chatInput.setPrefWidth(200);
        chatInput.setStyle(
                "-fx-background-color: #34495E; " +
                        "-fx-text-fill: white; " +
                        "-fx-prompt-text-fill: #95A5A6; " +
                        "-fx-border-color: #4CAF50; " +
                        "-fx-border-width: 1;"
        );

        // Обработка нажатия Enter
        chatInput.setOnAction(e -> sendChatMessage());

        chatSendButton = new Button("➤");
        chatSendButton.setStyle(
                "-fx-background-color: #4CAF50; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-min-width: 40;"
        );
        chatSendButton.setOnAction(e -> sendChatMessage());

        inputBox.getChildren().addAll(chatInput, chatSendButton);

        chatPanel.getChildren().addAll(chatTitle, chatArea, inputBox);

        // Инициализируем чат
        initializeChat();

        return chatPanel;
    }

    // Создание игрового поля
    private VBox createGameField(String title, boolean isPlayerField) {
        VBox fieldContainer = new VBox(15);
        fieldContainer.setAlignment(Pos.CENTER);
        fieldContainer.setPadding(new Insets(20));

        // Создаем панель с эффектом глубины
        StackPane panelContainer = new StackPane();

        // Фон панели с градиентом
        LinearGradient panelGradient = new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, FXDesignHelper.Colors.PANEL_BG),
                new Stop(1, FXDesignHelper.Colors.PANEL_BG.darker())
        );

        Region backgroundPanel = new Region();
        backgroundPanel.setBackground(new Background(new BackgroundFill(
                panelGradient,
                new CornerRadii(15),
                null
        )));

        // Обводка панели
        backgroundPanel.setBorder(new Border(new BorderStroke(
                isPlayerField ? FXDesignHelper.Colors.LIGHT_BLUE : Color.rgb(255, 107, 107),
                BorderStrokeStyle.SOLID,
                new CornerRadii(15),
                new BorderWidths(3)
        )));

        // Эффект тени
        javafx.scene.effect.DropShadow shadow = new javafx.scene.effect.DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.3));
        shadow.setRadius(15);
        shadow.setOffsetX(5);
        shadow.setOffsetY(5);
        backgroundPanel.setEffect(shadow);

        // Заголовок поля
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        titleLabel.setTextFill(isPlayerField ?
                FXDesignHelper.Colors.LIGHT_BLUE :
                Color.rgb(255, 107, 107));

        // Создаем сетку
        GridPane grid = new GridPane();
        grid.setHgap(2);
        grid.setVgap(2);
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(10));

        // Создаем координатную сетку
        createCoordinateGrid(grid, isPlayerField);

        // Сохраняем ссылки на сетки
        if (isPlayerField) {
            playerGrid = grid;
        } else {
            enemyGrid = grid;
        }

        // Добавляем элементы на панель
        fieldContainer.getChildren().addAll(titleLabel, grid);
        panelContainer.getChildren().addAll(backgroundPanel, fieldContainer);

        return fieldContainer;
    }

    // Создание координатной сетки
    private void createCoordinateGrid(GridPane grid, boolean isPlayerField) {
        // Добавляем буквенные координаты (слева)
        for (int row = 0; row < 10; row++) {
            Label rowLabel = new Label(String.valueOf((char) ('А' + row)));
            rowLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
            rowLabel.setTextFill(FXDesignHelper.Colors.TEXT_GOLD);
            grid.add(rowLabel, 0, row + 1);
        }

        // Добавляем числовые координаты (сверху)
        for (int col = 0; col < 10; col++) {
            Label colLabel = new Label(String.valueOf(col + 1));
            colLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
            colLabel.setTextFill(FXDesignHelper.Colors.TEXT_GOLD);
            grid.add(colLabel, col + 1, 0);
        }

        // Создаем игровые клетки
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                Rectangle cell = new Rectangle(35, 35);
                cell.setFill(emptyColor);
                cell.setStroke(FXDesignHelper.Colors.CELL_BORDER);
                cell.setStrokeWidth(1.5);

                final int x = col;
                final int y = row;

                if (isPlayerField) {
                    // Клетки своего поля
                    cell.setOnMouseEntered(e -> {
                        if (!gameStarted) {
                            cell.setFill(FXDesignHelper.Colors.CELL_BORDER.deriveColor(0, 1, 1, 0.3));
                        }
                    });

                    cell.setOnMouseExited(e -> {
                        if (!gameStarted) {
                            cell.setFill(emptyColor);
                        }
                    });

                    cell.setOnMouseClicked(e -> {
                        if (!gameStarted) {
                            placeAllShipsAutomatically();
                            updatePlayerGrid();

                            if (player.allShipsPlaced()) {
                                setStatus("✅ Все корабли расставлены!", FXDesignHelper.Colors.SUCCESS);
                                sendReadySignal();
                            }
                        }
                    });
                } else {
                    // Клетки поля противника
                    cell.setOnMouseEntered(e -> {
                        if (gameStarted && isMyTurn && isEmptyCell(cell)) {
                            cell.setFill(Color.rgb(255, 255, 100, 0.5));
                        }
                    });

                    cell.setOnMouseExited(e -> {
                        if (gameStarted && isMyTurn && isEmptyCell(cell)) {
                            cell.setFill(emptyColor);
                        }
                    });

                    cell.setOnMouseClicked(e -> {
                        if (gameStarted && isMyTurn) {
                            attackEnemy(x, y);
                        } else if (!gameStarted) {
                            showAlert("Игра не начата", "Дождитесь начала игры!");
                        } else if (!isMyTurn) {
                            showAlert("Не ваш ход", "Сейчас ход противника!");
                        }
                    });
                }

                grid.add(cell, col + 1, row + 1);
            }
        }
    }

    // Проверка, пустая ли клетка
    private boolean isEmptyCell(Rectangle cell) {
        Color fill = (Color) cell.getFill();
        return fill.equals(emptyColor);
    }

    // Создание нижней панели
    private HBox createBottomPanel() {
        HBox bottomPanel = new HBox(20);
        bottomPanel.setAlignment(Pos.CENTER);
        bottomPanel.setPadding(new Insets(20));

        // Левая группа кнопок
        VBox leftButtons = new VBox(10);
        leftButtons.setAlignment(Pos.CENTER);

        Button backButton = createStyledButton("◀  В главное меню", FXDesignHelper.Colors.ERROR);
        backButton.setOnAction(e -> goBack());

        Button restartButton = createStyledButton("🔄  Новая игра", FXDesignHelper.Colors.WARNING);
        restartButton.setOnAction(e -> restartGame());

        leftButtons.getChildren().addAll(backButton, restartButton);

        // Центральная группа кнопок
        VBox centerButtons = new VBox(10);
        centerButtons.setAlignment(Pos.CENTER);

        Button autoPlaceButton = createStyledButton("⚡  Авторасстановка", FXDesignHelper.Colors.WARNING);
        autoPlaceButton.setOnAction(e -> {
            placeAllShipsAutomatically();
            updatePlayerGrid();
            setStatus("Корабли расставлены автоматически!", FXDesignHelper.Colors.SUCCESS);
            updateReadyButtonState();
        });

        // Кнопка готовности для сетевой игры
        if (gameMode.equals("host") || gameMode.equals("client")) {
            Button readyButton = createStyledButton("✅  Готов к игре", FXDesignHelper.Colors.SUCCESS);
            readyButton.setId("readyButton");
            readyButton.setOnAction(e -> {
                if (player.allShipsPlaced()) {
                    sendReadySignal();
                    updateReadyButtonState();
                } else {
                    setStatus("Сначала расставьте все корабли!", FXDesignHelper.Colors.ERROR);
                }
            });
            centerButtons.getChildren().add(readyButton);
        }

        // Кнопка начала игры для одиночной игры
        if (gameMode.equals("single")) {
            Button startButton = createStyledButton("▶  Начать игру", FXDesignHelper.Colors.SUCCESS);
            startButton.setOnAction(e -> startSinglePlayerGame());
            centerButtons.getChildren().add(startButton);
        }

        centerButtons.getChildren().add(autoPlaceButton);

        // Правая группа кнопок (только для сетевой игры)
        VBox rightButtons = null;
        if (gameMode.equals("host") || gameMode.equals("client")) {
            rightButtons = new VBox(10);
            rightButtons.setAlignment(Pos.CENTER);

            Button chatButton = createStyledButton("💬  Открыть чат", FXDesignHelper.Colors.SUCCESS);
            chatButton.setOnAction(e -> openGameChat());

            rightButtons.getChildren().add(chatButton);

            // Кнопка сервера для хоста
            if (gameMode.equals("host")) {
                Button serverButton = createStyledButton("🌐  Запустить сервер", FXDesignHelper.Colors.BUTTON_BG);
                serverButton.setOnAction(e -> hostGame());
                rightButtons.getChildren().add(serverButton);
            }

            // Кнопка подключения для клиента
            if (gameMode.equals("client")) {
                Button connectButton = createStyledButton("🔗  Подключиться", FXDesignHelper.Colors.BUTTON_BG);
                connectButton.setOnAction(e -> showConnectDialog());
                rightButtons.getChildren().add(connectButton);
            }
        }

        // Добавляем группы кнопок на панель
        bottomPanel.getChildren().add(leftButtons);
        bottomPanel.getChildren().add(centerButtons);
        if (rightButtons != null) {
            bottomPanel.getChildren().add(rightButtons);
        }

        return bottomPanel;
    }

    // Создание стилизованной кнопки
    private Button createStyledButton(String text, Color color) {
        Button button = new Button(text);
        button.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        button.setPrefSize(200, 40);

        // Градиент для кнопки
        LinearGradient gradient = new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, color),
                new Stop(1, color.darker())
        );

        button.setBackground(new Background(new BackgroundFill(
                gradient,
                new CornerRadii(6),
                null
        )));

        button.setTextFill(FXDesignHelper.Colors.TEXT_WHITE);
        button.setBorder(new Border(new BorderStroke(
                color.darker(),
                BorderStrokeStyle.SOLID,
                new CornerRadii(6),
                new BorderWidths(2)
        )));

        // Эффекты при наведении
        button.setOnMouseEntered(e -> {
            button.setBackground(new Background(new BackgroundFill(
                    color.brighter(),
                    new CornerRadii(6),
                    null
            )));
            button.setTranslateY(-2);
        });

        button.setOnMouseExited(e -> {
            button.setBackground(new Background(new BackgroundFill(
                    gradient,
                    new CornerRadii(6),
                    null
            )));
            button.setTranslateY(0);
        });

        return button;
    }

    // Инициализация для режима
    private void initializeForMode() {
        Platform.runLater(() -> {
            switch (gameMode) {
                case "host":
                    playerLabel.setText("Хост");
                    turnIndicator.setText("⚓  Запустите сервер");
                    turnIndicator.setTextFill(FXDesignHelper.Colors.WARNING);
                    setStatus("Нажмите 'Запустить сервер' для создания игры", FXDesignHelper.Colors.TEXT_GOLD);
                    break;

                case "client":
                    playerLabel.setText("Клиент");
                    turnIndicator.setText("⚓  Подключитесь к серверу");
                    turnIndicator.setTextFill(FXDesignHelper.Colors.WARNING);
                    setStatus("Введите IP-адрес сервера для подключения", FXDesignHelper.Colors.TEXT_GOLD);
                    break;

                case "single":
                    playerLabel.setText("Игрок");
                    turnIndicator.setText("⚓  Расставьте корабли");
                    turnIndicator.setTextFill(FXDesignHelper.Colors.SUCCESS);
                    setStatus("Нажмите 'Авторасстановка' или кликните по своему полю", FXDesignHelper.Colors.TEXT_GOLD);
                    break;
            }
        });
    }

    // Инициализация чата
    private void initializeChat() {
        if (chatInitialized) return;

        chatArea.appendText("=== ИГРОВОЙ ЧАТ ===\n");
        chatArea.appendText("Добро пожаловать в игру!\n");
        chatArea.appendText("========================\n\n");

        if (gameMode.equals("host")) {
            chatArea.appendText("[Система] Вы создали игру как хост\n");
            chatArea.appendText("[Система] Ожидайте подключения других игроков\n");
        } else if (gameMode.equals("client")) {
            chatArea.appendText("[Система] Вы подключились как клиент\n");
            chatArea.appendText("[Система] Ожидайте начала игры\n");
        }

        chatInitialized = true;
    }

    // Отправка сообщения в чат
    private void sendChatMessage() {
        String message = chatInput.getText().trim();
        if (!message.isEmpty()) {
            String username = connectionType.equals("host") ? "Хост" : "Клиент";
            chatArea.appendText("Вы (" + username + "): " + message + "\n");
            chatInput.clear();

            // Симуляция ответа для демонстрации
            if (gameMode.equals("host")) {
                simulateOpponentResponse();
            }
        }
    }

    // Симуляция ответа противника в чате (для демонстрации)
    private void simulateOpponentResponse() {
        String[] responses = {
                "Отличный ход!",
                "Интересная стратегия...",
                "Мне нравится эта игра!",
                "Попробуйте атаковать другой сектор",
                "У меня осталось несколько кораблей",
                "Эта битва становится жаркой!",
                "Хорошая попытка, но промах!",
                "Мои корабли держатся стойко!"
        };

        int randomIndex = (int) (Math.random() * responses.length);
        String response = responses[randomIndex];

        // Задержка перед ответом (1-3 секунды)
        new Thread(() -> {
            try {
                Thread.sleep(1000 + (int)(Math.random() * 2000));
                Platform.runLater(() -> {
                    chatArea.appendText("Противник: " + response + "\n");
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    // Открытие чата
    private void openGameChat() {
        if (chatPanel != null && chatInput != null) {
            chatInput.requestFocus();
            setStatus("Чат активен. Введите сообщение и нажмите Enter", FXDesignHelper.Colors.TEXT_GOLD);
        }
    }

    // Автоматическая расстановка кораблей
    private void placeAllShipsAutomatically() {
        System.out.println("Начинаем автоматическую расстановку кораблей...");

        // Сбрасываем игрока
        player = new Player("Вы");

        // Создаем список кораблей
        List<Ship> shipsToPlace = new ArrayList<>(player.getShips());

        // Сортируем корабли по размеру (от большего к меньшему)
        shipsToPlace.sort((s1, s2) -> Integer.compare(s2.getSize(), s1.getSize()));

        System.out.println("Кораблей для расстановки: " + shipsToPlace.size());

        for (Ship ship : shipsToPlace) {
            boolean placed = false;
            int attempts = 0;
            int maxAttempts = 200;

            while (!placed && attempts < maxAttempts) {
                int x = (int) (Math.random() * 10);
                int y = (int) (Math.random() * 10);
                ShipDirection direction = Math.random() > 0.5 ? ShipDirection.HORIZONTAL : ShipDirection.VERTICAL;

                if (canPlaceShipWithMargin(ship, x, y, direction)) {
                    if (player.placeShip(ship, x, y, direction)) {
                        placed = true;
                        System.out.println("✓ Корабль размером " + ship.getSize() + " размещен");
                    }
                }
                attempts++;
            }

            if (!placed) {
                // Попробуем без свободного пространства
                placed = tryPlaceShipWithoutMargin(ship);

                if (!placed) {
                    setStatus("Ошибка расстановки кораблей!", FXDesignHelper.Colors.ERROR);
                    resetAndTryAgain();
                    return;
                }
            }
        }

        updatePlayerGrid();
        updateShipCounters();
        setStatus("✅ Все корабли расставлены автоматически!", FXDesignHelper.Colors.SUCCESS);

        // Обновляем состояние кнопки "Готов"
        updateReadyButtonState();
    }

    private boolean canPlaceShipWithMargin(Ship ship, int x, int y, ShipDirection direction) {
        int size = ship.getSize();
        GameBoard board = player.getBoard();

        if (direction == ShipDirection.HORIZONTAL) {
            if (x + size > 10) return false;

            for (int i = -1; i <= size; i++) {
                for (int j = -1; j <= 1; j++) {
                    int checkX = x + i;
                    int checkY = y + j;

                    if (checkX >= 0 && checkX < 10 && checkY >= 0 && checkY < 10) {
                        if (j == 0 && i >= 0 && i < size) {
                            if (board.getCell(checkX, checkY) != GameBoard.CellState.EMPTY) {
                                return false;
                            }
                        } else {
                            if (board.getCell(checkX, checkY) == GameBoard.CellState.SHIP) {
                                return false;
                            }
                        }
                    }
                }
            }

        } else {
            if (y + size > 10) return false;

            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= size; j++) {
                    int checkX = x + i;
                    int checkY = y + j;

                    if (checkX >= 0 && checkX < 10 && checkY >= 0 && checkY < 10) {
                        if (i == 0 && j >= 0 && j < size) {
                            if (board.getCell(checkX, checkY) != GameBoard.CellState.EMPTY) {
                                return false;
                            }
                        } else {
                            if (board.getCell(checkX, checkY) == GameBoard.CellState.SHIP) {
                                return false;
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    private boolean tryPlaceShipWithoutMargin(Ship ship) {
        boolean placed = false;
        int attempts = 0;

        while (!placed && attempts < 100) {
            int x = (int) (Math.random() * 10);
            int y = (int) (Math.random() * 10);
            ShipDirection direction = Math.random() > 0.5 ? ShipDirection.HORIZONTAL : ShipDirection.VERTICAL;

            if (player.placeShip(ship, x, y, direction)) {
                placed = true;
            }
            attempts++;
        }

        return placed;
    }

    private void resetAndTryAgain() {
        System.out.println("Пробуем расставить корабли заново...");
        player = new Player("Вы");
        placeAllShipsAutomatically();
    }

    // Обновление игрового поля игрока
    private void updatePlayerGrid() {
        if (playerGrid == null || player == null) return;

        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                Rectangle cell = (Rectangle) getNodeFromGridPane(playerGrid, x + 1, y + 1);
                if (cell != null) {
                    GameBoard.CellState state = player.getBoard().getCell(x, y);
                    updateCellColor(cell, state);
                }
            }
        }
    }

    // Обновление цвета клетки
    private void updateCellColor(Rectangle cell, GameBoard.CellState state) {
        switch (state) {
            case EMPTY:
                cell.setFill(emptyColor);
                break;
            case SHIP:
                cell.setFill(shipColor);
                break;
            case HIT:
                cell.setFill(hitColor);
                break;
            case MISS:
                cell.setFill(missColor);
                break;
            case SUNK:
                cell.setFill(sunkColor);
                break;
        }
    }

    // Обновление счетчиков кораблей
    private void updateShipCounters() {
        Platform.runLater(() -> {
            if (playerShipsLabel == null || enemyShipsLabel == null) {
                // Метки еще не инициализированы, откладываем обновление
                return;
            }

            if (player != null) {
                int playerShips = (int) player.getShips().stream()
                        .filter(ship -> !ship.isSunk())
                        .count();
                playerShipsLabel.setText("Ваши корабли: " + playerShips + "/10");
            }

            if (enemy != null) {
                int enemyShips = (int) enemy.getShips().stream()
                        .filter(ship -> !ship.isSunk())
                        .count();
                enemyShipsLabel.setText("Корабли противника: " + enemyShips + "/10");
            }
        });
    }

    // Обновление состояния кнопки "Готов"
    private void updateReadyButtonState() {
        Platform.runLater(() -> {
            Button readyButton = (Button) lookup("#readyButton");
            if (readyButton != null) {
                if (iAmReady) {
                    readyButton.setText("✓  Готов");
                    readyButton.setDisable(true);
                } else if (player.allShipsPlaced()) {
                    readyButton.setText("✅  Готов к игре");
                    readyButton.setDisable(false);
                } else {
                    readyButton.setText("Расставьте корабли");
                    readyButton.setDisable(true);
                }
            }
        });
    }

    // Атака противника
    private void attackEnemy(int x, int y) {
        if (!isMyTurn || !gameStarted) {
            setStatus("Сейчас не ваш ход!", FXDesignHelper.Colors.ERROR);
            return;
        }

        Rectangle cell = (Rectangle) getNodeFromGridPane(enemyGrid, x + 1, y + 1);
        if (cell != null) {
            Color fill = (Color) cell.getFill();
            if (fill.equals(hitColor) || fill.equals(missColor)) {
                setStatus("Вы уже стреляли в эту клетку!", FXDesignHelper.Colors.ERROR);
                return;
            }
        }

        // Симуляция атаки для демонстрации
        boolean isHit = Math.random() > 0.6;

        if (isHit) {
            cell.setFill(hitColor);
            enemyHits++;
            setStatus("✅ Попадание! Стреляйте снова", FXDesignHelper.Colors.SUCCESS);

            // Проверка победы
            if (enemyHits >= totalEnemyCells) {
                handleVictory();
                return;
            }
        } else {
            cell.setFill(missColor);
            setStatus("Промах! Ход противника", FXDesignHelper.Colors.WARNING);

            // Ход переходит противнику
            isMyTurn = false;
            turnIndicator.setText("⏳  Ход противника");
            turnIndicator.setTextFill(FXDesignHelper.Colors.WARNING);

            // В одиночной игре - ход компьютера
            if (gameMode.equals("single")) {
                computerTurn();
            }
        }

        updateShipCounters();
    }

    // Ход компьютера (для одиночной игры)
    private void computerTurn() {
        new Thread(() -> {
            try {
                Thread.sleep(1500); // Пауза перед ходом компьютера

                Platform.runLater(() -> {
                    // Случайная атака компьютера
                    int x, y;
                    Rectangle cell;
                    do {
                        x = (int) (Math.random() * 10);
                        y = (int) (Math.random() * 10);
                        cell = (Rectangle) getNodeFromGridPane(playerGrid, x + 1, y + 1);
                    } while (cell == null || !isEmptyCell(cell));

                    // Симуляция попадания
                    boolean isHit = Math.random() > 0.7;

                    if (isHit) {
                        cell.setFill(hitColor);
                        setStatus("Противник попал по вашему кораблю!", FXDesignHelper.Colors.ERROR);
                    } else {
                        cell.setFill(missColor);
                        setStatus("Противник промахнулся", FXDesignHelper.Colors.TEXT_GOLD);
                    }

                    // Проверка поражения
                    if (player.allShipsSunk()) {
                        handleDefeat();
                        return;
                    }

                    // Возвращаем ход игроку
                    isMyTurn = true;
                    turnIndicator.setText("🎯  Ваш ход");
                    turnIndicator.setTextFill(FXDesignHelper.Colors.SUCCESS);

                    updateShipCounters();
                });

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    // Обработка победы
    private void handleVictory() {
        gameStarted = false;
        isMyTurn = false;

        Platform.runLater(() -> {
            turnIndicator.setText("🏆  ВЫ ПОБЕДИЛИ!");
            turnIndicator.setTextFill(FXDesignHelper.Colors.SUCCESS);
            setStatus("Все корабли противника потоплены!", FXDesignHelper.Colors.SUCCESS);

            showAlert("ПОБЕДА!", "Поздравляем! Вы выиграли игру!");

            if (chatArea != null) {
                chatArea.appendText("[Система] Вы победили! Поздравляем!\n");
            }
        });
    }

    // Обработка поражения
    private void handleDefeat() {
        gameStarted = false;
        isMyTurn = false;

        Platform.runLater(() -> {
            turnIndicator.setText("💀  ВЫ ПРОИГРАЛИ");
            turnIndicator.setTextFill(FXDesignHelper.Colors.ERROR);
            setStatus("Все ваши корабли потоплены", FXDesignHelper.Colors.ERROR);

            showAlert("ПОРАЖЕНИЕ", "Все ваши корабли потоплены. Попробуйте еще раз!");

            if (chatArea != null) {
                chatArea.appendText("[Система] Вы проиграли. Попробуйте еще раз!\n");
            }
        });
    }

    // Начало одиночной игры
    private void startSinglePlayerGame() {
        if (!player.allShipsPlaced()) {
            setStatus("Сначала расставьте все корабли!", FXDesignHelper.Colors.ERROR);
            return;
        }

        gameStarted = true;
        isMyTurn = true;

        Platform.runLater(() -> {
            turnIndicator.setText("🎯  Ваш ход");
            turnIndicator.setTextFill(FXDesignHelper.Colors.SUCCESS);
            setStatus("Игра началась! Атакуйте поле противника", FXDesignHelper.Colors.TEXT_GOLD);
        });
    }

    // Отправка сигнала готовности
    private void sendReadySignal() {
        if (!player.allShipsPlaced()) {
            setStatus("Сначала расставьте все корабли!", FXDesignHelper.Colors.ERROR);
            return;
        }

        iAmReady = true;

        Platform.runLater(() -> {
            turnIndicator.setText("✅  Вы готовы");
            turnIndicator.setTextFill(FXDesignHelper.Colors.SUCCESS);
            setStatus("Ожидаем готовности противника...", FXDesignHelper.Colors.TEXT_GOLD);

            if (chatArea != null) {
                chatArea.appendText("[Система] Вы готовы к игре. Ожидание противника...\n");
            }

            // Обновляем кнопку
            updateReadyButtonState();
        });
    }

    // Запуск сервера (хост)
    private void hostGame() {
        try {
            setStatus("Запуск сервера...", FXDesignHelper.Colors.WARNING);

            // Симуляция запуска сервера
            new Thread(() -> {
                try {
                    Thread.sleep(2000); // Имитация задержки

                    Platform.runLater(() -> {
                        turnIndicator.setText("🌐  Сервер запущен");
                        turnIndicator.setTextFill(FXDesignHelper.Colors.SUCCESS);
                        setStatus("Сервер запущен на порту 5555. Ожидание подключения...", FXDesignHelper.Colors.TEXT_GOLD);

                        if (chatArea != null) {
                            chatArea.appendText("[Система] Сервер запущен. Ожидание подключения...\n");
                            chatArea.appendText("[Система] Сообщите свой IP другим игрокам\n");
                        }
                    });

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();

        } catch (Exception e) {
            setStatus("Ошибка запуска сервера: " + e.getMessage(), FXDesignHelper.Colors.ERROR);
        }
    }

    // Показать диалог подключения
    private void showConnectDialog() {
        if (connectionDialogShown) return;
        connectionDialogShown = true;

        TextInputDialog dialog = new TextInputDialog("localhost");
        dialog.setTitle("Подключение к игре");
        dialog.setHeaderText("Введите IP-адрес сервера");
        dialog.setContentText("IP-адрес:");
        dialog.getDialogPane().setPrefSize(400, 150);

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String serverAddress = result.get().trim();
            connectToGame(serverAddress);
        } else {
            setStatus("Подключение отменено", FXDesignHelper.Colors.ERROR);
        }
    }

    // Подключение к игре (клиент)
    private void connectToGame(String serverAddress) {
        try {
            setStatus("Подключение к " + serverAddress + "...", FXDesignHelper.Colors.WARNING);

            // Симуляция подключения
            new Thread(() -> {
                try {
                    Thread.sleep(1500); // Имитация задержки

                    Platform.runLater(() -> {
                        turnIndicator.setText("🔗  Подключено");
                        turnIndicator.setTextFill(FXDesignHelper.Colors.SUCCESS);
                        setStatus("Успешно подключено к серверу! Расставьте корабли", FXDesignHelper.Colors.TEXT_GOLD);

                        if (chatArea != null) {
                            chatArea.appendText("[Система] Подключено к серверу: " + serverAddress + "\n");
                            chatArea.appendText("[Система] Теперь можно общаться в чате\n");
                            chatArea.appendText("[Система] Расставьте корабли и нажмите 'Готов'\n");
                        }
                    });

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();

        } catch (Exception e) {
            setStatus("Ошибка подключения: " + e.getMessage(), FXDesignHelper.Colors.ERROR);
        }
    }

    // Перезапуск игры
    private void restartGame() {
        System.out.println("Перезапуск игры...");

        // Сбрасываем игроков
        player = new Player("Вы");
        enemy = new Player("Противник");

        // Сбрасываем состояние
        isMyTurn = false;
        gameStarted = false;
        iAmReady = false;
        opponentReady = false;
        enemyHits = 0;

        // Перерасставляем корабли
        placeAllShipsAutomatically();

        // Очищаем чат (если есть)
        if (chatArea != null) {
            chatArea.clear();
            chatInitialized = false;
            initializeChat();
        }

        Platform.runLater(() -> {
            turnIndicator.setText("⚓  Расставьте корабли");
            turnIndicator.setTextFill(FXDesignHelper.Colors.SUCCESS);
            setStatus("Игра перезапущена", FXDesignHelper.Colors.TEXT_GOLD);

            // Обновляем кнопку "Готов"
            updateReadyButtonState();
        });

        System.out.println("Игра перезапущена");
    }

    // Возврат в главное меню
    private void goBack() {
        HelloApplication app = HelloApplication.getInstance();
        if (app != null && app.getNavigator() != null) {
            app.getNavigator().navigateBack();
        }
    }

    // Вспомогательные методы
    private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }

    private void setStatus(String message, Color color) {
        Platform.runLater(() -> {
            statusLabel.setText(message);
            statusLabel.setTextFill(color);
        });
    }

    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}