package com.example.musiclearning;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class SongRepository {
    private SongDAO songDAO;
    private LiveData<List<Song>> songs;
    private LiveData<List<SongAndNote>> songsWithNote;

    public SongRepository(Application application) {
        MyRoomDB myRoomDB = MyRoomDB.get(application);
        this.songDAO = myRoomDB.songDAO();
        this.songs = songDAO.selectAll();
        this.songsWithNote = songDAO.selectAllSongsWithNote();
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

    public LiveData<Song> getById(Integer songId) throws ExecutionException, InterruptedException {
        GetSongByIdTask task = new GetSongByIdTask(songDAO);
        LiveData<Song> song = task.execute(songId).get();
        return song;
    }

    public LiveData<List<Song>> getSongs() {
        return songs;
    }

    public LiveData<List<SongAndNote>> getSongsWithNote() {
        return songsWithNote;
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

    public static class GetSongByIdTask extends AsyncTask<Integer, Void, LiveData<Song>> {
        private SongDAO songDAO;

        private GetSongByIdTask(SongDAO songDAO) {
            this.songDAO = songDAO;
        }

        @Override
        protected LiveData<Song> doInBackground(Integer... integers) {
            LiveData<Song> song = songDAO.findById(integers[0]);
            return song;
        }
    }

//    public static class DeleteAllSongsTask extends AsyncTask<Void, Void, Void> {
//        MyRoomDB myRoomDB;
//
//        private DeleteAllSongsTask(MyRoomDB myRoomDB) {
//            this.myRoomDB = myRoomDB;
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            this.myRoomDB.clearAllTables();
//            return null;
//        }
//    }
}
