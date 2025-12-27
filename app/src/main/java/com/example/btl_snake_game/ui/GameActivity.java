package com.example.btl_snake_game.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.btl_snake_game.R;
import com.example.btl_snake_game.game.GameController;
import com.example.btl_snake_game.util.AssetManager;
import com.example.btl_snake_game.util.SettingManager;
import com.example.btl_snake_game.util.SoundManager;

public class GameActivity extends AppCompatActivity {
    private GameController controller;
    private GameView gameView;
    private RelativeLayout rootLayout;
    private TextView tvScore;
    private ImageButton btnPause;
    private ImageButton btnSound;
    private AssetManager assetManager;
    private boolean isPaused = false;
    private boolean isSoundEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        hideSystemUI();
        initViews();
        initAssetManager();
        initGame();
        setupClickListeners();
        updateSoundButtonState();
    }

    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    private void initViews() {
        rootLayout = findViewById(R.id.root_layout);
        gameView = findViewById(R.id.game_view);
        tvScore = findViewById(R.id.tv_score);
        btnPause = findViewById(R.id.btn_pause);
        btnSound = findViewById(R.id.btn_sound);
    }

    private void initAssetManager() {
        // Init SettingManager first to access preferences
        SettingManager.getInstance().init(this);
        isSoundEnabled = SettingManager.getInstance().isSoundEnabled();

        // Load all base assets
        assetManager = new AssetManager(this);
        assetManager.loadAllAssets();

        // Load snake assets based on selected color (overrides default blue)
        int snakeColor = SettingManager.getInstance().getSnakeColor();
        android.util.Log.d("GameActivity", "Loading snake color: " + snakeColor);
        assetManager.loadSnakeAssetsByColor(snakeColor);

        applyAssets();
    }

    private void applyAssets() {
        if (AssetManager.gameFont != null) {
            tvScore.setTypeface(AssetManager.gameFont);
        }

        if (AssetManager.pauseIcon != null) {
            btnPause.setImageBitmap(AssetManager.pauseIcon);
        }
        if (AssetManager.volumeIcon != null) {
            btnSound.setImageBitmap(AssetManager.volumeIcon);
        }
    }

    private void initGame() {
        controller = new GameController();
        controller.setScoreUpdateListener(score -> runOnUiThread(() -> updateScore(score)));
        controller.init(this, gameView);
        setupPauseListener();
        setupGameOverListener();
    }

    private void setupPauseListener() {
        gameView.setOnPauseActionListener(new GameView.OnPauseActionListener() {
            @Override
            public void onContinue() {
                resumeGame();
            }

            @Override
            public void onReturnToMenu() {
                navigateToMenu();
            }
        });
    }

    private void setupGameOverListener() {
        gameView.setOnGameOverActionListener(new GameView.OnGameOverActionListener() {
            @Override
            public void onRestart() {
                restartGame();
            }

            @Override
            public void onReturnToMenu() {
                navigateToMenu();
            }
        });
    }

    private void restartGame() {
        if (controller != null && controller.getGameEngine() != null) {
            controller.getGameEngine().initGame();
            controller.start();
            SoundManager.getInstance().playGameMusic();
            updateScore(0);
        }
    }

    private void setupClickListeners() {
        btnPause.setOnClickListener(v -> togglePause());
        btnSound.setOnClickListener(v -> toggleSound());
    }

    private void togglePause() {
        isPaused = !isPaused;
        gameView.setPaused(isPaused);
        if (isPaused) {
            controller.stop();
            SoundManager.getInstance().pauseAll();
        } else {
            resumeGame();
        }
        updatePauseButtonState();
    }

    private void resumeGame() {
        isPaused = false;
        gameView.setPaused(false);
        if (controller != null && controller.getGameEngine() != null) {
            controller.getGameEngine().resumeToMenu();
        }
        controller.start();
        SoundManager.getInstance().playGameMusic();
        updatePauseButtonState();
    }

    private void toggleSound() {
        isSoundEnabled = !isSoundEnabled;
        SettingManager.getInstance().setSoundEnabled(isSoundEnabled);
        SoundManager.getInstance().toggleSound(isSoundEnabled);
        if (isSoundEnabled) {
            SoundManager.getInstance().playGameMusic();
        }
        updateSoundButtonState();
    }

    private void updateScore(int score) {
        tvScore.setText(getString(R.string.score_format, score));
    }

    private void updatePauseButtonState() {
        // Icon will be updated when asset images are set
    }

    private void updateSoundButtonState() {
        if (btnSound != null) {
            if (isSoundEnabled) {
                if (AssetManager.volumeIcon != null) {
                    btnSound.setImageBitmap(AssetManager.volumeIcon);
                }
            } else {
                if (AssetManager.muteIcon != null) {
                    btnSound.setImageBitmap(AssetManager.muteIcon);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (controller != null && controller.getGestureDetector() != null) {
            return controller.getGestureDetector().onTouchEvent(event) || super.onTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (controller != null) {
            controller.stop();
        }
        SoundManager.getInstance().pauseAll();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (controller != null && !isPaused) {
            controller.start();
        }
        SoundManager.getInstance().playGameMusic();
    }

    public void navigateToMenu() {
        Intent intent = new Intent(this, MenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void setPauseButtonImage(Bitmap bitmap) {
        if (bitmap != null && btnPause != null) {
            btnPause.setImageBitmap(bitmap);
        }
    }

    public void setSoundButtonImage(Bitmap bitmap) {
        if (bitmap != null && btnSound != null) {
            btnSound.setImageBitmap(bitmap);
        }
    }

    public void setGameBackground(Bitmap bitmap) {
        if (bitmap != null && rootLayout != null) {
            rootLayout.setBackground(new BitmapDrawable(getResources(), bitmap));
        }
    }

    public boolean isSoundEnabled() {
        return isSoundEnabled;
    }

    public boolean isPaused() {
        return isPaused;
    }
}
