package com.dandroids.studytracker;

import com.dandroids.studytracker.model.Session;
import com.dandroids.studytracker.utils.StatsCalculator;

import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Unit tests for StatsCalculator — lptrk
 */
public class StatsCalculatorTest {

    @Test
    public void getTodayMinutes_emptyList_returnsZero() {
        List<Session> sessions = new ArrayList<>();
        assertEquals(0, StatsCalculator.getTodayMinutes(sessions));
    }

    @Test
    public void getWeekMinutes_emptyList_returnsZero() {
        List<Session> sessions = new ArrayList<>();
        assertEquals(0, StatsCalculator.getWeekMinutes(sessions));
    }

    @Test
    public void getDailyAverage_emptyList_returnsZero() {
        List<Session> sessions = new ArrayList<>();
        assertEquals(0f, StatsCalculator.getDailyAverage(sessions), 0.01f);
    }

    @Test
    public void getCurrentStreak_emptyList_returnsZero() {
        List<Session> sessions = new ArrayList<>();
        assertEquals(0, StatsCalculator.getCurrentStreak(sessions));
    }

    @Test
    public void getMinutesPerSubject_correctlyGroupsBySubject() {
        List<Session> sessions = new ArrayList<>();

        Session s1 = new Session(1L, System.currentTimeMillis());
        s1.completed = true;
        s1.durationMinutes = 25;

        Session s2 = new Session(1L, System.currentTimeMillis());
        s2.completed = true;
        s2.durationMinutes = 25;

        Session s3 = new Session(2L, System.currentTimeMillis());
        s3.completed = true;
        s3.durationMinutes = 50;

        sessions.add(s1);
        sessions.add(s2);
        sessions.add(s3);

        Map<Long, Integer> result =
                StatsCalculator.getMinutesPerSubject(sessions);

        assertEquals(Integer.valueOf(50), result.get(1L));
        assertEquals(Integer.valueOf(50), result.get(2L));
    }

    @Test
    public void getWeeklyMinutesByDay_returnsArrayOfSeven() {
        List<Session> sessions = new ArrayList<>();
        float[] result = StatsCalculator.getWeeklyMinutesByDay(sessions);
        assertEquals(7, result.length);
    }
}
