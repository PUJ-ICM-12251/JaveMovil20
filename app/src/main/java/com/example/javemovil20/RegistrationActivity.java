package com.example.javemovil20;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.javemovil20.databinding.ActivityLoginBinding;
import com.example.javemovil20.databinding.ActivityRegistrationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegistrationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ActivityRegistrationBinding binding;
    private EditText emailEditText;
    private EditText passEditText;
    private EditText passEditText2;
    private Button buttonSignIn;
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
        buttonSignIn = binding.registerButton;

        mAuth = FirebaseAuth.getInstance();

        logInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), LoginActivity.class));
            }
        });

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInUser(emailEditText.getText().toString(), passEditText.getText().toString());
            }
        });

    }

    private boolean validateForm() {
        boolean valid = true;
        String email = emailEditText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Required");
            valid = false;
        } else {
            emailEditText.setError(null);
        }
        String password = passEditText.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passEditText.setError("Required");
            valid = false;
        } else {
            passEditText.setError(null);
        }

        String password2 = passEditText2.getText().toString();
        if (TextUtils.isEmpty(password2)) {
            passEditText2.setError("Required");
            valid = false;
        } else {
            passEditText2.setError(null);
        }

        if(!password.equals(password2)){
            passEditText.setError("Las contraseñas no coinciden");
            passEditText2.setError("Las contraseñas no coinciden");
            valid = false;
        }else {
            passEditText.setError(null);
            passEditText2.setError(null);
        }
        return valid;
    }

    private void signInUser(String email, String password) {
        if(validateForm()){
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        FirebaseUser user = mAuth.getCurrentUser();
                        if(user!=null) {
                            // Update user Info
                            /*UserProfileChangeRequest.Builder upcrb = new UserProfileChangeRequest.Builder();
                            upcrb.setDisplayName(mUserName.getText().toString()+" "+mUserLastName.getText().toString());
                            upcrb.setDisplayName(mUserName.getText().toString()+" "+mUserLastName.getText().toString());
                            upcrb.setPhotoUri(Uri.parse("path/to/pic")); //fake uri, use Firebase Storage
                            user.updateProfile(upcrb.build());*/
                            updateUI(user);
                        }
                    }
                    if (!task.isSuccessful()) {
                        Toast.makeText(RegistrationActivity.this, R.string.auth_failed+ task.getException().toString(),
                                Toast.LENGTH_SHORT).show();
                        Log.e(TAG, task.getException().getMessage());
                    }
                }
            });
        }

    }

    private void updateUI(FirebaseUser currentUser) {
        if(currentUser != null) {
            Intent intent = new Intent(getBaseContext(),
                    LoginActivity.class);
            intent.putExtra("user", currentUser.getEmail());
            startActivity(intent);
        } else {
            emailEditText.setText("");
            passEditText.setText("");
        }
    }

}