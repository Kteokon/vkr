package com.example.musiclearning;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ItemClickListener {
    DBHelperWithLoader DBHelper;
    SongsDB songsDB;
    private List<Song> songs;

    private SongViewModel songViewModel;
    private RecyclerView songList;
    Button buttonAddSong;

    private static final int PERMISSION_STORAGE = 101;
    private static final int ADD_PRODUCT_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*try {
            Resources res = getResources();
            InputStream inStream = res.openRawResource(R.raw._1);

            byte[] buff = new byte[inStream.available()];
            inStream.read(buff);

        } catch (Exception e) {
            e.printStackTrace();
        }*/
        DBHelper = new DBHelperWithLoader(this);
        songsDB = SongsDB.create(this, false);

        songList = findViewById(R.id.songList);
        songList.setLayoutManager(new LinearLayoutManager(this));
        buttonAddSong = findViewById(R.id.addSong);

        SongListAdapter adapter = new SongListAdapter(this, this);
        songList.setAdapter(adapter);

        songViewModel = new ViewModelProvider(this).get(SongViewModel.class);
        songViewModel.getSongs().observe(this, new Observer<List<Song>>() {
            @Override
            public void onChanged(List<Song> _songs) {
                songs = _songs;
                adapter.setSongs(songs);
                Log.d("mytag", "Records in adapter: " + adapter.getItemCount());
            }
        });

        buttonAddSong.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (PermissionUtils.hasPermissions(MainActivity.this)) {
                    addSongFile();
                }
                else {
                    PermissionUtils.requestPermissions(MainActivity.this, PERMISSION_STORAGE);
                }
            }
        });
    }

    @Override
    public void onItemClick(Song item) {
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra("song", (Parcelable) item);
        startActivity(intent);
    }

    private void addSongFile() {
        int PICK_FILE = 2;

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        String[] mimetypes = {"audio/mpeg", "audio/x-wav"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);

        startActivityForResult(intent, PICK_FILE);
    }


//    public void onAddSongClick(View v) {
//        new Thread() {
//            @Override
//            public void run() {
//                SongList songList = songsDB.songList();
//                int id = songList.selectAll().size() + 1;
//                // TODO: Вызвать функцию открытия и выбора файлов
//                //if (PermissionUtils.hasPermissions(MainActivity.this)) return;
//                //PermissionUtils.requestPermissions(MainActivity.this, PERMISSION_STORAGE);
//
//                Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
//                intent.putExtra("button", "addSong");
//                startActivity(intent);
//
//            }
//        }.start();
//    }

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
        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            Uri uri = null;

            if(resultData != null) {
                uri = resultData.getData();
                Song song = new Song("artist", "song", "", "", "", uri.toString());
                try {
                    InputStream in = getContentResolver().openInputStream(uri);

                    BufferedReader r = new BufferedReader(new InputStreamReader(in));
                    StringBuilder total = new StringBuilder();
                    for (String line; (line = r.readLine()) != null; ) {
                        total.append(line).append('\n');
                    }
                    String content = total.toString();
                    Log.d("mytag", content);
                }catch (Exception e) {

                }

//                try {
//                    File f = new File(text);
//                    Log.d("mytag", f.getName());
//                    FileInputStream is = new FileInputStream(f); // Fails on this line
//                    int size = is.available();
//                    byte[] buffer = new byte[size];
//                    is.read(buffer);
//                    is.close();
//                    text = new String(buffer);
//                    Log.d("mytag", text);
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
            }

//            MediaPlayer music = new MediaPlayer();
//            if (resultData != null) {
//                uri = resultData.getData();
//                try {
//                    if (music.isPlaying()){
//                        music.stop();
//                        music = new MediaPlayer();
//                    }
//                    music.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                    music.setDataSource(this, uri);
//                    music.prepare();
//                    music.start();
//                } catch (IOException e) {
//                    Log.d("mytag", "error: " + e.getLocalizedMessage());
//                    e.printStackTrace();
//                }
//                Log.d("mytag", uri.getPath());
//            }
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
}