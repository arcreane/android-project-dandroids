package com.dandroids.studytracker.activities;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.dandroids.studytracker.R;

public abstract class BaseActivity extends AppCompatActivity {

    public static final String PREFS_NAME    = "StudyTrackerPrefs";
    public static final String KEY_THEME     = "is_focus_theme";

    protected SharedPreferences prefs;
    private boolean isFocusTheme;

    private final ActivityResultLauncher<String> notificationPermissionLauncher =
            registerForActivityResultLauncher(
                    new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        // permission granted or denied — timer notification
                        // will still work but silently if denied
                    });

    private ActivityResultLauncher<String> registerForActivityResultLauncher(
            ActivityResultContracts.RequestPermission requestPermission,
            androidx.activity.result.ActivityResultCallback<Boolean> callback) {
        return registerForActivityResult(requestPermission, callback);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Read theme BEFORE super.onCreate and setContentView
        // This prevents the white flash when switching themes
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        isFocusTheme = prefs.getBoolean(KEY_THEME, true);
        applyTheme();

        super.onCreate(savedInstanceState);
        requestNotificationPermission();
    }

    private void applyTheme() {
        if (isFocusTheme) {
            setTheme(R.style.Theme_StudyTracker);
        } else {
            setTheme(R.style.Theme_StudyTracker_Review);
        }
    }

    /**
     * Call this to toggle between Focus and Review theme.
     * Saves preference and recreates the Activity.
     */
    protected void toggleTheme() {
        isFocusTheme = !isFocusTheme;
        prefs.edit().putBoolean(KEY_THEME, isFocusTheme).apply();
        recreate();
    }

    protected boolean isFocusTheme() {
        return isFocusTheme;
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(
                        Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }
}
