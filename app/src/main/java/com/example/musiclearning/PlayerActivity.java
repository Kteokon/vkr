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

import com.google.android.exoplayer2.DeviceInfo;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.MediaMetadata;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.Tracks;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.CueGroup;
import com.google.android.exoplayer2.trackselection.TrackSelectionParameters;
import com.google.android.exoplayer2.video.VideoSize;
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

    ExoPlayer exoPlayer;
    List<SongAndNote> songs;
    List<Integer> music;
    int nowPlaying;

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
                List<MediaItem> items = new ArrayList<>();
                for (int i = 0; i < songs.size(); i++) {
                    Uri uri;
                    if (songs.get(i).song.getSource().equals("base")) {
                        updateButton.setVisibility(View.GONE);
                        uri = Uri.parse("android.resource://" + getPackageName() + "/" + music.get(songs.get(i).song.getId() - 1));
                    }
                    else {
                        updateButton.setVisibility(View.VISIBLE);
                        uri = Uri.parse(songs.get(i).song.getSource());
                    }
                    MediaItem item = MediaItem.fromUri(uri);
                    items.add(item);
                }
                exoPlayer.setMediaItems(items, nowPlaying, 0);
                exoPlayer.prepare();
            }
        });

        nowPlaying = intent.getIntExtra("index", 0);

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

        music = getRawIds();

        exoPlayer = new ExoPlayer.Builder(getApplicationContext()).build();

        exoPlayer.addListener(new ExoPlayer.Listener() {

            @Override
            public void onTracksChanged(Tracks tracks) {
                Player.Listener.super.onTracksChanged(tracks);

                Log.d("mytag", "On track changed");
            }

            @Override
            public void onEvents(Player player, Player.Events events) {
                Player.Listener.super.onEvents(player, events);
                Log.d("mytag", "On events");
            }

            @Override
            public void onTimelineChanged(Timeline timeline, int reason) {
                Player.Listener.super.onTimelineChanged(timeline, reason);

                Log.d("mytag", "On timeline changed");
            }

            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                Player.Listener.super.onMediaItemTransition(mediaItem, reason);

                Log.d("mytag", "On media item transition");
                Song song = songs.get(nowPlaying).song;
                songTV.setText(song.getSong());
                artistTV.setText(song.getArtist());
                if (song.getSource().equals("base")) {
                    updateButton.setVisibility(View.GONE);
                }
                else {
                    updateButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onMediaMetadataChanged(MediaMetadata mediaMetadata) {
                Player.Listener.super.onMediaMetadataChanged(mediaMetadata);

                Log.d("mytag", "On media metadata changed");
            }

            @Override
            public void onPlaylistMetadataChanged(MediaMetadata mediaMetadata) {
                Player.Listener.super.onPlaylistMetadataChanged(mediaMetadata);

                Log.d("mytag", "On playlist metadata changed");
            }

            @Override
            public void onIsLoadingChanged(boolean isLoading) {
                Player.Listener.super.onIsLoadingChanged(isLoading);

                Log.d("mytag", "On is loading changed");
            }

            @Override
            public void onAvailableCommandsChanged(Player.Commands availableCommands) {
                Player.Listener.super.onAvailableCommandsChanged(availableCommands);

                Log.d("mytag", "On available commands changed");
            }

            @Override
            public void onTrackSelectionParametersChanged(TrackSelectionParameters parameters) {
                Player.Listener.super.onTrackSelectionParametersChanged(parameters);

                Log.d("mytag", "On track selection parameters changed");
            }

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                Player.Listener.super.onPlaybackStateChanged(playbackState);

                if (playbackState == ExoPlayer.STATE_READY) {
                    Log.d("mytag", "State ready");
                    timeOverTV.setText(getSongDuration((int) exoPlayer.getDuration()));
                    seekBar.setMax((int) (exoPlayer.getDuration() / 1000));
                }
                if (playbackState == ExoPlayer.STATE_IDLE) {
                    Log.d("mytag", "State idle");
                }
                if (playbackState == ExoPlayer.STATE_BUFFERING) {
                    Log.d("mytag", "State buffering");
                }
                if (playbackState == ExoPlayer.STATE_ENDED) {
                    Log.d("mytag", "State ended");
                }
            }

            @Override
            public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {
                Player.Listener.super.onPlayWhenReadyChanged(playWhenReady, reason);

                Log.d("mytag", "On play when ready changed");
            }

            @Override
            public void onPlaybackSuppressionReasonChanged(int playbackSuppressionReason) {
                Player.Listener.super.onPlaybackSuppressionReasonChanged(playbackSuppressionReason);

                Log.d("mytag", "On playback suppression reason changed");
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                Player.Listener.super.onIsPlayingChanged(isPlaying);

                Log.d("mytag", "On is playing changed");
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {
                Player.Listener.super.onRepeatModeChanged(repeatMode);

                Log.d("mytag", "On repeat mode changed");
            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
                Player.Listener.super.onShuffleModeEnabledChanged(shuffleModeEnabled);

                Log.d("mytag", "On shuffle mode enabled changed");
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                Player.Listener.super.onPlayerError(error);

                Log.d("mytag", "On player error");
            }

            @Override
            public void onPlayerErrorChanged(@Nullable PlaybackException error) {
                Player.Listener.super.onPlayerErrorChanged(error);

                Log.d("mytag", "On player error changed");
            }

            @Override
            public void onPositionDiscontinuity(Player.PositionInfo oldPosition, Player.PositionInfo newPosition, int reason) {
                Player.Listener.super.onPositionDiscontinuity(oldPosition, newPosition, reason);

                Log.d("mytag", "On position discontinuity old position new position");
            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
                Player.Listener.super.onPlaybackParametersChanged(playbackParameters);

                Log.d("mytag", "On playback parameters changed");
            }

            @Override
            public void onSeekBackIncrementChanged(long seekBackIncrementMs) {
                Player.Listener.super.onSeekBackIncrementChanged(seekBackIncrementMs);

                Log.d("mytag", "On seek back increment changed");
            }

            @Override
            public void onSeekForwardIncrementChanged(long seekForwardIncrementMs) {
                Player.Listener.super.onSeekForwardIncrementChanged(seekForwardIncrementMs);

                Log.d("mytag", "On seek forward increment changed");
            }

            @Override
            public void onMaxSeekToPreviousPositionChanged(long maxSeekToPreviousPositionMs) {
                Player.Listener.super.onMaxSeekToPreviousPositionChanged(maxSeekToPreviousPositionMs);

                Log.d("mytag", "On max seek to previous position changed");
            }

            @Override
            public void onAudioSessionIdChanged(int audioSessionId) {
                Player.Listener.super.onAudioSessionIdChanged(audioSessionId);

                Log.d("mytag", "On audio session id changed");
            }

            @Override
            public void onAudioAttributesChanged(AudioAttributes audioAttributes) {
                Player.Listener.super.onAudioAttributesChanged(audioAttributes);

                Log.d("mytag", "On audio attributes changed");
            }

            @Override
            public void onVolumeChanged(float volume) {
                Player.Listener.super.onVolumeChanged(volume);

                Log.d("mytag", "On volume changed");
            }

            @Override
            public void onSkipSilenceEnabledChanged(boolean skipSilenceEnabled) {
                Player.Listener.super.onSkipSilenceEnabledChanged(skipSilenceEnabled);

                Log.d("mytag", "On skip silence enabled changed");
            }

            @Override
            public void onDeviceInfoChanged(DeviceInfo deviceInfo) {
                Player.Listener.super.onDeviceInfoChanged(deviceInfo);

                Log.d("mytag", "On device info changed");
            }

            @Override
            public void onDeviceVolumeChanged(int volume, boolean muted) {
                Player.Listener.super.onDeviceVolumeChanged(volume, muted);

                Log.d("mytag", "On device volume changed");
            }

            @Override
            public void onVideoSizeChanged(VideoSize videoSize) {
                Player.Listener.super.onVideoSizeChanged(videoSize);

                Log.d("mytag", "On video size changed");
            }

            @Override
            public void onSurfaceSizeChanged(int width, int height) {
                Player.Listener.super.onSurfaceSizeChanged(width, height);

                Log.d("mytag", "On surface size changed");
            }

            @Override
            public void onRenderedFirstFrame() {
                Player.Listener.super.onRenderedFirstFrame();

                Log.d("mytag", "On rendered first frame");
            }

            @Override
            public void onCues(CueGroup cueGroup) {
                Player.Listener.super.onCues(cueGroup);

                Log.d("mytag", "On cues cue group");
            }

            @Override
            public void onMetadata(Metadata metadata) {
                Player.Listener.super.onMetadata(metadata);

                Log.d("mytag", "On metadata");
            }
        });

        Handler handler = new Handler();
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(exoPlayer != null){
                    int currentPosition = (int) exoPlayer.getCurrentPosition();
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
                if(exoPlayer != null && fromUser){
                    exoPlayer.seekTo(progress * 1000);
                    timePassedTV.setText(getSongDuration(progress * 1000));
                }
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (exoPlayer.isPlaying()) {
                    exoPlayer.pause();
                    playButton.setImageDrawable(ContextCompat.getDrawable(getApplication(), R.drawable.play_button));
                }
                else {
                    exoPlayer.play();
                    playButton.setImageDrawable(ContextCompat.getDrawable(getApplication(), R.drawable.pause_button));
                }
            }
        });

        loopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (exoPlayer.getRepeatMode() == Player.REPEAT_MODE_OFF) {
                    exoPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);
                }
                else {
                    if (exoPlayer.getRepeatMode() == Player.REPEAT_MODE_ONE) {
                        exoPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);
                    }
                    else {
                        exoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);
                    }
                }
            }
        });

        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int r = (int)(Math.random() * songs.size());
                nowPlaying = r;
                exoPlayer.seekTo(r, 0);
                exoPlayer.prepare();
                exoPlayer.play();
            }
        });
    }

    public void skipSong(View v){
        switch (v.getId()) {
            case R.id.skipLeftButton: {
                Log.d("mytag", "Previous song");
                if (nowPlaying > 0) {
                    nowPlaying--;
                    exoPlayer.seekTo(nowPlaying, 0);
                }
                break;
            }
            case R.id.skipRightButton: {
                Log.d("mytag", "Next song");
                if (nowPlaying < songs.size() - 1) {
                    nowPlaying++;
                    exoPlayer.seekTo(nowPlaying, 0);
                }
                break;
            }
        }
    }

    public void updateSong(View v) {
        Intent intent = new Intent(this, UpdateActivity.class);

        Song song = songs.get(nowPlaying).song;

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
        Song song = songs.get(nowPlaying).song;

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

            Song song = new Song(songArtist, songName, songLyrics, songTranslation, Integer.getInteger(songNotes), songSource);
            song.setId(id);

            String button = data.getStringExtra("button");
            SongViewModel songViewModel = new ViewModelProvider(this).get(SongViewModel.class);
            if (button.equals("delete")) {
                songViewModel.delete(song);

                Log.d("mytag", "Deleted");
                finish();
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
            Song song = songs.get(nowPlaying).song;

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
