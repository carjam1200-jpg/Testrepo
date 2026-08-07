package com.carjam.aerotheme;

import android.app.Activity;
import android.app.UiModeManager;
import android.os.Bundle;
import android.os.Build;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import rikka.shizuku.Shizuku;

public class MainActivity extends Activity {
    private static final int REQUEST_CODE = 42;
    private TextView status;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        showThemeLibrary();
    }

    private TextView label(String text, float size) {
        TextView v = new TextView(this);
        v.setText(text);
        v.setTextColor(Color.WHITE);
        v.setTextSize(size);
        v.setPadding(0, 12, 0, 12);
        return v;
    }

    private LinearLayout themeCard(String name, String description, int accent, final boolean dark) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(28, 24, 28, 24);

        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.rgb(28, 36, 46));
        background.setCornerRadius(28);
        background.setStroke(3, accent);
        card.setBackground(background);

        TextView cover = label("✦  " + name + "  ✦", 24);
        cover.setTextColor(accent);
        cover.setGravity(Gravity.CENTER);
        card.addView(cover);

        TextView desc = label(description, 15);
        card.addView(desc);

        Button preview = new Button(this);
        preview.setText("Preview");
        preview.setOnClickListener(v -> status.setText("Preview: " + name + "\n" + description));
        card.addView(preview);

        Button apply = new Button(this);
        apply.setText("Apply " + name);
        apply.setOnClickListener(v -> applyTheme(name, dark));
        card.addView(apply);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 28);
        card.setLayoutParams(params);
        return card;
    }

    private void showThemeLibrary() {
        ScrollView scroll = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(32, 48, 32, 48);
        root.setBackgroundColor(Color.rgb(16, 24, 32));

        TextView title = label("Aero Android Theme", 30);
        title.setTextColor(Color.rgb(79, 195, 247));
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        TextView subtitle = label("Original theme library • One UI 8 friendly", 15);
        subtitle.setGravity(Gravity.CENTER);
        root.addView(subtitle);

        status = label("Checking Shizuku…", 15);
        status.setGravity(Gravity.CENTER);
        root.addView(status);

        Button connect = new Button(this);
        connect.setText("Connect to Shizuku");
        connect.setOnClickListener(v -> requestShizuku());
        root.addView(connect);

        root.addView(themeCard("Aero Dark", "Deep Aero interface with a cool blue accent.", Color.rgb(79, 195, 247), true));
        root.addView(themeCard("Aero Light", "Bright Aero interface with a clean system feel.", Color.rgb(255, 193, 7), false));
        root.addView(themeCard("Aero AMOLED", "Dark-first Aero profile designed for OLED screens.", Color.rgb(0, 188, 212), true));
        root.addView(themeCard("Aero Night", "Low-light Aero profile with a subtle blue glow.", Color.rgb(103, 58, 183), true));
        root.addView(themeCard("Aero Glass", "Frosted-glass inspired Aero visual profile.", Color.rgb(128, 203, 196), true));

        scroll.addView(root);
        setContentView(scroll);
        updateShizukuStatus();
    }

    private void updateShizukuStatus() {
        if (!Shizuku.pingBinder()) {
            status.setText("Shizuku: not running");
        } else if (Shizuku.checkSelfPermission() != 0) {
            status.setText("Shizuku: connected • permission needed");
        } else {
            status.setText("Shizuku: connected ✓");
        }
    }

    private void requestShizuku() {
        if (!Shizuku.pingBinder()) {
            status.setText("Shizuku is not running. Start Shizuku and try again.");
            return;
        }
        if (Shizuku.checkSelfPermission() != 0) {
            Shizuku.requestPermission(REQUEST_CODE);
            status.setText("Shizuku permission requested.");
        } else {
            status.setText("Shizuku connected ✓");
        }
    }

    private void applyTheme(String name, boolean dark) {
        if (!Shizuku.pingBinder()) {
            status.setText("Start Shizuku before applying " + name + ".");
            return;
        }
        if (Shizuku.checkSelfPermission() != 0) {
            Shizuku.requestPermission(REQUEST_CODE);
            status.setText("Grant Shizuku permission, then tap Apply again.");
            return;
        }

        UiModeManager uiModeManager = getSystemService(UiModeManager.class);
        if (uiModeManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            uiModeManager.setApplicationNightMode(dark
                    ? UiModeManager.MODE_NIGHT_YES
                    : UiModeManager.MODE_NIGHT_NO);
            status.setText(name + " selected ✓\nAero's app theme was updated.");
        } else {
            status.setText(name + " selected. Android version does not support the full Aero mode API.");
        }
    }
}
