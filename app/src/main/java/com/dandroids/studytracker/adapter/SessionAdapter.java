package com.dandroids.studytracker.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dandroids.studytracker.R;
import com.dandroids.studytracker.model.Session;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.SessionViewHolder> {

    public interface OnSessionClickListener {
        void onSessionClick(Session session);
    }

    private List<Session> sessions = new ArrayList<>();
    private final OnSessionClickListener listener;

    public SessionAdapter(OnSessionClickListener listener) {
        this.listener = listener;
    }

    public void setSessions(List<Session> sessions) {
        this.sessions = sessions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_session, parent, false);
        return new SessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
        Session session = sessions.get(position);
        holder.bind(session, listener);
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    static class SessionViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvDuration;
        private final TextView tvDate;
        private final TextView tvStatus;

        public SessionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDuration = itemView.findViewById(R.id.tv_duration);
            tvDate     = itemView.findViewById(R.id.tv_date);
            tvStatus   = itemView.findViewById(R.id.tv_status);
        }

        public void bind(Session session, OnSessionClickListener listener) {
            tvDuration.setText(session.durationMinutes + " min");
            tvStatus.setText(session.completed ? "Completed" : "Incomplete");
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault());
            tvDate.setText(sdf.format(new Date(session.startTime)));
            itemView.setOnClickListener(v -> listener.onSessionClick(session));
        }
    }
}
