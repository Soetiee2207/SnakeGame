# TÀI LIỆU DỰ ÁN GAME RẮN SĂN MỒI (SNAKE GAME)
## Bài Tập Lớn - Lập Trình Ứng Dụng Di Động

---

## THÔNG TIN CHUNG

| Thông tin | Chi tiết |
|-----------|----------|
| Tên ứng dụng | BTL Snake Game |
| Nền tảng | Android |
| Ngôn ngữ | Java |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 36 |
| Build System | Gradle (Kotlin DSL) |

---

## 1. CÁC THƯ VIỆN VÀ API SỬ DỤNG

### 1.1. Thư viện Android Core
```groovy
implementation(libs.appcompat)           // AppCompatActivity, backward compatibility
implementation(libs.material)            // Material Design components
implementation(libs.activity)            // Activity lifecycle management
implementation(libs.constraintlayout)    // ConstraintLayout for flexible UI
```

### 1.2. API Android Quan Trọng Sử Dụng

| API/Class | Mục đích | File sử dụng |
|-----------|----------|--------------|
| `Canvas` | Vẽ đồ họa 2D (rắn, thức ăn, UI) | GameView.java |
| `Paint` | Định nghĩa màu sắc, style vẽ | GameView.java |
| `Bitmap` | Load và hiển thị hình ảnh PNG | AssetManager.java, GameView.java |
| `GestureDetector` | Nhận diện cử chỉ vuốt (swipe) | InputHandler.java |
| `MediaPlayer` | Phát nhạc nền | SoundManager.java |
| `SoundPool` | Phát hiệu ứng âm thanh ngắn | SoundManager.java |
| `SharedPreferences` | Lưu cài đặt và điểm cao | SettingManager.java |
| `Typeface` | Load font tùy chỉnh | AssetManager.java |
| `Thread` | Game loop chạy song song | GameThread.java |
| `ViewTreeObserver` | Lắng nghe khi View đã được layout | GameController.java |

---

## 2. ĐIỂM KHÁC BIỆT SO VỚI BÀI HỌC CƠ BẢN

### 2.1. Vẽ Con Rắn Bằng Canvas (Thay vì ImageView/DrawableView)

**Lý do sử dụng Canvas:**
- Cho phép vẽ trực tiếp lên màn hình với hiệu suất cao
- Linh hoạt trong việc vẽ các hình dạng phức tạp
- Có thể xoay hình ảnh đầu rắn theo hướng di chuyển
- Phù hợp với game loop liên tục cập nhật

**Cách thực hiện trong `GameView.java`:**

```java
// Override onDraw để vẽ mọi thứ
@Override
protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    drawBackground(canvas);  // Vẽ nền
    drawFood(canvas);        // Vẽ thức ăn
    drawSnake(canvas);       // Vẽ rắn
    // Vẽ các popup nếu cần
}
```

### 2.2. Xoay Đầu Rắn Theo Hướng Di Chuyển

**Vấn đề:** Hình ảnh đầu rắn cần xoay theo 4 hướng (lên, xuống, trái, phải)

**Giải pháp trong `drawRotatedHead()`:**

```java
private void drawRotatedHead(Canvas canvas, Bitmap bitmap, Vector2D position, Vector2D dir, int cellSize) {
    float angle = 0;
    
    // Tính góc xoay dựa trên hướng di chuyển
    if (dir.getX() > 0) angle = 0;          // Sang phải (mặc định)
    else if (dir.getX() < 0) angle = 180;   // Sang trái
    else if (dir.getY() > 0) angle = 90;    // Đi xuống
    else if (dir.getY() < 0) angle = 270;   // Đi lên

    canvas.save();                            // Lưu trạng thái canvas
    canvas.rotate(angle, centerX, centerY);   // Xoay quanh tâm
    canvas.drawBitmap(bitmap, null, destRect, null);
    canvas.restore();                         // Khôi phục trạng thái
}
```

### 2.3. Game Loop Riêng Biệt (GameThread)

**Thay vì:** Dùng Handler/Timer như bài học cơ bản

**Sử dụng:** Thread riêng với frame rate động

```java
// Trong GameThread.java
@Override
public void run() {
    while (alive) {
        long startTime = System.currentTimeMillis();
        
        if (running) {
            gameEngine.update();          // Cập nhật logic game
            currentFPS = Math.min(baseFPS + score / 50, MAX_FPS);  // Tăng tốc khi điểm cao
        }
        
        gameView.postInvalidate();        // Yêu cầu vẽ lại UI
        
        // Điều chỉnh thời gian ngủ để đạt FPS mong muốn
        long elapsedTime = System.currentTimeMillis() - startTime;
        if (elapsedTime < frameTime) {
            Thread.sleep(frameTime - elapsedTime);
        }
    }
}
```

### 2.4. Điều Khiển Bằng Cử Chỉ Vuốt (GestureDetector)

**Thay vì:** Dùng nút bấm trên màn hình

**Sử dụng:** `GestureDetector.SimpleOnGestureListener`

```java
// Trong InputHandler.java
@Override
public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
    float diffX = e2.getX() - e1.getX();
    float diffY = e2.getY() - e1.getY();

    if (Math.abs(diffX) > Math.abs(diffY)) {
        // Vuốt ngang
        if (diffX > 0) {
            gameEngine.changeDirection(new Vector2D(cellSize, 0));  // Phải
        } else {
            gameEngine.changeDirection(new Vector2D(-cellSize, 0)); // Trái
        }
    } else {
        // Vuốt dọc
        if (diffY > 0) {
            gameEngine.changeDirection(new Vector2D(0, cellSize));  // Xuống
        } else {
            gameEngine.changeDirection(new Vector2D(0, -cellSize)); // Lên
        }
    }
    return true;
}
```

### 2.5. Quản Lý Âm Thanh Phức Tạp (MediaPlayer + SoundPool)

| Class | Dùng cho | Đặc điểm |
|-------|----------|----------|
| `MediaPlayer` | Nhạc nền (menu, game) | File dài, loop, cần prepare() |
| `SoundPool` | Hiệu ứng âm thanh (ăn mồi, chết) | File ngắn, phát tức thì |

```java
// Khởi tạo SoundPool với AudioAttributes (API mới hơn)
AudioAttributes audioAttributes = new AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_GAME)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build();

soundPool = new SoundPool.Builder()
        .setMaxStreams(4)
        .setAudioAttributes(audioAttributes)
        .build();
```

---

## 3. KIẾN TRÚC ỨNG DỤNG

### 3.1. Mô Hình MVC (Model-View-Controller)

```
┌─────────────────────────────────────────────────────────────┐
│                      CONTROLLER                              │
│  ┌─────────────────┐  ┌─────────────────┐                   │
│  │  GameController │  │  GameActivity   │                   │
│  │  - Khởi tạo     │  │  - Lifecycle    │                   │
│  │  - Điều phối    │  │  - UI events    │                   │
│  └────────┬────────┘  └────────┬────────┘                   │
└───────────┼────────────────────┼────────────────────────────┘
            │                    │
            ▼                    ▼
┌─────────────────────┐  ┌─────────────────────────────────────┐
│       MODEL         │  │                VIEW                  │
│  ┌───────────────┐  │  │  ┌─────────────┐  ┌──────────────┐  │
│  │  GameEngine   │  │  │  │  GameView   │  │  GameThread  │  │
│  │  - Logic game │  │  │  │  - Vẽ UI    │  │  - Game loop │  │
│  │  - Trạng thái │  │  │  │  - Canvas   │  │  - FPS       │  │
│  ├───────────────┤  │  │  └─────────────┘  └──────────────┘  │
│  │    Snake      │  │  └─────────────────────────────────────┘
│  │    Food       │  │
│  └───────────────┘  │
└─────────────────────┘
```

### 3.2. Cấu Trúc Package

```
com.example.btl_snake_game/
├── MainActivity.java           # Entry point
├── game/                       # Logic game (Model)
│   ├── GameController.java     # Điều phối game
│   ├── GameEngine.java         # Core logic
│   ├── GameState.java          # Enum trạng thái
│   └── objects/
│       ├── Snake.java          # Đối tượng rắn
│       └── Food.java           # Đối tượng thức ăn
├── ui/                         # Giao diện (View)
│   ├── GameActivity.java       # Màn hình game
│   ├── MenuActivity.java       # Màn hình menu
│   ├── SettingActivity.java    # Màn hình cài đặt
│   ├── GameView.java           # Custom View vẽ game
│   ├── GameThread.java         # Thread vẽ
│   └── InputHandler.java       # Xử lý input
└── util/                       # Tiện ích
    ├── AssetManager.java       # Load tài nguyên
    ├── SettingManager.java     # Quản lý cài đặt
    ├── SoundManager.java       # Quản lý âm thanh
    └── Vector2D.java           # Lớp tọa độ 2D
```

---

## 4. LOGIC CÁC HÀM QUAN TRỌNG

### 4.1. GameEngine - Logic Core

#### `update()` - Cập nhật trạng thái game mỗi frame

```java
public void update() {
    if (state != GameState.PLAYING) return;

    snake.move();                           // 1. Di chuyển rắn

    // 2. Xử lý va chạm tường theo độ khó
    if (isHardMode) {
        if (snake.checkWallCollision(gridWidth, gridHeight)) {
            state = GameState.GAME_OVER;    // Chế độ khó: chết
            return;
        }
    } else {
        wrapSnakePosition();                // Chế độ dễ: xuyên tường
    }

    // 3. Kiểm tra ăn mồi
    if (food.isEaten(snake.getHead())) {
        snake.grow();
        score += 10;
        food.spawn();
        // Đảm bảo thức ăn không spawn trên thân rắn
        while (isOnSnake(food.getPosition())) {
            food.spawn();
        }
    }

    // 4. Kiểm tra tự cắn
    if (snake.checkSelfCollision()) {
        state = GameState.GAME_OVER;
    }
}
```

**Giải quyết vấn đề gì:**
- Điều phối toàn bộ logic game trong mỗi frame
- Xử lý khác biệt giữa chế độ dễ/khó
- Đảm bảo thức ăn không xuất hiện trên thân rắn

#### `wrapSnakePosition()` - Xuyên tường (chế độ dễ)

```java
private void wrapSnakePosition() {
    Vector2D head = snake.getHead();
    int maxX = gridWidth * cellSize;
    int maxY = gridHeight * cellSize;

    // Nếu ra khỏi biên trái → xuất hiện bên phải
    if (head.getX() < 0) {
        head.setX(maxX - cellSize);
    } 
    // Nếu ra khỏi biên phải → xuất hiện bên trái
    else if (head.getX() >= maxX) {
        head.setX(0);
    }
    // Tương tự cho trục Y
}
```

**Giải quyết vấn đề gì:**
- Tạo hiệu ứng "pacman" - rắn đi xuyên tường
- Tính toán vị trí mới dựa trên kích thước grid

---

### 4.2. Snake - Quản lý con rắn

#### `move()` - Di chuyển rắn

```java
public void move() {
    direction = nextDirection;              // Áp dụng hướng mới
    Vector2D head = body.get(0);
    
    // Tính vị trí đầu mới
    Vector2D newHead = new Vector2D(
        head.getX() + direction.getX(),
        head.getY() + direction.getY()
    );

    body.add(0, newHead);                   // Thêm đầu mới vào đầu danh sách

    if (!growing) {
        body.remove(body.size() - 1);       // Xóa đuôi (nếu không đang lớn)
    } else {
        growing = false;                     // Giữ đuôi = rắn dài thêm 1
    }
}
```

**Giải quyết vấn đề gì:**
- Mô phỏng chuyển động của rắn bằng LinkedList concept
- Thêm đầu mới + xóa đuôi = di chuyển
- Thêm đầu mới + giữ đuôi = tăng chiều dài

#### `setDirection()` - Đổi hướng an toàn

```java
public void setDirection(Vector2D newDirection) {
    // Không cho phép đi ngược lại (tự cắn ngay lập tức)
    if (direction.getX() + newDirection.getX() != 0 ||
            direction.getY() + newDirection.getY() != 0) {
        this.nextDirection = newDirection;
    }
}
```

**Giải quyết vấn đề gì:**
- Ngăn rắn tự cắn mình khi đổi hướng 180°
- Ví dụ: đang đi phải → không thể đi trái ngay

---

### 4.3. GameThread - Vòng lặp game

#### `run()` - Game loop chính

```java
@Override
public void run() {
    while (alive) {
        long startTime = System.currentTimeMillis();

        if (running) {
            gameEngine.update();
            
            // Tăng tốc độ theo điểm số
            currentFPS = Math.min(baseFPS + score / 50, MAX_FPS);
        }
        
        frameTime = 1000 / currentFPS;      // Tính thời gian mỗi frame
        gameView.postInvalidate();          // Yêu cầu vẽ lại

        // Điều chỉnh sleep để đạt FPS ổn định
        long elapsedTime = System.currentTimeMillis() - startTime;
        if (elapsedTime < frameTime) {
            Thread.sleep(frameTime - elapsedTime);
        }
    }
}
```

**Giải quyết vấn đề gì:**
- Tách logic game khỏi UI thread
- Điều khiển FPS động (tăng khi điểm cao)
- Đảm bảo game chạy mượt mà

---

### 4.4. InputHandler - Xử lý đầu vào

#### `onFling()` - Nhận diện vuốt

```java
@Override
public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
    float diffX = e2.getX() - e1.getX();
    float diffY = e2.getY() - e1.getY();

    // So sánh độ lệch ngang vs dọc để xác định hướng
    if (Math.abs(diffX) > Math.abs(diffY)) {
        // Vuốt ngang
        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
            if (diffX > 0) {
                gameEngine.changeDirection(new Vector2D(cellSize, 0));  // Phải
            } else {
                gameEngine.changeDirection(new Vector2D(-cellSize, 0)); // Trái
            }
        }
    } else {
        // Vuốt dọc - xử lý tương tự
    }
    return true;
}
```

**Giải quyết vấn đề gì:**
- Chuyển đổi cử chỉ vuốt thành hướng di chuyển
- Sử dụng ngưỡng để lọc vuốt không chủ đích
- Xác định hướng chính xác (ngang > dọc hay ngược lại)

---

### 4.5. GameView - Vẽ giao diện

#### `drawSnake()` - Vẽ con rắn

```java
private void drawSnake(Canvas canvas) {
    List<Vector2D> body = gameEngine.getSnake().getBody();
    int cellSize = gameEngine.getCellSize();
    Vector2D currentDir = gameEngine.getSnake().getDirection();

    for (int i = 0; i < body.size(); i++) {
        Vector2D segment = body.get(i);

        if (i == 0) {                       // Đầu rắn
            Bitmap headBmp = AssetManager.snakeHead;
            if (headBmp != null) {
                drawRotatedHead(canvas, headBmp, segment, currentDir, cellSize);
            } else {
                // Fallback: vẽ hình tròn
                canvas.drawCircle(...);
            }
        } else {                            // Thân rắn
            Bitmap bodyBmp = AssetManager.snakeBody;
            if (bodyBmp != null) {
                canvas.drawBitmap(bodyBmp, null, destRect, null);
            } else {
                canvas.drawCircle(...);
            }
        }
    }
}
```

**Giải quyết vấn đề gì:**
- Vẽ đầu rắn khác với thân (xoay theo hướng)
- Fallback sang hình học cơ bản nếu không có ảnh
- Lặp qua tất cả đốt của rắn

---

### 4.6. SettingManager - Singleton Pattern

```java
public class SettingManager {
    private static SettingManager instance;
    private SharedPreferences prefs;

    private SettingManager() {}             // Private constructor

    public static SettingManager getInstance() {
        if (instance == null) {
            instance = new SettingManager();
        }
        return instance;
    }

    public void init(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
}
```

**Giải quyết vấn đề gì:**
- Đảm bảo chỉ có 1 instance quản lý cài đặt
- Truy cập toàn cục từ mọi nơi trong app
- Lưu trữ persistent với SharedPreferences

---

## 5. TRẠNG THÁI GAME (GameState)

```java
public enum GameState {
    MENU,       // Màn hình chờ
    PLAYING,    // Đang chơi
    PAUSED,     // Tạm dừng
    GAME_OVER   // Kết thúc
}
```

**Sơ đồ chuyển trạng thái:**

```
       ┌──────────┐
       │   MENU   │ ◄────────────────┐
       └────┬─────┘                  │
            │ tap to start           │ return to menu
            ▼                        │
       ┌──────────┐            ┌─────┴─────┐
       │ PLAYING  │ ◄─────────►│  PAUSED   │
       └────┬─────┘  toggle    └───────────┘
            │
            │ collision
            ▼
       ┌──────────┐
       │ GAME_OVER│ ──────────► MENU (restart)
       └──────────┘
```

---

## 6. CHẾ ĐỘ KHÓ (DIFFICULTY)

| Chế độ | Tốc độ ban đầu | Va chạm tường | Mô tả |
|--------|---------------|---------------|-------|
| Easy (0) | 5 FPS | Xuyên qua | Phù hợp người mới chơi |
| Hard (1) | 8 FPS | Chết | Thử thách cao |

**Tốc độ tăng dần:**
```java
currentFPS = Math.min(baseFPS + score / 50, MAX_FPS);
// Cứ 50 điểm tăng 1 FPS, tối đa 15 FPS
```

---

## 7. TÀI NGUYÊN (ASSETS)

### 7.1. Cấu trúc thư mục assets

```
app/src/main/assets/
├── fonts/
│   └── yoster.ttf              # Font game
├── images/
│   ├── snake_head.png          # Đầu rắn
│   ├── snake_body.png          # Thân rắn
│   ├── food.png                # Thức ăn
│   ├── button.png              # Nút bấm
│   ├── game_background.png     # Nền game
│   ├── menu_background.png     # Nền menu
│   ├── pause_game.png          # Icon pause
│   ├── volume_game.png         # Icon âm lượng
│   ├── snake_logo.png          # Logo
│   └── snake_title.png         # Tiêu đề
└── sounds/
    ├── 01 The Piano.mp3        # Nhạc menu
    ├── 02 99 Problems.mp3      # Nhạc game
    ├── sfx_eat.wav             # Âm thanh ăn
    └── sfx_die.wav             # Âm thanh chết
```

### 7.2. Load tài nguyên

```java
// Trong AssetManager.java
private Bitmap loadBitmap(String fileName) {
    InputStream is = context.getAssets().open("images/" + fileName);
    Bitmap bitmap = BitmapFactory.decodeStream(is);
    is.close();
    return bitmap;
}

private Typeface loadFont(String fileName) {
    return Typeface.createFromAsset(context.getAssets(), "fonts/" + fileName);
}
```

---

## 8. TỔNG KẾT

### Các Design Pattern sử dụng:
1. **Singleton** - SettingManager, SoundManager
2. **MVC** - Phân tách Model/View/Controller
3. **Observer** - ScoreUpdateListener, OnPauseActionListener
4. **Game Loop** - GameThread với fixed timestep

### Điểm nổi bật so với bài học cơ bản:
1. ✅ Sử dụng Canvas API để vẽ đồ họa 2D
2. ✅ Game loop riêng với Thread
3. ✅ GestureDetector cho điều khiển vuốt
4. ✅ MediaPlayer + SoundPool cho âm thanh
5. ✅ Xoay hình ảnh theo hướng di chuyển
6. ✅ Tốc độ game động theo điểm số
7. ✅ Singleton pattern cho quản lý cài đặt
8. ✅ SharedPreferences lưu điểm cao

---

*Tài liệu được tạo tự động dựa trên phân tích mã nguồn dự án*
