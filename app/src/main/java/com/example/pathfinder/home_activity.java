package com.example.pathfinder;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class home_activity extends AppCompatActivity implements GestureDetector.OnGestureListener, TextToSpeech.OnInitListener {

    private GestureDetector gestureDetector;
    private ImageView imageView;
    private Button buttonObstacleDetection, buttonObjectDetection;
    private TextToSpeech textToSpeech;
    private boolean isTTSInitialized = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize views
        View buttonBlind = findViewById(R.id.obstacledetection);
        View buttonVolunteer = findViewById(R.id.objectdetection);
        View invisibleButton = findViewById(R.id.invisibleButton);

        // Initialize GestureDetector
        gestureDetector = new GestureDetector(this, this);

        // Initialize TextToSpeech
        textToSpeech = new TextToSpeech(this, this);

        // Add swipe listeners to the buttons
        buttonBlind.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }
        });

        buttonVolunteer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }
        });

        // Set click listener for the invisible button
        invisibleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Read the specified message
                String message = "Swipe up for obstacle detection and swipe down for object detection";
                speak(message);
            }
        });
    }

    private void speak(String text) {
        if (isTTSInitialized) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    private void speakActionLabels() {
        String labelText = "Swipe up to select Object Detection. Swipe down to select Obstacle Detection.";
        speak(labelText);
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    public void startObjectDetection() {
        speak("Opening Object Detection");
        Intent intent = new Intent(home_activity.this, object_detection.class);
        startActivity(intent);
    }

    public void startObstacleDetection() {
        speak("Opening Obstacle Detection");
        Intent intent = new Intent(home_activity.this, obstacle_detection.class);
        startActivity(intent);
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return true;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        float diffY = motionEvent1.getY() - motionEvent.getY();
        if (Math.abs(diffY) > 100) {
            if (diffY > 0) {
                // Swipe down
                // Start object detection
                startObjectDetection();
            } else {
                // Swipe up
                // Start obstacle detection
                startObstacleDetection();
            }
        }
        return true;
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            isTTSInitialized = true;
            int result = textToSpeech.setLanguage(Locale.getDefault());
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Language not supported");
            }
        } else {
            Log.e("TTS", "Initialization failed");
        }
    }
}
