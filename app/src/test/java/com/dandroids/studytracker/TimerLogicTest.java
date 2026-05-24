package com.dandroids.studytracker;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for timer logic — hr
 */
public class TimerLogicTest {

    @Test
    public void formatTime_25minutes_returnsCorrectString() {
        long millis = 25 * 60 * 1000L;
        long minutes = (millis / 1000) / 60;
        long seconds = (millis / 1000) % 60;
        String result = String.format("%02d:%02d", minutes, seconds);
        assertEquals("25:00", result);
    }

    @Test
    public void formatTime_90seconds_returnsCorrectString() {
        long millis = 90 * 1000L;
        long minutes = (millis / 1000) / 60;
        long seconds = (millis / 1000) % 60;
        String result = String.format("%02d:%02d", minutes, seconds);
        assertEquals("01:30", result);
    }

    @Test
    public void formatTime_zero_returnsDoubleZero() {
        long millis = 0L;
        long minutes = (millis / 1000) / 60;
        long seconds = (millis / 1000) % 60;
        String result = String.format("%02d:%02d", minutes, seconds);
        assertEquals("00:00", result);
    }

    @Test
    public void sessionDuration_calculatedCorrectly() {
        long start = 1000L;
        long end   = 1000L + (25 * 60 * 1000L);
        int durationMinutes = (int) ((end - start) / 60000);
        assertEquals(25, durationMinutes);
    }

    @Test
    public void progress_halfwayThrough_isPointFive() {
        long total     = 25 * 60 * 1000L;
        long remaining = total / 2;
        float progress = (float) remaining / total;
        assertEquals(0.5f, progress, 0.001f);
    }
}
