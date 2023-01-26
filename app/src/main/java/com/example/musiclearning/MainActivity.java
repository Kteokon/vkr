package com.example.musiclearning;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {
    ListView lv;
    String[] songs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //lv = findViewById(R.id.songList);

    }

    public void start(View v){
        Intent i = new Intent(this, PlayerActivity.class);
        startActivity(i);
    }
}