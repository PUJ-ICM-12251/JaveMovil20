package com.example.javemovil20;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btnentrar;
    Button btnregistrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnentrar = (Button)findViewById(R.id.btnentrar);
        btnregistrar = (Button)findViewById(R.id.btnregistrar);

        btnentrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent miIntent = new Intent(MainActivity.this, MenuActivity.class);
                startActivity(miIntent);
            }
        });

        btnregistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent miIntent = new Intent(MainActivity.this, NewUserActivity.class);
                startActivity(miIntent);
            }
        });

    }
}