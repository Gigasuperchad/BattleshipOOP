package com.example.battleshipoop.models;

public class GameBoard {
    private final int width;
    private final int height;
    private final CellState[][] cells;

    public enum CellState {
        EMPTY,
        SHIP,
        HIT,
        MISS,
        SUNK
    }

    public GameBoard(int width, int height) {
        this.width = width;
        this.height = height;
        this.cells = new CellState[height][width];
        initializeBoard();
    }

    private void initializeBoard() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                cells[y][x] = CellState.EMPTY;
            }
        }
    }

    public boolean canPlaceShip(Ship ship, int x, int y, ShipDirection direction) {
        int size = ship.getSize();

        // Проверяем, влезает ли корабль в поле
        if (direction == ShipDirection.HORIZONTAL) {
            if (x + size > width) return false;
            // Проверяем все клетки корабля и область вокруг них
            for (int i = -1; i <= size; i++) {
                for (int j = -1; j <= 1; j++) {
                    int checkX = x + i;
                    int checkY = y + j;
                    if (checkX >= 0 && checkX < width && checkY >= 0 && checkY < height) {
                        if (cells[checkY][checkX] == CellState.SHIP) {
                            return false;
                        }
                    }
                }
            }
        } else { // VERTICAL
            if (y + size > height) return false;
            // Проверяем все клетки корабля и область вокруг них
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= size; j++) {
                    int checkX = x + i;
                    int checkY = y + j;
                    if (checkX >= 0 && checkX < width && checkY >= 0 && checkY < height) {
                        if (cells[checkY][checkX] == CellState.SHIP) {
                            return false;
                        }
                    }
                }
            }
        }

        // Проверяем только клетки самого корабля (основная проверка)
        if (direction == ShipDirection.HORIZONTAL) {
            for (int i = 0; i < size; i++) {
                if (!isEmpty(x + i, y)) return false;
            }
        } else {
            for (int i = 0; i < size; i++) {
                if (!isEmpty(x, y + i)) return false;
            }
        }
        return true;
    }

    // Новый метод для проверки места с учетом свободного пространства
    public boolean canPlaceShipWithMargin(Ship ship, int x, int y, ShipDirection direction) {
        int size = ship.getSize();

        // Проверяем, влезает ли корабль в поле
        if (direction == ShipDirection.HORIZONTAL) {
            if (x + size > width) return false;

            // Проверяем клетки корабля
            for (int i = 0; i < size; i++) {
                if (!isEmpty(x + i, y)) {
                    return false;
                }
            }

            // Проверяем область вокруг корабля (буферная зона)
            for (int i = -1; i <= size; i++) {
                for (int j = -1; j <= 1; j++) {
                    int checkX = x + i;
                    int checkY = y + j;

                    // Проверяем только в пределах поля
                    if (checkX >= 0 && checkX < width && checkY >= 0 && checkY < height) {
                        // Клетки самого корабля уже проверены, проверяем только соседние
                        if (j != 0 || i < 0 || i >= size) {
                            if (cells[checkY][checkX] == CellState.SHIP) {
                                return false;
                            }
                        }
                    }
                }
            }

        } else { // VERTICAL
            if (y + size > height) return false;

            // Проверяем клетки корабля
            for (int i = 0; i < size; i++) {
                if (!isEmpty(x, y + i)) {
                    return false;
                }
            }

            // Проверяем область вокруг корабля (буферная зона)
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= size; j++) {
                    int checkX = x + i;
                    int checkY = y + j;

                    // Проверяем только в пределах поля
                    if (checkX >= 0 && checkX < width && checkY >= 0 && checkY < height) {
                        // Клетки самого корабля уже проверены, проверяем только соседние
                        if (i != 0 || j < 0 || j >= size) {
                            if (cells[checkY][checkX] == CellState.SHIP) {
                                return false;
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    public void placeShip(Ship ship, int x, int y, ShipDirection direction) {
        int size = ship.getSize();

        if (direction == ShipDirection.HORIZONTAL) {
            for (int i = 0; i < size; i++) {
                cells[y][x + i] = CellState.SHIP;
            }
        } else {
            for (int i = 0; i < size; i++) {
                cells[y + i][x] = CellState.SHIP;
            }
        }
    }

    public CellState attack(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return CellState.MISS;
        }

        if (cells[y][x] == CellState.SHIP) {
            cells[y][x] = CellState.HIT;

            // Пока просто возвращаем HIT
            return CellState.HIT;
        } else if (cells[y][x] == CellState.EMPTY) {
            cells[y][x] = CellState.MISS;
            return CellState.MISS;
        }

        return cells[y][x];
    }

    private boolean isEmpty(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return false;
        }
        return cells[y][x] == CellState.EMPTY;
    }

    // Getters
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public CellState getCell(int x, int y) { return cells[y][x]; }
}