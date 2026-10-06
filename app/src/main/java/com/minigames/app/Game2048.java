package com.minigames.app;

import java.util.Random;

/** 2048 rules only. Directions: 0 up, 1 right, 2 down, 3 left. */
final class Game2048 {
    final int[][] grid = new int[4][4];
    int score = 0;
    private final Random rnd = new Random();

    Game2048() {
        spawn();
        spawn();
    }

    /** Slides and merges the tiles. Returns true if anything moved (and then adds a new tile). */
    boolean move(int dir) {
        boolean moved = false;
        for (int i = 0; i < 4; i++) {
            int[] line = new int[4];
            for (int j = 0; j < 4; j++) line[j] = grid[row(dir, i, j)][col(dir, i, j)];
            int[] result = slide(line);
            for (int j = 0; j < 4; j++) {
                int r = row(dir, i, j);
                int c = col(dir, i, j);
                if (grid[r][c] != result[j]) moved = true;
                grid[r][c] = result[j];
            }
        }
        if (moved) spawn();
        return moved;
    }

    // Line index j = 0 is the wall the tiles slide toward.
    private static int row(int dir, int i, int j) {
        if (dir == 0) return j;
        if (dir == 2) return 3 - j;
        return i;
    }

    private static int col(int dir, int i, int j) {
        if (dir == 3) return j;
        if (dir == 1) return 3 - j;
        return i;
    }

    private int[] slide(int[] line) {
        int[] out = new int[4];
        int n = 0;
        int pending = 0;
        for (int v : line) {
            if (v == 0) continue;
            if (pending == v) {
                out[n++] = v * 2;
                score += v * 2;
                pending = 0;
            } else {
                if (pending != 0) out[n++] = pending;
                pending = v;
            }
        }
        if (pending != 0) out[n] = pending;
        return out;
    }

    private void spawn() {
        int empty = 0;
        for (int[] r : grid) for (int v : r) if (v == 0) empty++;
        if (empty == 0) return;
        int pick = rnd.nextInt(empty);
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                if (grid[r][c] == 0 && pick-- == 0) {
                    grid[r][c] = rnd.nextInt(10) == 0 ? 4 : 2;
                    return;
                }
            }
        }
    }

    boolean isOver() {
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                if (grid[r][c] == 0) return false;
                if (c < 3 && grid[r][c] == grid[r][c + 1]) return false;
                if (r < 3 && grid[r][c] == grid[r + 1][c]) return false;
            }
        }
        return true;
    }

    int maxTile() {
        int m = 0;
        for (int[] r : grid) for (int v : r) m = Math.max(m, v);
        return m;
    }
}
