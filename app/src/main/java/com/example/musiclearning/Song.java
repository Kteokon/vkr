package com.example.musiclearning;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "song")
public class Song {
    @PrimaryKey
    @NonNull
    int _id;

    String artist, song, lyrics, translation, notes, source;

    @Ignore
    public Song(@NonNull String artist, @NonNull String song, @NonNull String lyrics, @NonNull String translation, @NonNull String notes, @NonNull String source) {
        this.artist = artist;
        this.song = song;
        this.lyrics = lyrics;
        this.translation = translation;
        this.notes = notes;
        this.source = source;
    }

    public Song(int _id, @NonNull String artist, @NonNull String song, @NonNull String lyrics, @NonNull String translation, @NonNull String notes, @NonNull String source) {
        this._id = _id;
        this.artist = artist;
        this.song = song;
        this.lyrics = lyrics;
        this.translation = translation;
        this.notes = notes;
        this.source = source;
    }
}
