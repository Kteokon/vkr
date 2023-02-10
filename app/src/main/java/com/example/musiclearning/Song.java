package com.example.musiclearning;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "song")
public class Song implements Parcelable, Serializable {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int _id;

    @NonNull
    private String artist, song, source;

    private String lyrics, translation, notes;

    public Song(@NonNull String artist, @NonNull String song, @NonNull String lyrics, @NonNull String translation, @NonNull String notes, @NonNull String source) {
        this.artist = artist;
        this.song = song;
        this.lyrics = lyrics;
        this.translation = translation;
        this.notes = notes;
        this.source = source;
    }

    protected Song(Parcel in) {
        this._id = in.readInt();
        this.artist = in.readString();
        this.song = in.readString();
        this.lyrics = in.readString();
        this.translation = in.readString();
        this.notes = in.readString();
        this.source = in.readString();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    public int getId() {
        return this._id;
    }
    public void setId(int id) {
        this._id = id;
    }

    public String getArtist() {
        return this.artist;
    }
    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getSong() {
        return this.song;
    }
    public void setSong(String song) {
        this.song = song;
    }

    public String getLyrics() {
        return this.lyrics;
    }
    public void setLyrics(String lyrics) {
        this.artist = artist;
    }

    public String getTranslation() {
        return this.translation;
    }
    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getNotes() {
        return this.notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getSource() {
        return this.source;
    }
    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this._id);
        dest.writeString(this.artist);
        dest.writeString(this.song);
        dest.writeString(this.lyrics);
        dest.writeString(this.translation);
        dest.writeString(this.notes);
        dest.writeString(this.source);
    }
}
