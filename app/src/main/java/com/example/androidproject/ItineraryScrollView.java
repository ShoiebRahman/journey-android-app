package com.example.androidproject;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ItineraryScrollView extends AppCompatActivity {
    private final String tag="ItineraryScrollView";
    private TextView titleTextView;
    private TextView dateTextView;
    private LinearLayout timelineContainer;
    private UserDatabaseHelper db;
    private long itineraryId;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(tag, "onCreate called");
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_itinerary_scroll_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        titleTextView = findViewById(R.id.titleTextView);
        dateTextView = findViewById(R.id.dateTextView);
        timelineContainer = findViewById(R.id.timelineContainer);

        db = UserDatabaseHelper.getInstance(this);

        itineraryId = getIntent().getLongExtra("itineraryId", -1);
        userId = getIntent().getLongExtra("userId", -1);
        Log.d(tag, "Received itineraryId: " + itineraryId);

        if (itineraryId != -1) {
            String[] itineraryDetails = db.getItineraryDetails(itineraryId);

            if (itineraryDetails != null && itineraryDetails.length == 2) {
                String title = itineraryDetails[0];
                String date = itineraryDetails[1];

                Log.d(tag, "Received Title: " + title);
                Log.d(tag, "Received Date: " + date);

                titleTextView.setText(title != null && !title.isEmpty() ? title : getString(R.string.scrollTitle));
                dateTextView.setText(date != null && !date.isEmpty() ? date : getString(R.string.scrollDate));

                loadTimelines();
            } else {
                Log.e(tag, "Failed to retrieve itinerary details");
                titleTextView.setText(R.string.errorScroll);
                dateTextView.setText(R.string.retrieveScroll);
            }
        } else {
            Log.e(tag, "Invalid itinerary ID");
            titleTextView.setText(R.string.errorScroll);
            dateTextView.setText(R.string.invalidItineraryId);
        }
        RelativeLayout backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItineraryScrollView.this, ItineraryActivity.class);
                intent.putExtra("fullName", getIntent().getStringExtra("fullName"));
                intent.putExtra("email", getIntent().getStringExtra("email"));
                intent.putExtra("username",getIntent().getStringExtra("username"));
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });
        FloatingActionButton fabAddTimeline = findViewById(R.id.fabAddTimeline);
        fabAddTimeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddTimelineDialog();
            }
        });
    }

    private void showAddTimelineDialog() {
        final Dialog dialog = new Dialog(ItineraryScrollView.this);
        dialog.setContentView(R.layout.dialog_add_timeline);

        final EditText timelinePeriodEditText = dialog.findViewById(R.id.timelinePeriodEditText);
        final EditText notesEditText = dialog.findViewById(R.id.notesEditText);
        Button saveButton = dialog.findViewById(R.id.saveButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timelinePeriod = timelinePeriodEditText.getText().toString().trim();
                String notes = notesEditText.getText().toString().trim();

                if (timelinePeriod.isEmpty() || notes.isEmpty()) {
                    Toast.makeText(ItineraryScrollView.this, R.string.notesFill, Toast.LENGTH_SHORT).show();
                    return;
                }

                long timelineId = db.addTimeline(itineraryId, timelinePeriod, notes);
                if (timelineId != -1) {
                    addTimelineToLayout(timelinePeriod, notes, timelineId);
                    dialog.dismiss();
                } else {
                    Toast.makeText(ItineraryScrollView.this, R.string.failedAddTimeline, Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

    private void showEditTimelineDialog(final long timelineId, String timelinePeriod, String notes) {
        final Dialog dialog = new Dialog(ItineraryScrollView.this);
        dialog.setContentView(R.layout.dialog_add_timeline);

        final EditText timelinePeriodEditText = dialog.findViewById(R.id.timelinePeriodEditText);
        final EditText notesEditText = dialog.findViewById(R.id.notesEditText);
        Button saveButton = dialog.findViewById(R.id.saveButton);

        timelinePeriodEditText.setText(timelinePeriod);
        notesEditText.setText(notes);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String updatedTimelinePeriod = timelinePeriodEditText.getText().toString().trim();
                String updatedNotes = notesEditText.getText().toString().trim();

                if (updatedTimelinePeriod.isEmpty() || updatedNotes.isEmpty()) {
                    Toast.makeText(ItineraryScrollView.this, R.string.notesFill, Toast.LENGTH_SHORT).show();
                    return;
                }

                db.updateTimeline(timelineId, updatedTimelinePeriod, updatedNotes);
                loadTimelines();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void addTimelineToLayout(String timelinePeriod, String notes, final long timelineId) {
        View timelineView = getLayoutInflater().inflate(R.layout.activity_timeline_item, null);

        TextView timelinePeriodTextView = timelineView.findViewById(R.id.timelinePeriodTextView);
        LinearLayout notesContainer = timelineView.findViewById(R.id.notesContainer);

        timelinePeriodTextView.setText(timelinePeriod);
        addNotesToContainer(notesContainer, notes);

        timelineView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimelineItem timeline = db.getTimeline(timelineId);
                showEditTimelineDialog(timelineId, timeline.getTimelinePeriod(), timeline.getNotes());
            }
        });

        timelineContainer.addView(timelineView);
    }

    private void addNotesToContainer(LinearLayout container, String notes) {
        container.removeAllViews();
        String[] notesArray = notes.split("\\n");
        for (String note : notesArray) {
            TextView noteTextView = new TextView(this);
            noteTextView.setText("• " + note);
            noteTextView.setTextSize(14);
            noteTextView.setTextColor(getResources().getColor(android.R.color.black));
            container.addView(noteTextView);
        }
    }

    private void loadTimelines() {
        timelineContainer.removeAllViews();
        try {
            ArrayList<TimelineItem> timelines = db.getAllTimelines(itineraryId);
            for (TimelineItem timeline : timelines) {
                addTimelineToLayout(timeline.getTimelinePeriod(), timeline.getNotes(), timeline.getId());
            }
        } catch (Exception e) {
            Log.e(tag, "Error loading timelines", e);
            Toast.makeText(this, R.string.errorLoadingTimeline, Toast.LENGTH_SHORT).show();
        }
    }
}
