package com.example.androidproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LocationPage extends AppCompatActivity {
    private Button openMaps;
    private ImageView placeImage;
    private TextView placeNameCity;
    private TextView placeNameCountry;
    private TextView placeDescription;
    private ImageView one;
    private ImageView two;
    private ImageView three;
    private ImageView four;
    private double latitude, longitude;
    private static final String tag = "LocationPage";

    private String cityTag;
    private int position;

    private List<Map<String, Object>> dataList;
    private ImageButton heartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_page);

        initializeViews();
        setupBackButton();

        Intent intent = getIntent();
        cityTag = intent.getStringExtra("City");

        try {
            dataList = loadData("database.csv");
            if (!dataList.isEmpty()) {
                setLocationData(dataList, cityTag);
            } else {
                Log.e(tag, "Data list is empty");
                Toast.makeText(this, R.string.noData, Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Log.e(tag, "Error loading data: " + e.getMessage());
            Toast.makeText(this, R.string.errorLoading, Toast.LENGTH_SHORT).show();
        }

        openMaps.setOnClickListener(v -> openMapLocation());
        heartButton.setOnClickListener(v -> addToWishlist());
    }

    private void initializeViews() {
        openMaps = findViewById(R.id.open_maps);
        placeImage = findViewById(R.id.place_image);
        placeNameCity = findViewById(R.id.place_name);
        placeNameCountry = findViewById(R.id.place_location);
        placeDescription = findViewById(R.id.place_description);
        one = findViewById(R.id.one);
        two = findViewById(R.id.two);
        three = findViewById(R.id.three);
        four = findViewById(R.id.four);
        heartButton = findViewById(R.id.heart_button);
    }

    private void setupBackButton() {
        RelativeLayout backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            Log.i(tag, "Back button is pressed");
            Intent intent = new Intent(LocationPage.this, home_page.class);
            intent.putExtra("fullName", getIntent().getStringExtra("fullName"));
            intent.putExtra("email", getIntent().getStringExtra("email"));
            intent.putExtra("username", getIntent().getStringExtra("username"));
            startActivity(intent);
            finish();
        });
    }

    private void setLocationData(List<Map<String, Object>> dataList, String citytag) {
        Log.d(tag, "Setting location data for city: " + cityTag);
        for (int i = 0; i < dataList.size(); ++i) {
            if (Objects.equals(dataList.get(i).get("City"), cityTag)) {
                position = i;
                break;
            }
        }

        Map<String, Object> item = dataList.get(position);
        Picasso.get().load((String) item.get("ImageURL")).into(placeImage);
        placeNameCity.setText((String) item.get("City"));
        placeNameCountry.setText((String) item.get("Country"));
        //placeDescription.setText((String) item.get("Description"));
        String description = (String)item.get("Description");
        int stringResId = getResources().getIdentifier(description, "string", getPackageName());
        placeDescription.setText(getString(stringResId));
        int imageResIdOne = getResources().getIdentifier((String) item.get("One"), "drawable", getPackageName());
        int imageResIdTwo = getResources().getIdentifier((String) item.get("Two"), "drawable", getPackageName());
        int imageResIdThree = getResources().getIdentifier((String) item.get("Three"), "drawable", getPackageName());
        int imageResIdFour = getResources().getIdentifier((String) item.get("Four"), "drawable", getPackageName());
        /*Picasso.get().load((String) item.get("One")).into(one);
        Picasso.get().load((String) item.get("Two")).into(two);
        Picasso.get().load((String) item.get("Three")).into(three);
        Picasso.get().load((String) item.get("Four")).into(four);*/

        Picasso.get().load(imageResIdOne).into(one);
        Picasso.get().load(imageResIdTwo).into(two);
        Picasso.get().load(imageResIdThree).into(three);
        Picasso.get().load(imageResIdFour).into(four);

        String tempLat = (String) item.get("Lat");
        String tempLong= (String) item.get("Long");
        latitude = Double.parseDouble(tempLat);//(double) item.get("Lat");
        longitude = Double.parseDouble(tempLong);
    }

    private void openMapLocation() {
        Intent intent = new Intent(LocationPage.this, MapLocation.class);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        startActivity(intent);
    }
    private void addToWishlist() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        long userId = sharedPreferences.getLong("userId", -1);

        if (userId != -1) {
            UserDatabaseHelper dbHelper = UserDatabaseHelper.getInstance(this);
            Map<String, Object> item = dataList.get(position);
            dbHelper.addWishlistItem(userId, (String) item.get("City"), (String) item.get("Country"), (String) item.get("ImageURL"));
            Toast.makeText(this, R.string.addedWishlist, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.userLog, Toast.LENGTH_SHORT).show();
        }
    }

    private List<Map<String, Object>> loadData(String filename) throws IOException {
        List<Map<String, Object>> data = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open(filename)))) {
            String line;
            reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 10) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("Country", parts[0].trim());
                    map.put("City", parts[1].trim());
                    map.put("One", parts[2].trim());
                    map.put("Two", parts[3].trim());
                    map.put("Three", parts[4].trim());
                    map.put("Four", parts[5].trim());
                    map.put("ImageURL", parts[6].trim());
                    map.put("Description", parts[7].trim());
                    map.put("Lat", parts[8].trim());
                    map.put("Long", parts[9].trim());
                    data.add(map);
                    Log.d(tag, "Item added: " + map);
                } else {
                    Log.e(tag, "Unexpected number of columns in line: " + line);
                }
            }
        } catch (IOException e) {
            Log.e(tag, "Error reading file: " + e.getMessage());
        }
        Log.d(tag, "Data loaded, total items: " + data.size());
        return data;
    }
}
