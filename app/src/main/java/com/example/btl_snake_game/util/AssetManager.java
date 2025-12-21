package com.example.btl_snake_game.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import java.io.IOException;
import java.io.InputStream;

public class AssetManager {
    private final Context context;

    // Lưu trữ các Bitmap để dùng nhiều lần
    public static Bitmap snakeHead;
    public static Bitmap snakeBody;
    public static Bitmap food;
    public static Bitmap button;
    public static Bitmap gameBackground;
    public static Bitmap menuBackground;
    public static Bitmap pauseIcon;
    public static Bitmap volumeIcon;
    public static Bitmap snakeLogo;
    public static Bitmap snakeTitle;

    // Font
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
        // Load game sprites
        snakeHead = loadBitmap("snake_head.png");
        snakeBody = loadBitmap("snake_body.png");
        food = loadBitmap("food.png");

        // Load UI elements
        button = loadBitmap("button.png");
        gameBackground = loadBitmap("game_background.png");
        menuBackground = loadBitmap("menu_background.png");
        pauseIcon = loadBitmap("pause_game.png");
        volumeIcon = loadBitmap("volume_game.png");
        snakeLogo = loadBitmap("snake_logo.png");
        snakeTitle = loadBitmap("snake_title.png");

        // Load font
        gameFont = loadFont("yoster.ttf");
    }

    public static Typeface getGameFont() {
        return gameFont != null ? gameFont : Typeface.DEFAULT;
    }
}
