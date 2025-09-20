package com.example.androidproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.io.FileInputStream;
import android.content.res.AssetFileDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.AbstractMap;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Iterator;

public class ResultActivity extends AppCompatActivity {

    private static final String tag = "ResultActivity";

    private String selectedDestinationType;
    private String selectedActivity;
    private String selectedTravelWith;
    private String selectedClimate;
    private RecyclerView recyclerView;
    private RecommendedPlacesAdapter adapter;
    private List<RecommendedPlace> recommendedPlaces;

    private Interpreter tflite;
    private List<Map<String, Object>> data;
    private float[][] featureMatrix;
    private float[] clusters;  // Ensure clusters are floats
    private Map<String, Integer> vocab;
    private float[] idf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        selectedDestinationType = getIntent().getStringExtra("DESTINATION_TYPE");
        selectedActivity = getIntent().getStringExtra("ACTIVITY_TYPE");
        selectedTravelWith = getIntent().getStringExtra("TRAVEL_WITH");
        selectedClimate = getIntent().getStringExtra("CLIMATE");

        RelativeLayout backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(tag, "Back button is pressed");
                Intent intent = new Intent(ResultActivity.this, home_page.class);
                intent.putExtra("fullName", getIntent().getStringExtra("fullName"));
                intent.putExtra("email", getIntent().getStringExtra("email"));
                intent.putExtra("username", getIntent().getStringExtra("username"));
                startActivity(intent);
                finish();
            }
        });

        Log.d(tag, "Selected inputs: " +
                "DestinationType=" + selectedDestinationType +
                ", Activity=" + selectedActivity +
                ", TravelWith=" + selectedTravelWith +
                ", Climate=" + selectedClimate);

        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setVerticalScrollBarEnabled(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        recommendedPlaces = new ArrayList<>();
        adapter = new RecommendedPlacesAdapter(this, recommendedPlaces, new RecommendedPlacesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecommendedPlace place) {
                Intent intent = new Intent(ResultActivity.this, LocationPage.class);
                intent.putExtra("City", place.getCity());
                intent.putExtra("Country", place.getCountry());
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);

        try {
            tflite = new Interpreter(loadModelFile("model.tflite"));
            data = loadData("processed_data.csv");
            featureMatrix = loadFeatureMatrix("feature_matrix.json");
            clusters = loadClusters("clusters.json");
            vocab = loadVocab("tfidf_vocab.json");
            idf = loadIdf("tfidf_idf.json");
            Log.d(tag, "Model and data loaded successfully");
        } catch (IOException | JSONException e) {
            Log.e(tag, "Error loading model or data: " + e.getMessage());
            Toast.makeText(this, R.string.errorModel, Toast.LENGTH_SHORT).show();
        }

        runInference(selectedDestinationType, selectedActivity, selectedTravelWith, selectedClimate);
    }

    private MappedByteBuffer loadModelFile(String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = getAssets().openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private List<Map<String, Object>> loadData(String filename) throws IOException {
        List<Map<String, Object>> data = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open(filename)))) {
            String line;
            reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                Log.d(tag, "Reading line: " + line); // Log each line read
                String[] parts = line.split(",");
                if (parts.length == 9) { // Adjusted to match the number of columns
                    try {
                        Map<String, Object> map = new HashMap<>();
                        map.put("Country", parts[0].trim());
                        map.put("City", parts[1].trim());
                        map.put("Type of Destination", parts[2].trim());
                        map.put("Activities", parts[3].trim());
                        map.put("Who are you travelling with", parts[4].trim());
                        map.put("Climate", parts[5].trim());
                        map.put("Features", parts[6].trim()); // Assuming combined features are in the 7th column
                        float clusterValue = Float.parseFloat(parts[7].trim());
                        map.put("Cluster", clusterValue);  // Ensure clusters are floats
                        map.put("Image URL", parts[8].trim());  // Add Image URL
                        Log.d(tag, "Parsed cluster value: " + clusterValue);
                        data.add(map);
                        Log.d(tag, "Item added: " + map);
                    } catch (NumberFormatException e) {
                        Log.e(tag, "Error parsing cluster value in line: " + line + " - " + e.getMessage());
                    }
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

    private float[][] loadFeatureMatrix(String filename) throws IOException, JSONException {
        InputStream inputStream = getAssets().open(filename);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder jsonBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonBuilder.append(line);
        }
        JSONArray jsonArray = new JSONArray(jsonBuilder.toString());
        int rows = jsonArray.length();
        int cols = jsonArray.getJSONArray(0).length();
        float[][] matrix = new float[rows][cols];
        for (int i = 0; i < rows; i++) {
            JSONArray row = jsonArray.getJSONArray(i);
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = (float) row.getDouble(j);
            }
        }
        inputStream.close();
        return matrix;
    }

    private float[] loadClusters(String filename) throws IOException, JSONException {
        InputStream inputStream = getAssets().open(filename);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder jsonBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonBuilder.append(line);
        }
        JSONArray jsonArray = new JSONArray(jsonBuilder.toString());
        int length = jsonArray.length();
        float[] clusters = new float[length];  // Ensure clusters are floats
        for (int i = 0; i < length; i++) {
            clusters[i] = (float) jsonArray.getDouble(i);  // Ensure clusters are floats
        }
        inputStream.close();
        return clusters;
    }

    private Map<String, Integer> loadVocab(String filename) throws IOException, JSONException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open(filename)));
        StringBuilder jsonBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonBuilder.append(line);
        }
        JSONObject jsonObject = new JSONObject(jsonBuilder.toString());
        Map<String, Integer> vocab = new HashMap<>();
        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            vocab.put(key, jsonObject.getInt(key));
        }
        reader.close();
        return vocab;
    }

    private float[] loadIdf(String filename) throws IOException, JSONException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open(filename)));
        StringBuilder jsonBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonBuilder.append(line);
        }
        JSONArray jsonArray = new JSONArray(jsonBuilder.toString());
        float[] idf = new float[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            idf[i] = (float) jsonArray.getDouble(i);
        }
        reader.close();
        return idf;
    }

    private void runInference(String typeOfDestination, String activities, String travelWith, String climate) {
        try {
            String userInput = typeOfDestination + " " + activities + " " + travelWith + " " + climate;
            float[] userVector = transformInput(userInput);

            // Compute the magnitude of the userVector
            float userMagnitude = computeMagnitude(userVector);
            Log.d(tag, "User Vector Magnitude: " + userMagnitude);

            if (userMagnitude == 0.0f) {
                Log.e(tag, "User vector magnitude is zero. Check input and vocabulary.");
            } else {
                float[][] input = new float[1][userVector.length];
                input[0] = userVector;

                float userCluster = predictCluster(input);  // Ensure cluster is float
                processOutput(userCluster, userVector);
            }

        } catch (Exception e) {
            Log.e(tag, "Error during inference: " + e.getMessage());
            Toast.makeText(this, R.string.errorInference, Toast.LENGTH_SHORT).show();
        }
    }

    private float[] transformInput(String userInput) {
        // Tokenize input into words
        String[] words = userInput.split(" ");

        // Initialize TF-IDF vector with zeros
        float[] tfidfVector = new float[vocab.size()];

        // Calculate Term Frequency (TF)
        for (String word : words) {
            word = word.toLowerCase(); // Normalize the word (e.g., lowercase)
            if (vocab.containsKey(word)) {
                int index = vocab.get(word);
                tfidfVector[index] += 1.0; // Increment TF for the word
            }
        }

        // Apply IDF to TF vector
        for (int i = 0; i < tfidfVector.length; i++) {
            if (tfidfVector[i] > 0) {
                tfidfVector[i] *= idf[i]; // Apply IDF to TF-IDF vector
            }
        }

        // Log the TF-IDF vector for debugging
        Log.d(tag, "TF-IDF Vector: " + Arrays.toString(tfidfVector));

        return tfidfVector;
    }

    private float computeMagnitude(float[] vector) {
        float sumOfSquares = 0.0f;
        for (float value : vector) {
            sumOfSquares += value * value;
        }
        return (float) Math.sqrt(sumOfSquares);
    }

    private float predictCluster(float[][] input) {
        float[] output = new float[1]; // Adjust the output shape to match the model
        tflite.run(input, output);
        return output[0];  // Ensure output is float
    }

    private void processOutput(float userCluster, float[] userVector) {
        recommendedPlaces.clear();
        Log.d(tag, "Processing output for cluster: " + userCluster);

        List<Map<String, Object>> clusterData = new ArrayList<>();
        for (Map<String, Object> item : data) {
            if ((float) item.get("Cluster") == userCluster) {
                clusterData.add(item);
                Log.d(tag, "Item added to clusterData: " + item.get("City"));
            }
        }

        Log.d(tag, "Number of items in cluster: " + clusterData.size());

        List<Map.Entry<Map<String, Object>, Float>> similarityScores = new ArrayList<>();
        for (Map<String, Object> item : clusterData) {
            String features = (String) item.get("Features");
            float[] itemVector = transformInput(features);

            // Compute cosine similarity
            float dotProduct = 0.0f;
            float userMagnitude = 0.0f;
            float itemMagnitude = 0.0f;
            for (int i = 0; i < userVector.length; i++) {
                dotProduct += userVector[i] * itemVector[i];
                userMagnitude += userVector[i] * userVector[i];
                itemMagnitude += itemVector[i] * itemVector[i];
            }
            Log.d(tag, "User Vector Magnitude: " + Math.sqrt(userMagnitude));
            Log.d(tag, "Item Vector Magnitude: " + Math.sqrt(itemMagnitude));
            float similarity;
            if (userMagnitude == 0 || itemMagnitude == 0) {
                similarity = 0; // Handle as appropriate
            } else {
                similarity = dotProduct / (float) (Math.sqrt(userMagnitude) * Math.sqrt(itemMagnitude));
            }
            similarityScores.add(new AbstractMap.SimpleEntry<>(item, similarity));
            Log.d(tag, "Similarity score for " + item.get("City") + ": " + similarity);
        }

        // Sort by similarity score in descending order
        Collections.sort(similarityScores, new Comparator<Map.Entry<Map<String, Object>, Float>>() {
            @Override
            public int compare(Map.Entry<Map<String, Object>, Float> o1, Map.Entry<Map<String, Object>, Float> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        Log.d(tag, "Top recommendations:");

        // Get top 4 recommended places
        for (int i = 0; i < Math.min(4, similarityScores.size()); i++) {
            Map<String, Object> item = similarityScores.get(i).getKey();
            String city = (String) item.get("City");
            String country = (String) item.get("Country");
            String imageUrl = (String) item.get("Image URL");  // Retrieve the image URL
            recommendedPlaces.add(new RecommendedPlace(city, country, imageUrl));  // Pass the image URL to the constructor
            Log.d(tag, "Recommended Place: " + city + ", " + country + ", " + imageUrl);
        }

        // Notify adapter
        adapter.notifyDataSetChanged();
        Log.d(tag, "Adapter notified of data change");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tflite != null) {
            tflite.close();
        }
    }
}
