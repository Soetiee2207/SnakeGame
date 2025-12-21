package com.example.btl_snake_game.ui;

import com.example.btl_snake_game.game.GameEngine;
import com.example.btl_snake_game.util.SettingManager;

public class GameThread extends Thread {
    private GameEngine gameEngine;
    private GameView gameView;
    private volatile boolean running;
    private volatile boolean alive = true;
    private int currentFPS;
    private int baseFPS;
    private static final int MAX_FPS = 15;
    private long frameTime;
    private int lastScore = -1;
    private ScoreUpdateListener scoreListener;

    public interface ScoreUpdateListener {
        void onScoreUpdated(int score);
    }

    public GameThread(GameEngine gameEngine, GameView gameView) {
        this.gameEngine = gameEngine;
        this.gameView = gameView;
        this.running = true;
        
        boolean isHardMode = SettingManager.getInstance().isHardMode();
        this.baseFPS = isHardMode ? 8 : 5;
        this.currentFPS = baseFPS;
    }

    public void setScoreUpdateListener(ScoreUpdateListener listener) {
        this.scoreListener = listener;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void stopThread() {
        this.alive = false;
        this.running = false;
    }

    @Override
    public void run() {
        while (alive) {
            long startTime = System.currentTimeMillis();

            if (running) {
                gameEngine.update();

                int score = gameEngine.getScore();
                if (score != lastScore) {
                    lastScore = score;
                    if (scoreListener != null) {
                        scoreListener.onScoreUpdated(score);
                    }
                }
                
                currentFPS = Math.min(baseFPS + score / 50, MAX_FPS);
            } else {
                currentFPS = 10;
            }
            
            frameTime = 1000 / currentFPS;
            gameView.postInvalidate();

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