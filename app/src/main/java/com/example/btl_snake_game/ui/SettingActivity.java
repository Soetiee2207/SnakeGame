package com.example.btl_snake_game.ui;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.example.btl_snake_game.R;
import com.example.btl_snake_game.util.AssetManager;
import com.example.btl_snake_game.util.SettingManager;

public class SettingActivity extends AppCompatActivity {
    private ConstraintLayout rootLayout;
    private TextView tvSettingsTitle;
    private TextView tvDifficultyLabel;
    private RadioGroup rgDifficulty;
    private RadioButton rbEasy;
    private RadioButton rbHard;
    private Button btnBack;
    private AssetManager assetManager;
    private SettingManager settingManager;

    public static final String DIFFICULTY_KEY = "difficulty";
    public static final int DIFFICULTY_EASY = 0;
    public static final int DIFFICULTY_HARD = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        hideSystemUI();
        initViews();
        initManagers();
        loadSettings();
        setupClickListeners();
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
        tvSettingsTitle = findViewById(R.id.tv_settings_title);
        tvDifficultyLabel = findViewById(R.id.tv_difficulty_label);
        rgDifficulty = findViewById(R.id.rg_difficulty);
        rbEasy = findViewById(R.id.rb_easy);
        rbHard = findViewById(R.id.rb_hard);
        btnBack = findViewById(R.id.btn_back);
    }

    private void initManagers() {
        assetManager = new AssetManager(this);
        assetManager.loadAllAssets();
        
        settingManager = SettingManager.getInstance();
        settingManager.init(this);

        // Apply assets to UI
        applyAssets();
    }

    private void applyAssets() {
        // Apply font
        if (AssetManager.gameFont != null) {
            tvSettingsTitle.setTypeface(AssetManager.gameFont);
            tvDifficultyLabel.setTypeface(AssetManager.gameFont);
            rbEasy.setTypeface(AssetManager.gameFont);
            rbHard.setTypeface(AssetManager.gameFont);
            btnBack.setTypeface(AssetManager.gameFont);
        }

        // Apply button background
        if (AssetManager.button != null) {
            btnBack.setBackground(new android.graphics.drawable.BitmapDrawable(getResources(), AssetManager.button));
        }
    }

    private void loadSettings() {
        int difficulty = getDifficulty();
        if (difficulty == DIFFICULTY_EASY) {
            rbEasy.setChecked(true);
        } else {
            rbHard.setChecked(true);
        }
    }

    private void setupClickListeners() {
        rgDifficulty.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_easy) {
                saveDifficulty(DIFFICULTY_EASY);
            } else if (checkedId == R.id.rb_hard) {
                saveDifficulty(DIFFICULTY_HARD);
            }
        });

        btnBack.setOnClickListener(v -> navigateBack());
    }

    private void saveDifficulty(int difficulty) {
        settingManager.setDifficulty(difficulty);
    }

    private int getDifficulty() {
        return settingManager.getDifficulty();
    }

    private void navigateBack() {
        finish();
    }

    public void setSettingsBackground(Bitmap bitmap) {
        if (bitmap != null && rootLayout != null) {
            rootLayout.setBackground(new BitmapDrawable(getResources(), bitmap));
        }
    }

    public void setBackButtonBackground(Bitmap bitmap) {
        if (bitmap != null && btnBack != null) {
            btnBack.setBackground(new BitmapDrawable(getResources(), bitmap));
        }
    }

    public void setTitleTextColor(int color) {
        if (tvSettingsTitle != null) {
            tvSettingsTitle.setTextColor(color);
        }
    }

    public void setDifficultyLabelTextColor(int color) {
        if (tvDifficultyLabel != null) {
            tvDifficultyLabel.setTextColor(color);
        }
    }

    public static int getDifficultyFromPrefs(android.content.Context context) {
        return context.getSharedPreferences("SnakeGamePrefs", MODE_PRIVATE)
                .getInt(DIFFICULTY_KEY, DIFFICULTY_EASY);
    }
}
