package com.dandroids.studytracker.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "subjects")
public class Subject {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String name;
    public String colorHex;

    public Subject(String name, String colorHex) {
        this.name = name;
        this.colorHex = colorHex;
    }
}
