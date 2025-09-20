package com.example.androidproject;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddItineraryActivity extends AppCompatActivity {
    private EditText titleEditText;
    private EditText dateEditText;
    private Button saveButton;
    private UserDatabaseHelper db;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_itinerary);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        titleEditText = findViewById(R.id.titleEditText);
        dateEditText = findViewById(R.id.dateEditText);
        saveButton = findViewById(R.id.saveButton);

        db = UserDatabaseHelper.getInstance(this);
        calendar = Calendar.getInstance();


        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString().trim();
                String date = dateEditText.getText().toString().trim();

                if (title.isEmpty() || date.isEmpty()) {
                    Toast.makeText(AddItineraryActivity.this, R.string.addItineraryEmptyTitle, Toast.LENGTH_SHORT).show();
                    return;
                }
                SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
                String username = sharedPreferences.getString("username", "");
                UserDatabaseHelper userDbHelper = UserDatabaseHelper.getInstance(AddItineraryActivity.this);
                long userId = userDbHelper.getUserId(username);
                if (userId != -1) {
                    long itineraryId = db.addItinerary(userId, title, date);
                    if (itineraryId != -1) {
                        Toast.makeText(AddItineraryActivity.this, R.string.addItinerarySuccessfully, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddItineraryActivity.this, R.string.addItineraryFailed, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddItineraryActivity.this,R.string.addItineraryUser, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateDateEditText();
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void updateDateEditText() {
        String myFormat = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dateEditText.setText(sdf.format(calendar.getTime()));
    }
}
