package com.example.pathfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class obstacle_detection extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obstacle_detection); // Set the content view first

        Intent intent = new Intent();
        intent.setClassName("org.tensorflow.lite.examples.detection", "org.tensorflow.lite.examples.detection.DetectActivity");

        // Start the activity if it exists, otherwise catch the exception
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Handle the case where the activity is not found
            // For example, display a message to the user or take appropriate action
        }
    }
}