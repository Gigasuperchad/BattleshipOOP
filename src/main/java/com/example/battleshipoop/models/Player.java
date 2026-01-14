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

    // Getters
    public String getName() { return name; }
    public GameBoard getBoard() { return board; }
    public List<Ship> getShips() { return ships; }
    public boolean isReady() { return ready; }
    public void setReady(boolean ready) { this.ready = ready; }
}