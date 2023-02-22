package com.example.musiclearning;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "note")
public class Note implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private Integer _id;

    @ColumnInfo(name = "song_notes")
    private String songNotes;

    public Note(@NonNull String songNotes) {
        this.songNotes = songNotes;
    }

    public Integer getId() {
        return this._id;
    }
    public void setId(Integer _id) {
        this._id = _id;
    }

    public String getSongNotes() {
        return this.songNotes;
    }
    public void setSongNotes(String songNotes) {
        this.songNotes = songNotes;
    }
}
