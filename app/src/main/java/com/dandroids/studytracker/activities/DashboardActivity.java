package com.dandroids.studytracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.dandroids.studytracker.R;
import com.dandroids.studytracker.fragments.TodayFragment;
import com.dandroids.studytracker.fragments.WeekFragment;
import com.dandroids.studytracker.fragments.SubjectsFragment;
import com.google.android.material.tabs.TabLayout;

public class DashboardActivity extends AppCompatActivity {

    private boolean isFocusTheme = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applyCurrentTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        setupTabs();

        findViewById(R.id.btn_start_session).setOnClickListener(v -> {
            Intent intent = new Intent(this, SessionActivity.class);
            startActivity(intent);
        });
    }

    private void setupTabs() {
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_today));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_week));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_subjects));

        loadFragment(new TodayFragment());

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment fragment;
                switch (tab.getPosition()) {
                    case 1:  fragment = new WeekFragment();     break;
                    case 2:  fragment = new SubjectsFragment(); break;
                    default: fragment = new TodayFragment();    break;
                }
                loadFragment(fragment);
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_export) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_theme) {
            isFocusTheme = !isFocusTheme;
            recreate(); // applies new theme
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void applyCurrentTheme() {
        if (isFocusTheme) {
            setTheme(R.style.Theme_StudyTracker);
        } else {
            setTheme(R.style.Theme_StudyTracker_Review);
        }
    }
}