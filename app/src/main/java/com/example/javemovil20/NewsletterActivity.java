package com.example.javemovil20;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

public class NewsletterActivity extends AppCompatActivity {

    private VideoView v1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsletter);
        v1=findViewById(R.id.v1);

        v1.setVideoURI(Uri.parse("android.resource//"+getPackageName()+"/"+R.raw.video1));
        v1.start();

    }
}