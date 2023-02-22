package com.example.musiclearning;

import androidx.room.Embedded;
import androidx.room.Relation;

public class SongAndNote {
    @Embedded
    public Song song;
    @Relation(
            parentColumn = "note_id",
            entityColumn = "_id"
    )
    public Note note;
}
