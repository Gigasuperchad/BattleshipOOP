package com.example.battleshipoop.network;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class GameServer {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private ExecutorService executor;
    private GameMessageListener listener;
    private volatile boolean clientConnected = false;

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

            executor = Executors.newSingleThreadExecutor();
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

                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        if (listener != null) {
                            listener.onMessageReceived(inputLine);
                        }
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
}