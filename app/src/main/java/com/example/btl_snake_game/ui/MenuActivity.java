package com.example.btl_snake_game.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.example.btl_snake_game.R;
import com.example.btl_snake_game.util.AssetManager;
import com.example.btl_snake_game.util.SettingManager;
import com.example.btl_snake_game.util.SoundManager;

public class MenuActivity extends AppCompatActivity {
    private ConstraintLayout rootLayout;
    private ImageView ivLogo;
    private TextView tvGameTitle;
    private TextView tvHighScore;
    private Button btnPlay;
    private Button btnSettings;
    private Button btnExit;
    private AssetManager assetManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        hideSystemUI();
        initViews();
        initAssetManager();
        setupClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateHighScore();
        SoundManager.getInstance().playMenuMusic();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SoundManager.getInstance().pauseAll();
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
        ivLogo = findViewById(R.id.iv_logo);
        tvGameTitle = findViewById(R.id.tv_game_title);
        tvHighScore = findViewById(R.id.tv_high_score);
        btnPlay = findViewById(R.id.btn_play);
        btnSettings = findViewById(R.id.btn_settings);
        btnExit = findViewById(R.id.btn_exit);
    }

    private void initAssetManager() {
        assetManager = new AssetManager(this);
        assetManager.loadAllAssets();
        
        SettingManager.getInstance().init(this);
        SoundManager.getInstance().init(this);
        updateHighScore();

        // Apply assets to UI
        applyAssets();
    }

    private void applyAssets() {
        // Apply font
        if (AssetManager.gameFont != null) {
            tvGameTitle.setTypeface(AssetManager.gameFont);
            tvHighScore.setTypeface(AssetManager.gameFont);
            btnPlay.setTypeface(AssetManager.gameFont);
            btnSettings.setTypeface(AssetManager.gameFont);
            btnExit.setTypeface(AssetManager.gameFont);
        }

        // Apply background
        if (AssetManager.menuBackground != null) {
            rootLayout.setBackground(new BitmapDrawable(getResources(), AssetManager.menuBackground));
        }

        // Apply logo
        if (AssetManager.snakeLogo != null) {
            ivLogo.setImageBitmap(AssetManager.snakeLogo);
        }

        // Apply button backgrounds
        if (AssetManager.button != null) {
            btnPlay.setBackground(new BitmapDrawable(getResources(), AssetManager.button));
            btnSettings.setBackground(new BitmapDrawable(getResources(), AssetManager.button));
            btnExit.setBackground(new BitmapDrawable(getResources(), AssetManager.button));
        }
    }

    private void updateHighScore() {
        int highScore = SettingManager.getInstance().getHighScore();
        tvHighScore.setText(getString(R.string.high_score_format, highScore));
    }

    private void setupClickListeners() {
        btnPlay.setOnClickListener(v -> navigateToGame());
        btnSettings.setOnClickListener(v -> navigateToSettings());
        btnExit.setOnClickListener(v -> exitApp());
    }

    private void navigateToGame() {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    private void navigateToSettings() {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    private void exitApp() {
        finishAffinity();
    }

    public void setLogoImage(Bitmap bitmap) {
        if (bitmap != null && ivLogo != null) {
            ivLogo.setImageBitmap(bitmap);
        }
    }

    public void setPlayButtonBackground(Bitmap bitmap) {
        if (bitmap != null && btnPlay != null) {
            btnPlay.setBackground(new BitmapDrawable(getResources(), bitmap));
        }
    }

    public void setSettingsButtonBackground(Bitmap bitmap) {
        if (bitmap != null && btnSettings != null) {
            btnSettings.setBackground(new BitmapDrawable(getResources(), bitmap));
        }
    }

    public void setExitButtonBackground(Bitmap bitmap) {
        if (bitmap != null && btnExit != null) {
            btnExit.setBackground(new BitmapDrawable(getResources(), bitmap));
        }
    }

    public void setMenuBackground(Bitmap bitmap) {
        if (bitmap != null && rootLayout != null) {
            rootLayout.setBackground(new BitmapDrawable(getResources(), bitmap));
        }
    }
}
