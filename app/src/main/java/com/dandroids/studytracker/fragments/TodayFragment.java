package com.dandroids.studytracker.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.dandroids.studytracker.viewmodel.StudyViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TodayFragment extends Fragment {

    private StudyViewModel viewModel;
    private SessionAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_today, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.rv_today_sessions);
        adapter = new SessionAdapter(session -> {
            Intent intent = new Intent(requireContext(), SessionDetailActivity.class);
            intent.putExtra(SessionDetailActivity.EXTRA_SESSION_ID, session.id);
            startActivity(intent);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity()).get(StudyViewModel.class);

        Calendar start = Calendar.getInstance();
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);

        viewModel.allSessions.observe(getViewLifecycleOwner(), sessions -> {
            long startOfDay = start.getTimeInMillis();
            long now = System.currentTimeMillis();
            List<Session> todaySessions = new ArrayList<>();
            for (Session s : sessions) {
                if (s.startTime >= startOfDay && s.startTime <= now) {
                    todaySessions.add(s);
                }
            }
            adapter.setSessions(todaySessions);
        });
    }
}
