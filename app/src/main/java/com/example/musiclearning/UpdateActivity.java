package com.example.musiclearning;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class UpdateActivity extends AppCompatActivity {
    public static final String SONG_ID = "com.example.musiclearning.SONG_ID";
    public static final String SONG_NAME = "com.example.musiclearning.SONG_NAME";
    public static final String SONG_ARTIST = "com.example.musiclearning.SONG_ARTIST";
    public static final String SONG_LYRICS = "com.example.musiclearning.SONG_LYRICS";
    public static final String SONG_TRANSLATION = "com.example.musiclearning.SONG_TRANSLATION";
    public static final String SONG_NOTES = "com.example.musiclearning.SONG_NOTES";
    public static final String SONG_SOURCE = "com.example.musiclearning.SONG_SOURCE";

    EditText songNameET, songArtistET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        songNameET = findViewById(R.id.songName);
        songArtistET = findViewById(R.id.songArtist);

        Intent intent = getIntent();
        songNameET.setText(intent.getStringExtra(SONG_NAME));
        songArtistET.setText(intent.getStringExtra(SONG_ARTIST));
    }

    public void saveChanges(View v) {
        Intent intent = new Intent();

        int id = getIntent().getIntExtra(SONG_ID, -1);
        String songName = songNameET.getText().toString();
        String songArtist = songArtistET.getText().toString();
        String songLyrics = getIntent().getStringExtra(SONG_LYRICS);
        String songTranslation = getIntent().getStringExtra(SONG_TRANSLATION);
        String songNotes = getIntent().getStringExtra(SONG_NOTES);
        String songSource = getIntent().getStringExtra(SONG_SOURCE);

        Log.d("mytag", "id: " + id);

        intent.putExtra("button", "save");
        intent.putExtra(UpdateActivity.SONG_ID, id);
        intent.putExtra(UpdateActivity.SONG_NAME, songName);
        intent.putExtra(UpdateActivity.SONG_ARTIST, songArtist);
        intent.putExtra(UpdateActivity.SONG_LYRICS, songLyrics);
        intent.putExtra(UpdateActivity.SONG_TRANSLATION, songTranslation);
        intent.putExtra(UpdateActivity.SONG_NOTES, songNotes);
        intent.putExtra(UpdateActivity.SONG_SOURCE, songSource);

        if (v.getId() == R.id.deleteButton) {
            intent.putExtra("button", "delete");
        }
        else {
            intent.putExtra("button", "save");
        }

        setResult(RESULT_OK, intent);
        finish();
    }

    public void cancelChanges(View v) {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    public void deleteSong(View v) {

    }
}