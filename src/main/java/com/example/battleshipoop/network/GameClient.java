package com.example.battleshipoop.network;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class GameClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private ExecutorService executor;
    private GameMessageListener listener;
    private String username;

    public interface GameMessageListener {
        void onMessageReceived(String message);
        void onConnected();
        void onConnectionClosed();
        void onChatMessageReceived(String sender, String message); // Новый метод для чата
    }

    public void connect(String serverAddress, int port, GameMessageListener listener) throws IOException {
        this.listener = listener;

        try {
            socket = new Socket(serverAddress, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            if (listener != null) {
                listener.onConnected();
            }

            executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                try {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        // Проверяем, является ли сообщение чатом
                        if (inputLine.startsWith("CHAT:")) {
                            handleChatMessage(inputLine);
                        } else if (listener != null) {
                            listener.onMessageReceived(inputLine);
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Ошибка клиента: " + e.getMessage());
                } finally {
                    disconnect();
                }
            });
        } catch (IOException e) {
            System.err.println("Не удалось подключиться к серверу: " + e.getMessage());
            throw e;
        }
    }

    private void handleChatMessage(String message) {
        String chatContent = message.substring(5);

        // Разделяем отправителя и сообщение
        String sender = "Система";
        String chatMessage = chatContent;

        if (chatContent.contains(":")) {
            int colonIndex = chatContent.indexOf(":");
            sender = chatContent.substring(0, colonIndex);
            chatMessage = chatContent.substring(colonIndex + 1);
        }

        // Если это сообщение от вас самих, пропускаем его
        if (sender.equals("Вы")) {
            return; // Не добавляем дубль
        }

        if (listener != null) {
            listener.onChatMessageReceived(sender, chatMessage);
        }
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    public void sendChatMessage(String message) {
        if (out != null) {
            out.println("CHAT:" + message);
        }
    }

    public void disconnect() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
            if (executor != null) executor.shutdown();

            if (listener != null) {
                listener.onConnectionClosed();
            }
        } catch (IOException e) {
            System.err.println("Ошибка при отключении: " + e.getMessage());
        }
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}