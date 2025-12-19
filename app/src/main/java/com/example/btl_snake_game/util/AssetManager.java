package com.example.btl_snake_game.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.IOException;
import java.io.InputStream;

public class AssetManager {
    private final Context context;

    // Lưu trữ các Bitmap để dùng nhiều lần, tránh load lại gây lag
    public static Bitmap snakeHead;
    public static Bitmap snakeBody;
    public static Bitmap food;
//    public static Bitmap btnUp, btnDown, btnLeft, btnRight;
    public AssetManager(Context context) {
        this.context = context;
    }

    // Hàm load ảnh từ thư mục assets/images/
    private Bitmap loadBitmap(String fileName) {
        InputStream is = new InputStream() {
            @Override
            public int read() throws IOException {
                return 0;
            }
        };
        try {
            is = context.getAssets().open("images/" + fileName);
            return BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void loadAllAssets() {
        // Tuần 1: Huynh cứ đặt tên file sẵn ở đây,
        // Designer B cứ thế mà xuất file đúng tên là được.
        snakeHead = loadBitmap("snake_head.png");
        snakeBody = loadBitmap("snake_body.png");
        food = loadBitmap("apple.png");
    }
}
