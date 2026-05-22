package com.dandroids.studytracker.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;

import com.dandroids.studytracker.model.Subject;
import java.util.List;

@Dao
public interface SubjectDao {

    @Insert
    long insert(Subject subject);

    @Delete
    void delete(Subject subject);

    @Query("SELECT * FROM subjects ORDER BY name ASC")
    LiveData<List<Subject>> getAllSubjects();

    @Query("SELECT * FROM subjects WHERE id = :id")
    LiveData<Subject> getSubjectById(long id);
}
