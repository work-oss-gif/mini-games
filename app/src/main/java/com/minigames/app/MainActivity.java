package com.minigames.app;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static final int BG = Color.rgb(18, 24, 38);
    private static final int CARD = Color.rgb(32, 42, 64);

    private SharedPreferences prefs;
    private float density;
    private boolean inGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences("scores", MODE_PRIVATE);
        density = getResources().getDisplayMetrics().density;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!inGame) showMenu();
    }

    @Override
    public void onBackPressed() {
        if (inGame) {
            inGame = false;
            showMenu();
        } else {
            super.onBackPressed();
        }
    }

    private int dp(int v) {
        return (int) (v * density);
    }

    private void showMenu() {
        inGame = false;
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setBackgroundColor(BG);
        page.setPadding(dp(16), dp(40), dp(16), dp(16));

        TextView title = new TextView(this);
        title.setText("Mini Games");
        title.setTextSize(30);
        title.setTextColor(Color.WHITE);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        page.addView(title);

        TextView sub = new TextView(this);
        sub.setText("Pick a game");
        sub.setTextSize(16);
        sub.setTextColor(Color.LTGRAY);
        sub.setPadding(0, dp(4), 0, dp(12));
        page.addView(sub);

        int memBest = prefs.getInt("memory_best", 0);
        addGame(page, "🐍  Snake\nBest score: " + prefs.getInt("snake_best", 0), 0);
        addGame(page, "🔢  2048\nBest score: " + prefs.getInt("g2048_best", 0), 1);
        addGame(page, "🃏  Memory Match\nFewest moves: " + (memBest == 0 ? "-" : String.valueOf(memBest)), 2);
        setContentView(page);
    }

    private void addGame(LinearLayout page, String label, final int which) {
        Button b = new Button(this);
        b.setText(label);
        b.setAllCaps(false);
        b.setTextSize(18);
        b.setTextColor(Color.WHITE);
        b.setBackgroundColor(CARD);
        b.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        b.setPadding(dp(18), dp(18), dp(18), dp(18));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, dp(8), 0, dp(8));
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(which);
            }
        });
        page.addView(b, lp);
    }

    private void startGame(int which) {
        inGame = true;
        if (which == 0) setContentView(new SnakeView(this, prefs));
        else if (which == 1) setContentView(new Game2048View(this, prefs));
        else setContentView(new MemoryScreen(this, prefs));
    }
}
