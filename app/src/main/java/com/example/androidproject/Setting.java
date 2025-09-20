package com.example.androidproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class Setting extends AppCompatActivity {
    public final String tag = "Settings";
    private UserDatabaseHelper dbHelper;
    private SharedPreferences themePreferences;
    public String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        LinearLayout accountPrivacy = findViewById(R.id.accountPrivacyLayout);
        LinearLayout notification = findViewById(R.id.notificationLayout);
        LinearLayout language = findViewById(R.id.languageLayout);
        LinearLayout theme = findViewById(R.id.themeLayout);
        LinearLayout delete = findViewById(R.id.deleteLayout);
        themePreferences = getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE);
        RelativeLayout backButton = findViewById(R.id.backButton);
        dbHelper = new UserDatabaseHelper(this);
        username = getIntent().getStringExtra("username");

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(tag, "back button is pressed");
                Intent intent  = new Intent(Setting.this, ProfileSection.class);
                intent.putExtra("fullName", getIntent().getStringExtra("fullName"));
                intent.putExtra("email", getIntent().getStringExtra("email"));
                intent.putExtra("username",getIntent().getStringExtra("username"));
                startActivity(intent);
                finish();
            }
        });

        accountPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(tag, "Clicked on account privacy");
                Intent intent = new Intent(Setting.this, accountPrivacy.class);
                intent.putExtra("username",getIntent().getStringExtra("username"));
                intent.putExtra("fullName", getIntent().getStringExtra("fullName"));
                intent.putExtra("email", getIntent().getStringExtra("email"));
                startActivity(intent);
                finish();
            }
        });
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(tag, "Clicked on notification");
                Intent intent = new Intent(Setting.this, notifications.class);
                intent.putExtra("username",getIntent().getStringExtra("username"));
                intent.putExtra("fullName", getIntent().getStringExtra("fullName"));
                intent.putExtra("email", getIntent().getStringExtra("email"));
                startActivity(intent);
                finish();
            }
        });
        language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i(tag, "Clicked on language");
                changeLanguage();
            }
        });
        theme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i(tag, "Clicked on theme");
                changeTheme();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(tag, "Clicked on delete");
                showDeleteAccountDialog();
            }
        });
    }
    private void changeLanguage(){
        new AlertDialog.Builder(this).setTitle(getString(R.string.changeLanguageDialog))
                .setMessage(getString(R.string.changeLanguageMessage))
                .setPositiveButton(getString(R.string.gotoSettings), (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
                    startActivity(intent);
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }

    private void changeTheme() {
        final String[] themes = {"Light", "Dark"};
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.chooseTheme))
                .setSingleChoiceItems(themes, getSelectedTheme(), (dialog, which) -> {
                    if (which == 0) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        saveTheme(AppCompatDelegate.MODE_NIGHT_NO);
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        saveTheme(AppCompatDelegate.MODE_NIGHT_YES);
                    }
                    dialog.dismiss();
                    recreate();
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }
    private void saveTheme(int mode) {
        SharedPreferences.Editor editor = themePreferences.edit();
        editor.putInt("theme_mode", mode);
        editor.apply();
    }

    private int getSelectedTheme() {
        return themePreferences.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_NO) == AppCompatDelegate.MODE_NIGHT_NO ? 0 : 1;
    }

    private void setAppTheme() {
        int themeMode = themePreferences.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_NO);
        AppCompatDelegate.setDefaultNightMode(themeMode);
    }
    private void showDeleteAccountDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.deleteAccount))
                .setMessage(getString(R.string.deleteMessage))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAccount();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }
    private void deleteAccount() {
        String email = getIntent().getStringExtra("email");
        dbHelper.deleteUser(email);
        clearLoginState();
        Intent intent = new Intent(Setting.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    private void clearLoginState() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
