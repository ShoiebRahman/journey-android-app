package com.example.androidproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final String tag = "MainActivity";
    private ViewFlipper viewFlipper;
    private Handler handler;
    private final int flipInterval = 2000;
    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        viewFlipper = findViewById(R.id.imageSlider);
        handler = new Handler();
        startFlipping();
        Button getStarted = findViewById(R.id.getStartedButton);
        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(tag, "Get Started was clicked");
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void startFlipping() {
        handler.postDelayed(flipRunnable, flipInterval);
    }

    private final Runnable flipRunnable = new Runnable() {
        @Override
        public void run() {
            if (currentIndex < viewFlipper.getChildCount() - 1) {
                viewFlipper.showNext();
                currentIndex++;
                handler.postDelayed(this, flipInterval);
            }
        }
    };
}
