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
public interface NoteDAO {
    @Query("SELECT * FROM Note")
    LiveData<List<Note>> selectAll();

//    @Transaction
//    @Query("SELECT * FROM note")
//    LiveData<List<SongAndNote>> selectAllNotesWithSong();

    @Query("SELECT * FROM Note WHERE _id=:id")
    LiveData<Note> findById(Integer id);

    @Insert
    void insert(Note... notes);

    @Delete
    void delete(Note... notes);

    @Update
    void update(Note... notes);
}
