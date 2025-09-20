package com.example.androidproject;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class ActivitySelectionActivity extends AppCompatActivity {

    private String selectedActivity;
    private String selectedDestinationType;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        // Get the selected destination type from the previous activity
        Intent intent = getIntent();
        selectedDestinationType = intent.getStringExtra("DESTINATION_TYPE");

        LinearLayout adventureSports = findViewById(R.id.adventure_sports);
        LinearLayout culturalExperiences = findViewById(R.id.cultural_experiences);
        LinearLayout relaxationWellness = findViewById(R.id.relaxation_wellness);
        LinearLayout foodDining = findViewById(R.id.food_dining);
        LinearLayout shopping = findViewById(R.id.shopping);
        LinearLayout natureWildlife = findViewById(R.id.nature_wildlife);
        Button continueButton = findViewById(R.id.continueButton);



        progressBar = findViewById(R.id.progress_bar);
        progressBar.setProgress(25);
        progressBar.setVisibility(View.VISIBLE);

        View.OnClickListener activityClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.adventure_sports) {
                    selectedActivity = "Adventure Sports";
                } else if (v.getId() == R.id.cultural_experiences) {
                    selectedActivity = "Cultural Experiences";
                } else if (v.getId() == R.id.relaxation_wellness) {
                    selectedActivity = "Relaxation and Wellness";
                } else if (v.getId() == R.id.food_dining) {
                    selectedActivity = "Food and Dining";
                } else if (v.getId() == R.id.shopping) {
                    selectedActivity = "Shopping";
                } else if (v.getId() == R.id.nature_wildlife) {
                    selectedActivity = "Nature and Wildlife";
                }
                progressBar.setProgress(50);
                continueButton.setEnabled(true);
            }
        };

        adventureSports.setOnClickListener(activityClickListener);
        culturalExperiences.setOnClickListener(activityClickListener);
        relaxationWellness.setOnClickListener(activityClickListener);
        foodDining.setOnClickListener(activityClickListener);
        shopping.setOnClickListener(activityClickListener);
        natureWildlife.setOnClickListener(activityClickListener);

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pass the selected activity and destination type to the next activity
                Intent intent = new Intent(ActivitySelectionActivity.this, TravelWithSelectionActivity.class);
                intent.putExtra("DESTINATION_TYPE", selectedDestinationType);
                intent.putExtra("ACTIVITY_TYPE", selectedActivity);
                startActivity(intent);
            }
        });
    }
}
