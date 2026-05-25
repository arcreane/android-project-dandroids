package com.dandroids.studytracker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
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
import com.dandroids.studytracker.model.Subject;
import com.dandroids.studytracker.viewmodel.StudyViewModel;

import java.util.ArrayList;
import java.util.List;

public class SubjectsFragment extends Fragment {

    private StudyViewModel viewModel;
    private SubjectAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_subjects, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.rv_subjects);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new SubjectAdapter();
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity()).get(StudyViewModel.class);
        viewModel.allSubjects.observe(getViewLifecycleOwner(), subjects -> {
            adapter.setSubjects(subjects);
        });
    }

    // Simple inline adapter for subjects list
    static class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {

        private List<Subject> subjects = new ArrayList<>();

        public void setSubjects(List<Subject> subjects) {
            this.subjects = subjects != null ? subjects : new ArrayList<>();
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView tv = new TextView(parent.getContext());
            tv.setPadding(48, 32, 48, 32);
            tv.setTextSize(16f);
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT);
            tv.setLayoutParams(params);
            return new SubjectViewHolder(tv);
        }

        @Override
        public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
            holder.bind(subjects.get(position));
        }

        @Override
        public int getItemCount() { return subjects.size(); }

        static class SubjectViewHolder extends RecyclerView.ViewHolder {
            SubjectViewHolder(@NonNull View itemView) { super(itemView); }
            void bind(Subject subject) {
                ((TextView) itemView).setText("📚 " + subject.name);
            }
        }
    }
}
