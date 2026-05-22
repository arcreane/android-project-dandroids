package com.dandroids.studytracker.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dandroids.studytracker.R;
import com.dandroids.studytracker.service.TimerService;

public class SessionActivity extends AppCompatActivity {

    private TimerService timerService;
    private boolean isBound = false;
    private boolean isPaused = false;

    private TextView tvTimer;
    private Button btnPauseResume;
    private Button btnStop;

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
                        long minutes = (millisRemaining / 1000) / 60;
                        long seconds = (millisRemaining / 1000) % 60;
                        tvTimer.setText(String.format("%02d:%02d", minutes, seconds));
                    });
                }

                @Override
                public void onFinish() {
                    runOnUiThread(() -> tvTimer.setText("00:00"));
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

        tvTimer = findViewById(R.id.tv_timer);
        btnPauseResume = findViewById(R.id.btn_pause_resume);
        btnStop = findViewById(R.id.btn_stop);

        Intent serviceIntent = new Intent(this, TimerService.class);
        serviceIntent.putExtra("DURATION_MILLIS", 25 * 60 * 1000L);
        startService(serviceIntent);
        bindService(serviceIntent, connection, BIND_AUTO_CREATE);

        btnPauseResume.setOnClickListener(v -> {
            if (!isBound)
                return;
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
            if (isBound)
                timerService.stopTimer();
            stopService(new Intent(this, TimerService.class));
            finish();
        });
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
