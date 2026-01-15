package com.example.battleshipoop.app;

public class ChatManager {
    // Теперь это просто заглушка, так как чат встроен в игровой сервер
    public ChatManager() {
        System.out.println("✅ Встроенный чат активирован через игровой сервер");
        System.out.println("ℹ️ Чат работает автоматически, не требует отдельного запуска");
    }

    public boolean launchChatServer() {
        System.out.println("ℹ️ Сервер чата не требуется - используется игровой сервер");
        return true; // Всегда возвращаем true
    }

    public boolean launchChatClient(String username) {
        System.out.println("ℹ️ Клиент чата не требуется - чат встроен в игровое окно");
        return true; // Всегда возвращаем true
    }

    public void stopAll() {
        System.out.println("ChatManager: Остановлен");
    }

    public boolean isChatLaunched() {
        return true; // Всегда запущен, так как встроен
    }
}