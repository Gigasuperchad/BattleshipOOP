package com.example.battleshipoop.app;

import javafx.application.Application;

public class Launcher {
    public static void main(String[] args) {
        System.out.println("Запуск игры Морской бой...");
        System.out.println("Версия: " + AppInfo.VERSION);
        Application.launch(HelloApplication.class, args);
    }
}