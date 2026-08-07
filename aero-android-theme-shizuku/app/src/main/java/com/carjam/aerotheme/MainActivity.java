package com.carjam.aerotheme;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import rikka.shizuku.Shizuku;

public class MainActivity extends Activity {
    private static final int REQUEST_CODE = 42;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(48, 72, 48, 48);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setBackgroundColor(Color.rgb(16, 24, 32));

        TextView title = new TextView(this);
        title.setText("Aero Android Theme");
        title.setTextColor(Color.rgb(79, 195, 247));
        title.setTextSize(28);
        title.setGravity(Gravity.CENTER);

        TextView status = new TextView(this);
        status.setTextColor(Color.WHITE);
        status.setTextSize(16);
        status.setGravity(Gravity.CENTER);
        status.setPadding(0, 24, 0, 32);
        status.setText("Original Shizuku-powered theme controller");

        Button permission = new Button(this);
        permission.setText("Connect to Shizuku");
        permission.setOnClickListener(v -> requestShizuku(status));

        Button dark = new Button(this);
        dark.setText("Apply Aero Dark Mode");
        dark.setOnClickListener(v -> runUiMode("yes", status));

        Button light = new Button(this);
        light.setText("Apply Aero Light Mode");
        light.setOnClickListener(v -> runUiMode("no", status));

        root.addView(title);
        root.addView(status);
        root.addView(permission);
        root.addView(dark);
        root.addView(light);
        setContentView(root);
    }

    private void requestShizuku(TextView status) {
        if (!Shizuku.pingBinder()) {
            status.setText("Shizuku is not running. Start Shizuku, then try again.");
            return;
        }
        if (Shizuku.checkSelfPermission() != 0) {
            Shizuku.requestPermission(REQUEST_CODE);
            status.setText("Shizuku permission requested.");
        } else {
            status.setText("Shizuku connected (UID " + Shizuku.getUid() + ").");
        }
    }

    private void runUiMode(String night, TextView status) {
        if (!Shizuku.pingBinder()) {
            status.setText("Start Shizuku first.");
            return;
        }
        if (Shizuku.checkSelfPermission() != 0) {
            Shizuku.requestPermission(REQUEST_CODE);
            status.setText("Grant Aero Android Theme Shizuku permission, then retry.");
            return;
        }
        try {
            Process process = Shizuku.newProcess(
                    new String[]{"cmd", "uimode", "night", night}, null, null);
            int result = process.waitFor();
            status.setText(result == 0
                    ? "Aero mode applied successfully."
                    : "Android rejected the theme command (exit " + result + ").");
        } catch (Exception e) {
            status.setText("Theme command failed: " + e.getClass().getSimpleName());
        }
    }
}
