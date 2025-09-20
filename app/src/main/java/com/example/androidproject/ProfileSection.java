package com.example.androidproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ProfileSection extends AppCompatActivity {
    public String tag="profileSection";
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_section);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        TextView fullNameTextView = findViewById(R.id.fullName);
        TextView emailIDTextView = findViewById(R.id.emailID);
        Button editProfile = findViewById(R.id.editProfile);
        LinearLayout settings = findViewById(R.id.settingsLayout);
        LinearLayout itinerary = findViewById(R.id.itineraryLayout);
        LinearLayout changePassword = findViewById(R.id.changePasswordLayout);
        LinearLayout help = findViewById(R.id.helpLayout);
        LinearLayout logout = findViewById(R.id.logoutLayout);
        RelativeLayout backButton = findViewById(R.id.backButton);
        view = findViewById(R.id.back_view);

        String fullName = getIntent().getStringExtra("fullName");
        String email = getIntent().getStringExtra("email");
        String username = getIntent().getStringExtra("username");

        if (fullName == null || email == null|| username == null) {
            SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
            fullName = sharedPreferences.getString("fullName", "");
            email = sharedPreferences.getString("email", "");
            username = sharedPreferences.getString("username", "");
        }

        fullNameTextView.setText(fullName);
        emailIDTextView.setText(email);
        final String finalFullName = fullName;
        final String finalEmail = email;
        final String finalUsername = username;

        view.setOnClickListener(v->finish());

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(tag, "back button is pressed");
                Intent intent  = new Intent(ProfileSection.this, home_page.class);
                intent.putExtra("fullName", finalFullName);
                intent.putExtra("email",finalEmail);
                intent.putExtra("username",finalUsername);
                startActivity(intent);
                finish();
            }
        });
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileSection.this, EditProfile.class);
                intent.putExtra("fullName",finalFullName);
                intent.putExtra("email",finalEmail);
                intent.putExtra("username",finalUsername);
                startActivity(intent);
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileSection.this, Setting.class);
                intent.putExtra("fullName",finalFullName );
                intent.putExtra("email", finalEmail);
                intent.putExtra("username",finalUsername);
                startActivity(intent);
            }
        });

        itinerary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(tag, "Clicking on itinerary section");
                Intent intent = new Intent(ProfileSection.this, ItineraryActivity.class);
                intent.putExtra("fullName", finalFullName);
                intent.putExtra("email", finalEmail);
                intent.putExtra("username", finalUsername);
                long userId = getIntent().getLongExtra("userId", -1);
                if (userId == -1) {
                    SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
                    userId = sharedPreferences.getLong("userId", -1);
                }
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileSection.this, changePassword.class);
                intent.putExtra("fullName", finalFullName);
                intent.putExtra("email", finalEmail);
                intent.putExtra("username",finalUsername);
                startActivity(intent);
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileSection.this, Help.class);
                intent.putExtra("fullName", finalFullName);
                intent.putExtra("email", finalEmail);
                intent.putExtra("username",finalUsername);
                startActivity(intent);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(tag,"Clicking on logout");
                clearLoginState();
                Intent intent = new Intent(ProfileSection.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void clearLoginState(){
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}