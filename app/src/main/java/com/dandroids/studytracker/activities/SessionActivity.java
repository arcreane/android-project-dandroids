package com.dandroids.studytracker.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.dandroids.studytracker.R;

public class SessionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);
        // hr (TimerService) wires into this Activity
    }
}