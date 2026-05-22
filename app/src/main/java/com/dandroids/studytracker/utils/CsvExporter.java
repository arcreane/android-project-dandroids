package com.dandroids.studytracker.utils;

import android.content.Context;

import com.dandroids.studytracker.model.Session;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CsvExporter {

    public static File export(Context context, List<Session> sessions) throws IOException {
        File exportDir = new File(context.getCacheDir(), "exports");
        if (!exportDir.exists()) exportDir.mkdirs();

        File csvFile = new File(exportDir, "studytracker_export.csv");
        FileWriter writer = new FileWriter(csvFile);

        // Header
        writer.append("ID,Subject ID,Start Time,End Time,Duration (min),Completed\n");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        for (Session s : sessions) {
            writer.append(String.valueOf(s.id)).append(",");
            writer.append(String.valueOf(s.subjectId)).append(",");
            writer.append(sdf.format(new Date(s.startTime))).append(",");
            writer.append(sdf.format(new Date(s.endTime))).append(",");
            writer.append(String.valueOf(s.durationMinutes)).append(",");
            writer.append(s.completed ? "Yes" : "No").append("\n");
        }

        writer.flush();
        writer.close();
        return csvFile;
    }
}
