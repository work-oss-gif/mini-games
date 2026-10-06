package com.minigames.app;

import java.util.LinkedList;
import java.util.Random;

/** Snake rules only (no drawing), so they are easy to test. Directions: 0 up, 1 right, 2 down, 3 left. */
final class SnakeGame {
    final int cols;
    final int rows;
    final LinkedList<int[]> body = new LinkedList<int[]>(); // head is first
    int dir = 1;
    int nextDir = 1;
    int score = 0;
    boolean alive = true;
    int foodX;
    int foodY;
    private final Random rnd = new Random();

    SnakeGame(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;
        int y = rows / 2;
        body.add(new int[] {cols / 2, y});
        body.add(new int[] {cols / 2 - 1, y});
        body.add(new int[] {cols / 2 - 2, y});
        placeFood();
    }

    /** Ignore a turn that would reverse straight into the snake's own neck. */
    void turn(int d) {
        if ((d + 2) % 4 != dir) nextDir = d;
    }

    void step() {
        if (!alive) return;
        dir = nextDir;
        int[] head = body.getFirst();
        int nx = head[0];
        int ny = head[1];
        if (dir == 0) ny--;
        else if (dir == 1) nx++;
        else if (dir == 2) ny++;
        else nx--;

        if (nx < 0 || ny < 0 || nx >= cols || ny >= rows) {
            alive = false;
            return;
        }
        boolean eat = nx == foodX && ny == foodY;
        // The tail moves away this step, so it is only an obstacle when we grow.
        int last = body.size() - (eat ? 0 : 1);
        for (int i = 0; i < last; i++) {
            int[] seg = body.get(i);
            if (seg[0] == nx && seg[1] == ny) {
                alive = false;
                return;
            }
        }
        body.addFirst(new int[] {nx, ny});
        if (eat) {
            score++;
            placeFood();
        } else {
            body.removeLast();
        }
    }

    private void placeFood() {
        if (body.size() >= cols * rows) {
            alive = false; // the snake fills the board: the player wins
            return;
        }
        while (true) {
            int x = rnd.nextInt(cols);
            int y = rnd.nextInt(rows);
            boolean free = true;
            for (int[] seg : body) {
                if (seg[0] == x && seg[1] == y) {
                    free = false;
                    break;
                }
            }
            if (free) {
                foodX = x;
                foodY = y;
                return;
            }
        }
    }
}
