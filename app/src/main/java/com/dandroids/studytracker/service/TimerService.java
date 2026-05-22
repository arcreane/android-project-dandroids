package com.dandroids.studytracker.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;

import com.dandroids.studytracker.R;
import com.dandroids.studytracker.activities.SessionActivity;

public class TimerService extends Service {

    public static final String CHANNEL_ID      = "study_timer_channel";
    public static final String ACTION_PAUSE    = "ACTION_PAUSE";
    public static final String ACTION_STOP     = "ACTION_STOP";
    public static final int    NOTIFICATION_ID = 1;

    private CountDownTimer countDownTimer;
    private long remainingMillis;
    private boolean isRunning = false;

    private final IBinder binder = new TimerBinder();

    public class TimerBinder extends Binder {
        public TimerService getService() {
            return TimerService.this;
        }
    }

    public interface TimerCallback {
        void onTick(long millisRemaining);
        void onFinish();
    }

    private TimerCallback callback;

    public void setCallback(TimerCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (ACTION_PAUSE.equals(action)) {
                pauseTimer();
            } else if (ACTION_STOP.equals(action)) {
                stopTimer();
                stopSelf();
            } else {
                long duration = intent.getLongExtra("DURATION_MILLIS", 25 * 60 * 1000L);
                startForeground(NOTIFICATION_ID, buildNotification("Starting..."));
                startTimer(duration);
            }
        }
        return START_STICKY;
    }

    public void startTimer(long durationMillis) {
        remainingMillis = durationMillis;
        isRunning = true;

        countDownTimer = new CountDownTimer(durationMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingMillis = millisUntilFinished;
                updateNotification(formatTime(millisUntilFinished));
                if (callback != null) callback.onTick(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                isRunning = false;
                vibrateOnFinish();
                updateNotification("Session complete!");
                if (callback != null) callback.onFinish();
            }
        }.start();
    }

    public void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            isRunning = false;
            updateNotification("Paused — " + formatTime(remainingMillis));
        }
    }

    public void resumeTimer() {
        if (!isRunning && remainingMillis > 0) {
            startTimer(remainingMillis);
        }
    }

    public void stopTimer() {
        if (countDownTimer != null) countDownTimer.cancel();
        isRunning = false;
        remainingMillis = 0;
    }

    public boolean isRunning() { return isRunning; }
    public long getRemainingMillis() { return remainingMillis; }

    private void vibrateOnFinish() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(
                        new long[]{0, 500, 200, 500, 200, 500}, -1));
            } else {
                vibrator.vibrate(new long[]{0, 500, 200, 500, 200, 500}, -1);
            }
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Study Timer",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Shows the running study timer");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }

    private Notification buildNotification(String contentText) {
        Intent openIntent = new Intent(this, SessionActivity.class);
        openIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingOpen = PendingIntent.getActivity(
                this, 0, openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent pauseIntent = new Intent(this, TimerService.class);
        pauseIntent.setAction(ACTION_PAUSE);
        PendingIntent pendingPause = PendingIntent.getService(
                this, 1, pauseIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Intent stopIntent = new Intent(this, TimerService.class);
        stopIntent.setAction(ACTION_STOP);
        PendingIntent pendingStop = PendingIntent.getService(
                this, 2, stopIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("StudyTracker")
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_timer)
                .setContentIntent(pendingOpen)
                .addAction(R.drawable.ic_timer, "Pause", pendingPause)
                .addAction(R.drawable.ic_timer, "Stop",  pendingStop)
                .setOngoing(true)
                .build();
    }

    private void updateNotification(String text) {
        NotificationManager manager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) manager.notify(NOTIFICATION_ID, buildNotification(text));
    }

    private String formatTime(long millis) {
        long minutes = (millis / 1000) / 60;
        long seconds = (millis / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    public IBinder onBind(Intent intent) { return binder; }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }
}
