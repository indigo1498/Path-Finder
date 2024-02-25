package com.example.pathfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.app.SearchableInfo;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class registration_page extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText Name1;
    private EditText MobileNum1;
    private RadioGroup genderRadioGroup;
    private RadioButton radioButtonMale;
    private RadioButton radioButtonFemale;

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private ImageSwitcher imageViewShowPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_page);

        // Find the views by their IDs
        usernameEditText = findViewById(R.id.editTextUsername);
        passwordEditText = findViewById(R.id.pass);
        Name1 = findViewById(R.id.Name1);
        MobileNum1 = findViewById(R.id.MobileNum1);
        genderRadioGroup = findViewById(R.id.radioGroupGender);
        radioButtonMale = findViewById(R.id.ButtonMale);
        radioButtonFemale = findViewById(R.id.ButtonFemale);


        TextView passwordErrorTextView = findViewById(R.id.passwordError);
        TextView usernameErrorTextView = findViewById(R.id.usernameError);

        // Set click listener for the login button
        Button buttonLogin = findViewById(R.id.Login4);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate back to the MainActivity when login button is clicked
                Intent intent = new Intent(registration_page.this, MainActivity.class);
                startActivity(intent);
            }
        });



        // Set click listener for the registration button
        Button buttonRegister = findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Store the user data in the Firebase Realtime Database
                database = FirebaseDatabase.getInstance();
                reference = database.getReference("users");
                // Get the username, password, name, and mobile number from the edit texts
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String name = Name1.getText().toString();
                String number = MobileNum1.getText().toString();

                // Determine the selected radio button for gender
                boolean isMale = radioButtonMale.isChecked();
                boolean isFemale = radioButtonFemale.isChecked();

                // Perform registration or other actions using username, password, name, number, and gender
                // For example:
                // - Send registration request to a server
                // - Store user data in a local database or Firebase

                // Assuming you have a Firebase Realtime Database structure like "users" -> "username" -> HelperClass
                // Replace "HelperClass" with your actual data class used to store user information
                // You can set the gender field of HelperClass based on the selected radio button
                HelperClass helperClass = new HelperClass(name, number, username, password, isMale);


                reference.child(username).setValue(helperClass);

                // Other validation and toast messages
                usernameErrorTextView.setVisibility(View.GONE);
                passwordErrorTextView.setVisibility(View.GONE);

                // Check if username contains '@' symbol
                if (!username.contains("@")) {
                    usernameErrorTextView.setVisibility(View.VISIBLE);
                    usernameErrorTextView.setText("Please enter a valid username");
                    return;
                }

                // Check if password is at least 8 characters long
                if (password.length() < 8) {
                    passwordErrorTextView.setVisibility(View.VISIBLE);
                    passwordErrorTextView.setText("Password must be at least 8 characters long");
                    return;
                }
                if (number.length() < 10) {
                    passwordErrorTextView.setVisibility(View.VISIBLE);
                    passwordErrorTextView.setText("Enter a valid Mobile Number");
                    return;
                }

                // Input validation (consider more rigorous checks based on your requirements)
                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(registration_page.this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(registration_page.this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (TextUtils.isEmpty(name)) {
                    Toast.makeText(registration_page.this, "name cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (TextUtils.isEmpty(number)) {
                    Toast.makeText(registration_page.this, "Mobile Number cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Perform registration or other actions using username and password (replace with your logic)
                // For example:
                // - Send registration request to a server
                // - Store credentials in a local database

                Toast.makeText(registration_page.this, "Registration successful!", Toast.LENGTH_SHORT).show();

                // Replace this placeholder with your actual registration logic
                // and handle successful/failed registration scenarios appropriately
            }
        });
    }
}
