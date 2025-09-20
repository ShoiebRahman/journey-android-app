package com.example.androidproject;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

public class DestinationSelectionActivity extends AppCompatActivity {
    private final String tag="DestinationSelection ";

    private String selectedDestinationType;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination_selection);

        LinearLayout beaches = findViewById(R.id.beaches);
        LinearLayout mountains = findViewById(R.id.mountains);
        LinearLayout cities = findViewById(R.id.city);
        LinearLayout countryside = findViewById(R.id.countryside);
        LinearLayout historicalSites = findViewById(R.id.historical_site);
        Button continueButton = findViewById(R.id.continueButton);

        progressBar = findViewById(R.id.progress_bar);
        progressBar.setProgress(0);
        progressBar.setVisibility(View.VISIBLE);



        View.OnClickListener destinationClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.beaches) {
                    selectedDestinationType = "Beaches";
                } else if (v.getId() == R.id.mountains) {
                    selectedDestinationType = "Mountains";
                } else if (v.getId() == R.id.city) {
                    selectedDestinationType = "Cities";
                } else if (v.getId() == R.id.countryside) {
                    selectedDestinationType = "Countryside";
                } else if (v.getId() == R.id.historical_site) {
                    selectedDestinationType = "Historical sites";
                }
                progressBar.setProgress(25);
                continueButton.setEnabled(true);
            }
        };

        beaches.setOnClickListener(destinationClickListener);
        mountains.setOnClickListener(destinationClickListener);
        cities.setOnClickListener(destinationClickListener);
        countryside.setOnClickListener(destinationClickListener);
        historicalSites.setOnClickListener(destinationClickListener);

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pass the selected destination type to the next activity
                Intent intent = new Intent(DestinationSelectionActivity.this, ActivitySelectionActivity.class);
                intent.putExtra("DESTINATION_TYPE", selectedDestinationType);
                startActivity(intent);
            }
        });
    }
}
