package com.example.javemovil20;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class NewsletterActivity extends AppCompatActivity {

    VideoView v1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsletter);
        v1=findViewById(R.id.v1);
        Uri url = Uri.parse("https://www.youtube.com/watch?v=xV8jjDRgSyM&pp=ygUVamF2ZXJpYW5hIHNvbW9zIHRvZG9z");
        v1.setMediaController(new MediaController(this));
        //v1.setVideoURI(url);
        v1.setVideoURI(Uri.parse("android.resource//"+getPackageName()+"/"+R.raw.video1));
        v1.requestFocus();
        v1.start();

    }
}