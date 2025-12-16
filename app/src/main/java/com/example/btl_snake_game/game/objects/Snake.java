package com.example.btl_snake_game.game.objects;

import com.example.btl_snake_game.util.Vector2D;
import java.util.ArrayList;
import java.util.List;

public class Snake {
    private List<Vector2D> body;
    private Vector2D direction;
    private Vector2D nextDirection;
    private int cellSize;
    private boolean growing;

    public Snake(int startX, int startY, int cellSize) {
        this.cellSize = cellSize;
        this.body = new ArrayList<>();
        this.body.add(new Vector2D(startX, startY));
        this.direction = new Vector2D(cellSize, 0); // Di chuyển sang phải
        this.nextDirection = direction;
        this.growing = false;
    }

    public void move() {
        direction = nextDirection;
        Vector2D head = body.get(0);
        Vector2D newHead = new Vector2D(head.getX() + direction.getX(),
                head.getY() + direction.getY());

        body.add(0, newHead);

        if (!growing) {
            body.remove(body.size() - 1);
        } else {
            growing = false;
        }
    }

    public void setDirection(Vector2D newDirection) {
        // Không cho phép đi ngược lại
        if (direction.getX() + newDirection.getX() != 0 ||
                direction.getY() + newDirection.getY() != 0) {
            this.nextDirection = newDirection;
        }
    }

    public void grow() {
        growing = true;
    }

    public Vector2D getHead() {
        return body.get(0);
    }

    public List<Vector2D> getBody() {
        return body;
    }

    public boolean checkSelfCollision() {
        Vector2D head = getHead();
        for (int i = 1; i < body.size(); i++) {
            if (head.equals(body.get(i))) {
                return true;
            }
        }
        return false;
    }

    public boolean checkWallCollision(int gridWidth, int gridHeight) {
        Vector2D head = getHead();
        return head.getX() < 0 || head.getX() >= gridWidth * cellSize ||
                head.getY() < 0 || head.getY() >= gridHeight * cellSize;
    }

    public int getLength() {
        return body.size();
    }
}