package com.dandroids.studytracker.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.dandroids.studytracker.model.Session;
import java.util.List;

@Dao
public interface SessionDao {

    @Insert
    long insert(Session session);

    @Update
    void update(Session session);

    @Query("SELECT * FROM sessions ORDER BY startTime DESC")
    LiveData<List<Session>> getAllSessions();

    @Query("SELECT * FROM sessions WHERE startTime >= :from AND startTime <= :to ORDER BY startTime DESC")
    LiveData<List<Session>> getSessionsBetween(long from, long to);

    @Query("SELECT * FROM sessions WHERE subjectId = :subjectId ORDER BY startTime DESC")
    LiveData<List<Session>> getSessionsBySubject(long subjectId);

    @Query("SELECT * FROM sessions ORDER BY startTime DESC")
    List<Session> getAllSessionsSync();

    @Query("DELETE FROM sessions")
    void deleteAll();
}
