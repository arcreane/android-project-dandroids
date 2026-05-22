package com.dandroids.studytracker.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.dandroids.studytracker.R;
import com.dandroids.studytracker.utils.CsvExporter;
import com.dandroids.studytracker.viewmodel.StudyViewModel;

import java.io.File;
import java.util.concurrent.Executors;

public class SettingsActivity extends AppCompatActivity {

    private StudyViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        viewModel = new ViewModelProvider(this).get(StudyViewModel.class);

        Button btnExport = findViewById(R.id.btn_export_csv);
        Button btnReset  = findViewById(R.id.btn_reset_stats);

        btnExport.setOnClickListener(v -> exportCsv());
        btnReset.setOnClickListener(v -> resetStats());
    }

    private void exportCsv() {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {

                // Fetch sessions from database
                java.util.List<com.dandroids.studytracker.model.Session> sessions =
                        viewModel.getAllSessionsSync();

                // Create CSV file
                File csvFile = CsvExporter.export(getApplicationContext(), sessions);

                // Share file using FileProvider
                Uri uri = FileProvider.getUriForFile(
                        this,
                        getPackageName() + ".fileprovider",
                        csvFile
                );

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/csv");
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                runOnUiThread(() ->
                        startActivity(
                                Intent.createChooser(
                                        shareIntent,
                                        "Export study data"
                                )
                        )
                );

            } catch (Exception e) {

                runOnUiThread(() ->
                        Toast.makeText(
                                this,
                                "Export failed: " + e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show()
                );
            }
        });
    }

    private void resetStats() {
        viewModel.deleteAllSessions();

        Toast.makeText(
                this,
                "All sessions deleted",
                Toast.LENGTH_SHORT
        ).show();
    }
}