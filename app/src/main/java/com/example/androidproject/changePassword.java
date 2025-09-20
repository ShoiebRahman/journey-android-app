package com.example.androidproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class changePassword extends AppCompatActivity {
    public String tag="changePassword";
    private UserDatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = UserDatabaseHelper.getInstance(this);
        EditText enterPassword = findViewById(R.id.enterPasswordEditText);
        EditText confirmPassword = findViewById(R.id.confirmPasswordEditText);
        Button resetPassword = findViewById(R.id.resetPassword);
        RelativeLayout backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(tag, "back button is pressed");
                Intent intent  = new Intent(changePassword.this, ProfileSection.class);
                intent.putExtra("fullName", getIntent().getStringExtra("fullName"));
                intent.putExtra("email", getIntent().getStringExtra("email"));
                intent.putExtra("username",getIntent().getStringExtra("username"));
                startActivity(intent);
                finish();
            }
        });
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(tag,"Clicked on reset password");
                String newPassword = enterPassword.getText().toString();
                String confirmNewPassword = confirmPassword.getText().toString();
                if(newPassword.isEmpty() || confirmNewPassword.isEmpty()){
                    Toast.makeText(changePassword.this,R.string.fillFields,Toast.LENGTH_SHORT).show();
                } else if (!newPassword.equals(confirmNewPassword)) {
                    Toast.makeText(changePassword.this,R.string.passwordNotMatch,Toast.LENGTH_SHORT).show();
                }else{
                    changeUserPassword(newPassword);
                    navigateToProfileSection();
                }
            }
        });
    }
    private void changeUserPassword(String newPassword){
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username","");
        if(dbHelper.updatePassword(username,newPassword)){
            Toast.makeText(changePassword.this,R.string.passwordChanged, Toast.LENGTH_SHORT).show();
            Log.i(tag,"Password changed successfully for user: "+username);
        }else{
            Toast.makeText(changePassword.this,R.string.failedPasswordChange, Toast.LENGTH_SHORT).show();
            Log.i(tag,"Failed to change password for user: "+username);
        }
    }
    private void navigateToProfileSection(){
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        String fullName = sharedPreferences.getString("fullName", "");
        String email = sharedPreferences.getString("email", "");
        String username = sharedPreferences.getString("username", "");

        Intent intent = new Intent(changePassword.this, ProfileSection.class);
        intent.putExtra("fullName", fullName);
        intent.putExtra("email", email);
        intent.putExtra("username", username);
        startActivity(intent);
        finish();
    }
}