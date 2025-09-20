package com.example.androidproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class ClimateSelectionActivity extends AppCompatActivity {

    private String selectedClimate;
    private String selectedDestinationType;
    private String selectedActivity;
    private String selectedTravelWith;
    private ProgressBar progressBar;
    private LinearLayout tropical;
    private LinearLayout temperate;
    private LinearLayout cold;
    private LinearLayout hot;
    private Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_climate_selection);

        // Get the selected destination type, activity, and travel with from the previous activity
        Intent intent = getIntent();
        selectedDestinationType = intent.getStringExtra("DESTINATION_TYPE");
        selectedActivity = intent.getStringExtra("ACTIVITY_TYPE");
        selectedTravelWith = intent.getStringExtra("TRAVEL_WITH");


        tropical = findViewById(R.id.tropical);
        temperate = findViewById(R.id.temperate);
        cold = findViewById(R.id.cold);
        hot = findViewById(R.id.hot);
        continueButton = findViewById(R.id.continueButton);

        progressBar = findViewById(R.id.progress_bar);
        progressBar.setProgress(75);
        progressBar.setVisibility(View.VISIBLE);

        View.OnClickListener climateClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.tropical) {
                    selectedClimate = "Tropical";
                } else if (v.getId() == R.id.temperate) {
                    selectedClimate = "Temperate";
                } else if (v.getId() == R.id.cold) {
                    selectedClimate = "Cold";
                } else if (v.getId() == R.id.hot) {
                    selectedClimate = "Hot";
                }
                progressBar.setProgress(100);
                continueButton.setEnabled(true);
            }
        };

        tropical.setOnClickListener(climateClickListener);
        temperate.setOnClickListener(climateClickListener);
        cold.setOnClickListener(climateClickListener);
        hot.setOnClickListener(climateClickListener);

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pass all selected inputs to the ML algorithm or result activity
                Intent intent = new Intent(ClimateSelectionActivity.this, ResultActivity.class);
                intent.putExtra("DESTINATION_TYPE", selectedDestinationType);
                intent.putExtra("ACTIVITY_TYPE", selectedActivity);
                intent.putExtra("TRAVEL_WITH", selectedTravelWith);
                intent.putExtra("CLIMATE", selectedClimate);
                startActivity(intent);
            }
        });
    }
}
