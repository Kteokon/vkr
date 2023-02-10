package com.example.musiclearning;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class SongRepository {
    private SongDAO songDAO;
    private LiveData<List<Song>> songs;

    public SongRepository(Application application) {
        SongsDB songsDB = SongsDB.get(application);
        this.songDAO = songsDB.songDAO();
        this.songs = songDAO.selectAll();
    }

    public void insert(Song song) {
        new InsertSongTask(songDAO).execute(song);
    }

    public void update(Song song) {
        new UpdateSongTask(songDAO).execute(song);
    }

    public void delete(Song song) {
        new DeleteSongTask(songDAO).execute(song);
    }

    public void deleteAllSongs(Application application) {
        SongsDB songsDB = SongsDB.get(application);
        new DeleteAllSongsTask(songsDB).execute();
    }

    public LiveData<List<Song>> getSongs() {
        return songs;
    }

    public static class InsertSongTask extends AsyncTask<Song, Void, Void> {
        private SongDAO songDAO;

        private InsertSongTask(SongDAO songDAO) {
            this.songDAO = songDAO;
        }

        @Override
        protected Void doInBackground(Song... songs) {
            songDAO.insert(songs[0]);
            return null;
        }
    }

    public static class UpdateSongTask extends AsyncTask<Song, Void, Void> {
        private SongDAO songDAO;

        private UpdateSongTask(SongDAO songDAO) {
            this.songDAO = songDAO;
        }

        @Override
        protected Void doInBackground(Song... songs) {
            songDAO.update(songs[0]);
            return null;
        }
    }

    public static class DeleteSongTask extends AsyncTask<Song, Void, Void> {
        private SongDAO songDAO;

        private DeleteSongTask(SongDAO songDAO) {
            this.songDAO = songDAO;
        }

        @Override
        protected Void doInBackground(Song... songs) {
            songDAO.delete(songs[0]);
            return null;
        }
    }

    public static class DeleteAllSongsTask extends AsyncTask<Void, Void, Void> {
        SongsDB songsDB;

        private DeleteAllSongsTask(SongsDB songsDB) {
            this.songsDB = songsDB;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            this.songsDB.clearAllTables();
            return null;
        }
    }
}
