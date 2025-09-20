package com.example.androidproject;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class EditProfile extends AppCompatActivity {
    public String tag = "editProfile";
    public EditText dobEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView fullNameTextView = findViewById(R.id.fullName);
        TextView emailTextView = findViewById(R.id.email);
        EditText fullNameEditText = findViewById(R.id.fullNameEditText);
        Spinner genderSpinner = findViewById(R.id.genderSpinner);
        dobEditText = findViewById(R.id.dobEditText);
        EditText phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        EditText emailEditText = findViewById(R.id.emailEditText);
        Button saveButton = findViewById(R.id.saveEditProfile);
        RelativeLayout backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(tag, "back button is pressed");
                Intent intent  = new Intent(EditProfile.this, ProfileSection.class);
                intent.putExtra("fullName", getIntent().getStringExtra("fullName"));
                intent.putExtra("email", getIntent().getStringExtra("email"));
                intent.putExtra("username",getIntent().getStringExtra("username"));
                startActivity(intent);
                finish();
            }
        });

        String fullName = getIntent().getStringExtra("fullName");
        String email = getIntent().getStringExtra("email");

        fullNameTextView.setText(fullName);
        emailTextView.setText(email);
        fullNameEditText.setText(fullName);
        emailEditText.setText(email);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.genderArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);

        genderSpinner.setSelection(adapter.getPosition("Select Gender"));

        dobEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(tag, "Clicked on save button");
                // Handle save profile changes
            }
        });
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                        dobEditText.setText(date);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }
}
