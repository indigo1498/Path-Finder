package com.example.pathfinder;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class blind_detail extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private EditText labelEditText;
    private Button proceedButton, voiceCommandButton;
    private TextToSpeech textToSpeech;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blind_detail);

        labelEditText = findViewById(R.id.editTextUsername1);
        proceedButton = findViewById(R.id.Login55);
        voiceCommandButton = findViewById(R.id.voiceButton);

        textToSpeech = new TextToSpeech(this, this);

        // Speak the label of the text box when entering the page
        speakLabel();

        // Start speech to text automatically when the activity is created
        startSpeechToText();

        voiceCommandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSpeechToText();
            }
        });
    }

    private void speakLabel() {
        // Speak the label of the text box
        String label = labelEditText.getHint().toString();
        textToSpeech.speak(label, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    private void proceed() {
        String labelText = labelEditText.getText().toString().trim();
        if (!labelText.isEmpty()) {
            Intent intent = new Intent(blind_detail.this, home_activity.class);
            startActivity(intent);
        }
    }

    private void startSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak something");

        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Speech recognition not supported on this device", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to handle voice commands
    private void handleVoiceCommand(String command) {
        if (!command.isEmpty()) {
            // Set spoken text to the text field
            labelEditText.setText(command);

            // Proceed automatically
            proceed();
        }
    }

    // Override method to handle voice input result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                String spokenText = result.get(0);

                // Handle voice command
                handleVoiceCommand(spokenText.toLowerCase());
            }
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.getDefault());
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "Text-to-Speech language not supported", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Text-to-Speech initialization failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}
