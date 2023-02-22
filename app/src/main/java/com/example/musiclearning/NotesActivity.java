package com.example.musiclearning;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.Serializable;
import java.util.List;

public class NotesActivity extends AppCompatActivity implements ItemClickListener {
    private List<Note> notes;
    private List<SongAndNote> songs;

    private NoteViewModel noteViewModel;
    private RecyclerView notesList;
    Button songsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        notesList = findViewById(R.id.notesList);
        notesList.setLayoutManager(new LinearLayoutManager(this));
        songsButton = findViewById(R.id.songsButton);

        NoteListAdapter adapter = new NoteListAdapter(this, this);
        notesList.setAdapter(adapter);

        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        noteViewModel.getNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> _notes) {
                notes = _notes;
                adapter.setNotes(notes);
                Log.d("mytag", "Records in adapter: " + notes.size());
            }
        });

        SongViewModel songViewModel = new ViewModelProvider(this).get(SongViewModel.class);
        songViewModel.getSongsWithNote().observe(this, new Observer<List<SongAndNote>>() {
            @Override
            public void onChanged(List<SongAndNote> songAndNotes) {
                songs = songAndNotes;
            }
        });

        songsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onItemClick(SongAndNote item) {

    }

    @Override
    public void onItemClick(Note item) {
        Intent intent = new Intent(this, NoteActivity.class);
        intent.putExtra("note", (Serializable) item);
        startActivity(intent);
    }
}