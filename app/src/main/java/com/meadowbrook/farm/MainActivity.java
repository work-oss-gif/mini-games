package com.meadowbrook.farm;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;

// The whole game lives in app/src/main/assets/index.html (3D, built with three.js).
// This screen just shows it full screen. Everything is packed inside the APK,
// so the game works without internet.
public class MainActivity extends Activity {
    private WebView web;

    @Override
    protected void onCreate(Bundle saved) {
        super.onCreate(saved);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        web = new WebView(this);
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true); // lets the game save your farm
        web.loadUrl("file:///android_asset/index.html");
        setContentView(web);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true); // back button hides the game instead of closing it
    }

    @Override
    protected void onPause() { super.onPause(); web.onPause(); }

    @Override
    protected void onResume() { super.onResume(); web.onResume(); }
}
