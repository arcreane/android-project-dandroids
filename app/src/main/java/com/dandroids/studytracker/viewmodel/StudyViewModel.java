package com.dandroids.studytracker.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.dandroids.studytracker.db.AppDatabase;
import com.dandroids.studytracker.db.SessionDao;
import com.dandroids.studytracker.db.SubjectDao;
import com.dandroids.studytracker.model.Session;
import com.dandroids.studytracker.model.Subject;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StudyViewModel extends AndroidViewModel {

    private final SessionDao sessionDao;
    private final SubjectDao subjectDao;
    private final ExecutorService executor;

    public final LiveData<List<Session>> allSessions;
    public final LiveData<List<Subject>> allSubjects;

    public StudyViewModel(Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application);
        sessionDao  = db.sessionDao();
        subjectDao  = db.subjectDao();
        executor    = Executors.newSingleThreadExecutor();
        allSessions = sessionDao.getAllSessions();
        allSubjects = subjectDao.getAllSubjects();
    }

    public void insertSession(Session session) {
        executor.execute(() -> sessionDao.insert(session));
    }

    public void updateSession(Session session) {
        executor.execute(() -> sessionDao.update(session));
    }

    public void insertSubject(Subject subject) {
        executor.execute(() -> subjectDao.insert(subject));
    }

    public void deleteAllSessions() {
        executor.execute(sessionDao::deleteAll);
    }

    public LiveData<List<Session>> getSessionsBySubject(long subjectId) {
        return sessionDao.getSessionsBySubject(subjectId);
    }

    public List<Session> getAllSessionsSync() {
        return sessionDao.getAllSessionsSync();
    }
}
