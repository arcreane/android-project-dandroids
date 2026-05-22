package com.dandroids.studytracker.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.dandroids.studytracker.R;

public class SessionDetailActivity extends AppCompatActivity {

    public static final String EXTRA_SESSION_ID = "session_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_detail);

        long sessionId = getIntent().getLongExtra(EXTRA_SESSION_ID, -1);
        // bg wires LiveData from ViewModel here
    }
}