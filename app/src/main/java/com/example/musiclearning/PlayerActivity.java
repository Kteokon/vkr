package com.example.musiclearning;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PlayerActivity extends AppCompatActivity {
    public static final int UPDATE_SONG_REQUEST = 1;
    public static final int ADD_NOTE_REQUEST = 2;

    FloatingActionButton playButton, shuffleButton, loopButton;
    SeekBar seekBar;
    TextView timePassedTV, timeOverTV, songTV, artistTV;
    Button homeButton, updateButton, notesButton;

    MediaPlayer mediaPlayer;
    List<SongAndNote> songs;
    Song song;
//    SongViewModel songViewModel;
    List<Integer> music;
    boolean wasPlaying;
    int nowPlaying, isLooping, isRandom; // isLooping может иметь 3 состояние (0 - нет повтора, 1 - повтор одной песни, 2 - повтор всего плейлиста)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        Intent intent = getIntent();
        SongViewModel songViewModel = new ViewModelProvider(this).get(SongViewModel.class);
        songViewModel.getSongsWithNote().observe(this, new Observer<List<SongAndNote>>() {
            @Override
            public void onChanged(List<SongAndNote> songAndNotes) {
                songs = songAndNotes;
            }
        });
        nowPlaying = intent.getIntExtra("index", 0);
        song = (Song) intent.getSerializableExtra("song");

//        songViewModel = new ViewModelProvider(this).get(SongViewModel.class);

//        songViewModel.getSongs().observe(this, new Observer<List<Song>>() {
//            @Override
//            public void onChanged(List<Song> _songs) {
//                songs = _songs;
//            }
//        });

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

//        try {
//            checkButtons();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        songTV.setText(song.getSong());
        artistTV.setText(song.getArtist());

        if (song.getSource().equals("base")) {
            updateButton.setVisibility(View.GONE);
            mediaPlayer = MediaPlayer.create(this, music.get(song.getId() - 1));
        }
        else {
            updateButton.setVisibility(View.VISIBLE);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mediaPlayer.setDataSource(this, Uri.parse(song.getSource()));
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
    }

    public void skipSong(View v) throws IOException, ExecutionException, InterruptedException {
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

    public void addUpdateNotes(View v) {
        Intent intent = new Intent(this, NoteActivity.class);
        Note note = songs.get(nowPlaying).note;

        intent.putExtra("note", (Serializable) note); // может быть null
        intent.putExtra("song", (Serializable) song); // всегда есть

        if (note == null) {
            startActivityForResult(intent, ADD_NOTE_REQUEST);
        }
        else {
            Log.d("newNote", "song: " + nowPlaying + " " + song.getId() + " " + song.getSong());
            Log.d("newNote", "note: " + note.getId() + " " + note.getSongNotes());
            startActivity(intent);
        }
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

            Log.d("mytag", "Song " + id + " " + song.getId() + "\n" +
                    songArtist + " " + song.getArtist() + "\n" +
                    songName + " " + song.getSong() + "\n" +
                    songLyrics + " " + song.getLyrics() + "\n" +
                    songTranslation + " " + song.getTranslation() + "\n" +
                    songNotes + " " + song.getNotes() + "\n" +
                    songSource + " " + song.getSource());

            song.setId(id);
            song.setArtist(songArtist);
            song.setSong(songName);
            song.setLyrics(songLyrics);
            song.setTranslation(songTranslation);
            song.setNotes(Integer.getInteger(songNotes));
            song.setSource(songSource);

            String button = data.getStringExtra("button");
            SongViewModel songViewModel = new ViewModelProvider(this).get(SongViewModel.class);
            if (button.equals("delete")) {
                songViewModel.delete(song);

                Log.d("mytag", "Deleted");

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
            else{
                songViewModel.update(song);

                songTV.setText(song.getSong());
                artistTV.setText(song.getArtist());

                Log.d("mytag", "Updated");
            }
        }

        if (requestCode == ADD_NOTE_REQUEST && resultCode == RESULT_OK) {
            Integer noteId = Integer.valueOf(data.getIntExtra(NoteActivity.NOTE_ID, -1));
            String songNote = data.getStringExtra(NoteActivity.SONG_NOTE);
            Song _song = songs.get(nowPlaying).song;

            Log.d("newNote", "Song: " + song.getId() + " " + song.getSong() + "\nNote: " + noteId + " " + songNote);
            Log.d("newNote", "_song: " + _song.getId() + " " + _song.getSong());

            Note note = new Note(songNote);
            note.setId(noteId);
            NoteViewModel noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
            noteViewModel.insert(note);
            song.setNotes(noteId);
            SongViewModel songViewModel = new ViewModelProvider(this).get(SongViewModel.class);
            songViewModel.update(song);
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
        song = songs.get(nowPlaying).song;
        songTV.setText(song.getSong());
        artistTV.setText(song.getArtist());

        if (song.getSource().equals("base")) {
            updateButton.setVisibility(View.GONE);
            mediaPlayer = MediaPlayer.create(this, music.get(song.getId() - 1));
        }
        else {
            updateButton.setVisibility(View.VISIBLE);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(this, Uri.parse(song.getSource()));
            mediaPlayer.prepare();
        }
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

    class ChangeSongTask extends AsyncTask<Integer, Void, Note> {

        @Override
        protected Note doInBackground(Integer... integers) {
            Integer noteId = integers[0];
            return null;
        }
    }
}
