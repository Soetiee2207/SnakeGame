package com.example.btl_snake_game.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingManager {
    private static SettingManager instance;
    private SharedPreferences prefs;
    private static final String PREF_NAME = "SnakeGamePrefs";
    private static final String HIGH_SCORE_KEY = "highScore";
    private static final String SOUND_ENABLED_KEY = "soundEnabled";
    private static final String DIFFICULTY_KEY = "difficulty";
    private static final String SNAKE_COLOR_KEY = "snakeColor";

    public static final int DIFFICULTY_EASY = 0;
    public static final int DIFFICULTY_HARD = 1;

    public static final int COLOR_BLUE = 0;
    public static final int COLOR_GREEN = 1;
    public static final int COLOR_PURPLE = 2;

    private SettingManager() {}

    public static SettingManager getInstance() {
        if (instance == null) {
            instance = new SettingManager();
        }
        return instance;
    }

    public void init(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveHighScore(int score) {
        if (prefs != null) {
            prefs.edit().putInt(HIGH_SCORE_KEY, score).apply();
        }
    }

    public int getHighScore() {
        if (prefs != null) {
            return prefs.getInt(HIGH_SCORE_KEY, 0);
        }
        return 0;
    }

    public void setSoundEnabled(boolean enabled) {
        if (prefs != null) {
            prefs.edit().putBoolean(SOUND_ENABLED_KEY, enabled).apply();
        }
    }

    public boolean isSoundEnabled() {
        if (prefs != null) {
            return prefs.getBoolean(SOUND_ENABLED_KEY, true);
        }
        return true;
    }

    public void setDifficulty(int difficulty) {
        if (prefs != null) {
            prefs.edit().putInt(DIFFICULTY_KEY, difficulty).apply();
        }
    }

    public int getDifficulty() {
        if (prefs != null) {
            return prefs.getInt(DIFFICULTY_KEY, DIFFICULTY_EASY);
        }
        return DIFFICULTY_EASY;
    }

    public boolean isHardMode() {
        return getDifficulty() == DIFFICULTY_HARD;
    }

    public void setSnakeColor(int color) {
        if (prefs != null) {
            prefs.edit().putInt(SNAKE_COLOR_KEY, color).apply();
        }
    }

    public int getSnakeColor() {
        if (prefs != null) {
            return prefs.getInt(SNAKE_COLOR_KEY, COLOR_BLUE);
        }
        return COLOR_BLUE;
    }
}