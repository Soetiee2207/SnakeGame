package com.example.btl_snake_game;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.btl_snake_game.game.GameEngine;
import com.example.btl_snake_game.ui.GameThread;
import com.example.btl_snake_game.ui.GameView;
import com.example.btl_snake_game.ui.InputHandler;
import com.example.btl_snake_game.util.AssetManager;
import com.example.btl_snake_game.util.SettingManager;

public class MainActivity extends AppCompatActivity {
    private GameView gameView;
    private GameEngine gameEngine;
    private GameThread gameThread;
    private GestureDetector gestureDetector;

    private static final int CELL_SIZE = 50; // Kích thước mỗi ô

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Khởi tạo đúng với Constructor nhận Context
        com.example.btl_snake_game.util.AssetManager assetLoader = new com.example.btl_snake_game.util.AssetManager(this);

        // Gọi ĐÚNG tên hàm huynh đã viết trong lớp AssetManager
        assetLoader.loadAllAssets();
        // Ẩn thanh trạng thái và navigation bar
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );

        // Khởi tạo SettingManager
        SettingManager.getInstance().init(this);

        // Lấy kích thước màn hình
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        // Tính số ô trên lưới
        int gridWidth = screenWidth / CELL_SIZE;
        int gridHeight = screenHeight / CELL_SIZE;

        // Khởi tạo game
        gameEngine = new GameEngine(gridWidth, gridHeight, CELL_SIZE);

        // Khởi tạo GameView
        gameView = new GameView(this, null);
        gameView.setGameEngine(gameEngine);
        setContentView(gameView);

        // Khởi tạo input handler
        InputHandler inputHandler = new InputHandler(gameEngine, CELL_SIZE);
        gestureDetector = new GestureDetector(this, inputHandler);

        // Khởi tạo game thread
        gameThread = new GameThread(gameEngine, gameView);
        gameThread.setRunning(true);
        gameThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (gameThread != null) {
            gameThread.setRunning(false);
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gameThread != null && !gameThread.isAlive()) {
            gameThread = new GameThread(gameEngine, gameView);
            gameThread.setRunning(true);
            gameThread.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gameThread != null) {
            gameThread.setRunning(false);
        }
    }
}