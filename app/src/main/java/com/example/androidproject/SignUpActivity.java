package com.example.androidproject;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignUpActivity extends AppCompatActivity {
    private EditText fullNameEdit;
    private EditText usernameEdit;
    private EditText emailIDEdit;
    private EditText passwordEdit;
    private EditText confirmPasswordEdit;
    private Button signUpButton;
    private TextView alreadyUser;
    private UserDatabaseHelper dbHelper;
    private String tag = "SignUp Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fullNameEdit = findViewById(R.id.fullNameEdit);
        usernameEdit = findViewById(R.id.usernameEdit);
        emailIDEdit = findViewById(R.id.emailIDEdit);
        passwordEdit = findViewById(R.id.passwordEdit);
        confirmPasswordEdit = findViewById(R.id.confirmPasswordEdit);
        signUpButton = findViewById(R.id.signUpButton);
        alreadyUser = findViewById(R.id.alreadyUser);

        dbHelper = new UserDatabaseHelper(this);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = fullNameEdit.getText().toString();
                String username = usernameEdit.getText().toString();
                String email = emailIDEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                String confirmPassword = confirmPasswordEdit.getText().toString();
                if (isInputValid(fullName, username, email, password, confirmPassword)) {
                    long userId = addUserToDatabase(fullName, username, email, password);
                    if (userId != -1) {
                        Toast.makeText(SignUpActivity.this, R.string.signUpSucess, Toast.LENGTH_SHORT).show();
                        saveLoginState(userId, username, fullName, email);
                        navigateNext(userId, fullName, email, username);
                    } else {
                        Toast.makeText(SignUpActivity.this, R.string.signUpFail, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        alreadyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean isInputValid(String fullName, String username, String email, String password, String confirmPassword) {
        if (fullName.isEmpty()) {
            showToast("Full name cannot be empty");
            return false;
        }
        if (username.isEmpty()) {
            showToast("Username cannot be empty");
            return false;
        }
        if (email.isEmpty()) {
            showToast("Email cannot be empty");
            return false;
        }
        if (!isValidEmailFormat(email)) {
            showToast("Email format is invalid");
            return false;
        }
        if (password.isEmpty()) {
            showToast("Password cannot be empty");
            return false;
        }
        if (confirmPassword.isEmpty()) {
            showToast("Confirm password cannot be empty");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            showToast("Passwords do not match");
            return false;
        }
        return true;
    }

    private boolean isValidEmailFormat(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    private void showToast(String message) {
        Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private long addUserToDatabase(String fullName, String username, String email, String password) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cValues = new ContentValues();
        cValues.put(UserDatabaseHelper.FULL_NAME, fullName);
        cValues.put(UserDatabaseHelper.USERNAME, username);
        cValues.put(UserDatabaseHelper.EMAIL, email);
        cValues.put(UserDatabaseHelper.PASSWORD, password);
        cValues.put(UserDatabaseHelper.THIRD_PARTY, 1);
        cValues.put(UserDatabaseHelper.RECEIVE_EMAILS, 1);
        cValues.put(UserDatabaseHelper.REMINDERS, 1);
        cValues.put(UserDatabaseHelper.TRENDING_PLACES, 1);
        cValues.put(UserDatabaseHelper.FEEDBACK, 1);
        cValues.put(UserDatabaseHelper.SUPPORT, 1);
        Log.i(tag, "Entry added to the Database Users with Username " + username + " and password " + password);
        long newRowId = db.insert(UserDatabaseHelper.TRAVELERS, null, cValues);
        db.close();
        return newRowId;
    }

    private void saveLoginState(long userId, String username, String fullName, String email) {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putLong("userId", userId);
        editor.putString("username", username);
        editor.putString("fullName", fullName);
        editor.putString("email", email);
        editor.apply();
    }

    private void navigateNext(long userId, String fullName, String email, String username) {
        Intent intent = new Intent(SignUpActivity.this, home_page.class);
        intent.putExtra("userId", userId);
        intent.putExtra("fullName", fullName);
        intent.putExtra("email", email);
        intent.putExtra("username", username);
        startActivity(intent);
        finish();
    }
}
