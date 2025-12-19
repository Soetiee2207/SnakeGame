package com.example.btl_snake_game.ui;

import android.content.Context;
import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import com.example.btl_snake_game.game.GameEngine;
import com.example.btl_snake_game.game.GameState;
import com.example.btl_snake_game.util.Vector2D;
import java.util.List;

public class GameView extends View {
    private GameEngine gameEngine;
    private Paint snakePaint;
    private Paint foodPaint;
    private Paint textPaint;
    private Paint gridPaint;

    // Bitmap cho snake và food
    private Bitmap snakeHeadBitmap;
    private Bitmap snakeBodyBitmap;
    private Bitmap foodBitmap;

    // Flag để chọn mode vẽ
    private boolean useImages = false;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        snakePaint = new Paint();
        snakePaint.setColor(Color.GREEN);
        snakePaint.setStyle(Paint.Style.FILL);
        snakePaint.setAntiAlias(true);

        foodPaint = new Paint();
        foodPaint.setColor(Color.RED);
        foodPaint.setStyle(Paint.Style.FILL);
        foodPaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(60);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);

        gridPaint = new Paint();
        gridPaint.setColor(Color.DKGRAY);
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(1);

        // Thử load hình ảnh (nếu có)
        loadImages(context);
    }

    private void loadImages(Context context) {
        try {
            // Thử load hình ảnh từ res/drawable
            // Nếu không có hình thì sẽ dùng hình vuông mặc định

            // Ví dụ: snakeHeadBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.snake_head);
            // Ví dụ: snakeBodyBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.snake_body);
            // Ví dụ: foodBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.food);

            // Nếu load thành công, bật chế độ dùng hình ảnh
            if (snakeHeadBitmap != null && snakeBodyBitmap != null && foodBitmap != null) {
                useImages = true;
            }
        } catch (Exception e) {
            // Nếu không load được hình, dùng hình vuông mặc định
            useImages = false;
        }
    }

    public void setGameEngine(GameEngine engine) {
        this.gameEngine = engine;
    }

    // Phương thức để set hình ảnh từ bên ngoài
//    public void setSnakeHeadImage(Bitmap bitmap) {
//        this.snakeHeadBitmap = bitmap;
//        updateImageMode();
//    }

//    public void setSnakeBodyImage(Bitmap bitmap) {
//        this.snakeBodyBitmap = bitmap;
//        updateImageMode();
//    }

    public void setFoodImage(Bitmap bitmap) {
        this.foodBitmap = bitmap;
        updateImageMode();
    }

    private void updateImageMode() {
        useImages = (snakeHeadBitmap != null && snakeBodyBitmap != null && foodBitmap != null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (gameEngine == null) {
            return;
        }

        canvas.drawColor(Color.BLACK);

        // Vẽ lưới (có thể tắt nếu dùng hình ảnh)
        if (!useImages) {
            drawGrid(canvas);
        }

        // Vẽ thức ăn
        drawFood(canvas);

        // Vẽ rắn
        drawSnake(canvas);

        // Vẽ điểm số
        canvas.drawText("Score: " + gameEngine.getScore(),
                getWidth() / 2, 80, textPaint);

        // Vẽ màn hình game over
        if (gameEngine.getState() == GameState.GAME_OVER) {
            drawGameOver(canvas);
        }

        // Vẽ màn hình menu
        if (gameEngine.getState() == GameState.MENU) {
            drawMenu(canvas);
        }
    }

    private void drawFood(Canvas canvas) {
        Vector2D foodPos = gameEngine.getFood().getPosition();
        int cellSize = gameEngine.getCellSize();

        // Lấy Bitmap từ AssetManager static
        Bitmap foodBmp = com.example.btl_snake_game.util.AssetManager.food; //

        if (foodBmp != null) {
            Rect destRect = new Rect(foodPos.getX(), foodPos.getY(),
                    foodPos.getX() + cellSize, foodPos.getY() + cellSize);
            canvas.drawBitmap(foodBmp, null, destRect, null);
        } else {
            // Vẽ hình tròn mặc định nếu không có ảnh
            float centerX = foodPos.getX() + cellSize / 2f;
            float centerY = foodPos.getY() + cellSize / 2f;
            canvas.drawCircle(centerX, centerY, cellSize / 2f - 2, foodPaint);
        }
    }

    private void drawSnake(Canvas canvas) {
        List<Vector2D> body = gameEngine.getSnake().getBody();
        int cellSize = gameEngine.getCellSize();

        for (int i = 0; i < body.size(); i++) {
            Vector2D segment = body.get(i);
            Bitmap bmp = (i == 0) ? com.example.btl_snake_game.util.AssetManager.snakeHead
                    : com.example.btl_snake_game.util.AssetManager.snakeBody; //

            if (bmp != null) {
                Rect destRect = new Rect(segment.getX(), segment.getY(),
                        segment.getX() + cellSize, segment.getY() + cellSize);
                canvas.drawBitmap(bmp, null, destRect, null);
            } else {
                // Vẽ hình tròn mặc định
                snakePaint.setColor((i == 0) ? Color.rgb(0, 200, 0) : Color.rgb(0, 150, 0));
                canvas.drawCircle(segment.getX() + cellSize / 2f, segment.getY() + cellSize / 2f,
                        cellSize / 2f - 2, snakePaint);
            }
        }
    }

    private void drawGameOver(Canvas canvas) {
        // Vẽ overlay tối
        Paint overlayPaint = new Paint();
        overlayPaint.setColor(Color.argb(180, 0, 0, 0));
        canvas.drawRect(0, 0, getWidth(), getHeight(), overlayPaint);

        textPaint.setTextSize(80);
        canvas.drawText("GAME OVER", getWidth() / 2, getHeight() / 2 - 50, textPaint);

        textPaint.setTextSize(50);
        canvas.drawText("Score: " + gameEngine.getScore(),
                getWidth() / 2, getHeight() / 2 + 50, textPaint);

        textPaint.setTextSize(40);
        canvas.drawText("Tap to Restart", getWidth() / 2,
                getHeight() / 2 + 120, textPaint);

        textPaint.setTextSize(60);
    }

    private void drawMenu(Canvas canvas) {
        textPaint.setTextSize(100);
        canvas.drawText("SNAKE", getWidth() / 2, getHeight() / 2 - 150, textPaint);

        textPaint.setTextSize(80);
        canvas.drawText("GAME", getWidth() / 2, getHeight() / 2 - 50, textPaint);

        textPaint.setTextSize(50);
        canvas.drawText("Tap to Start", getWidth() / 2,
                getHeight() / 2 + 100, textPaint);

        textPaint.setTextSize(60);
    }

    private void drawGrid(Canvas canvas) {
        int cellSize = gameEngine.getCellSize();
        int width = getWidth();
        int height = getHeight();

        // Vẽ đường dọc
        for (int x = 0; x <= width; x += cellSize) {
            canvas.drawLine(x, 0, x, height, gridPaint);
        }

        // Vẽ đường ngang
        for (int y = 0; y <= height; y += cellSize) {
            canvas.drawLine(0, y, width, y, gridPaint);
        }
    }
}