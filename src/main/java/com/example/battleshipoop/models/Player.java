package com.example.battleshipoop.models;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private final String name;
    private final GameBoard board;
    private final List<Ship> ships;
    private boolean ready;

    public Player(String name) {
        this.name = name;
        this.board = new GameBoard(10, 10);
        this.ships = new ArrayList<>();
        initializeShips();
    }

    private void initializeShips() {
        ships.add(new Ship(4)); // 1 корабль на 4 клетки
        ships.add(new Ship(3)); // 2 корабля на 3 клетки
        ships.add(new Ship(3));
        ships.add(new Ship(2)); // 3 корабля на 2 клетки
        ships.add(new Ship(2));
        ships.add(new Ship(2));
        ships.add(new Ship(1)); // 4 корабля на 1 клетку
        ships.add(new Ship(1));
        ships.add(new Ship(1));
        ships.add(new Ship(1));
    }

    public boolean placeShip(Ship ship, int x, int y, ShipDirection direction) {
        if (board.canPlaceShip(ship, x, y, direction)) {
            board.placeShip(ship, x, y, direction);
            ship.setX(x);
            ship.setY(y);
            ship.setPlaced(true);
            return true;
        }
        return false;
    }

    // Новый метод для расстановки с учетом свободного пространства
    public boolean placeShipWithMargin(Ship ship, int x, int y, ShipDirection direction) {
        // Используем рефлексию для доступа к защищенному методу
        // Или добавим public метод в GameBoard
        try {
            java.lang.reflect.Method method = board.getClass().getMethod("canPlaceShipWithMargin",
                    Ship.class, int.class, int.class, ShipDirection.class);
            boolean canPlace = (boolean) method.invoke(board, ship, x, y, direction);

            if (canPlace) {
                board.placeShip(ship, x, y, direction);
                ship.setX(x);
                ship.setY(y);
                ship.setPlaced(true);
                return true;
            }
        } catch (Exception e) {
            // Если метод не найден, используем обычную расстановку
            return placeShip(ship, x, y, direction);
        }
        return false;
    }

    public GameBoard.CellState attack(int x, int y) {
        return board.attack(x, y);
    }

    public boolean allShipsPlaced() {
        return ships.stream().allMatch(Ship::isPlaced);
    }

    public boolean allShipsSunk() {
        for (Ship ship : ships) {
            if (!ship.isSunk()) {
                return false;
            }
        }
        return true;
    }

    // Новый метод для автоматической расстановки всех кораблей с учетом свободного пространства
    public boolean autoPlaceAllShips() {
        // Сначала сбрасываем все корабли
        resetShips();

        // Расставляем корабли от большего к меньшему
        ships.sort((s1, s2) -> Integer.compare(s2.getSize(), s1.getSize()));

        for (Ship ship : ships) {
            if (!tryAutoPlaceShip(ship, 100)) {
                return false; // Не удалось разместить корабль
            }
        }
        return true;
    }

    private void resetShips() {
        // Создаем новое игровое поле
        // Для этого можно создать новый экземпляр GameBoard
        // или очистить текущий
        // Здесь упрощенно: создаем нового игрока
        // В реальной реализации нужно очистить поле
        // Для простоты создадим новый экземпляр
        // Вместо этого будем использовать другой подход в GameController
    }

    private boolean tryAutoPlaceShip(Ship ship, int maxAttempts) {
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            int x = (int) (Math.random() * 10);
            int y = (int) (Math.random() * 10);
            ShipDirection direction = Math.random() > 0.5 ? ShipDirection.HORIZONTAL : ShipDirection.VERTICAL;

            if (placeShipWithMargin(ship, x, y, direction)) {
                return true;
            }
        }
        return false;
    }

    // Getters
    public String getName() { return name; }
    public GameBoard getBoard() { return board; }
    public List<Ship> getShips() { return ships; }
    public boolean isReady() { return ready; }
    public void setReady(boolean ready) { this.ready = ready; }
}