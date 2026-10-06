package com.minigames.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Flip two cards at a time and find all eight pairs in as few moves as you can. */
class MemoryScreen extends LinearLayout {
    private static final String[] SYMBOLS = {
        "🍎", "🍌", "🍇", "🍒",
        "🍓", "🍉", "🍊", "🍋"
    };
    private static final int CARD_BACK = Color.rgb(32, 42, 64);
    private static final int CARD_FACE = Color.rgb(60, 80, 120);
    private static final int CARD_DONE = Color.rgb(50, 120, 80);

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final SharedPreferences prefs;
    private final Button[] cards = new Button[16];
    private final String[] symbol = new String[16];
    private final boolean[] matched = new boolean[16];
    private final TextView status;
    private int first = -1;
    private boolean locked;
    private int moves;
    private int pairs;

    MemoryScreen(Context context, SharedPreferences prefs) {
        super(context);
        this.prefs = prefs;
        final float d = context.getResources().getDisplayMetrics().density;
        setOrientation(VERTICAL);
        setBackgroundColor(Color.rgb(18, 24, 38));
        setPadding((int) (12 * d), (int) (28 * d), (int) (12 * d), (int) (12 * d));

        status = new TextView(context);
        status.setTextColor(Color.WHITE);
        status.setTextSize(20);
        status.setPadding(0, 0, 0, (int) (12 * d));
        addView(status);

        for (int row = 0; row < 4; row++) {
            LinearLayout line = new LinearLayout(context);
            line.setOrientation(HORIZONTAL);
            addView(line, new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f));
            for (int col = 0; col < 4; col++) {
                final int index = row * 4 + col;
                Button b = new Button(context);
                b.setAllCaps(false);
                b.setTextSize(32);
                b.setTextColor(Color.WHITE);
                b.setGravity(Gravity.CENTER);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        flip(index);
                    }
                });
                LayoutParams lp = new LayoutParams(0, LayoutParams.MATCH_PARENT, 1f);
                lp.setMargins((int) (4 * d), (int) (4 * d), (int) (4 * d), (int) (4 * d));
                line.addView(b, lp);
                cards[index] = b;
            }
        }

        Button again = new Button(context);
        again.setAllCaps(false);
        again.setText("New game");
        again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newGame();
            }
        });
        addView(again, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        newGame();
    }

    private void newGame() {
        handler.removeCallbacksAndMessages(null);
        List<String> deck = new ArrayList<String>();
        for (String s : SYMBOLS) {
            deck.add(s);
            deck.add(s);
        }
        Collections.shuffle(deck);
        for (int i = 0; i < 16; i++) {
            symbol[i] = deck.get(i);
            matched[i] = false;
            cards[i].setText("?");
            cards[i].setBackgroundColor(CARD_BACK);
        }
        first = -1;
        locked = false;
        moves = 0;
        pairs = 0;
        updateStatus(null);
    }

    private void updateStatus(String message) {
        int best = prefs.getInt("memory_best", 0);
        String bestText = best == 0 ? "-" : String.valueOf(best);
        status.setText(message != null ? message : "Moves " + moves + "   Best " + bestText);
    }

    private void flip(int i) {
        if (locked || matched[i] || i == first) return;
        cards[i].setText(symbol[i]);
        cards[i].setBackgroundColor(CARD_FACE);
        if (first < 0) {
            first = i;
            return;
        }
        final int a = first;
        final int b = i;
        first = -1;
        moves++;
        if (symbol[a].equals(symbol[b])) {
            matched[a] = true;
            matched[b] = true;
            cards[a].setBackgroundColor(CARD_DONE);
            cards[b].setBackgroundColor(CARD_DONE);
            pairs++;
            if (pairs == SYMBOLS.length) {
                int best = prefs.getInt("memory_best", 0);
                if (best == 0 || moves < best) prefs.edit().putInt("memory_best", moves).apply();
                updateStatus("You won in " + moves + " moves!");
            } else {
                updateStatus(null);
            }
        } else {
            locked = true;
            updateStatus(null);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    cards[a].setText("?");
                    cards[b].setText("?");
                    cards[a].setBackgroundColor(CARD_BACK);
                    cards[b].setBackgroundColor(CARD_BACK);
                    locked = false;
                }
            }, 800);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacksAndMessages(null);
    }
}
