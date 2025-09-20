package com.example.androidproject;

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

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEdit;
    private EditText passwordEdit;
    private Button loginButton;
    private TextView newUser;
    private UserDatabaseHelper dbHelper;
    public String tag="LoginActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        usernameEdit = findViewById(R.id.usernameEdit);
        passwordEdit = findViewById(R.id.passwordEdit);
        loginButton = findViewById(R.id.loginButton);
        newUser = findViewById(R.id.newUser);

        dbHelper = new UserDatabaseHelper(this);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEdit.getText().toString();

                String password = passwordEdit.getText().toString();
                if(validateLogin(username,password)){
                    Toast.makeText(LoginActivity.this,R.string.loginSuccessful,Toast.LENGTH_SHORT).show();
                    saveLogin(username,password);
                    navigateNext(username);
                }
                else{
                    Toast.makeText(LoginActivity.this,R.string.invalidLogin,Toast.LENGTH_SHORT).show();
                }

            }
        });
        newUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
        checkLoginState();
    }
    private boolean validateLogin(String username,String password){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {UserDatabaseHelper.USERNAME,UserDatabaseHelper.PASSWORD};
        String selection = UserDatabaseHelper.USERNAME + "= ? AND " +UserDatabaseHelper.PASSWORD + " = ?";
        String[] selectionArgs = {username,password};

        Cursor cursor = db.query(
                UserDatabaseHelper.TRAVELERS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        boolean loginSuccessful = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return loginSuccessful;
    }
    private void navigateNext(String username){
        Cursor cursor = dbHelper.getUserInfo(username);
        if(cursor!=null && cursor.moveToFirst()){
            int fullNameIndex = cursor.getColumnIndex(UserDatabaseHelper.FULL_NAME);
            int emailIndex = cursor.getColumnIndex(UserDatabaseHelper.EMAIL);
            if(fullNameIndex!=-1 && emailIndex !=-1){
                String fullName = cursor.getString(fullNameIndex);
                String email = cursor.getString(emailIndex);
                cursor.close();
                long userId = getIntent().getLongExtra("userId", -1);
                Intent intent = new Intent(LoginActivity.this, home_page.class);
                intent.putExtra("fullName",fullName);
                intent.putExtra("email",email);
                intent.putExtra("username",username);
                intent.putExtra("userId", userId);
                startActivity(intent);
                finish();
            }
            else{
                Log.e(tag,"Error: Invalid column index");
                cursor.close();
            }
        }
    }
    private void saveLogin(String username,String password){
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn",true);
        editor.putString("username",username);
        editor.putString("password",password);
        long userId = dbHelper.getUserId(username);
        editor.putLong("userId",userId);
        editor.apply();
    }
    private void checkLoginState(){
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs",Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn",false);
        if(isLoggedIn){
            String username = sharedPreferences.getString("username","");
            navigateNext(username);
        }
    }
}