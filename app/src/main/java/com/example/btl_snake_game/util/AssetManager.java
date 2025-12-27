package com.example.btl_snake_game.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import java.io.IOException;
import java.io.InputStream;

public class AssetManager {
    private final Context context;

    public static Bitmap snakeHead;
    public static Bitmap snakeBody;
    public static Bitmap food;
    public static Bitmap button;
    public static Bitmap gameBackground;
    public static Bitmap menuBackground;
    public static Bitmap pauseIcon;
    public static Bitmap volumeIcon;
    public static Bitmap muteIcon;
    public static Bitmap snakeLogo;
    public static Bitmap snakeTitle;

    public static Typeface gameFont;

    public AssetManager(Context context) {
        this.context = context;
    }

    private Bitmap loadBitmap(String fileName) {
        try {
            InputStream is = context.getAssets().open("images/" + fileName);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            is.close();
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Typeface loadFont(String fileName) {
        try {
            return Typeface.createFromAsset(context.getAssets(), "fonts/" + fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return Typeface.DEFAULT;
        }
    }

    public void loadAllAssets() {
        food = loadBitmap("food.png");
        button = loadBitmap("button.png");
        gameBackground = loadBitmap("game_background.png");
        menuBackground = loadBitmap("menu_background.png");
        pauseIcon = loadBitmap("pause_game.png");
        volumeIcon = loadBitmap("volume_game.png");
        muteIcon = loadBitmap("mute_game.png");
        snakeLogo = loadBitmap("snake_logo.png");
        snakeTitle = loadBitmap("snake_title.png");
        gameFont = loadFont("yoster.ttf");
    }

    public void loadSnakeAssetsByColor(int color) {
        String colorName;
        switch (color) {
            case SettingManager.COLOR_GREEN:
                colorName = "green";
                break;
            case SettingManager.COLOR_PURPLE:
                colorName = "purple";
                break;
            case SettingManager.COLOR_BLUE:
            default:
                colorName = "blue";
                break;
        }
        snakeHead = loadBitmap("snake_head_" + colorName + ".png");
        snakeBody = loadBitmap("snake_body_" + colorName + ".png");
    }
}
