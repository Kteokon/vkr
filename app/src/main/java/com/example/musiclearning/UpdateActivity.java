package com.example.musiclearning;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateActivity extends AppCompatActivity {
    public static final String SONG_ID = "com.example.musiclearning.SONG_ID";
    public static final String SONG_NAME = "com.example.musiclearning.SONG_NAME";
    public static final String SONG_ARTIST = "com.example.musiclearning.SONG_ARTIST";
    public static final String SONG_LYRICS = "com.example.musiclearning.SONG_LYRICS";
    public static final String SONG_TRANSLATION = "com.example.musiclearning.SONG_TRANSLATION";
    public static final String SONG_NOTES = "com.example.musiclearning.SONG_NOTES";
    public static final String SONG_SOURCE = "com.example.musiclearning.SONG_SOURCE";
    public static final int ADD_SONG_LYRICS = 3;

    EditText songNameET, songArtistET;
    String songLyrics;
    String songTranslation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        songNameET = findViewById(R.id.songName);
        songArtistET = findViewById(R.id.songArtist);

        Intent intent = getIntent();
        songNameET.setText(intent.getStringExtra(SONG_NAME));
        songArtistET.setText(intent.getStringExtra(SONG_ARTIST));
        songLyrics = intent.getStringExtra(SONG_LYRICS);
        songTranslation = intent.getStringExtra(SONG_TRANSLATION);
    }

    public void saveChanges(View v) {
        Intent intent = new Intent();

        int id = getIntent().getIntExtra(SONG_ID, -1);
        String newName = songNameET.getText().toString();
        String newArtist = songArtistET.getText().toString();
        String newLyrics = songLyrics;
        String newTranslation = songTranslation;
        String newNotes = getIntent().getStringExtra(SONG_NOTES);
        String newSource = getIntent().getStringExtra(SONG_SOURCE);

        Log.d("mytag", "id: " + id);

        intent.putExtra("button", "save");
        intent.putExtra(UpdateActivity.SONG_ID, id);
        intent.putExtra(UpdateActivity.SONG_NAME, newName);
        intent.putExtra(UpdateActivity.SONG_ARTIST, newArtist);
        intent.putExtra(UpdateActivity.SONG_LYRICS, newLyrics);
        intent.putExtra(UpdateActivity.SONG_TRANSLATION, newTranslation);
        intent.putExtra(UpdateActivity.SONG_NOTES, newNotes);
        intent.putExtra(UpdateActivity.SONG_SOURCE, newSource);

        if (v.getId() == R.id.deleteButton) {
            intent.putExtra("button", "delete");
        }
        else {
            intent.putExtra("button", "save");
        }

        Log.d("mylyrics", newLyrics);
        Log.d("mytranslation", newTranslation);

        setResult(RESULT_OK, intent);
        finish();
    }

    public void cancelChanges(View v) {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    public void addSongLyrics(View v) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        String[] mimetypes = {"text/plain", "application/lrc"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);

        startActivityForResult(intent, ADD_SONG_LYRICS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == ADD_SONG_LYRICS && resultCode == Activity.RESULT_OK) {
            Uri uri = null;

            if(resultData != null) {
                uri = resultData.getData();
                File f = new File(uri.getPath());
                try {
                    InputStream in = getContentResolver().openInputStream(uri);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder total = new StringBuilder();
                    for (String line; (line = reader.readLine()) != null; ) {
                        total.append(line).append('\n');
                    }
                    songLyrics = total.toString();
                } catch (Exception e) {
                    Log.d("mytag", "No file");
                }

                if (!(songLyrics.equals(null) || songLyrics.equals("") || songLyrics.equals("\n"))) {
                    TranslatorTask task = new TranslatorTask();
                    task.execute("hello");
                }
            }
        }
    }

    class TranslatorTask extends AsyncTask<String, Void, String> {
        String theKey = getString(R.string.theKey);
        String folderId = getString(R.string.folderId);
        String targetLanguageCode = getString(R.string.targetLanguageCode);
        String set_server_url = getString(R.string.yandex_translator_server_url);

        @Override
        protected String doInBackground(String... strings) {
            TranslatorResponse response = null;
            String res = "";
            try {
                String textToTranslate = strings[0];
                URL url = new URL(set_server_url);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Authorization", "Api-Key " + theKey);
                urlConnection.setDoOutput(true);

                OutputStream stream = urlConnection.getOutputStream();
                String postData = "{\n" + "\"folderId\": \"" + folderId + "\",\r\n" +
                        "\"texts\": ['" + textToTranslate + "'],\r\n" +
                        "\"targetLanguageCode\": \"" + targetLanguageCode + "\"}";
                stream.write(postData.getBytes());

                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);

                Gson gson = new Gson();
                response = gson.fromJson(reader, TranslatorResponse.class);
                urlConnection.disconnect();
                Translation[] texts = response.translations;
                for (int i = 0; i < texts.length; i++) {
                    res += texts[i].text + " ";
                }
            } catch (IOException e) {
                Log.d("mytag", "error: " + e.getLocalizedMessage());
            }
            return res;
        }

        @Override
        protected void onPostExecute(String res){
            super.onPostExecute(res);
            songTranslation = res;
        }
    }
}