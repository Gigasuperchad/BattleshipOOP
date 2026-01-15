package com.example.battleshipoop.app.controllers;

import com.example.battleshipoop.app.HelloApplication;
import com.example.battleshipoop.models.*;
import com.example.battleshipoop.network.GameClient;
import com.example.battleshipoop.network.GameServer;
import com.example.battleshipoop.app.ChatManager;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;


import java.util.Optional;

public class GameController extends BorderPane {
    private GameClient gameClient;
    private GameServer gameServer;
    private Player player;
    private Player enemy;
    private GridPane playerGrid;
    private GridPane enemyGrid;
    private Label statusLabel;
    private Label playerLabel;
    private boolean isMyTurn;
    private boolean gameStarted;
    private String connectionType; // "host" или "client"
    private boolean opponentReady = false;
    private boolean iAmReady = false;
    private int enemyHits = 0;
    private final int totalEnemyCells = 20; // 10 кораблей у противника
    private String gameMode = "single"; // "single", "host", "client"
    private boolean connectionDialogShown = false;
    private ChatManager chatManager;
    private boolean chatLaunched = false;
    private VBox chatPanel;
    private TextArea chatArea;
    private TextField chatInput;
    private Button chatSendButton;
    private boolean chatInitialized = false;


    public GameController(String gameMode) {
        this.gameMode = gameMode;
        System.out.println("[GameController] Создан в режиме: " + gameMode);

        if (gameMode.equals("host")) {
            connectionType = "host";
        } else if (gameMode.equals("client")) {
            connectionType = "client";
        }

        this.chatManager = new ChatManager();

        initializeUI();
        initializeGame();

        // Автоматически запускаем действия для режима
        initializeForMode();
    }

    // Оставьте старый конструктор для обратной совместимости
    public GameController() {
        this("single"); // По умолчанию одиночная игра
    }

    private void initializeUI() {
        setBackground(new Background(new BackgroundFill(Color.DARKSLATEGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

        HBox topPanel = createTopPanel();
        setTop(topPanel);

        HBox gameArea = createGameArea();
        setCenter(gameArea);

        HBox bottomPanel = createBottomPanel();
        setBottom(bottomPanel);
    }

    public void setGameMode(String mode) {
        this.gameMode = mode;
        System.out.println("Установлен режим игры: " + mode);

        Platform.runLater(() -> {
            switch (gameMode) {
                case "host":
                    playerLabel.setText("Создание игры (Хост)");
                    statusLabel.setText("Нажмите 'Запустить сервер'");
                    showInfo("Вы создаете игру. Другой игрок должен подключиться к вашему IP.");
                    // Автоматически запускаем сервер
                    new Thread(() -> {
                        try {
                            Thread.sleep(500); // Небольшая задержка для инициализации UI
                            Platform.runLater(() -> hostGame());
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }).start();
                    break;

                case "client":
                    playerLabel.setText("Подключение к игре");
                    statusLabel.setText("Введите IP-адрес сервера");
                    showInfo("Подключитесь к игре, введя IP-адрес хоста.");
                    // Автоматически показываем диалог подключения
                    new Thread(() -> {
                        try {
                            Thread.sleep(500); // Небольшая задержка для инициализации UI
                            Platform.runLater(() -> showConnectDialog());
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }).start();
                    break;

                case "single":
                default:
                    playerLabel.setText("Одиночная игра");
                    statusLabel.setText("Расставьте корабли");
                    break;
            }
        });
    }

    private void initializeForMode() {
        Platform.runLater(() -> {
            switch (gameMode) {
                case "host":
                    playerLabel.setText("Создание игры (Хост)");
                    statusLabel.setText("Запуск сервера...");
                    showInfo("Вы создаете игру. Сообщите свой IP другому игроку.");

                    // Запускаем сервер чата в фоновом режиме
                    new Thread(() -> {
                        try {
                            Thread.sleep(2000); // Даем время на инициализацию UI

                            if (chatManager != null) {
                                System.out.println("Запуск сервера чата для хоста...");
                                boolean chatStarted = chatManager.launchChatServer();

                                if (chatStarted) {
                                    Platform.runLater(() -> {
                                        showInfo("Сервер чата запущен. Вы можете открыть чат после подключения противника.");
                                    });
                                }
                            }
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }).start();

                    // Запускаем игровой сервер
                    new Thread(() -> {
                        try {
                            Thread.sleep(1000);
                            Platform.runLater(() -> hostGame());
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }).start();
                    break;

                case "client":
                    playerLabel.setText("Подключение к игре");
                    statusLabel.setText("Введите IP-адрес сервера");
                    showInfo("Подключитесь к игре, введя IP-адрес хоста.");

                    // Показываем диалог с задержкой
                    new Thread(() -> {
                        try {
                            Thread.sleep(1000);
                            Platform.runLater(() -> showConnectDialog());
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }).start();
                    break;
            }
        });
    }

    private void showConnectDialog() {
        if (connectionDialogShown) {
            return; // Не показываем диалог повторно
        }

        connectionDialogShown = true;

        TextInputDialog dialog = new TextInputDialog("localhost");
        dialog.setTitle("Подключение к игре");
        dialog.setHeaderText("Введите IP-адрес сервера");
        dialog.setContentText("IP-адрес:");
        dialog.getDialogPane().setPrefSize(400, 150);

        // Примеры IP адресов для подсказки
        Label hintLabel = new Label("Примеры: localhost, 192.168.1.100, 10.0.0.5");
        hintLabel.setStyle("-fx-text-fill: gray; -fx-font-size: 11px;");

        // ИСПРАВЛЕНА ЭТА СТРОКА:
        dialog.getDialogPane().setContent(new VBox(5, new Label(dialog.getContentText()), new TextField(), hintLabel));

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String serverAddress = result.get().trim();
            System.out.println("Подключение к серверу: " + serverAddress);
            connectToGame(serverAddress);
        } else {
            // Если пользователь отменил, возвращаемся в меню
            showInfo("Подключение отменено. Возвращаемся в меню...");
            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                    Platform.runLater(() -> goBack());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }

    private void initializeGame() {
        player = new Player("Игрок 1");
        enemy = new Player("Противник");
        isMyTurn = false;
        gameStarted = false;
    }

    private HBox createTopPanel() {
        HBox topPanel = new HBox(20);
        topPanel.setAlignment(Pos.CENTER);
        topPanel.setPadding(new Insets(15));
        topPanel.setStyle("-fx-background-color: #2C3E50;");

        playerLabel = new Label("Игрок 1");
        playerLabel.setFont(Font.font("Arial", 20));
        playerLabel.setTextFill(Color.WHITE);

        statusLabel = new Label("Расставьте корабли");
        statusLabel.setFont(Font.font("Arial", 16));
        statusLabel.setTextFill(Color.LIGHTGREEN);

        topPanel.getChildren().addAll(playerLabel, statusLabel);
        return topPanel;
    }

    private HBox createGameArea() {
        HBox gameArea = new HBox(30);
        gameArea.setAlignment(Pos.CENTER);
        gameArea.setPadding(new Insets(20));

        VBox playerField = createPlayerField();
        VBox enemyField = createEnemyField();

        // Создаем панель чата для сетевой игры
        if (gameMode.equals("host") || gameMode.equals("client")) {
            chatPanel = createChatPanel();
            gameArea.getChildren().addAll(playerField, enemyField, chatPanel);
        } else {
            // Для одиночной игры чат не нужен
            gameArea.getChildren().addAll(playerField, enemyField);
        }

        return gameArea;
    }

    private VBox createChatPanel() {
        VBox chatPanel = new VBox(5);
        chatPanel.setPrefWidth(300);
        chatPanel.setStyle("-fx-background-color: rgba(30, 34, 42, 0.95); -fx-padding: 10; -fx-border-color: #4CAF50; -fx-border-width: 2; -fx-border-radius: 5;");

        // Заголовок чата
        HBox titleBox = new HBox();
        titleBox.setAlignment(Pos.CENTER_LEFT);

        Label chatTitle = new Label("💬 Игровой чат");
        chatTitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #4CAF50; -fx-font-weight: bold;");

        titleBox.getChildren().add(chatTitle);

        // Область сообщений
        chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setWrapText(true);
        chatArea.setPrefHeight(400);
        chatArea.setStyle("-fx-control-inner-background: #2C3E50; -fx-text-fill: #ECF0F1; -fx-font-family: 'Consolas'; -fx-font-size: 12px;");

        // Панель ввода
        HBox inputBox = new HBox(5);
        inputBox.setPadding(new Insets(5, 0, 0, 0));

        chatInput = new TextField();
        chatInput.setPromptText("Введите сообщение...");
        chatInput.setPrefWidth(200);
        chatInput.setStyle("-fx-background-color: #34495E; -fx-text-fill: white; -fx-prompt-text-fill: #95A5A6; -fx-border-color: #4CAF50; -fx-border-width: 1;");

        // Обработка нажатия Enter
        chatInput.setOnAction(e -> sendChatMessage());

        chatSendButton = new Button("➤");
        chatSendButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 40;");
        chatSendButton.setOnAction(e -> sendChatMessage());

        inputBox.getChildren().addAll(chatInput, chatSendButton);

        chatPanel.getChildren().addAll(titleBox, chatArea, inputBox);

        // Инициализируем чат
        initializeChat();

        return chatPanel;
    }

    private void initializeChat() {
        if (chatInitialized) return;

        chatArea.appendText("=== ИГРОВОЙ ЧАТ ===\n");
        chatArea.appendText("Автоматически подключен\n");
        chatArea.appendText("Общайтесь с противником\n");
        chatArea.appendText("===================\n\n");

        chatInitialized = true;
    }

    private void sendChatMessage() {
        String message = chatInput.getText().trim();
        if (!message.isEmpty()) {
            // Добавляем свое сообщение сразу
            chatArea.appendText("Вы: " + message + "\n");

            // Отправляем через сетевое соединение
            if (gameClient != null && gameClient.isConnected()) {
                gameClient.sendChatMessage(message);
            } else if (gameServer != null && gameServer.isRunning()) {
                // Хост отправляет сообщение через свой канал
                gameServer.sendMessage("CHAT:Хост:" + message);
            }

            chatInput.clear();
        }
    }

    private void handleChatMessage(String sender, String message) {
        Platform.runLater(() -> {
            // Фильтруем системные сообщения
            if (sender.equals("История")) {
                chatArea.appendText("[История] " + message + "\n");
            } else if (!sender.equals("Система") && !sender.equals("Вы")) {
                chatArea.appendText(sender + ": " + message + "\n");
            } else if (sender.equals("Система")) {
                chatArea.appendText("⚡ " + message + "\n");
            }
        });
    }

    private VBox createPlayerField() {
        VBox fieldBox = new VBox(10);
        fieldBox.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Ваше поле");
        titleLabel.setFont(Font.font("Arial", 18));
        titleLabel.setTextFill(Color.WHITE);

        playerGrid = new GridPane();
        playerGrid.setHgap(2);
        playerGrid.setVgap(2);

        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                Rectangle cell = new Rectangle(35, 35);
                cell.setFill(Color.LIGHTBLUE);
                cell.setStroke(Color.DARKBLUE);

                final int x = col;
                final int y = row;

                // Для расстановки кораблей
                cell.setOnMouseClicked(e -> {
                    if (!gameStarted && player != null) {
                        placeAllShipsAutomatically();
                        updatePlayerGrid();

                        if (player.allShipsPlaced()) {
                            statusLabel.setText("Все корабли расставлены. Ожидаем противника...");
                            sendReadySignal();
                        }
                    }
                });

                playerGrid.add(cell, col, row);
            }
        }

        fieldBox.getChildren().addAll(titleLabel, playerGrid);
        return fieldBox;
    }

    private VBox createEnemyField() {
        VBox fieldBox = new VBox(10);
        fieldBox.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Поле противника");
        titleLabel.setFont(Font.font("Arial", 18));
        titleLabel.setTextFill(Color.WHITE);

        enemyGrid = new GridPane();
        enemyGrid.setHgap(2);
        enemyGrid.setVgap(2);

        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                Rectangle cell = new Rectangle(35, 35);
                cell.setFill(Color.LIGHTBLUE);
                cell.setStroke(Color.DARKBLUE);

                final int x = col;
                final int y = row;

                cell.setOnMouseClicked(e -> {
                    if (gameStarted && isMyTurn) {
                        attackEnemy(x, y);
                    }
                });

                enemyGrid.add(cell, col, row);
            }
        }

        fieldBox.getChildren().addAll(titleLabel, enemyGrid);
        return fieldBox;
    }

    private HBox createBottomPanel() {
        HBox bottomPanel = new HBox(15);
        bottomPanel.setAlignment(Pos.CENTER);
        bottomPanel.setPadding(new Insets(15));

        Button backButton = new Button("В главное меню");
        backButton.setOnAction(e -> goBack());

        Button restartButton = new Button("Новая игра");
        restartButton.setOnAction(e -> restartGame());

        Button autoPlaceButton = new Button("Авторасстановка");
        autoPlaceButton.setOnAction(e -> {
            placeAllShipsAutomatically();
            updatePlayerGrid();
            showInfo("Корабли расставлены автоматически");
            updateReadyButtonState();
        });

        // Кнопка "Готов"
        Button readyButton = new Button("Готов к игре");
        readyButton.setId("readyButton");
        readyButton.setOnAction(e -> {
            if (player.allShipsPlaced()) {
                sendReadySignal();
                updateReadyButtonState();
            } else {
                showInfo("Сначала расставьте все корабли!");
            }
        });

        VBox leftBox = new VBox(5, backButton, restartButton);
        VBox centerBox = new VBox(5, autoPlaceButton, readyButton);

        bottomPanel.getChildren().addAll(leftBox, centerBox);

        // Кнопка чата (только для сетевой игры)
        if (gameMode.equals("host") || gameMode.equals("client")) {
            Button chatButton = new Button("💬 Чат");
            chatButton.setId("chatButton");
            chatButton.setOnAction(e -> openGameChat());
            chatButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

            VBox rightBox = new VBox(5, chatButton);
            bottomPanel.getChildren().add(rightBox);
        }

        bottomPanel.setSpacing(30);
        return bottomPanel;
    }

    private void updateReadyButtonState() {
        Platform.runLater(() -> {
            // Находим кнопку "Готов" в интерфейсе
            Button readyButton = (Button) lookup("#readyButton");
            if (readyButton != null) {
                if (iAmReady) {
                    readyButton.setText("✓ Готов");
                    readyButton.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white;");
                    readyButton.setDisable(true);
                } else if (player.allShipsPlaced()) {
                    readyButton.setText("Готов к игре");
                    readyButton.setStyle("-fx-background-color: #2E8B57; -fx-text-fill: white;");
                    readyButton.setDisable(false);
                } else {
                    readyButton.setText("Расставьте корабли");
                    readyButton.setStyle("-fx-background-color: #7F8C8D; -fx-text-fill: white;");
                    readyButton.setDisable(true);
                }
            }
        });
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
                            cell.setFill(Color.LIGHTBLUE);
                            break;
                        case SHIP:
                            cell.setFill(Color.DARKGRAY);
                            break;
                        case HIT:
                            cell.setFill(Color.RED);
                            break;
                        case MISS:
                            cell.setFill(Color.WHITE);
                            break;
                        case SUNK:
                            cell.setFill(Color.DARKRED);
                            break;
                    }
                }
            }
        }
    }


    private void launchChatClient(String username) {
        new Thread(() -> {
            try {
                System.out.println("Запуск клиента чата: " + username);

                if (chatManager.launchChatClient(username)) {
                    Platform.runLater(() -> {
                        showInfo("Чат запущен! Вы можете общаться с противником.");
                        updateChatButtonState();
                    });
                }
            } catch (Exception e) {
                System.err.println("Ошибка запуска чата: " + e.getMessage());
            }
        }).start();
    }



    private void updateEnemyGrid() {
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                Rectangle cell = (Rectangle) getNodeFromGridPane(enemyGrid, x, y);
                if (cell != null) {
                    // Показываем только результаты выстрелов
                    // Не показываем расположение кораблей противника
                    cell.setFill(Color.LIGHTBLUE);
                }
            }
        }
    }

    private void attackEnemy(int x, int y) {
        if (!isMyTurn || !gameStarted) {
            showInfo("Сейчас не ваш ход!");
            return;
        }

        // Проверяем, не стреляли ли уже в эту клетку
        Rectangle cell = (Rectangle) getNodeFromGridPane(enemyGrid, x, y);
        if (cell != null) {
            Color fill = (Color) cell.getFill();
            if (fill.equals(Color.RED) || fill.equals(Color.WHITE)) {
                showInfo("Вы уже стреляли в эту клетку!");
                return;
            }
        }

        // Отправляем ход противнику
        String message = "ATTACK:" + x + "," + y;
        boolean sent = false;

        if (gameClient != null && gameClient.isConnected()) {
            gameClient.sendMessage(message);
            sent = true;
        } else if (gameServer != null && gameServer.isRunning()) {
            gameServer.sendMessage(message);
            sent = true;
        }

        if (sent) {
            // Временно помечаем клетку
            if (cell != null) {
                cell.setFill(Color.ORANGE);
            }

            isMyTurn = false;
            statusLabel.setText("Ожидаем результат выстрела...");
            statusLabel.setTextFill(Color.YELLOW);
            setEnemyFieldEnabled(false);
        } else {
            showError("Не удалось отправить ход. Проверьте соединение.");
        }
    }

    private boolean checkConnection() {
        if (gameMode.equals("host")) {
            return gameServer != null && gameServer.isRunning();
        } else if (gameMode.equals("client")) {
            return gameClient != null && gameClient.isConnected();
        }
        return false;
    }

    private void sendReadySignal() {
        System.out.println("=== ОТПРАВКА СИГНАЛА ГОТОВНОСТИ ===");
        System.out.println("Режим игры: " + gameMode);
        System.out.println("Соединение type: " + connectionType);
        System.out.println("Все корабли расставлены: " + player.allShipsPlaced());
        System.out.println("Уже готов: " + iAmReady);

        // Показываем состояние соединения
        if (gameMode.equals("host")) {
            System.out.println("Сервер: " + (gameServer != null ? "существует" : "null"));
            System.out.println("Сервер работает: " + (gameServer != null && gameServer.isRunning()));
        } else if (gameMode.equals("client")) {
            System.out.println("Клиент: " + (gameClient != null ? "существует" : "null"));
            System.out.println("Клиент подключен: " + (gameClient != null && gameClient.isConnected()));
        }

        // Проверяем расстановку кораблей
        if (!player.allShipsPlaced()) {
            showInfo("Сначала расставьте все корабли!");
            System.out.println("ОШИБКА: Не все корабли расставлены");
            return;
        }

        // Проверяем, не готовы ли уже
        if (iAmReady) {
            showInfo("Вы уже готовы к игре!");
            System.out.println("ОШИБКА: Уже готов");
            return;
        }

        // Проверяем соединение ОТДЕЛЬНО для каждого режима
        if (gameMode.equals("host")) {
            if (gameServer == null) {
                showError("Сервер не создан! Нажмите 'Запустить сервер'");
                System.out.println("ОШИБКА: Сервер не создан");
                return;
            }

            if (!gameServer.isRunning()) {
                showError("Сервер не запущен! Нажмите 'Запустить сервер'");
                System.out.println("ОШИБКА: Сервер не запущен");
                return;
            }

            // Проверяем, подключен ли клиент
            System.out.println("Проверяем подключение клиента...");
            // В GameServer нужно добавить метод для проверки подключения клиента

        } else if (gameMode.equals("client")) {
            if (gameClient == null) {
                showError("Клиент не создан! Нажмите 'Подключиться'");
                System.out.println("ОШИБКА: Клиент не создан");
                return;
            }

            if (!gameClient.isConnected()) {
                showError("Нет подключения к серверу! Нажмите 'Подключиться'");
                System.out.println("ОШИБКА: Клиент не подключен");
                return;
            }
        } else {
            showError("Неизвестный режим игры: " + gameMode);
            return;
        }

        iAmReady = true;
        System.out.println("Устанавливаем iAmReady = true");

        String message = "READY:" + connectionType;
        System.out.println("Отправляем сообщение: " + message);

        boolean sent = false;

        try {
            if (gameClient != null && gameClient.isConnected()) {
                gameClient.sendMessage(message);
                sent = true;
                System.out.println("Сообщение отправлено через клиент");
            } else if (gameServer != null && gameServer.isRunning()) {
                gameServer.sendMessage(message);
                sent = true;
                System.out.println("Сообщение отправлено через сервер");
            }
        } catch (Exception e) {
            System.err.println("Ошибка отправки: " + e.getMessage());
            e.printStackTrace();
        }

        if (sent) {
            System.out.println("Сообщение успешно отправлено");

            Platform.runLater(() -> {
                statusLabel.setText("✓ Вы готовы. Ожидаем противника...");
                statusLabel.setTextFill(Color.GREEN);

                // Обновляем кнопку
                Button readyButton = (Button) lookup("#readyButton");
                if (readyButton != null) {
                    readyButton.setText("✓ Готов");
                    readyButton.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white;");
                    readyButton.setDisable(true);
                }
            });

            // Проверяем готовность противника
            if (opponentReady) {
                System.out.println("Противник уже готов! Начинаем игру...");
                startGame("opponent");
            } else {
                System.out.println("Ждем готовности противника...");
            }

        } else {
            iAmReady = false;
            System.out.println("ОШИБКА: Не удалось отправить сообщение");
            showError("Не удалось отправить сигнал готовности. Проверьте соединение.");
        }

        System.out.println("=== ЗАВЕРШЕНИЕ ОТПРАВКИ СИГНАЛА ГОТОВНОСТИ ===");
    }

    private void sendAttack(int x, int y) {
        String message = "ATTACK:" + x + "," + y;
        if (gameClient != null && gameClient.isConnected()) {
            gameClient.sendMessage(message);
        } else if (gameServer != null && gameServer.isRunning()) {
            gameServer.sendMessage(message);
        }
    }

    private void hostGame() {
        try {
            System.out.println("Запуск сервера игры с встроенным чатом...");

            String localIP = getLocalIP();
            statusLabel.setText("Сервер запускается... IP: " + localIP);

            gameServer = new GameServer();
            gameServer.start(5555, new GameServer.GameMessageListener() {
                @Override
                public void onMessageReceived(String message) {
                    System.out.println("Сервер получил: " + message);
                    handleMessage(message);
                }

                @Override
                public void onClientConnected(String clientAddress) {
                    Platform.runLater(() -> {
                        System.out.println("Клиент подключен: " + clientAddress);
                        statusLabel.setText("Противник подключен: " + clientAddress);
                        playerLabel.setText("Хост (Ожидание готовности)");
                        connectionType = "host";

                        // Приветственное сообщение в чат
                        if (chatArea != null) {
                            chatArea.appendText("⚡ Противник подключился к игре\n");
                            chatArea.appendText("⚡ Начинайте общение в чате\n");
                        }

                        showInfo("Противник подключился! Расставьте корабли и нажмите 'Готов'.");
                    });
                }

                @Override
                public void onConnectionClosed() {
                    Platform.runLater(() -> {
                        statusLabel.setText("Соединение разорвано");
                        if (chatArea != null) {
                            chatArea.appendText("⚡ Противник отключился\n");
                        }
                        gameStarted = false;
                    });
                }
            });

            statusLabel.setText("Сервер запущен. Ожидаем подключения...");
            showInfo("Сервер запущен на порту 5555. Ваш IP: " + localIP + "\nСообщите этот IP другому игроку.");

            // Инициализируем чат для хоста
            initializeHostChat();

        } catch (Exception e) {
            showError("Ошибка запуска сервера: " + e.getMessage());
            System.err.println("Ошибка hostGame: " + e.getMessage());
        }
    }

    private void initializeHostChat() {
        if (chatArea != null) {
            chatArea.appendText("⚡ Вы - хост игры\n");
            chatArea.appendText("⚡ Ожидаем подключения противника\n");
            chatArea.appendText("⚡ Чат будет доступен после подключения\n");
        }
    }

    private void launchHostChat() {
        new Thread(() -> {
            try {
                Thread.sleep(1000); // Небольшая задержка

                System.out.println("🔄 Запуск чата для хоста...");

                // Запускаем сервер чата
                boolean serverStarted = chatManager.launchChatServer();

                if (serverStarted) {
                    // Ждем запуска сервера
                    Thread.sleep(3000);

                    // Запускаем клиент чата для хоста
                    chatManager.launchChatClient("Хост_Игрок");

                    Platform.runLater(() -> {
                        showInfo("✅ Чат запущен! Вы можете общаться с противником.");
                    });
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }


    private void launchClientChat() {
        new Thread(() -> {
            try {
                Thread.sleep(1000); // Небольшая задержка

                System.out.println("🔄 Запуск чата для клиента...");

                // Запускаем только клиент чата
                chatManager.launchChatClient("Клиент_Игрок");

                Platform.runLater(() -> {
                    showInfo("✅ Чат запущен! Подключитесь к localhost:12345");
                });

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private void openGameChat() {
        if (chatPanel != null) {
            // Просто фокусируемся на поле ввода
            chatInput.requestFocus();

            if (!chatInitialized) {
                initializeChat();
            }

            showInfo("Чат активен. Введите сообщение и нажмите Enter.");
        } else {
            showInfo("Чат доступен только в сетевой игре");
        }
    }


    private boolean isChatAvailableForUser() {
        // Чат доступен, если есть подключение к противнику
        if (gameMode.equals("host")) {
            return gameServer != null && gameServer.isRunning();
        } else if (gameMode.equals("client")) {
            return gameClient != null && gameClient.isConnected();
        }
        return false;
    }

    private void updateChatButtonState() {
        Platform.runLater(() -> {
            Button chatButton = (Button) lookup("#chatButton");
            if (chatButton != null) {
                boolean available = isChatAvailableForUser();

                if (available) {
                    chatButton.setDisable(false);

                    if (chatLaunched) {
                        chatButton.setStyle("-fx-background-color: #2ECC71; -fx-text-fill: white;");
                        chatButton.setText("💬 Чат (запущен)");
                    } else if (connectionType != null && connectionType.equals("host")) {
                        chatButton.setStyle("-fx-background-color: #F39C12; -fx-text-fill: white;");
                        chatButton.setText("💬 Запустить чат");
                    } else {
                        chatButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white;");
                        chatButton.setText("💬 Открыть чат");
                    }
                } else {
                    chatButton.setDisable(true);
                    chatButton.setStyle("-fx-background-color: #7F8C8D; -fx-text-fill: white;");
                    chatButton.setText("💬 Чат");
                }
            }
        });
    }

    private void connectToGame(String serverAddress) {
        try {
            System.out.println("Подключение к " + serverAddress + ":5555");

            gameClient = new GameClient();
            gameClient.connect(serverAddress, 5555, new GameClient.GameMessageListener() {
                @Override
                public void onMessageReceived(String message) {
                    System.out.println("Клиент получил: " + message);
                    handleMessage(message);
                }

                @Override
                public void onChatMessageReceived(String sender, String message) {
                    handleChatMessage(sender, message);
                }

                @Override
                public void onConnected() {
                    Platform.runLater(() -> {
                        System.out.println("Успешно подключено к серверу");
                        statusLabel.setText("Подключено к " + serverAddress);
                        playerLabel.setText("Клиент (Ожидание хода)");
                        connectionType = "client";

                        // Приветственное сообщение в чат
                        if (chatArea != null) {
                            chatArea.appendText("⚡ Подключено к игре\n");
                            chatArea.appendText("⚡ Вы - клиент\n");
                            chatArea.appendText("⚡ Можете общаться в чате\n");
                        }

                        showInfo("Успешно подключено! Расставьте корабли и нажмите 'Готов'.");
                    });
                }

                @Override
                public void onConnectionClosed() {
                    Platform.runLater(() -> {
                        statusLabel.setText("Соединение разорвано");
                        if (chatArea != null) {
                            chatArea.appendText("⚡ Соединение с сервером потеряно\n");
                        }
                        gameStarted = false;
                    });
                }
            });

        } catch (Exception e) {
            showError("Не удалось подключиться к " + serverAddress + ": " + e.getMessage());
            System.err.println("Ошибка подключения: " + e.getMessage());

            Platform.runLater(() -> {
                Alert retryAlert = new Alert(Alert.AlertType.CONFIRMATION);
                retryAlert.setTitle("Ошибка подключения");
                retryAlert.setHeaderText("Не удалось подключиться к серверу");
                retryAlert.setContentText("Хотите попробовать другой адрес?");

                Optional<ButtonType> retryResult = retryAlert.showAndWait();
                if (retryResult.isPresent() && retryResult.get() == ButtonType.OK) {
                    connectionDialogShown = false;
                    showConnectDialog();
                } else {
                    goBack();
                }
            });
        }
    }

    private void updateUIForConnectedState() {
        // Добавляем кнопку "Готов" после подключения
        HBox bottomPanel = (HBox) getBottom();
        if (bottomPanel != null) {
            Button readyButton = new Button("Готов к игре");
            readyButton.setOnAction(e -> {
                if (player.allShipsPlaced()) {
                    sendReadySignal();
                } else {
                    showInfo("Сначала расставьте все корабли!");
                }
            });

            VBox readyBox = new VBox(5, readyButton);
            bottomPanel.getChildren().add(readyBox);
        }
    }



    private void placeAllShipsAutomatically() {
        // Сбрасываем игрока
        player = new Player("Игрок");

        for (Ship ship : player.getShips()) {
            boolean placed = false;
            int attempts = 0;

            while (!placed && attempts < 100) {
                int x = (int) (Math.random() * 10);
                int y = (int) (Math.random() * 10);
                ShipDirection direction = Math.random() > 0.5 ? ShipDirection.HORIZONTAL : ShipDirection.VERTICAL;

                if (player.getBoard().canPlaceShip(ship, x, y, direction)) {
                    player.placeShip(ship, x, y, direction);
                    placed = true;
                }
                attempts++;
            }

            if (!placed) {
                showError("Не удалось разместить корабль автоматически");
                return;
            }
        }

        updatePlayerGrid();
        showInfo("Все корабли расставлены!");

        // Обновляем состояние кнопки "Готов"
        updateReadyButtonState();
    }

    private String getLocalIP() {
        try {
            return java.net.InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "неизвестен";
        }
    }

    private void handleMessage(String message) {
        Platform.runLater(() -> {
            try {
                System.out.println("Получено сообщение: " + message);

                if (message.startsWith("READY:")) {
                    handleReadyMessage(message);
                } else if (message.startsWith("ATTACK:")) {
                    handleAttackMessage(message);
                } else if (message.startsWith("RESULT:")) {
                    handleResultMessage(message);
                } else if (message.startsWith("WIN:")) {
                    handleWinMessage(message);
                } else if (message.startsWith("CHAT:")) {
                    // Обработка сообщений чата
                    String chatContent = message.substring(5);
                    String sender = "Противник";
                    String chatMessage = chatContent;

                    if (chatContent.contains(":")) {
                        int colonIndex = chatContent.indexOf(":");
                        sender = chatContent.substring(0, colonIndex);
                        chatMessage = chatContent.substring(colonIndex + 1);
                    }

                    handleChatMessage(sender, chatMessage);
                } else if (message.equals("READY")) {
                    handleReadyMessage("READY:unknown");
                }

            } catch (Exception e) {
                System.err.println("Ошибка обработки сообщения: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }

    private void handleReadyMessage(String message) {
        Platform.runLater(() -> {
            try {
                String[] parts = message.split(":");
                String opponentType = parts.length > 1 ? parts[1] : "unknown";

                opponentReady = true;
                System.out.println("Противник готов. Тип: " + opponentType);

                showInfo("Противник готов к игру!");

                // Автоматически активируем чат при готовности противника
                if (chatArea != null) {
                    chatArea.appendText("⚡ Противник готов к игре!\n");
                    chatArea.appendText("⚡ Теперь можете общаться в чате\n");
                    chatInput.requestFocus(); // Фокус на поле ввода
                }

                if (iAmReady) {
                    statusLabel.setText("Оба игрока готовы! Начинаем игру...");
                    statusLabel.setTextFill(Color.GREEN);

                    // Отправляем приветственное сообщение в чат
                    sendWelcomeChatMessage();

                    // Запускаем игру через 3 секунды
                    new Thread(() -> {
                        try {
                            Thread.sleep(3000);
                            Platform.runLater(() -> startGame(opponentType));
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }).start();
                } else {
                    statusLabel.setText("Противник готов. Вы еще не готовы.");
                    statusLabel.setTextFill(Color.ORANGE);
                }

            } catch (Exception e) {
                System.err.println("Ошибка обработки READY: " + e.getMessage());
            }
        });
    }

    private void startGame(String opponentType) {
        if (!gameStarted) {
            gameStarted = true;
            System.out.println("Игра началась! Противник: " + opponentType);

            // Определяем, кто ходит первым
            if (connectionType != null && opponentType != null) {
                if (connectionType.equals("host") && opponentType.equals("client")) {
                    isMyTurn = true; // Хост ходит первым
                } else if (connectionType.equals("client") && opponentType.equals("host")) {
                    isMyTurn = false; // Клиент ходит вторым
                } else {
                    // Случайный выбор
                    isMyTurn = Math.random() > 0.5;
                }
            } else {
                isMyTurn = Math.random() > 0.5;
            }

            if (isMyTurn) {
                statusLabel.setText("Игра началась! Ваш ход.");
                statusLabel.setTextFill(Color.LIGHTGREEN);
                setEnemyFieldEnabled(true);
                showAlert("Игра началась!", "Ваш ход! Атакуйте поле противника.");
            } else {
                statusLabel.setText("Игра началась! Ход противника.");
                statusLabel.setTextFill(Color.ORANGE);
                setEnemyFieldEnabled(false);
                showAlert("Игра началась!", "Ход противника. Ожидайте своей очереди.");
            }
        }
    }

    private void setEnemyFieldEnabled(boolean enabled) {
        if (enemyGrid != null) {
            for (javafx.scene.Node node : enemyGrid.getChildren()) {
                node.setDisable(!enabled);
                if (enabled) {
                    node.setOpacity(1.0);
                    node.setCursor(javafx.scene.Cursor.HAND);
                } else {
                    node.setOpacity(0.7);
                    node.setCursor(javafx.scene.Cursor.DEFAULT);
                }
            }
        }
    }

    private void sendWelcomeChatMessage() {
        String welcomeMessage = "Привет! Готов играть!";
        if (gameClient != null && gameClient.isConnected()) {
            gameClient.sendChatMessage(welcomeMessage);
        } else if (gameServer != null && gameServer.isRunning()) {
            gameServer.sendMessage("CHAT:Хост:" + welcomeMessage);
        }
    }

    private void debugGameState() {
        System.out.println("=== СОСТОЯНИЕ ИГРЫ ===");
        System.out.println("gameStarted: " + gameStarted);
        System.out.println("isMyTurn: " + isMyTurn);
        System.out.println("player.allShipsSunk(): " + (player != null ? player.allShipsSunk() : "player is null"));
        System.out.println("enemy.allShipsSunk(): " + (enemy != null ? enemy.allShipsSunk() : "enemy is null"));
        System.out.println("=== КОНЕЦ СОСТОЯНИЯ ===");
    }

    private void handleAttackMessage(String message) {
        try {
            String[] parts = message.substring(7).split(",");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);

            System.out.println("[GameController] Атака противника: " + x + "," + y);

            GameBoard.CellState result = player.attack(x, y);

            // Отправляем результат
            String response = "RESULT:" + x + "," + y + "," + result;
            if (gameClient != null && gameClient.isConnected()) {
                gameClient.sendMessage(response);
            } else if (gameServer != null && gameServer.isRunning()) {
                gameServer.sendMessage(response);
            }

            updatePlayerGrid();

            // Проверяем, не потопил ли противник все наши корабли
            if (player.allShipsSunk()) {
                // Мы проиграли
                System.out.println("[GameController] ПОРАЖЕНИЕ! Все наши корабли потоплены");

                Platform.runLater(() -> {
                    statusLabel.setText("ВЫ ПРОИГРАЛИ!");
                    statusLabel.setTextFill(Color.RED);
                    gameStarted = false;
                    setEnemyFieldEnabled(false);

                    // Отправляем сообщение о победе противнику
                    String winMessage = "WIN:you";
                    if (gameClient != null) gameClient.sendMessage(winMessage);
                    if (gameServer != null) gameServer.sendMessage(winMessage);

                    showAlert("Игра окончена", "Вы проиграли! Все ваши корабли потоплены.");
                });
            } else {
                // Теперь наш ход
                isMyTurn = true;
                statusLabel.setText("Ваш ход");
                statusLabel.setTextFill(Color.LIGHTGREEN);
                setEnemyFieldEnabled(true);
            }

        } catch (Exception e) {
            System.err.println("[GameController] Ошибка обработки атаки: " + e.getMessage());
        }
    }

    private void handleResultMessage(String message) {
        try {
            String[] parts = message.substring(7).split(",");
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            String result = parts[2];

            Rectangle cell = (Rectangle) getNodeFromGridPane(enemyGrid, x, y);
            if (cell != null) {
                if (result.equals("HIT") || result.equals("SUNK")) {
                    cell.setFill(Color.RED);
                    enemyHits++;

                    System.out.println("[GameController] Попадание! Всего попаданий: " + enemyHits + "/" + totalEnemyCells);

                    // Проверяем победу
                    if (enemyHits >= totalEnemyCells) {
                        handleVictory();
                        return;
                    }

                    // Дополнительный ход при попадании
                    isMyTurn = true;
                    statusLabel.setText("Вы попали! Стреляйте снова");
                    statusLabel.setTextFill(Color.LIGHTGREEN);
                    setEnemyFieldEnabled(true);

                } else if (result.equals("MISS")) {
                    cell.setFill(Color.WHITE);

                    // Промах - ход противника
                    isMyTurn = false;
                    statusLabel.setText("Ход противника");
                    statusLabel.setTextFill(Color.ORANGE);
                    setEnemyFieldEnabled(false);
                }
            }

        } catch (Exception e) {
            System.err.println("[GameController] Ошибка: " + e.getMessage());
        }
    }

    private void handleVictory() {
        System.out.println("[GameController] ПОБЕДА! Все корабли противника потоплены!");

        Platform.runLater(() -> {
            statusLabel.setText("ВЫ ВЫИГРАЛИ!");
            statusLabel.setTextFill(Color.GREEN);
            gameStarted = false;
            setEnemyFieldEnabled(false);

            // Отправляем сообщение о победе
            String winMessage = "WIN:you";
            if (gameClient != null && gameClient.isConnected()) {
                gameClient.sendMessage(winMessage);
            } else if (gameServer != null && gameServer.isRunning()) {
                gameServer.sendMessage(winMessage);
            }

            showAlert("Поздравляем!", "Вы выиграли! Все корабли противника потоплены.");
        });
    }



    private void handleChatMessage(String message) {
        String chatMessage = message.substring(5);
        showInfo("Противник: " + chatMessage);
    }

    private void handleWinMessage(String message) {
        System.out.println("[GameController] Получено сообщение о победе: " + message);

        Platform.runLater(() -> {
            if (message.equals("WIN:you")) {
                statusLabel.setText("ВЫ ПРОИГРАЛИ!");
                statusLabel.setTextFill(Color.RED);
                gameStarted = false;
                setEnemyFieldEnabled(false);
                showAlert("Игра окончена", "Вы проиграли! Все ваши корабли потоплены.");
            }
        });
    }

    private void handleCheckWinMessage() {
        // Проверяем, все ли корабли противника потоплены
        // В реальной игре нужно отслеживать попадания по противнику
        // Здесь упрощенная логика

        Platform.runLater(() -> {
            // Отправляем текущее состояние
            String stateMessage = "GAME_STATE:playing";
            if (gameClient != null) gameClient.sendMessage(stateMessage);
            if (gameServer != null) gameServer.sendMessage(stateMessage);
        });
    }



    private void showInfo(String message) {
        System.out.println("INFO: " + message);
        // Можно добавить отображение в чат или статус-бар
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

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void goBack() {
        // Сохраняем ссылки на приложение до закрытия соединений
        HelloApplication app = HelloApplication.getInstance();

        if (chatArea != null) {
            chatArea.clear();
            chatInitialized = false;
        }
        // Закрываем соединения в правильном порядке
        try {
            if (gameClient != null) {
                gameClient.disconnect();
                gameClient = null;
            }

            if (gameServer != null) {
                gameServer.stop();
                gameServer = null;
            }
        } catch (Exception e) {
            System.err.println("Ошибка при закрытии соединений: " + e.getMessage());
            // Не прерываем выход из-за ошибки закрытия
        }

        // Делаем небольшую паузу для завершения операций закрытия
        new Thread(() -> {
            try {
                Thread.sleep(100); // 100ms пауза
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            Platform.runLater(() -> {
                if (app != null && app.getNavigator() != null) {
                    app.getNavigator().navigateBack();
                }
            });
        }).start();
    }

    private void restartGame() {
        System.out.println("[GameController] Перезапуск игры...");

        if (chatArea != null) {
            chatArea.clear();
            chatInitialized = false;
            initializeChat();
        }
        chatLaunched = false;

        // Сбрасываем игроков
        player = new Player("Игрок");
        enemy = new Player("Противник");

        // Сбрасываем состояние игры
        isMyTurn = false;
        gameStarted = false;
        iAmReady = false;
        opponentReady = false;
        enemyHits = 0;

        // Перерасставляем корабли
        placeAllShipsAutomatically();

        // Обновляем UI
        updatePlayerGrid();
        updateEnemyGrid();

        statusLabel.setText("Расставьте корабли");
        statusLabel.setTextFill(Color.WHITE);
        setEnemyFieldEnabled(false);

        // Обновляем кнопку "Готов"
        updateReadyButtonState();

        System.out.println("[GameController] Игра перезапущена");
    }

    private void updateGameStatus() {
        Platform.runLater(() -> {
            if (!gameStarted) {
                if (iAmReady && !opponentReady) {
                    statusLabel.setText("Вы готовы. Ожидаем противника...");
                    statusLabel.setTextFill(Color.YELLOW);
                } else if (!iAmReady && opponentReady) {
                    statusLabel.setText("Противник готов. Расставьте корабли!");
                    statusLabel.setTextFill(Color.ORANGE);
                } else if (iAmReady && opponentReady) {
                    statusLabel.setText("Оба готовы. Начинаем игру...");
                    statusLabel.setTextFill(Color.GREEN);
                } else {
                    statusLabel.setText("Расставьте корабли");
                    statusLabel.setTextFill(Color.WHITE);
                }
            } else {
                if (isMyTurn) {
                    statusLabel.setText("Ваш ход");
                    statusLabel.setTextFill(Color.LIGHTGREEN);
                } else {
                    statusLabel.setText("Ход противника");
                    statusLabel.setTextFill(Color.ORANGE);
                }
            }
        });
    }
}