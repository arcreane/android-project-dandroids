package com.dandroids.studytracker.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.dandroids.studytracker.R;
import com.dandroids.studytracker.viewmodel.StudyViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SessionDetailActivity extends BaseActivity {

    public static final String EXTRA_SESSION_ID = "session_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_detail);

        long sessionId = getIntent().getLongExtra(EXTRA_SESSION_ID, -1);
        if (sessionId == -1) {
            finish();
            return;
        }

        TextView tvDuration = findViewById(R.id.tv_detail_duration);
        TextView tvDate     = findViewById(R.id.tv_detail_date);
        TextView tvStatus   = findViewById(R.id.tv_detail_status);

        StudyViewModel viewModel =
                new ViewModelProvider(this).get(StudyViewModel.class);

        // Observe all sessions and find the one matching sessionId
        viewModel.allSessions.observe(this, sessions -> {
            for (com.dandroids.studytracker.model.Session s : sessions) {
                if (s.id == sessionId) {
                    tvDuration.setText(s.durationMinutes + " minutes");
                    SimpleDateFormat sdf =
                            new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
                    tvDate.setText(sdf.format(new Date(s.startTime)));
                    tvStatus.setText(s.completed ? "Completed ✓" : "Incomplete");
                    break;
                }
            }
        });
    }
}
