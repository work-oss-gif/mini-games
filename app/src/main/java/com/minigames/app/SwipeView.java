package com.minigames.app;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

/** A View that reports swipes (0 up, 1 right, 2 down, 3 left) and taps. */
abstract class SwipeView extends View {
    private float startX;
    private float startY;
    private boolean swiped;
    private final float minDistance;

    SwipeView(Context context) {
        super(context);
        minDistance = 32 * context.getResources().getDisplayMetrics().density;
    }

    abstract void onSwipe(int dir);

    abstract void onTap();

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                startX = e.getX();
                startY = e.getY();
                swiped = false;
                return true;
            case MotionEvent.ACTION_MOVE: {
                float dx = e.getX() - startX;
                float dy = e.getY() - startY;
                if (Math.abs(dx) >= minDistance || Math.abs(dy) >= minDistance) {
                    if (Math.abs(dx) > Math.abs(dy)) onSwipe(dx > 0 ? 1 : 3);
                    else onSwipe(dy > 0 ? 2 : 0);
                    swiped = true;
                    // Start measuring again, so one long drag can make several turns.
                    startX = e.getX();
                    startY = e.getY();
                }
                return true;
            }
            case MotionEvent.ACTION_UP:
                if (!swiped) {
                    performClick();
                    onTap();
                }
                return true;
            default:
                return true;
        }
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
