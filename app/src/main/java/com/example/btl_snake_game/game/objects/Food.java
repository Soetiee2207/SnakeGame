package com.example.btl_snake_game.game.objects;

import com.example.btl_snake_game.util.Vector2D;
import java.util.Random;

public class Food {
    private Vector2D position;
    private Random random;
    private int gridWidth;
    private int gridHeight;
    private int cellSize;

    public Food(int gridWidth, int gridHeight, int cellSize) {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.cellSize = cellSize;
        this.random = new Random();
        spawn();
    }

    public void spawn() {
        int x = random.nextInt(gridWidth) * cellSize;
        int y = random.nextInt(gridHeight) * cellSize;
        position = new Vector2D(x, y);
    }

    public void spawn(Vector2D newPosition) {
        this.position = newPosition;
    }

    public Vector2D getPosition() {
        return position;
    }

    public boolean isEaten(Vector2D snakeHeadPos) {
        return position.equals(snakeHeadPos);
    }
}