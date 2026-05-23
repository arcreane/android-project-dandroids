package com.dandroids.studytracker.manager;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * PomodoroManager — designed and built by MSJ
 *
 * Controls the Pomodoro cycle:
 * [Focus 25min] → [Short Break 5min] × 3
 * → [Focus 25min] → [Long Break 30min]
 * → repeat
 *
 * Persists cycle state in SharedPreferences
 * so progress survives app restarts.
 */
public class PomodoroManager {

    // Durations in milliseconds
    public static final long FOCUS_DURATION       = 25 * 60 * 1000L;
    public static final long SHORT_BREAK_DURATION =  5 * 60 * 1000L;
    public static final long LONG_BREAK_DURATION  = 30 * 60 * 1000L;
    public static final int  CYCLES_BEFORE_LONG   = 4;

    public enum SessionType {
        FOCUS,
        SHORT_BREAK,
        LONG_BREAK
    }

    private static final String PREFS_NAME     = "PomodoroPrefs";
    private static final String KEY_CYCLE      = "current_cycle";
    private static final String KEY_TYPE       = "current_type";

    private int currentCycle;
    private SessionType currentType;
    private final SharedPreferences prefs;

    public PomodoroManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        // Restore state from SharedPreferences
        currentCycle = prefs.getInt(KEY_CYCLE, 1);
        String savedType = prefs.getString(KEY_TYPE, SessionType.FOCUS.name());
        currentType = SessionType.valueOf(savedType);
    }

    /**
     * Call this when a session finishes.
     * Advances to the next session type and saves state.
     */
    public void advance() {
        if (currentType == SessionType.FOCUS) {
            if (currentCycle >= CYCLES_BEFORE_LONG) {
                currentType = SessionType.LONG_BREAK;
            } else {
                currentType = SessionType.SHORT_BREAK;
            }
        } else if (currentType == SessionType.SHORT_BREAK) {
            currentCycle++;
            currentType = SessionType.FOCUS;
        } else {
            // After long break → reset
            currentCycle = 1;
            currentType = SessionType.FOCUS;
        }
        saveState();
    }

    /**
     * Returns duration in millis for the current session.
     */
    public long getCurrentDuration() {
        switch (currentType) {
            case SHORT_BREAK: return SHORT_BREAK_DURATION;
            case LONG_BREAK:  return LONG_BREAK_DURATION;
            default:          return FOCUS_DURATION;
        }
    }

    /**
     * Returns a human readable label for the current session.
     */
    public String getCurrentLabel() {
        switch (currentType) {
            case SHORT_BREAK: return "Short Break";
            case LONG_BREAK:  return "Long Break";
            default:          return "Focus — cycle " + currentCycle;
        }
    }

    public SessionType getCurrentType() { return currentType; }
    public int getCurrentCycle()        { return currentCycle; }

    /**
     * Resets to the beginning of cycle 1.
     */
    public void reset() {
        currentCycle = 1;
        currentType  = SessionType.FOCUS;
        saveState();
    }

    private void saveState() {
        prefs.edit()
                .putInt(KEY_CYCLE, currentCycle)
                .putString(KEY_TYPE, currentType.name())
                .apply();
    }
}
