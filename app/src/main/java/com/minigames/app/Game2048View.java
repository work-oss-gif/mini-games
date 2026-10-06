package com.minigames.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

/** Swipe to slide the tiles. Equal tiles merge. Tap after game over to restart. */
class Game2048View extends SwipeView {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rect = new RectF();
    private final SharedPreferences prefs;
    private final float density;
    private Game2048 game = new Game2048();
    private int best;

    Game2048View(Context context, SharedPreferences prefs) {
        super(context);
        this.prefs = prefs;
        this.density = context.getResources().getDisplayMetrics().density;
        this.best = prefs.getInt("g2048_best", 0);
    }

    @Override
    void onSwipe(int dir) {
        if (game.isOver()) return;
        if (game.move(dir)) {
            if (game.score > best) {
                best = game.score;
                prefs.edit().putInt("g2048_best", best).apply();
            }
            invalidate();
        }
    }

    @Override
    void onTap() {
        if (game.isOver()) {
            game = new Game2048();
            invalidate();
        }
    }

    private int tileColor(int v) {
        switch (v) {
            case 0: return Color.rgb(44, 56, 82);
            case 2: return Color.rgb(90, 130, 190);
            case 4: return Color.rgb(80, 160, 200);
            case 8: return Color.rgb(70, 190, 170);
            case 16: return Color.rgb(90, 200, 120);
            case 32: return Color.rgb(170, 210, 80);
            case 64: return Color.rgb(240, 200, 60);
            case 128: return Color.rgb(250, 160, 50);
            case 256: return Color.rgb(250, 120, 60);
            case 512: return Color.rgb(240, 80, 80);
            case 1024: return Color.rgb(220, 70, 150);
            default: return Color.rgb(170, 90, 220);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.rgb(18, 24, 38));
        float w = getWidth();
        float h = getHeight();
        float margin = 16 * density;
        float board = Math.min(w - 2 * margin, h - 140 * density);
        float left = (w - board) / 2f;
        float top = 90 * density;
        float gap = 8 * density;
        float size = (board - 5 * gap) / 4f;

        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setColor(Color.WHITE);
        paint.setTextSize(22 * density);
        canvas.drawText("Score " + game.score + "   Best " + Math.max(best, game.score), left, 44 * density, paint);

        paint.setColor(Color.rgb(28, 38, 58));
        rect.set(left, top, left + board, top + board);
        canvas.drawRoundRect(rect, 10 * density, 10 * density, paint);

        paint.setTextAlign(Paint.Align.CENTER);
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                int v = game.grid[r][c];
                float x = left + gap + c * (size + gap);
                float y = top + gap + r * (size + gap);
                paint.setColor(tileColor(v));
                rect.set(x, y, x + size, y + size);
                canvas.drawRoundRect(rect, 8 * density, 8 * density, paint);
                if (v != 0) {
                    paint.setColor(Color.WHITE);
                    paint.setTextSize(size * (v < 100 ? 0.45f : v < 1000 ? 0.36f : 0.28f));
                    float baseline = y + size / 2f - (paint.ascent() + paint.descent()) / 2f;
                    canvas.drawText(String.valueOf(v), x + size / 2f, baseline, paint);
                }
            }
        }

        paint.setColor(Color.LTGRAY);
        paint.setTextSize(15 * density);
        String hint = game.isOver() ? "Game over. Tap to play again." : "Swipe to slide. Join equal numbers.";
        canvas.drawText(hint, w / 2f, top + board + 32 * density, paint);
        paint.setTextAlign(Paint.Align.LEFT);
    }
}
