package com.dandroids.studytracker.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.dandroids.studytracker.R;
import com.dandroids.studytracker.model.Session;
import com.dandroids.studytracker.utils.CsvExporter;
import com.dandroids.studytracker.viewmodel.StudyViewModel;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executors;

public class SettingsActivity extends BaseActivity {

    private StudyViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(StudyViewModel.class);

        // Buttons
        Button btnExport = findViewById(R.id.btn_export_csv);
        Button btnReset = findViewById(R.id.btn_reset_stats);

        // Export CSV
        btnExport.setOnClickListener(v -> exportCsv());

        // Reset statistics
        btnReset.setOnClickListener(v -> resetStats());
    }

    /**
     * Export study sessions as CSV
     */
    private void exportCsv() {

        Executors.newSingleThreadExecutor().execute(() -> {

            try {

                // Fetch all sessions from database
                List<Session> sessions = viewModel.getAllSessionsSync();

                // Generate CSV file
                File csvFile = CsvExporter.export(
                        getApplicationContext(),
                        sessions
                );

                // Create secure URI using FileProvider
                Uri uri = FileProvider.getUriForFile(
                        this,
                        getPackageName() + ".fileprovider",
                        csvFile
                );

                // Share intent
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/csv");
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                // Open chooser
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
                                Toast.LENGTH_LONG
                        ).show()
                );
            }
        });
    }

    /**
     * Delete all study sessions
     */
    private void resetStats() {

        Executors.newSingleThreadExecutor().execute(() -> {

            try {

                viewModel.deleteAllSessions();

                runOnUiThread(() ->
                        Toast.makeText(
                                this,
                                "All sessions deleted",
                                Toast.LENGTH_SHORT
                        ).show()
                );

            } catch (Exception e) {

                runOnUiThread(() ->
                        Toast.makeText(
                                this,
                                "Delete failed: " + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
            }
        });
    }
}