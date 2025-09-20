package com.example.androidproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.search.SearchBar;
import androidx.appcompat.widget.SearchView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class home_page extends AppCompatActivity {

    private RecyclerView recyclerView;
    private destinationAdapter destinationAdapter;
    private List<Map<String, Object>> destinationList;
    private BottomNavigationView navBar;
    private SearchBar searchBar;
    private SearchView searchView;

    private Button featuredButton;

    private Button personalRec;
    private TextView fullNameTextView;
    private final String tag = "homeActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        recyclerView = findViewById(R.id.recycler_view);
        navBar = findViewById(R.id.navBar);
        personalRec = findViewById(R.id.tabRecommended);
        featuredButton = findViewById(R.id.tabFeatured);
        searchView = findViewById(R.id.search_bar);
        fullNameTextView = findViewById(R.id.fullName);

        personalRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(home_page.this, DestinationSelectionActivity.class);
                startActivity(intent);
            }
        });

        featuredButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(home_page.this, FeaturedActivity.class);
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
                    Log.d(tag, "Favorites icon clicked");
                    //Toast.makeText(home_page.this, "Opening Wishlist", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(home_page.this, Wishlist.class));
                    return true;
                }
                else if(item.getItemId() == R.id.nav_profile) {
                    Intent intent = new Intent(home_page.this, ProfileSection.class);
                    SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
                    String fullName = sharedPreferences.getString("fullName", "");
                    String email = sharedPreferences.getString("email", "");
                    String username = sharedPreferences.getString("username", "");
                    intent.putExtra("fullName", fullName);
                    intent.putExtra("email", email);
                    intent.putExtra("username", username);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
    }
    @Override
    protected void onResume(){
        super.onResume();
        updateFullName();
    }
    private void updateFullName(){
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        String fullName = sharedPreferences.getString("fullName", "");
        fullNameTextView.setText(fullName);
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
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                Log.d(tag, "Reading line: " + line);
                String[] parts = line.split(";");
                if (parts.length == 10) {
                    try {
                        Map<String, Object> map = new HashMap<>();
                        map.put("Country", parts[0].trim());
                        map.put("City", parts[1].trim());
                        map.put("ImageURL", parts[6].trim());
                        data.add(map);
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

