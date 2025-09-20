package com.example.androidproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeaturedActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private destinationAdapter destinationAdapter;
    private List<Map<String, Object>> destinationList;
    private BottomNavigationView navBar;

    private Button allTab;
    private SearchView searchView;

    private Button personalRec;
    String tag = "home";
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_featured);

        recyclerView = findViewById(R.id.recycler_view);
        navBar = findViewById(R.id.navBar);
        personalRec = findViewById(R.id.tabRecommended);
        allTab = findViewById(R.id.tabAll);
        searchView = findViewById(R.id.search_bar);

        allTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeaturedActivity.this, home_page.class);
                startActivity(intent);
            }
        });


        count = 1;


        personalRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeaturedActivity.this, DestinationSelectionActivity.class);
                startActivity(intent);
            }
        });


        recyclerView.setVerticalScrollBarEnabled(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        destinationList = new ArrayList<Map<String, Object>>();

        try {
            destinationList = loadData("database.csv");
        } catch (IOException e) {
            Log.e(tag, "Error loading data: " + e.getMessage());
        }

        destinationAdapter = new destinationAdapter(this, destinationList);
        recyclerView.setAdapter(destinationAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterDestinations(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterDestinations(newText);
                return false;
            }
        });

        navBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_favorites) {
                    startActivity(new Intent(FeaturedActivity.this, Wishlist.class));
                    return true;
                }else if(item.getItemId() == R.id.nav_profile) {
                    startActivity(new Intent(FeaturedActivity.this, ProfileSection.class));
                    return true;
                }
                return false;
            }
        });
    }

    private void filterDestinations(String query) {
        List<Map<String, Object>> filteredList = new ArrayList<>();
        for (Map<String, Object> destination : destinationList) {
            String city = (String) destination.get("City");
            String country = (String) destination.get("Country");
            if (city.toLowerCase().contains(query.toLowerCase()) ||
                    country.toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(destination);
            }
        }
        destinationAdapter.updateList(filteredList);
    }

    private List<Map<String, Object>> loadData(String filename) throws IOException {
        List<Map<String, Object>> data = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open(filename)))) {
            String line;
            reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null && count <= 10) {
                Log.d(tag, "Reading line: " + line); // Log the line read
                String[] parts = line.split(";");
                if (parts.length == 10) { // Adjusted to match the number of columns
                    try {
                        Map<String, Object> map = new HashMap<>();
                        map.put("Country", parts[0].trim());
                        map.put("City", parts[1].trim());
                        map.put("ImageURL", parts[6].trim());
                        data.add(map); // Add the map to the data list
                        count += 1;
                        Log.d(tag, "Item added: " + map);
                    } catch (NumberFormatException e) {
                        Log.e(tag, "Error parsing cluster value in line: " + line + " - " + e.getMessage());
                    }
                } else {
                    Log.e(tag, "Unexpected number of columns in line: " + line);
                    Log.e(tag, "Read: " + parts.length);
                }
            }
        } catch (IOException e) {
            Log.e(tag, "Error reading file: " + e.getMessage());
        }
        Log.d(tag, "Data loaded, total items: " + data.size());
        return data;
    }
}