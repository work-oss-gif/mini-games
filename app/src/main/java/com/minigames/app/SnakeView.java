package com.minigames.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;

/** Swipe to steer, eat the red dots, do not hit the walls or yourself. Tap to restart. */
class SnakeView extends SwipeView {
    private static final int COLS = 18;

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final SharedPreferences prefs;
    private final float density;
    private SnakeGame game;
    private int cell;
    private int top;
    private int best;
    private boolean running;

    private final Runnable tick = new Runnable() {
        @Override
        public void run() {
            if (!running || game == null) return;
            game.step();
            if (!game.alive && game.score > best) {
                best = game.score;
                prefs.edit().putInt("snake_best", best).apply();
            }
            invalidate();
            if (game.alive) handler.postDelayed(this, Math.max(70, 160 - game.score * 4));
        }
    };

    SnakeView(Context context, SharedPreferences prefs) {
        super(context);
        this.prefs = prefs;
        this.density = context.getResources().getDisplayMetrics().density;
        this.best = prefs.getInt("snake_best", 0);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        cell = w / COLS;
        top = (int) (72 * density);
        int rows = Math.max(5, (h - top - (int) (16 * density)) / cell);
        game = new SnakeGame(COLS, rows);
        startLoop();
    }

    private void startLoop() {
        handler.removeCallbacks(tick);
        running = true;
        handler.postDelayed(tick, 300);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility != VISIBLE) {
            running = false;
            handler.removeCallbacks(tick);
        } else if (game != null && game.alive) {
            startLoop();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        running = false;
        handler.removeCallbacks(tick);
    }

    @Override
    void onSwipe(int dir) {
        if (game != null) game.turn(dir);
    }

    @Override
    void onTap() {
        if (game != null && !game.alive) {
            game = new SnakeGame(game.cols, game.rows);
            startLoop();
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.rgb(18, 24, 38));
        if (game == null) return;
        int left = (getWidth() - cell * COLS) / 2;

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(28, 38, 58));
        canvas.drawRect(left, top, left + cell * game.cols, top + cell * game.rows, paint);

        paint.setColor(Color.rgb(235, 90, 90));
        canvas.drawCircle(left + (game.foodX + 0.5f) * cell, top + (game.foodY + 0.5f) * cell, cell * 0.38f, paint);

        boolean first = true;
        for (int[] seg : game.body) {
            paint.setColor(first ? Color.rgb(120, 230, 140) : Color.rgb(70, 190, 100));
            float pad = cell * 0.06f;
            canvas.drawRect(left + seg[0] * cell + pad, top + seg[1] * cell + pad,
                    left + (seg[0] + 1) * cell - pad, top + (seg[1] + 1) * cell - pad, paint);
            first = false;
        }

        paint.setColor(Color.WHITE);
        paint.setTextSize(22 * density);
        canvas.drawText("Score " + game.score + "   Best " + Math.max(best, game.score), left, 40 * density, paint);

        if (!game.alive) {
            paint.setColor(Color.argb(190, 0, 0, 0));
            canvas.drawRect(left, top, left + cell * game.cols, top + cell * game.rows, paint);
            paint.setColor(Color.WHITE);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(30 * density);
            float cx = getWidth() / 2f;
            float cy = top + cell * game.rows / 2f;
            canvas.drawText("Game over", cx, cy, paint);
            paint.setTextSize(18 * density);
            canvas.drawText("Tap to play again", cx, cy + 34 * density, paint);
            paint.setTextAlign(Paint.Align.LEFT);
        }
    }
}
