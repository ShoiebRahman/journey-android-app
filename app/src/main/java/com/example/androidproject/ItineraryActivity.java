package com.example.androidproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class ItineraryActivity extends AppCompatActivity {
    private final String tag="ItineraryActivity";
    private ListView itineraryListView;
    private ArrayList<ItineraryItem> itineraryList;
    private ItineraryAdapter adapter;
    private UserDatabaseHelper db;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_itinerary);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = UserDatabaseHelper.getInstance(this);
        itineraryListView = findViewById(R.id.itineraryListView);

        // Get user ID from Intent or SharedPreferences
        userId = getIntent().getLongExtra("userId", -1);
        if (userId == -1) {
            SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
            userId = sharedPreferences.getLong("userId", -1);
        }

        if (userId != -1) {
            itineraryList = db.getAllItineraries(userId); // Fetch itineraries for the specific user
            adapter = new ItineraryAdapter(this, R.layout.itinerary_list_item, itineraryList);
            itineraryListView.setAdapter(adapter);

            itineraryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ItineraryItem item = itineraryList.get(position);
                    Intent intent = new Intent(ItineraryActivity.this, ItineraryScrollView.class);
                    intent.putExtra("itineraryId", item.getId());
                    intent.putExtra("userId", userId);
                    Log.i(tag, "Starting ItineraryScrollView with ID: " + item.getId());
                    startActivity(intent);
                }
            });

            Button addButton = findViewById(R.id.addButton);
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ItineraryActivity.this, AddItineraryActivity.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                }
            });

            RelativeLayout backButton = findViewById(R.id.backButton);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ItineraryActivity.this, ProfileSection.class);
                    intent.putExtra("fullName", getIntent().getStringExtra("fullName"));
                    intent.putExtra("email", getIntent().getStringExtra("email"));
                    intent.putExtra("username",getIntent().getStringExtra("username"));
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                }
            });
        } else {
            Log.e(tag, "Invalid user ID");
            Toast.makeText(this, R.string.invalidUser, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (userId != -1) {
            itineraryList.clear();
            itineraryList.addAll(db.getAllItineraries(userId));
            adapter.notifyDataSetChanged();
        }
    }
}
