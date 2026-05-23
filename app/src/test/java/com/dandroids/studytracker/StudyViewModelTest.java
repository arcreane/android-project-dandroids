package com.dandroids.studytracker;

import com.dandroids.studytracker.model.Session;
import com.dandroids.studytracker.model.Subject;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for data model logic — bg
 */
public class StudyViewModelTest {

    @Test
    public void session_getDurationMillis_returnsCorrectValue() {
        Session session = new Session(1L, 1000L);
        session.endTime = 61000L;
        assertEquals(60000L, session.getDurationMillis());
    }

    @Test
    public void session_defaultCompleted_isFalse() {
        Session session = new Session(1L, System.currentTimeMillis());
        assertFalse(session.completed);
    }

    @Test
    public void session_completedFlag_canBeSetTrue() {
        Session session = new Session(1L, System.currentTimeMillis());
        session.completed = true;
        assertTrue(session.completed);
    }

    @Test
    public void subject_name_isSetCorrectly() {
        Subject subject = new Subject("Mathematics", "#5C6BC0");
        assertEquals("Mathematics", subject.name);
        assertEquals("#5C6BC0", subject.colorHex);
    }

    @Test
    public void session_subjectId_isSetCorrectly() {
        long subjectId = 42L;
        Session session = new Session(subjectId, System.currentTimeMillis());
        assertEquals(subjectId, session.subjectId);
    }

    @Test
    public void session_startTime_isSetCorrectly() {
        long startTime = 123456789L;
        Session session = new Session(1L, startTime);
        assertEquals(startTime, session.startTime);
    }
}
