package com.example.musiclearning;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "song",
        foreignKeys = {@ForeignKey(entity = Note.class,
                parentColumns = "_id",
                childColumns = "note_id",
                onDelete = ForeignKey.CASCADE)
        })
public class Song implements Parcelable, Serializable {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int _id;

    @NonNull
    private String artist, song, source;

    private String lyrics, translation;
    @ColumnInfo(name = "note_id")
    private Integer notes;

    public Song(@NonNull String artist, @NonNull String song, @NonNull String lyrics, @NonNull String translation, @NonNull Integer notes, @NonNull String source) {
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
        int helpie = in.readInt();
        if (helpie == 0) {
            this.notes = null;
        }
        else {
            this.notes = Integer.valueOf(helpie);
        }

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

    public Integer getNotes() {
        return this.notes;
    }
    public void setNotes(Integer notes) {
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
        if (this.notes == null) {
            dest.writeInt(0);
        }
        else {
            dest.writeInt(this.notes);
        }
        dest.writeString(this.source);
    }
}
