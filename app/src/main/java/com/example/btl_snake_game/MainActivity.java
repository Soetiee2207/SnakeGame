package com.example.btl_snake_game;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.btl_snake_game.game.GameController;
import com.example.btl_snake_game.ui.GameView;

public class MainActivity extends AppCompatActivity {
    private GameController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Chỉnh giao diện
        hideSystemUI();

        // 2. Khởi tạo View & Controller
        GameView gameView = new GameView(this, null);
        controller = new GameController();
        controller.init(this, gameView);

        setContentView(gameView);
        controller.start();
    }

    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return controller.getGestureDetector().onTouchEvent(event) || super.onTouchEvent(event);
    }

    @Override
    protected void onPause() { super.onPause(); controller.stop(); }

    @Override
    protected void onResume() { super.onResume(); controller.start(); }
}