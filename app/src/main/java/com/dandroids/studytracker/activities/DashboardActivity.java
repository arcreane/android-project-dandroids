package com.dandroids.studytracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.dandroids.studytracker.R;
import com.dandroids.studytracker.fragments.SubjectsFragment;
import com.dandroids.studytracker.fragments.TodayFragment;
import com.dandroids.studytracker.fragments.WeekFragment;
import com.dandroids.studytracker.manager.PomodoroManager;
import com.dandroids.studytracker.model.Subject;
import com.dandroids.studytracker.viewmodel.StudyViewModel;
import com.google.android.material.tabs.TabLayout;

public class DashboardActivity extends BaseActivity {

    private static final String KEY_TAB = "selected_tab";

    private StudyViewModel viewModel;
    private PomodoroManager pomodoroManager;
    private int selectedTab = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        viewModel        = new ViewModelProvider(this).get(StudyViewModel.class);
        pomodoroManager  = new PomodoroManager(this);

        // Restore tab position after rotation (R2)
        if (savedInstanceState != null) {
            selectedTab = savedInstanceState.getInt(KEY_TAB, 0);
        }

        setupTabs();

        findViewById(R.id.btn_start_session).setOnClickListener(v -> {
            Intent intent = new Intent(this, SessionActivity.class);
            // Pass Pomodoro info to SessionActivity
            intent.putExtra("DURATION_MILLIS", pomodoroManager.getCurrentDuration());
            intent.putExtra("SESSION_LABEL",   pomodoroManager.getCurrentLabel());
            startActivity(intent);
        });
    }

    // Save tab position before rotation (R2)
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_TAB, selectedTab);
    }

    private void setupTabs() {
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_today));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_week));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_subjects));

        // Restore selected tab after rotation
        tabLayout.selectTab(tabLayout.getTabAt(selectedTab));
        loadFragment(getFragmentForTab(selectedTab));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectedTab = tab.getPosition();
                loadFragment(getFragmentForTab(selectedTab));
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private Fragment getFragmentForTab(int position) {
        switch (position) {
            case 1:  return new WeekFragment();
            case 2:  return new SubjectsFragment();
            default: return new TodayFragment();
        }
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
        if (id == R.id.action_theme) {
            toggleTheme(); // BaseActivity handles this
            return true;
        } else if (id == R.id.action_export) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_reset) {
            showResetConfirmation();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showResetConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Reset Statistics")
                .setMessage("This will delete all study sessions. Are you sure?")
                .setPositiveButton("Reset", (dialog, which) -> {
                    viewModel.deleteAllSessions();
                    pomodoroManager.reset();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Subject creation dialog — MSJ
    public void showAddSubjectDialog() {
        EditText input = new EditText(this);
        input.setHint("Subject name (e.g. Maths)");

        new AlertDialog.Builder(this)
                .setTitle("Add Subject")
                .setView(input)
                .setPositiveButton("Add", (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    if (name.isEmpty()) {
                        input.setError("Name cannot be empty");
                        return;
                    }
                    viewModel.insertSubject(new Subject(name, "#5C6BC0"));
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
