package com.example.musiclearning;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.Serializable;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ItemClickListener {
    DBHelperWithLoader DBHelper;
    MyRoomDB myRoomDB;
    private List<SongAndNote> songs;

    private SongViewModel songViewModel;
    private RecyclerView songList;
    Button addSongButton, allNotesButton;

    private static final int PERMISSION_STORAGE = 101;
    public static final int ADD_SONG_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DBHelper = new DBHelperWithLoader(this);
        myRoomDB = MyRoomDB.create(this, false);

        songList = findViewById(R.id.songList);
        songList.setLayoutManager(new LinearLayoutManager(this));
        addSongButton = findViewById(R.id.addSongButton);
        allNotesButton = findViewById(R.id.notesButton);

        SongListAdapter adapter = new SongListAdapter(this, this);
        songList.setAdapter(adapter);

        songViewModel = new ViewModelProvider(this).get(SongViewModel.class);
        songViewModel.getSongsWithNote().observe(this, new Observer<List<SongAndNote>>() {
            @Override
            public void onChanged(List<SongAndNote> _songAndNotes) {
                songs = _songAndNotes;
                adapter.setSongs(songs);
            }
        });

        addSongButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (PermissionUtils.hasPermissions(MainActivity.this)) {
                    addSongFile();
                }
                else {
                    PermissionUtils.requestPermissions(MainActivity.this, PERMISSION_STORAGE);
                }
            }
        });

        allNotesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NotesActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onItemClick(SongAndNote item) {
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra("index", songs.indexOf(item));
        intent.putExtra("song", (Serializable) item.song);
//        intent.putExtra("songs", (Serializable) songs);
        startActivity(intent);
    }

    @Override
    public void onItemClick(Note item) {

    }

    private void addSongFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        String[] mimetypes = {"audio/mpeg", "audio/x-wav"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);

        startActivityForResult(intent, ADD_SONG_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent resultData) {
        if (requestCode == PERMISSION_STORAGE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (PermissionUtils.hasPermissions(this)) {
                    Log.d("mytag", "Разрешение получено");
                    addSongFile();
                } else {
                    Log.d("mytag", "Разрешение не предоставлено");
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == ADD_SONG_REQUEST && resultCode == Activity.RESULT_OK) {
            Uri uri = null;

            if(resultData != null) {
                uri = resultData.getData();
                String properPath = getProperPath(uri.getPath());
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                Log.d("mytag", properPath);
                retriever.setDataSource(properPath);
                String songTitle = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                String songArtist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                if (songTitle == null) {
                    songTitle = "Без названия";
                }
                if (songArtist == null) {
                    songArtist = "Исполнитель неизвестен";
                }
                Log.d("mytag", "song title: " + songTitle);
                Log.d("mytag", "song artist: " + songArtist);
                Song song = new Song(songArtist, songTitle, "", "", null, properPath);
                songViewModel.insert(song);
            }
        }
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                     @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("mytag", "Разрешение получено");
            } else {
                Log.d("mytag", "Разрешение не предоставлено");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private String getProperPath(String path) {
        String res = "storage/";
        int colon = path.indexOf(":");
        String beforeColon = path.substring(0, colon);
        String afterColon = path.substring(colon + 1);
        int slash = beforeColon.indexOf("/");
        String afterFirstSlash = beforeColon.substring(slash + 1);
        int slash2 = afterFirstSlash.indexOf("/");
        String storage = afterFirstSlash.substring(slash2 + 1);
        if (storage.equals("primary")) {
            res += "emulated/0/";
        }
        else {
            res += storage + "/";
        }
        res += afterColon;
        return res;
    }
}