package com.example.btl_snake_game.ui;

import android.view.GestureDetector;
import android.view.MotionEvent;
import com.example.btl_snake_game.game.GameEngine;
import com.example.btl_snake_game.game.GameState;
import com.example.btl_snake_game.util.Vector2D;

public class InputHandler extends GestureDetector.SimpleOnGestureListener {
    private GameEngine gameEngine;
    private int cellSize;
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;

    public InputHandler(GameEngine gameEngine, int cellSize) {
        this.gameEngine = gameEngine;
        this.cellSize = cellSize;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        GameState state = gameEngine.getState();
        if (state == GameState.MENU) {
            gameEngine.startGame();
            return true;
        }
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2,
                           float velocityX, float velocityY) {
        if (gameEngine.getState() != GameState.PLAYING) {
            return false;
        }

        float diffX = e2.getX() - e1.getX();
        float diffY = e2.getY() - e1.getY();

        if (Math.abs(diffX) > Math.abs(diffY)) {
            // Vuốt ngang
            if (Math.abs(diffX) > SWIPE_THRESHOLD &&
                    Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX > 0) {
                    // Vuốt phải
                    gameEngine.changeDirection(new Vector2D(cellSize, 0));
                } else {
                    // Vuốt trái
                    gameEngine.changeDirection(new Vector2D(-cellSize, 0));
                }
                return true;
            }
        } else {
            // Vuốt dọc
            if (Math.abs(diffY) > SWIPE_THRESHOLD &&
                    Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffY > 0) {
                    // Vuốt xuống
                    gameEngine.changeDirection(new Vector2D(0, cellSize));
                } else {
                    // Vuốt lên
                    gameEngine.changeDirection(new Vector2D(0, -cellSize));
                }
                return true;
            }
        }
        return false;
    }
}