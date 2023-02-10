package com.example.musiclearning;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class SongViewModel extends AndroidViewModel {
    private SongRepository repository;
    private LiveData<List<Song>> songs;

    public SongViewModel(@NonNull Application application) {
        super(application);
        this.repository = new SongRepository(application);
        this.songs = repository.getSongs();
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

    public void deleteAllSongs(@NonNull Application application) {
        this.repository.deleteAllSongs(application);
    }

    public LiveData<List<Song>> getSongs() {
        return this.songs;
    }
}
