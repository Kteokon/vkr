package com.example.musiclearning;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class NoteViewModel extends AndroidViewModel {
    private NoteRepository repository;
    private LiveData<List<Note>> notes;
//    private LiveData<List<SongAndNote>> notesWithSong;

    public NoteViewModel(@NonNull Application application) {
        super(application);
        this.repository = new NoteRepository(application);
        this.notes = repository.getNotes();
//        this.notesWithSong = repository.getNotesWithSong();
    }

    public void insert(Note note) {
        repository.insert(note);
    }

    public void update(Note note) {
        repository.update(note);
    }

    public void delete(Note note) {
        repository.delete(note);
    }

    public LiveData<Note> getById(Integer noteId) throws ExecutionException, InterruptedException {
        return repository.getById(noteId);
    }

    public LiveData<List<Note>> getNotes() {
        return this.notes;
    }

//    public LiveData<List<SongAndNote>> getNotesWithSong() {
//        return this.notesWithSong;
//    }
}
