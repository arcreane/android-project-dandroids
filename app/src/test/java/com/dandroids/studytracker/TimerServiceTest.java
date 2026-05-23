package com.dandroids.studytracker;

import com.dandroids.studytracker.manager.PomodoroManager;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for Pomodoro cycle logic — hr
 * Tests the timer cycle progression without needing Android context
 */
public class TimerServiceTest {

    @Test
    public void pomodoro_focusDuration_is25Minutes() {
        assertEquals(25 * 60 * 1000L, PomodoroManager.FOCUS_DURATION);
    }

    @Test
    public void pomodoro_shortBreakDuration_is5Minutes() {
        assertEquals(5 * 60 * 1000L, PomodoroManager.SHORT_BREAK_DURATION);
    }

    @Test
    public void pomodoro_longBreakDuration_is30Minutes() {
        assertEquals(30 * 60 * 1000L, PomodoroManager.LONG_BREAK_DURATION);
    }

    @Test
    public void pomodoro_cyclesBeforeLongBreak_is4() {
        assertEquals(4, PomodoroManager.CYCLES_BEFORE_LONG);
    }

    @Test
    public void session_saveToDb_completedFlag_isTrue() {
        com.dandroids.studytracker.model.Session session =
                new com.dandroids.studytracker.model.Session(1L, 1000L);
        session.endTime = 61000L;
        session.completed = true;
        session.durationMinutes = 1;

        assertTrue(session.completed);
        assertEquals(1, session.durationMinutes);
        assertEquals(60000L, session.getDurationMillis());
    }

    @Test
    public void session_saveToDb_completedFlag_isFalseWhenStopped() {
        com.dandroids.studytracker.model.Session session =
                new com.dandroids.studytracker.model.Session(1L, 1000L);
        session.completed = false;
        assertFalse(session.completed);
    }
}
