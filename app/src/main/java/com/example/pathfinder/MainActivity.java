package com.example.pathfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;

import android.text.InputType;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import android.widget.ImageView;


public class MainActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private ImageView imageViewShowPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the views by their IDs
        usernameEditText = findViewById(R.id.editTextUsername);
        passwordEditText = findViewById(R.id.pass1);
        imageViewShowPassword = findViewById(R.id.imageViewShowPassword);

        // Set click listener for the login button
        findViewById(R.id.Login4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the username and password from the edit texts
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (!validateUsername() | !validatePassword()) {
                    return;
                } else {
                    checkUser();
                }
            }
        });

        // Set click listener for the register button
        Button registerButton = findViewById(R.id.button2);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, registration_page.class);
                startActivity(intent);
            }
        });

        // Set click listener for the "Show Password" icon
        imageViewShowPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toggle password visibility
                if (passwordEditText.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    imageViewShowPassword.setImageResource(R.drawable.ic_visibility_off);
                } else {
                    passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    imageViewShowPassword.setImageResource(R.drawable.ic_visibility_off);
                }
                // Move cursor to the end of the text
                passwordEditText.setSelection(passwordEditText.getText().length());
            }
        });
    }

    public Boolean validateUsername() {
        String val = usernameEditText.getText().toString();
        if (val.isEmpty()) {
            usernameEditText.setError("Username cannot be empty");
            return false;
        } else {
            usernameEditText.setError(null);
            return true;
        }
    }

    public Boolean validatePassword() {
        String val = passwordEditText.getText().toString();
        if (val.isEmpty()) {
            passwordEditText.setError("Password cannot be empty");
            return false;
        } else {
            passwordEditText.setError(null);
            return true;
        }
    }

    public void checkUser() {
        String userUsername = usernameEditText.getText().toString().trim();
        String userPassword = passwordEditText.getText().toString().trim();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(userUsername);
        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    usernameEditText.setError(null);
                    String passwordFromDB = snapshot.child(userUsername).child("password").getValue(String.class);
                    if (passwordFromDB.equals(userPassword)) {
                        usernameEditText.setError(null);
                        String nameFromDB = snapshot.child(userUsername).child("name").getValue(String.class);
                        String emailFromDB = snapshot.child(userUsername).child("email").getValue(String.class);
                        String usernameFromDB = snapshot.child(userUsername).child("username").getValue(String.class);
                        Intent intent = new Intent(MainActivity.this, home_activity.class);
                        intent.putExtra("name", nameFromDB);
                        intent.putExtra("email", emailFromDB);
                        intent.putExtra("username", usernameFromDB);
                        intent.putExtra("password", passwordFromDB);
                        startActivity(intent);
                    } else {
                        passwordEditText.setError("Invalid Credentials");
                        passwordEditText.requestFocus();
                    }
                } else {
                    usernameEditText.setError("User does not exist");
                    usernameEditText.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
