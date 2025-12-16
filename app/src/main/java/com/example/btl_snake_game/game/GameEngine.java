package com.example.btl_snake_game.game;

import com.example.btl_snake_game.game.objects.Food;
import com.example.btl_snake_game.game.objects.Snake;
import com.example.btl_snake_game.util.SettingManager;
import com.example.btl_snake_game.util.Vector2D;

public class GameEngine {
    private Snake snake;
    private Food food;
    private GameState state;
    private int score;
    private int gridWidth;
    private int gridHeight;
    private int cellSize;
    private SettingManager settingManager;

    public GameEngine(int gridWidth, int gridHeight, int cellSize) {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.cellSize = cellSize;
        this.settingManager = SettingManager.getInstance();
        initGame();
    }

    public void initGame() {
        int startX = (gridWidth / 2) * cellSize;
        int startY = (gridHeight / 2) * cellSize;
        snake = new Snake(startX, startY, cellSize);
        food = new Food(gridWidth, gridHeight, cellSize);
        score = 0;
        state = GameState.MENU;
    }

    public void update() {
        if (state != GameState.PLAYING) {
            return;
        }

        snake.move();

        // Kiểm tra ăn thức ăn
        if (food.isEaten(snake.getHead())) {
            snake.grow();
            score += 10;
            food.spawn();
            // Đảm bảo thức ăn không spawn trên thân rắn
            while (isOnSnake(food.getPosition())) {
                food.spawn();
            }
        }

        // Kiểm tra va chạm
        if (snake.checkWallCollision(gridWidth, gridHeight) ||
                snake.checkSelfCollision()) {
            state = GameState.GAME_OVER;
            saveHighScore();
        }
    }

    private boolean isOnSnake(Vector2D position) {
        for (Vector2D segment : snake.getBody()) {
            if (segment.equals(position)) {
                return true;
            }
        }
        return false;
    }

    public void changeDirection(Vector2D direction) {
        if (state == GameState.PLAYING) {
            snake.setDirection(direction);
        }
    }

    public void pause() {
        if (state == GameState.PLAYING) {
            state = GameState.PAUSED;
        } else if (state == GameState.PAUSED) {
            state = GameState.PLAYING;
        }
    }

    public void startGame() {
        if (state == GameState.MENU || state == GameState.GAME_OVER) {
            initGame();
            state = GameState.PLAYING;
        }
    }

    private void saveHighScore() {
        int highScore = settingManager.getHighScore();
        if (score > highScore) {
            settingManager.saveHighScore(score);
        }
    }

    // Getters
    public Snake getSnake() {
        return snake;
    }

    public Food getFood() {
        return food;
    }

    public GameState getState() {
        return state;
    }

    public int getScore() {
        return score;
    }

    public int getGridWidth() {
        return gridWidth;
    }

    public int getGridHeight() {
        return gridHeight;
    }

    public int getCellSize() {
        return cellSize;
    }
}