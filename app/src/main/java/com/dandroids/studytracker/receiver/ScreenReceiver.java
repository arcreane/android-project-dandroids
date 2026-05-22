package com.dandroids.studytracker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ScreenReceiver extends BroadcastReceiver {

    private static final String TAG = "ScreenReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) return;

        switch (intent.getAction()) {
            case Intent.ACTION_SCREEN_OFF:
                Log.d(TAG, "Screen OFF — timer continues in background");
                break;
            case Intent.ACTION_SCREEN_ON:
                Log.d(TAG, "Screen ON — user returned");
                break;
            case Intent.ACTION_USER_PRESENT:
                Log.d(TAG, "User unlocked device");
                break;
        }
    }
}
