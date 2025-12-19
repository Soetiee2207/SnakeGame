package com.example.btl_snake_game.game;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.WindowManager;
import com.example.btl_snake_game.ui.GameThread;
import com.example.btl_snake_game.ui.GameView;
import com.example.btl_snake_game.ui.InputHandler;
import com.example.btl_snake_game.util.AssetManager;
import com.example.btl_snake_game.util.SettingManager;

public class GameController {
    private GameEngine gameEngine;
    private GameThread gameThread;
    private GestureDetector gestureDetector;
    private static final int CELL_SIZE = 50;

    public void init(Context context, GameView gameView) {
        // 1. Khởi tạo Managers
        SettingManager.getInstance().init(context);
        AssetManager assetLoader = new AssetManager(context);
        assetLoader.loadAllAssets();

        // 2. Tính toán lưới
        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
        int gridWidth = dm.widthPixels / CELL_SIZE;
        int gridHeight = dm.heightPixels / CELL_SIZE;

        // 3. Khởi tạo Core Logic
        gameEngine = new GameEngine(gridWidth, gridHeight, CELL_SIZE);
        gameView.setGameEngine(gameEngine);

        // 4. Khởi tạo Input & Thread
        InputHandler inputHandler = new InputHandler(gameEngine, CELL_SIZE);
        gestureDetector = new GestureDetector(context, inputHandler);
        gameThread = new GameThread(gameEngine, gameView);
    }

    public void start() {
        if (gameThread != null) {
            gameThread.setRunning(true);
            if (!gameThread.isAlive()) gameThread.start();
        }
    }

    public void stop() {
        if (gameThread != null) {
            gameThread.setRunning(false);
            try { gameThread.join(); } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }

    public GestureDetector getGestureDetector() { return gestureDetector; }
}