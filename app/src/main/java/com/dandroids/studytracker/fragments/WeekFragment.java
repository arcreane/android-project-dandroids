package com.dandroids.studytracker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.dandroids.studytracker.R;
import com.dandroids.studytracker.viewmodel.StudyViewModel;
import com.dandroids.studytracker.views.BarChartView;

public class WeekFragment extends Fragment {

    private StudyViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_week, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(StudyViewModel.class);

        BarChartView barChart = view.findViewById(R.id.bar_chart);

        viewModel.allSessions.observe(getViewLifecycleOwner(), sessions -> {
            // lptrk wires chart data here after BarChartView is ready
            if (barChart != null && sessions != null) {
                barChart.setSessions(sessions);
            }
        });
    }
}
