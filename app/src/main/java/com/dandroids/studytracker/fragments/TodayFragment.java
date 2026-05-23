package com.dandroids.studytracker.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dandroids.studytracker.R;
import com.dandroids.studytracker.activities.SessionDetailActivity;
import com.dandroids.studytracker.adapter.SessionAdapter;
import com.dandroids.studytracker.model.Session;
import com.dandroids.studytracker.model.Subject;
import com.dandroids.studytracker.viewmodel.StudyViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TodayFragment extends Fragment {

    private StudyViewModel viewModel;
    private SessionAdapter adapter;
    private TextView tvTotalTime;

    // Filter state — null means show all subjects
    private Long filterSubjectId = null;
    private List<Subject> availableSubjects = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_today, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.rv_today_sessions);
        tvTotalTime = view.findViewById(R.id.tv_total_time);

        adapter = new SessionAdapter(session -> {
            Intent intent = new Intent(requireContext(), SessionDetailActivity.class);
            intent.putExtra(SessionDetailActivity.EXTRA_SESSION_ID, session.id);
            startActivity(intent);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity()).get(StudyViewModel.class);

        // Observe subjects for filter menu
        viewModel.allSubjects.observe(getViewLifecycleOwner(), subjects -> {
            availableSubjects = subjects != null ? subjects : new ArrayList<>();
        });

        // Observe sessions and apply filter
        Calendar start = Calendar.getInstance();
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);

        viewModel.allSessions.observe(getViewLifecycleOwner(), sessions -> {
            long startOfDay = start.getTimeInMillis();
            long now = System.currentTimeMillis();

            List<Session> filtered = new ArrayList<>();
            int totalMinutes = 0;

            for (Session s : sessions) {
                boolean inToday = s.startTime >= startOfDay && s.startTime <= now;
                boolean matchesFilter = filterSubjectId == null
                        || s.subjectId == filterSubjectId;

                if (inToday && matchesFilter) {
                    filtered.add(s);
                    totalMinutes += s.durationMinutes;
                }
            }

            adapter.setSessions(filtered);

            // Show total study time for today
            if (tvTotalTime != null) {
                tvTotalTime.setText("Total today: " + totalMinutes + " min");
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Add filter options dynamically per subject
        menu.add(Menu.NONE, 0, Menu.NONE, "All subjects");
        for (int i = 0; i < availableSubjects.size(); i++) {
            menu.add(Menu.NONE, i + 1, Menu.NONE,
                    availableSubjects.get(i).name);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == 0) {
            filterSubjectId = null; // show all
        } else if (id <= availableSubjects.size()) {
            filterSubjectId = availableSubjects.get(id - 1).id;
        }
        // Trigger re-filter by re-observing
        viewModel.allSessions.getValue();
        return true;
    }
}
