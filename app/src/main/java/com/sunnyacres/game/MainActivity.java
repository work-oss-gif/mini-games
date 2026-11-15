package com.sunnyacres.game;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/** Shows the 3D farm game (assets/index.html) full screen. */
public class MainActivity extends Activity {
    private WebView web;

    private boolean isDark() {
        int mode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return mode == Configuration.UI_MODE_NIGHT_YES;
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        web = new WebView(this);
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);      // saves the farm between sessions
        s.setAllowFileAccess(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        web.setWebViewClient(new WebViewClient() {
            @Override public void onPageFinished(WebView v, String url) { pushTheme(); }
        });
        setContentView(web);
        applyColors();
        web.loadUrl("file:///android_asset/index.html");
    }

    /** Tells the game page whether to use the day (light) or evening (dark) look. */
    private void pushTheme() {
        web.evaluateJavascript("window.setTheme&&setTheme('" + (isDark() ? "dark" : "light") + "')", null);
    }

    private void applyColors() {
        int bg = isDark() ? 0xFF1A2340 : 0xFF9FD8F5;
        web.setBackgroundColor(bg);
        Window w = getWindow();
        w.setStatusBarColor(bg);
        w.setNavigationBarColor(isDark() ? 0xFF121417 : 0xFF6CBF4A);
        View d = w.getDecorView();
        int f = d.getSystemUiVisibility();
        if (Build.VERSION.SDK_INT >= 23) {
            f = isDark() ? (f & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) : (f | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        d.setSystemUiVisibility(f);
    }

    @Override
    public void onConfigurationChanged(Configuration c) {
        super.onConfigurationChanged(c);
        applyColors();
        pushTheme();
    }

    @Override
    public void onBackPressed() {
        web.evaluateJavascript("window.appBack&&appBack()", r -> {
            if (!"true".equals(r)) MainActivity.super.onBackPressed();
        });
    }

    // Stop the music when the app goes to the background, start it again on return.
    @Override protected void onPause() {
        web.evaluateJavascript("window.appPause&&appPause()", null);
        super.onPause(); web.onPause();
    }
    @Override protected void onResume() {
        super.onResume(); web.onResume();
        web.evaluateJavascript("window.appResume&&appResume()", null);
    }
}
