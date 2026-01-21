package com.example.battleshipoop.network;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class GameServer {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private ExecutorService executor;
    private GameMessageListener listener;

    // Добавляем систему чата
    private List<ClientHandler> clients = new ArrayList<>();
    private List<String> chatMessages = new ArrayList<>();
    private Map<String, String> usernames = new HashMap<>(); // clientAddress -> username

    public interface GameMessageListener {
        void onMessageReceived(String message);
        void onClientConnected(String clientAddress);
        void onConnectionClosed();
    }

    public void start(int port, GameMessageListener listener) throws IOException {
        this.listener = listener;

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Сервер запущен на порту " + port);
            System.out.println("✅ Встроенный чат активирован");

            executor = Executors.newCachedThreadPool();

            // Основной поток для принятия подключений
            executor.execute(() -> {
                try {
                    System.out.println("Сервер ожидает подключения...");
                    clientSocket = serverSocket.accept();
                    String clientAddress = clientSocket.getInetAddress().getHostAddress();
                    System.out.println("Клиент подключен: " + clientAddress);

                    if (listener != null) {
                        listener.onClientConnected(clientAddress);
                    }

                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                    // Регистрируем клиента в чате
                    registerClient(clientAddress, out);

                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        if (listener != null) {
                            listener.onMessageReceived(inputLine);
                        }

                        // Обработка сообщений чата
                        handleChatMessage(clientAddress, inputLine);
                    }

                } catch (IOException e) {
                    System.out.println("Ошибка сервера: " + e.getMessage());
                } finally {
                    stop();
                }
            });

        } catch (IOException e) {
            System.err.println("Не удалось запустить сервер: " + e.getMessage());
            throw e;
        }
    }

    private void registerClient(String clientAddress, PrintWriter writer) {
        ClientHandler client = new ClientHandler(clientAddress, writer);
        clients.add(client);

        // Автоматически задаем имя пользователя
        String username = "Игрок_" + (clients.size());
        usernames.put(clientAddress, username);

        System.out.println("✅ Зарегистрирован в чате: " + username + " (" + clientAddress + ")");

        // Отправляем приветственное сообщение
        writer.println("CHAT:Система:Добро пожаловать в чат, " + username + "!");
        writer.println("CHAT:Система:Теперь вы можете общаться с противником.");

        // Отправляем историю чата новому пользователю
        sendChatHistory(clientAddress);
    }

    private void handleChatMessage(String clientAddress, String message) {
        if (message.startsWith("CHATMSG:")) {
            // Это уже форматированное сообщение
            String chatContent = message.substring(8);
            System.out.println("Сервер рассылает CHATMSG: " + chatContent);

            // Рассылаем всем клиентам
            broadcastMessage("CHATMSG:" + chatContent);

            // И отправляем хозяину сервера (хосту) тоже
            if (listener != null) {
                System.out.println("Отправляем хосту: CHATMSG:" + chatContent);
                listener.onMessageReceived("CHATMSG:" + chatContent);
            }

        } else if (message.startsWith("CHAT:")) {
            // Это сырое сообщение (устаревший формат)
            String chatMessage = message.substring(5);
            String username = usernames.getOrDefault(clientAddress, "Игрок_1");

            System.out.println("Сервер: сырое сообщение от " + username + ": " + chatMessage);

            // Преобразуем в формат CHATMSG
            String formattedMessage = username + ":" + chatMessage;

            // Сохраняем в историю
            synchronized (chatMessages) {
                chatMessages.add(formattedMessage);
            }

            // Рассылаем всем клиентам
            broadcastMessage("CHATMSG:" + formattedMessage);

            // И отправляем хозяину сервера (хосту)
            if (listener != null) {
                System.out.println("Отправляем хосту: CHATMSG:" + formattedMessage);
                listener.onMessageReceived("CHATMSG:" + formattedMessage);
            }
        }
    }

    // Добавьте метод для рассылки сообщений всем клиентам
    private void broadcastMessage(String message) {
        System.out.println("Броадкаст сообщения: " + message + " для " + clients.size() + " клиентов");

        if (clients.isEmpty()) {
            System.out.println("Нет подключенных клиентов для рассылки");
            return;
        }

        List<ClientHandler> clientsCopy = new ArrayList<>(clients);
        for (ClientHandler client : clientsCopy) {
            try {
                System.out.println("Отправка клиенту " + client.address + ": " + message);
                client.writer.println(message);
                client.writer.flush();
            } catch (Exception e) {
                System.err.println("Ошибка отправки клиенту " + client.address + ": " + e.getMessage());
                clients.remove(client);
            }
        }
    }

    private void sendChatHistory(String clientAddress) {
        ClientHandler client = findClient(clientAddress);
        if (client != null) {
            // Отправляем последние 20 сообщений
            int start = Math.max(0, chatMessages.size() - 20);
            for (int i = start; i < chatMessages.size(); i++) {
                String message = chatMessages.get(i);
                int colonIndex = message.indexOf(":");
                String sender = message.substring(0, colonIndex);
                String text = message.substring(colonIndex + 1);
                client.writer.println("CHAT:История:" + sender + ": " + text);
            }
        }
    }

    public void sendChatMessage(String message) {
        System.out.println("Сервер: отправка сообщения от хоста: " + message);

        String formattedMessage = "Хост:" + message;

        // Сохраняем в историю
        synchronized (chatMessages) {
            chatMessages.add(formattedMessage);
        }

        // Рассылаем всем подключенным клиентам
        if (!clients.isEmpty()) {
            broadcastMessage("CHAT:" + formattedMessage);
            System.out.println("Сообщение отправлено " + clients.size() + " клиентам");
        } else {
            System.out.println("Нет подключенных клиентов для отправки сообщения");
        }

        // Также отправляем сообщение самому хосту через listener
        if (listener != null) {
            listener.onMessageReceived("CHAT:" + formattedMessage);
        }
    }



    private ClientHandler findClient(String clientAddress) {
        for (ClientHandler client : clients) {
            if (client.address.equals(clientAddress)) {
                return client;
            }
        }
        return null;
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    public void stop() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null) clientSocket.close();
            if (serverSocket != null) serverSocket.close();
            if (executor != null) executor.shutdown();

            // Очищаем клиентов чата
            clients.clear();
            chatMessages.clear();
            usernames.clear();

            if (listener != null) {
                listener.onConnectionClosed();
            }
        } catch (IOException e) {
            System.err.println("Ошибка при остановке сервера: " + e.getMessage());
        }
    }

    public boolean isRunning() {
        return serverSocket != null && !serverSocket.isClosed();
    }

    public boolean isClientConnected() {
        return clientSocket != null && clientSocket.isConnected() && !clientSocket.isClosed();
    }

    // Внутренний класс для хранения информации о клиентах чата
    private static class ClientHandler {
        String address;
        PrintWriter writer;

        ClientHandler(String address, PrintWriter writer) {
            this.address = address;
            this.writer = writer;
        }
    }
}