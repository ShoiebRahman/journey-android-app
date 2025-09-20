package com.example.androidproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

public class Help extends AppCompatActivity {
    private final String tag="Help";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_help);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        RelativeLayout backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(tag, "Back button is pressed");
                Intent intent  = new Intent(Help.this, ProfileSection.class);
                intent.putExtra("fullName", getIntent().getStringExtra("fullName"));
                intent.putExtra("email", getIntent().getStringExtra("email"));
                intent.putExtra("username",getIntent().getStringExtra("username"));
                startActivity(intent);
                finish();
            }
        });
        TextView faq = findViewById(R.id.faqHeading);
        faq.setOnClickListener(v -> {
            Intent intent = new Intent(Help.this,faq.class);
            intent.putExtra("fullName", getIntent().getStringExtra("fullName"));
            intent.putExtra("email", getIntent().getStringExtra("email"));
            intent.putExtra("username",getIntent().getStringExtra("username"));
            startActivity(intent);
        });
        TextView contactUs = findViewById(R.id.contactUsHeading);
        contactUs.setOnClickListener(v -> {
            Intent intent = new Intent(Help.this,contactUs.class);
            intent.putExtra("fullName", getIntent().getStringExtra("fullName"));
            intent.putExtra("email", getIntent().getStringExtra("email"));
            intent.putExtra("username",getIntent().getStringExtra("username"));
            startActivity(intent);
        });
        TextView feedback = findViewById(R.id.feedbackHeading);
        feedback.setOnClickListener(v->{
            Snackbar.make(findViewById(R.id.main),getString(R.string.feedbackMessage), Snackbar.LENGTH_LONG).show();
            showFeedbackDialog();
        });
    }
    private void showFeedbackDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_feeback, null);
        final EditText feedbackInput = dialogView.findViewById(R.id.feedback_input);

        builder.setView(dialogView)
                .setTitle(getString(R.string.feedbackDialogTitle))
                .setPositiveButton(getString(R.string.feedbacKSubmit), (dialog, which) -> {
                    String feedback = feedbackInput.getText().toString();
                    Toast.makeText(Help.this, getString(R.string.feedbackToast) + feedback, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

}
