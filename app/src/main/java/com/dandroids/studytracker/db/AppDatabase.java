package com.dandroids.studytracker.db;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.dandroids.studytracker.model.Session;
import com.dandroids.studytracker.model.Subject;

@Database(entities = {Session.class, Subject.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract SessionDao sessionDao();
    public abstract SubjectDao subjectDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "studytracker_db"
            ).fallbackToDestructiveMigration().build();
        }
        return instance;
    }
}
