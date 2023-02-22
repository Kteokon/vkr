package com.example.musiclearning;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class NoteActivity extends AppCompatActivity {
    public static final String NOTE_ID = "com.example.musiclearning.NOTE_ID";
    public static final String SONG_NOTE = "com.example.musiclearning.SONG_NOTE";
    NoteViewModel noteViewModel;

    Note note;
    boolean isNew = false;
    Integer notesSize;

    EditText songNoteET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        songNoteET = findViewById(R.id.songNote);

        Intent intent = getIntent();

        note = (Note) intent.getSerializableExtra("note");

        if (note == null) {
            isNew = true;
            songNoteET.setText("");
        }
        else{
            songNoteET.setText(note.getSongNotes());
        }

        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        noteViewModel.getNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                notesSize = Integer.valueOf(notes.size());
            }
        });

    }

    public void saveChanges(View v) {
        Intent intent = new Intent();

        String songNote = songNoteET.getText().toString();

        if (isNew) {
            Integer noteId = notesSize + 1;
            intent.putExtra(NoteActivity.NOTE_ID, noteId);
            intent.putExtra(NoteActivity.SONG_NOTE, songNote);
        }
        else {
            note.setSongNotes(songNote);
            noteViewModel.update(note);
        }

        setResult(RESULT_OK, intent);
        finish();
    }

    public void cancelChanges(View v) {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }
}