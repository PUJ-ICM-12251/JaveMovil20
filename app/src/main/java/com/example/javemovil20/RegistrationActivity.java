package com.example.javemovil20;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.javemovil20.databinding.ActivityLoginBinding;
import com.example.javemovil20.databinding.ActivityRegistrationBinding;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ActivityRegistrationBinding binding;
    private EditText emailEditText;
    private EditText passEditText;
    private EditText passEditText2;
    private Button buttonLogIn;
    private TextView logInTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Window window = getWindow();
        window.getDecorView().setBackgroundColor(Color.parseColor("#23559A"));

        emailEditText = binding.emailEditText;
        passEditText = binding.passEditText;
        passEditText2 = binding.passEditText2;
        logInTextView = binding.LogInTextView;
        buttonLogIn = binding.registerButton;

        mAuth = FirebaseAuth.getInstance();

        logInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), LoginActivity.class));
            }
        });

    }
}