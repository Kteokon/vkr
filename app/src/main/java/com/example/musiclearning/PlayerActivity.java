package com.example.musiclearning;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class PlayerActivity extends AppCompatActivity {
    public static final int UPDATE_SONG_REQUEST = 2;

    FloatingActionButton playButton, shuffleButton, loopButton;
    SeekBar seekBar;
    TextView timePassedTV, timeOverTV, songTV, artistTV;
    Button homeButton, updateButton, notesButton;

    MediaPlayer mediaPlayer;
    List<Song> songs;
    Song song;
    List<Integer> music;
    boolean wasPlaying;
    int nowPlaying, isLooping, isRandom; // isLooping может иметь 3 состояние (0 - нет повтора, 1 - повтор одной песни, 2 - повтор всего плейлиста)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        Intent intent = getIntent();
        songs = (List<Song>) intent.getSerializableExtra("songs");
        nowPlaying = intent.getIntExtra("index", 0);
        song = songs.get(nowPlaying);

        Log.d("mytag", Integer.toString(nowPlaying) + " " + song.getSong());

        playButton = findViewById(R.id.playButton);
        shuffleButton = findViewById(R.id.shuffleButton);
        loopButton = findViewById(R.id.loopButton);
        seekBar = findViewById(R.id.seekBar);
        timePassedTV = findViewById(R.id.timePassed);
        timeOverTV = findViewById(R.id.timeOver);
        songTV = findViewById(R.id.songName);
        artistTV = findViewById(R.id.songArtist);
        homeButton = findViewById(R.id.homeButton);
        updateButton = findViewById(R.id.updateButton);
        notesButton = findViewById(R.id.notesButton);

        isLooping = 0;
        isRandom = 0;
        wasPlaying = false;
        music = getRawIds();

        try {
            checkButtons();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Handler handler = new Handler();
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer != null){
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(currentPosition / 1000);
                    timePassedTV.setText(getSongDuration(currentPosition));
                }
                handler.postDelayed(this, 1000);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer != null && fromUser){
                    mediaPlayer.seekTo(progress * 1000);
                    timePassedTV.setText(getSongDuration(progress * 1000));
                }
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.d("mytag", "Song is finished");
                mediaPlayer.pause();
                playButton.setImageDrawable(ContextCompat.getDrawable(getApplication(), R.drawable.play_button));
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    Log.d("mytag", "Pause song");
                    mediaPlayer.pause();
                    wasPlaying = false;
                    playButton.setImageDrawable(ContextCompat.getDrawable(getApplication(), R.drawable.play_button));
                }
                else {
                    Log.d("mytag", "Play song");
                    wasPlaying = true;
                    mediaPlayer.start();
                    playButton.setImageDrawable(ContextCompat.getDrawable(getApplication(), R.drawable.pause_button));
                }
            }
        });

        loopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isLooping()) {
                    Log.d("mytag", "Song isn't looped");
                    isLooping = 0;
                    mediaPlayer.setLooping(false);
                }
                else {
                    Log.d("mytag", "Song is looped");
                    isLooping = 1;
                    mediaPlayer.setLooping(true);
                }
            }
        });

        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int r = (int)(Math.random() * songs.size());
                nowPlaying = r;
                Log.d("mytag", "Random song " + Integer.toString(nowPlaying));
                try {
                    checkButtons();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

//        int resId = getResources().getIdentifier(sId, "raw", getPackageName());
//        mediaPlayer = MediaPlayer.create(this, resId);
    }

    public void skipSong(View v) throws IOException {
        switch (v.getId()) {
            case R.id.skipLeftButton: {
                Log.d("mytag", "Previous song");
                if (nowPlaying == 0) {
                    nowPlaying = songs.size();
                }
                nowPlaying--;
                break;
            }
            case R.id.skipRightButton: {
                Log.d("mytag", "Next song");
                nowPlaying++;
                if (nowPlaying == songs.size()) {
                    nowPlaying = 0;
                }
                break;
            }
        }
        checkButtons();
    }

    public void updateSong(View v) {
        Intent intent = new Intent(this, UpdateActivity.class);

        intent.putExtra(UpdateActivity.SONG_ID, song.getId());
        intent.putExtra(UpdateActivity.SONG_NAME, song.getSong());
        intent.putExtra(UpdateActivity.SONG_ARTIST, song.getArtist());
        intent.putExtra(UpdateActivity.SONG_LYRICS, song.getLyrics());
        intent.putExtra(UpdateActivity.SONG_TRANSLATION, song.getTranslation());
        intent.putExtra(UpdateActivity.SONG_NOTES, song.getNotes());
        intent.putExtra(UpdateActivity.SONG_SOURCE, song.getSource());

        startActivityForResult(intent, UPDATE_SONG_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UPDATE_SONG_REQUEST && resultCode == RESULT_OK) {
            int id = data.getIntExtra(UpdateActivity.SONG_ID, -1);

            if (id == -1) {
                Log.d("mytag", "No updates, id = -1");
                return;
            }

            String songName = data.getStringExtra(UpdateActivity.SONG_NAME);
            String songArtist = data.getStringExtra(UpdateActivity.SONG_ARTIST);
            String songLyrics = data.getStringExtra(UpdateActivity.SONG_LYRICS);
            String songTranslation = data.getStringExtra(UpdateActivity.SONG_TRANSLATION);
            String songNotes = data.getStringExtra(UpdateActivity.SONG_NOTES);
            String songSource = data.getStringExtra(UpdateActivity.SONG_SOURCE);

            Log.d("mytag", "Song " + id + " " + songName + " " + songArtist);

            Song song = new Song(songArtist, songName, songLyrics, songTranslation, songNotes, songSource);
            song.setId(id);
            String button = data.getStringExtra("button");
            SongViewModel songViewModel = new ViewModelProvider(this).get(SongViewModel.class);
            if (button.equals("delete")) {
                songViewModel.delete(song);

                Log.d("mytag", "Deleted");
            }
            else{
                songViewModel.update(song);

                Log.d("mytag", "Updated");
            }
        }
    }

    private List<Integer> getRawIds() {
        List<Integer> rawIds = new ArrayList<>();
        Field[] fields = R.raw.class.getFields();
        for(int i = 0; i < fields.length; i++){
            int resId = getResources().getIdentifier(fields[i].getName(), "raw", getPackageName());
            rawIds.add(resId);
        }
        return rawIds;
    }

    private void checkButtons() throws IOException {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
        }
        song = songs.get(nowPlaying);
        if (song.getSource().equals("base")) {
            mediaPlayer = MediaPlayer.create(this, music.get(song.getId() - 1));
        }
        else {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(this, Uri.parse(song.getSource()));
            mediaPlayer.prepare();
        }

        songTV.setText(song.getSong());
        artistTV.setText(song.getArtist());

        seekBar.setMax(mediaPlayer.getDuration() / 1000);
        if (wasPlaying){
            mediaPlayer.start();
            playButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pause_button));
        }
        if (isLooping == 1) {
            mediaPlayer.setLooping(true);
        }

        timePassedTV.setText("00:00");
        timeOverTV.setText(getSongDuration(mediaPlayer.getDuration()));
        Log.d("mytag", "Song duration: " + getSongDuration(mediaPlayer.getDuration()));
    }

    private String getSongDuration(int dur) {
        int songMin = dur / 1000 / 60;
        int songSec = dur / 1000 % 60;
        String res = Integer.toString(songMin) + ":";
        if (songMin / 10 == 0) {
            res = "0" + res;
        }
        if (songSec / 10 == 0) {
            res = res + "0" + Integer.toString(songSec);
        }
        else {
            res = res + Integer.toString(songSec);
        }
        return res;
    }
}
