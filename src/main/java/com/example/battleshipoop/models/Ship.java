package com.example.battleshipoop.models;

public class Ship {
    private final int size;
    private final boolean[] hits;
    private final ShipDirection direction;
    private int x, y;
    private boolean placed;

    public Ship(int size) {
        this.size = size;
        this.hits = new boolean[size];
        this.direction = ShipDirection.HORIZONTAL;
    }

    public boolean isSunk() {
        for (boolean hit : hits) {
            if (!hit) {
                return false;
            }
        }
        return true;
    }

    public void hit(int position) {
        if (position >= 0 && position < size) {
            hits[position] = true;
        }
    }

    public int getSize() { return size; }
    public ShipDirection getDirection() { return direction; }
    public void setDirection(ShipDirection direction) { }
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public boolean isPlaced() { return placed; }
    public void setPlaced(boolean placed) { this.placed = placed; }
}