package com.dandroids.studytracker.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "sessions",
    foreignKeys = @ForeignKey(
        entity = Subject.class,
        parentColumns = "id",
        childColumns = "subjectId",
        onDelete = ForeignKey.CASCADE
    )
)
public class Session {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long subjectId;
    public long startTime;
    public long endTime;
    public int  durationMinutes;
    public boolean completed;

    public Session(long subjectId, long startTime) {
        this.subjectId = subjectId;
        this.startTime = startTime;
        this.completed = false;
    }

    public long getDurationMillis() {
        return endTime - startTime;
    }
}
