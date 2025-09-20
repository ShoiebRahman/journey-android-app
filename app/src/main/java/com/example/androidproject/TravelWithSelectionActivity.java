package com.example.androidproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class TravelWithSelectionActivity extends AppCompatActivity {

    private String selectedTravelWith;
    private String selectedDestinationType;
    private String selectedActivity;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_with_selection);

        // Get the selected destination type and activity from the previous activity
        Intent intent = getIntent();
        selectedDestinationType = intent.getStringExtra("DESTINATION_TYPE");
        selectedActivity = intent.getStringExtra("ACTIVITY_TYPE");

        LinearLayout single = findViewById(R.id.single);
        LinearLayout couple = findViewById(R.id.couple);
        LinearLayout family = findViewById(R.id.family);
        LinearLayout friends = findViewById(R.id.friends);
        LinearLayout group = findViewById(R.id.group);
        Button continueButton = findViewById(R.id.continueButton);

        progressBar = findViewById(R.id.progress_bar);
        progressBar.setProgress(50);
        progressBar.setVisibility(View.VISIBLE);

        View.OnClickListener travelWithClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.single) {
                    selectedTravelWith = "Single";
                } else if (v.getId() == R.id.couple) {
                    selectedTravelWith = "Couple";
                } else if (v.getId() == R.id.family) {
                    selectedTravelWith = "Family";
                } else if (v.getId() == R.id.friends) {
                    selectedTravelWith = "Friends";
                } else if (v.getId() == R.id.group) {
                    selectedTravelWith = "Group";
                }
                progressBar.setProgress(75);
                continueButton.setEnabled(true);
            }
        };

        single.setOnClickListener(travelWithClickListener);
        couple.setOnClickListener(travelWithClickListener);
        family.setOnClickListener(travelWithClickListener);
        friends.setOnClickListener(travelWithClickListener);
        group.setOnClickListener(travelWithClickListener);

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pass the selected travel with, activity, and destination type to the next activity
                Intent intent = new Intent(TravelWithSelectionActivity.this, ClimateSelectionActivity.class);
                intent.putExtra("DESTINATION_TYPE", selectedDestinationType);
                intent.putExtra("ACTIVITY_TYPE", selectedActivity);
                intent.putExtra("TRAVEL_WITH", selectedTravelWith);
                startActivity(intent);
            }
        });
    }
}
