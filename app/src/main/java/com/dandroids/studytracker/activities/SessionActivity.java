package com.dandroids.studytracker.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Button;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.dandroids.studytracker.R;
import com.dandroids.studytracker.manager.PomodoroManager;
import com.dandroids.studytracker.model.Session;
import com.dandroids.studytracker.service.TimerService;
import com.dandroids.studytracker.viewmodel.StudyViewModel;
import com.dandroids.studytracker.views.TimerProgressView;

public class SessionActivity extends BaseActivity {

    private TimerService timerService;
    private boolean isBound  = false;
    private boolean isPaused = false;

    private TimerProgressView timerProgressView;
    private TextView tvSubjectName;
    private Button btnPauseResume;
    private Button btnStop;

    private StudyViewModel viewModel;
    private PomodoroManager pomodoroManager;

    private long sessionStartTime;
    private long totalDuration;
    private long subjectId = -1L;

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TimerService.TimerBinder binder = (TimerService.TimerBinder) service;
            timerService = binder.getService();
            isBound = true;

            timerService.setCallback(new TimerService.TimerCallback() {
                @Override
                public void onTick(long millisRemaining) {
                    runOnUiThread(() -> {
                        if (timerProgressView != null) {
                            timerProgressView.setProgress(millisRemaining, totalDuration);
                        }
                    });
                }

                @Override
                public void onFinish() {
                    runOnUiThread(() -> {
                        if (timerProgressView != null) {
                            timerProgressView.setProgress(0, totalDuration);
                        }
                        saveSessionToDatabase(true);
                        pomodoroManager.advance();
                    });
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        viewModel       = new ViewModelProvider(this).get(StudyViewModel.class);
        pomodoroManager = new PomodoroManager(this);

        timerProgressView = findViewById(R.id.timer_progress_view);
        tvSubjectName     = findViewById(R.id.tv_subject_name);
        btnPauseResume    = findViewById(R.id.btn_pause_resume);
        btnStop           = findViewById(R.id.btn_stop);

        totalDuration = getIntent().getLongExtra("DURATION_MILLIS",
                PomodoroManager.FOCUS_DURATION);
        subjectId     = getIntent().getLongExtra("SUBJECT_ID", -1L);
        String label  = getIntent().getStringExtra("SESSION_LABEL");
        if (label == null) label = pomodoroManager.getCurrentLabel();

        if (tvSubjectName != null)     tvSubjectName.setText(label);
        if (timerProgressView != null) timerProgressView.setLabelText(label);

        sessionStartTime = System.currentTimeMillis();

        Intent serviceIntent = new Intent(this, TimerService.class);
        serviceIntent.putExtra("DURATION_MILLIS", totalDuration);
        startService(serviceIntent);
        bindService(serviceIntent, connection, BIND_AUTO_CREATE);

        btnPauseResume.setOnClickListener(v -> {
            if (!isBound) return;
            if (isPaused) {
                timerService.resumeTimer();
                btnPauseResume.setText(R.string.pause_session);
                isPaused = false;
            } else {
                timerService.pauseTimer();
                btnPauseResume.setText(R.string.start_session);
                isPaused = true;
            }
        });

        btnStop.setOnClickListener(v -> {
            stopSession(false);
        });
    }

    private void stopSession(boolean completed) {
        if (isBound) {
            timerService.stopTimer();
            unbindService(connection);
            isBound = false;
        }
        stopService(new Intent(this, TimerService.class));
        saveSessionToDatabase(completed);
    }

    private void saveSessionToDatabase(boolean completed) {
        if (subjectId > 0) {
            Session session         = new Session(subjectId, sessionStartTime);
            session.endTime         = System.currentTimeMillis();
            session.completed       = completed;
            session.durationMinutes = (int) ((session.endTime - sessionStartTime) / 60000);
            viewModel.insertSession(session);
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            unbindService(connection);
            isBound = false;
        }
    }
}
