package com.example.btl_snake_game.game;

import android.content.Context;
import android.view.GestureDetector;
import android.view.ViewTreeObserver;
import com.example.btl_snake_game.ui.GameThread;
import com.example.btl_snake_game.ui.GameView;
import com.example.btl_snake_game.ui.InputHandler;
import com.example.btl_snake_game.util.AssetManager;
import com.example.btl_snake_game.util.SettingManager;

public class GameController {
    private GameEngine gameEngine;
    private GameThread gameThread;
    private GestureDetector gestureDetector;
    private static final int CELL_SIZE = 70;
    private Context context;
    private GameView gameView;
    private boolean isInitialized = false;
    private GameThread.ScoreUpdateListener scoreListener;

    public void init(Context context, GameView gameView) {
        this.context = context;
        this.gameView = gameView;

        SettingManager.getInstance().init(context);
        AssetManager assetLoader = new AssetManager(context);
        assetLoader.loadAllAssets();

        gameView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!isInitialized && gameView.getWidth() > 0 && gameView.getHeight() > 0) {
                    initGameWithViewSize();
                    isInitialized = true;
                    gameView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
    }

    private void initGameWithViewSize() {
        int viewWidth = gameView.getWidth();
        int viewHeight = gameView.getHeight();
        int gridWidth = viewWidth / CELL_SIZE;
        int gridHeight = viewHeight / CELL_SIZE;

        gameEngine = new GameEngine(gridWidth, gridHeight, CELL_SIZE);
        gameView.setGameEngine(gameEngine);

        InputHandler inputHandler = new InputHandler(gameEngine, CELL_SIZE);
        gestureDetector = new GestureDetector(context, inputHandler);
        
        gameThread = new GameThread(gameEngine, gameView);
        if (scoreListener != null) {
            gameThread.setScoreUpdateListener(scoreListener);
        }
        gameThread.start();
    }

    public void setScoreUpdateListener(GameThread.ScoreUpdateListener listener) {
        this.scoreListener = listener;
        if (gameThread != null) {
            gameThread.setScoreUpdateListener(listener);
        }
    }

    public void start() {
        if (gameThread != null) {
            gameThread.setRunning(true);
        }
    }

    public void stop() {
        if (gameThread != null) {
            gameThread.setRunning(false);
        }
    }

    public void destroy() {
        if (gameThread != null) {
            gameThread.stopThread();
        }
    }

    public GestureDetector getGestureDetector() { return gestureDetector; }

    public GameEngine getGameEngine() { return gameEngine; }
}