package com.example.pathfinder;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class launch_page extends AppCompatActivity implements GestureDetector.OnGestureListener, TextToSpeech.OnInitListener {

    private Button buttonVolunteer, buttonBlind, invisibleButton;
    private GestureDetector gestureDetector;
    private TextToSpeech textToSpeech;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_page);

        // Initialize views
        buttonBlind = findViewById(R.id.visuallyimpaired);
        buttonVolunteer = findViewById(R.id.volunteer);
        invisibleButton = findViewById(R.id.invisibleButton);

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
                String message = "Swipe up for visually impaired persons and swipe down for volunteers";
                speak(message);
            }
        });
    }

    @Override
    public boolean onDown(@NonNull MotionEvent motionEvent) {
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
        float diffX = motionEvent1.getX() - motionEvent.getX();

        if (Math.abs(diffY) > Math.abs(diffX)) {
            if (diffY > 0) {
                // Swipe down
                speak("Volunteer selected");
                startVolunteer();
            } else {
                // Swipe up
                speak("Visually Impaired selected");
                Intent intent = new Intent(launch_page.this, blind_detail.class);
                startActivity(intent);
            }
        }
        return true;
    }

    public void startVolunteer() {
        Intent intent = new Intent(launch_page.this, MainActivity.class);
        startActivity(intent);
    }

    private void speak(String text) {
        // Speak the given text
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            // Set language to English
            int langResult = textToSpeech.setLanguage(Locale.ENGLISH);

            if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Handle language not supported or missing data
            }
        } else {
            // Handle initialization failure
        }
    }

    @Override
    protected void onDestroy() {
        // Release resources when the activity is destroyed
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }

        super.onDestroy();
    }
}
