package com.example.musiclearning;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class SongViewModel extends AndroidViewModel {
    private SongRepository repository;
    private LiveData<List<Song>> songs;
    private LiveData<List<SongAndNote>> songsWithNote;

    public SongViewModel(@NonNull Application application) {
        super(application);
        this.repository = new SongRepository(application);
        this.songs = repository.getSongs();
        this.songsWithNote = repository.getSongsWithNote();
    }

    public void insert(Song song) {
        this.repository.insert(song);
    }

    public void update(Song song) {
        this.repository.update(song);
    }

    public void delete(Song song) {
        this.repository.delete(song);
    }

    public LiveData<Song> getById(Integer songId) throws ExecutionException, InterruptedException {
        return this.repository.getById(songId);
    }

    public LiveData<List<Song>> getSongs() {
        return this.songs;
    }

    public LiveData<List<SongAndNote>> getSongsWithNote() {
        return this.songsWithNote;
    }
}
