package com.dandroids.studytracker;

import com.dandroids.studytracker.manager.PomodoroManager;
import org.junit.Test;
import static org.junit.Assert.*;

public class PomodoroManagerTest {

    @Test
    public void focusDuration_is25Minutes() {
        assertEquals(25 * 60 * 1000L, PomodoroManager.FOCUS_DURATION);
    }

    @Test
    public void shortBreakDuration_is5Minutes() {
        assertEquals(5 * 60 * 1000L, PomodoroManager.SHORT_BREAK_DURATION);
    }

    @Test
    public void longBreakDuration_is30Minutes() {
        assertEquals(30 * 60 * 1000L, PomodoroManager.LONG_BREAK_DURATION);
    }

    @Test
    public void cyclesBeforeLongBreak_is4() {
        assertEquals(4, PomodoroManager.CYCLES_BEFORE_LONG);
    }
}