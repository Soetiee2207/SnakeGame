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
        try {
            // Xóa dòng InputStream is = new InputStream() {...} cũ đi
            InputStream is = context.getAssets().open("images/" + fileName);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            is.close(); // Nhớ đóng stream sau khi đọc xong
            return bitmap;
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
        food = loadBitmap("food.png");
    }
}
