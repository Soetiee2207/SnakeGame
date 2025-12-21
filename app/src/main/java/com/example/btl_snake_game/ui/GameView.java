package com.example.btl_snake_game.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
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

    private Bitmap snakeHeadBitmap;
    private Bitmap snakeBodyBitmap;
    private Bitmap foodBitmap;

    private boolean useImages = false;
    
    // Pause popup
    private boolean isPaused = false;
    private Paint buttonPaint;
    private Paint buttonTextPaint;
    private RectF continueButtonRect;
    private RectF menuButtonRect;
    private OnPauseActionListener pauseActionListener;

    // Game Over popup
    private RectF restartButtonRect;
    private RectF gameOverMenuButtonRect;
    private OnGameOverActionListener gameOverActionListener;

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
        
        // Apply game font if available
        if (com.example.btl_snake_game.util.AssetManager.gameFont != null) {
            textPaint.setTypeface(com.example.btl_snake_game.util.AssetManager.gameFont);
        }

        gridPaint = new Paint();
        gridPaint.setColor(Color.DKGRAY);
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(1);

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

        // Vẽ game background (sân cỏ)
        drawBackground(canvas);

        // Vẽ thức ăn
        drawFood(canvas);

        // Vẽ rắn
        drawSnake(canvas);

        // Vẽ màn hình game over
        if (gameEngine.getState() == GameState.GAME_OVER) {
            drawGameOver(canvas);
        }

        // Vẽ màn hình menu
        if (gameEngine.getState() == GameState.MENU) {
            drawMenu(canvas);
        }

        // Vẽ pause popup
        if (isPaused) {
            drawPausePopup(canvas);
        }
    }

    public interface OnPauseActionListener {
        void onContinue();
        void onReturnToMenu();
    }

    public void setOnPauseActionListener(OnPauseActionListener listener) {
        this.pauseActionListener = listener;
    }

    public void setPaused(boolean paused) {
        this.isPaused = paused;
        postInvalidate();
    }

    public boolean isPaused() {
        return isPaused;
    }

    private void drawPausePopup(Canvas canvas) {
        // Vẽ overlay tối
        Paint overlayPaint = new Paint();
        overlayPaint.setColor(Color.argb(180, 0, 0, 0));
        canvas.drawRect(0, 0, getWidth(), getHeight(), overlayPaint);

        // Vẽ popup box
        float popupWidth = getWidth() * 0.7f;
        float popupHeight = getHeight() * 0.4f;
        float popupLeft = (getWidth() - popupWidth) / 2;
        float popupTop = (getHeight() - popupHeight) / 2;
        RectF popupRect = new RectF(popupLeft, popupTop, popupLeft + popupWidth, popupTop + popupHeight);
        
        Paint popupPaint = new Paint();
        popupPaint.setColor(Color.rgb(30, 30, 60));
        popupPaint.setAntiAlias(true);
        canvas.drawRoundRect(popupRect, 30, 30, popupPaint);

        // Vẽ viền
        Paint borderPaint = new Paint();
        borderPaint.setColor(Color.rgb(76, 175, 80));
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(5);
        borderPaint.setAntiAlias(true);
        canvas.drawRoundRect(popupRect, 30, 30, borderPaint);

        // Vẽ title "PAUSED"
        textPaint.setTextSize(60);
        textPaint.setColor(Color.WHITE);
        canvas.drawText("PAUSED", getWidth() / 2, popupTop + 80, textPaint);

        // Vẽ nút Continue
        float buttonWidth = popupWidth * 0.6f;
        float buttonHeight = 80;
        float buttonLeft = (getWidth() - buttonWidth) / 2;
        float continueTop = popupTop + 130;
        continueButtonRect = new RectF(buttonLeft, continueTop, buttonLeft + buttonWidth, continueTop + buttonHeight);
        
        buttonPaint = new Paint();
        buttonPaint.setColor(Color.rgb(76, 175, 80));
        buttonPaint.setAntiAlias(true);
        canvas.drawRoundRect(continueButtonRect, 15, 15, buttonPaint);

        buttonTextPaint = new Paint();
        buttonTextPaint.setColor(Color.WHITE);
        buttonTextPaint.setTextSize(40);
        buttonTextPaint.setTextAlign(Paint.Align.CENTER);
        buttonTextPaint.setAntiAlias(true);
        if (com.example.btl_snake_game.util.AssetManager.gameFont != null) {
            buttonTextPaint.setTypeface(com.example.btl_snake_game.util.AssetManager.gameFont);
        }
        canvas.drawText("RESUME", getWidth() / 2, continueTop + 52, buttonTextPaint);

        // Vẽ nút Menu
        float menuTop = continueTop + buttonHeight + 30;
        menuButtonRect = new RectF(buttonLeft, menuTop, buttonLeft + buttonWidth, menuTop + buttonHeight);
        
        buttonPaint.setColor(Color.rgb(255, 82, 82));
        canvas.drawRoundRect(menuButtonRect, 15, 15, buttonPaint);
        canvas.drawText("MENU", getWidth() / 2, menuTop + 52, buttonTextPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Handle pause popup
        if (isPaused) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                float x = event.getX();
                float y = event.getY();

                if (continueButtonRect != null && continueButtonRect.contains(x, y)) {
                    if (pauseActionListener != null) {
                        pauseActionListener.onContinue();
                    }
                    return true;
                }

                if (menuButtonRect != null && menuButtonRect.contains(x, y)) {
                    if (pauseActionListener != null) {
                        pauseActionListener.onReturnToMenu();
                    }
                    return true;
                }
            }
            return true;
        }

        // Handle game over popup
        if (gameEngine != null && gameEngine.getState() == GameState.GAME_OVER) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                float x = event.getX();
                float y = event.getY();

                if (restartButtonRect != null && restartButtonRect.contains(x, y)) {
                    if (gameOverActionListener != null) {
                        gameOverActionListener.onRestart();
                    }
                    return true;
                }

                if (gameOverMenuButtonRect != null && gameOverMenuButtonRect.contains(x, y)) {
                    if (gameOverActionListener != null) {
                        gameOverActionListener.onReturnToMenu();
                    }
                    return true;
                }
            }
            return true;
        }

        return super.onTouchEvent(event);
    }

    private void drawBackground(Canvas canvas) {
        Bitmap bgBitmap = com.example.btl_snake_game.util.AssetManager.gameBackground;
        
        if (bgBitmap != null) {
            // Tile background để phủ kín màn hình
            int bgWidth = bgBitmap.getWidth();
            int bgHeight = bgBitmap.getHeight();
            
            for (int x = 0; x < getWidth(); x += bgWidth) {
                for (int y = 0; y < getHeight(); y += bgHeight) {
                    canvas.drawBitmap(bgBitmap, x, y, null);
                }
            }
        } else {
            // Fallback: vẽ màu xanh lá đậm nếu không có hình
            canvas.drawColor(Color.rgb(34, 139, 34)); // Forest Green
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
    private void drawRotatedHead(Canvas canvas, Bitmap bitmap, Vector2D position, Vector2D dir, int cellSize) {
        float angle = 0;

        // Tính toán góc xoay dựa trên Vector2D direction
        // Giả định ảnh gốc của huynh đang nhìn sang PHẢI (Right)
        if (dir.getX() > 0) angle = 0;          // Sang phải
        else if (dir.getX() < 0) angle = 180;   // Sang trái
        else if (dir.getY() > 0) angle = 90;    // Đi xuống
        else if (dir.getY() < 0) angle = 270;   // Đi lên

        canvas.save(); // Lưu trạng thái canvas trước khi xoay

        // Chuyển tâm xoay về giữa ô của đầu rắn
        float centerX = position.getX() + cellSize / 2f;
        float centerY = position.getY() + cellSize / 2f;
        canvas.rotate(angle, centerX, centerY);

        // Vẽ đầu rắn đã xoay
        Rect destRect = new Rect(position.getX(), position.getY(),
                position.getX() + cellSize, position.getY() + cellSize);
        canvas.drawBitmap(bitmap, null, destRect, null);

        canvas.restore(); // Khôi phục lại trạng thái canvas gốc
    }
    private void drawSnake(Canvas canvas) {
        List<Vector2D> body = gameEngine.getSnake().getBody();
        int cellSize = gameEngine.getCellSize();
        // Lấy hướng hiện tại từ Snake để biết đường mà xoay đầu
        Vector2D currentDir = gameEngine.getSnake().getDirection();

        for (int i = 0; i < body.size(); i++) {
            Vector2D segment = body.get(i);

            if (i == 0) { // Xử lý riêng cho cái ĐẦU
                Bitmap headBmp = com.example.btl_snake_game.util.AssetManager.snakeHead;
                if (headBmp != null) {
                    drawRotatedHead(canvas, headBmp, segment, currentDir, cellSize);
                } else {
                    // Vẽ hình tròn mặc định nếu chưa load được ảnh
                    snakePaint.setColor(Color.rgb(0, 200, 0));
                    canvas.drawCircle(segment.getX() + cellSize / 2f, segment.getY() + cellSize / 2f,
                            cellSize / 2f - 2, snakePaint);
                }
            } else { // Vẽ THÂN như bình thường
                Bitmap bodyBmp = com.example.btl_snake_game.util.AssetManager.snakeBody;
                if (bodyBmp != null) {
                    Rect destRect = new Rect(segment.getX(), segment.getY(),
                            segment.getX() + cellSize, segment.getY() + cellSize);
                    canvas.drawBitmap(bodyBmp, null, destRect, null);
                } else {
                    snakePaint.setColor(Color.rgb(0, 150, 0));
                    canvas.drawCircle(segment.getX() + cellSize / 2f, segment.getY() + cellSize / 2f,
                            cellSize / 2f - 2, snakePaint);
                }
            }
        }
    }

    private void drawGameOver(Canvas canvas) {
        // Vẽ overlay tối
        Paint overlayPaint = new Paint();
        overlayPaint.setColor(Color.argb(180, 0, 0, 0));
        canvas.drawRect(0, 0, getWidth(), getHeight(), overlayPaint);

        // Vẽ popup box
        float popupWidth = getWidth() * 0.75f;
        float popupHeight = getHeight() * 0.45f;
        float popupLeft = (getWidth() - popupWidth) / 2;
        float popupTop = (getHeight() - popupHeight) / 2;
        RectF popupRect = new RectF(popupLeft, popupTop, popupLeft + popupWidth, popupTop + popupHeight);
        
        Paint popupPaint = new Paint();
        popupPaint.setColor(Color.rgb(30, 30, 60));
        popupPaint.setAntiAlias(true);
        canvas.drawRoundRect(popupRect, 30, 30, popupPaint);

        // Vẽ viền đỏ
        Paint borderPaint = new Paint();
        borderPaint.setColor(Color.rgb(255, 82, 82));
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(5);
        borderPaint.setAntiAlias(true);
        canvas.drawRoundRect(popupRect, 30, 30, borderPaint);

        // Vẽ title "GAME OVER"
        textPaint.setTextSize(60);
        textPaint.setColor(Color.rgb(255, 82, 82));
        canvas.drawText("GAME OVER", getWidth() / 2, popupTop + 70, textPaint);

        // Vẽ Score
        textPaint.setTextSize(40);
        textPaint.setColor(Color.WHITE);
        canvas.drawText("Score: " + gameEngine.getScore(), getWidth() / 2, popupTop + 130, textPaint);

        // Vẽ nút Restart
        float buttonWidth = popupWidth * 0.6f;
        float buttonHeight = 80;
        float buttonLeft = (getWidth() - buttonWidth) / 2;
        float restartTop = popupTop + 170;
        restartButtonRect = new RectF(buttonLeft, restartTop, buttonLeft + buttonWidth, restartTop + buttonHeight);
        
        if (buttonPaint == null) buttonPaint = new Paint();
        buttonPaint.setColor(Color.rgb(76, 175, 80));
        buttonPaint.setAntiAlias(true);
        canvas.drawRoundRect(restartButtonRect, 15, 15, buttonPaint);

        if (buttonTextPaint == null) {
            buttonTextPaint = new Paint();
            buttonTextPaint.setColor(Color.WHITE);
            buttonTextPaint.setTextSize(40);
            buttonTextPaint.setTextAlign(Paint.Align.CENTER);
            buttonTextPaint.setAntiAlias(true);
            if (com.example.btl_snake_game.util.AssetManager.gameFont != null) {
                buttonTextPaint.setTypeface(com.example.btl_snake_game.util.AssetManager.gameFont);
            }
        }
        canvas.drawText("RESTART", getWidth() / 2, restartTop + 52, buttonTextPaint);

        // Vẽ nút Menu
        float menuTop = restartTop + buttonHeight + 25;
        gameOverMenuButtonRect = new RectF(buttonLeft, menuTop, buttonLeft + buttonWidth, menuTop + buttonHeight);
        
        buttonPaint.setColor(Color.rgb(255, 82, 82));
        canvas.drawRoundRect(gameOverMenuButtonRect, 15, 15, buttonPaint);
        canvas.drawText("MENU", getWidth() / 2, menuTop + 52, buttonTextPaint);

        textPaint.setTextSize(60);
        textPaint.setColor(Color.WHITE);
    }

    public interface OnGameOverActionListener {
        void onRestart();
        void onReturnToMenu();
    }

    public void setOnGameOverActionListener(OnGameOverActionListener listener) {
        this.gameOverActionListener = listener;
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