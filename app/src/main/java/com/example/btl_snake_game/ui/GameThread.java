package com.example.btl_snake_game.ui;

import com.example.btl_snake_game.game.GameEngine;

public class GameThread extends Thread {
    private GameEngine gameEngine;
    private GameView gameView;
    private boolean running;
    private int currentFPS = 5; // Tốc độ ban đầu
    private static final int MAX_FPS = 12; // Tốc độ tối đa
    private long frameTime;

    public GameThread(GameEngine gameEngine, GameView gameView) {
        this.gameEngine = gameEngine;
        this.gameView = gameView;
        this.running = false;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        while (running) {
            long startTime = System.currentTimeMillis();

            // Cập nhật game
            gameEngine.update();

            // Tăng tốc độ theo điểm số (mỗi 50 điểm tăng 1 FPS)
            int score = gameEngine.getScore();
            currentFPS = Math.min(5 + score / 50, MAX_FPS);
            frameTime = 1000 / currentFPS;

            // Vẽ lại màn hình
            gameView.postInvalidate();

            // Điều chỉnh FPS
            long elapsedTime = System.currentTimeMillis() - startTime;
            if (elapsedTime < frameTime) {
                try {
                    Thread.sleep(frameTime - elapsedTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}