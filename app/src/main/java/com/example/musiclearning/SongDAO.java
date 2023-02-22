package com.example.musiclearning;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SongDAO {
    @Query("SELECT * FROM song ORDER BY artist")
    LiveData<List<Song>> selectAll();

    @Transaction
    @Query("SELECT * FROM song ORDER BY artist")
    LiveData<List<SongAndNote>> selectAllSongsWithNote();

    @Query("SELECT * FROM song WHERE _id=:id")
    LiveData<Song> findById(int id);

    @Query("SELECT * FROM song WHERE note_id=:id")
    Song findByNoteId(Integer id);

    @Query("SELECT * FROM song WHERE artist=:artist")
    LiveData<List<Song>> findByArtist(String artist);

    @Insert
    void insert(Song... songs);

    @Delete
    void delete(Song... songs);

    @Update
    void update(Song... songs);
}
