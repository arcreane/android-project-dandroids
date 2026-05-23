package com.dandroids.studytracker.utils;

import com.dandroids.studytracker.model.Session;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatsCalculator {

    public static int getTodayMinutes(List<Session> sessions) {
        Calendar start = Calendar.getInstance();
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        long startOfDay = start.getTimeInMillis();
        long now = System.currentTimeMillis();

        int total = 0;
        for (Session s : sessions) {
            if (s.startTime >= startOfDay && s.startTime <= now && s.completed) {
                total += s.durationMinutes;
            }
        }
        return total;
    }

    public static int getWeekMinutes(List<Session> sessions) {
        Calendar now = Calendar.getInstance();
        int currentWeek = now.get(Calendar.WEEK_OF_YEAR);
        int currentYear = now.get(Calendar.YEAR);

        int total = 0;
        for (Session s : sessions) {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(s.startTime);
            if (c.get(Calendar.WEEK_OF_YEAR) == currentWeek
                    && c.get(Calendar.YEAR) == currentYear
                    && s.completed) {
                total += s.durationMinutes;
            }
        }
        return total;
    }

    public static float getDailyAverage(List<Session> sessions) {
        return getWeekMinutes(sessions) / 7f;
    }

    public static Map<Long, Integer> getMinutesPerSubject(List<Session> sessions) {
        Map<Long, Integer> map = new HashMap<>();
        for (Session s : sessions) {
            if (s.completed) {
                map.put(s.subjectId,
                        map.getOrDefault(s.subjectId, 0) + s.durationMinutes);
            }
        }
        return map;
    }

    public static int getCurrentStreak(List<Session> sessions) {
        if (sessions == null || sessions.isEmpty()) return 0;

        Calendar day = Calendar.getInstance();
        day.set(Calendar.HOUR_OF_DAY, 0);
        day.set(Calendar.MINUTE, 0);
        day.set(Calendar.SECOND, 0);
        day.set(Calendar.MILLISECOND, 0);

        int streak = 0;
        while (true) {
            long dayStart = day.getTimeInMillis();
            day.add(Calendar.DAY_OF_YEAR, 1);
            long dayEnd = day.getTimeInMillis();

            boolean hasSession = false;
            for (Session s : sessions) {
                if (s.startTime >= dayStart && s.startTime < dayEnd && s.completed) {
                    hasSession = true;
                    break;
                }
            }
            if (hasSession) {
                streak++;
                day.add(Calendar.DAY_OF_YEAR, -2);
            } else {
                break;
            }
        }
        return streak;
    }

    public static float[] getWeeklyMinutesByDay(List<Session> sessions) {
        float[] result = new float[7];
        Calendar now = Calendar.getInstance();
        int currentWeek = now.get(Calendar.WEEK_OF_YEAR);
        int currentYear = now.get(Calendar.YEAR);

        for (Session s : sessions) {
            if (!s.completed) continue;
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(s.startTime);
            if (c.get(Calendar.WEEK_OF_YEAR) == currentWeek
                    && c.get(Calendar.YEAR) == currentYear) {
                int day = c.get(Calendar.DAY_OF_WEEK);
                int index = (day == Calendar.SUNDAY) ? 6 : day - 2;
                if (index >= 0 && index < 7) result[index] += s.durationMinutes;
            }
        }
        return result;
    }
}
