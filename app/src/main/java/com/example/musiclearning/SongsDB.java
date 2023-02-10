package com.example.musiclearning;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Song.class}, version = 1)
public abstract class SongsDB extends RoomDatabase {
    abstract SongDAO songDAO();

    private static final String DB_NAME = "songs.db";
    private static volatile SongsDB INSTANCE = null;

    static SongsDB create(Context ctxt, boolean memoryOnly) {
        RoomDatabase.Builder<SongsDB> b;
        if (memoryOnly) {
            b = Room.inMemoryDatabaseBuilder(ctxt.getApplicationContext(),
                    SongsDB.class);
        }
        else {
            b = Room.databaseBuilder(ctxt.getApplicationContext(), SongsDB.class,
                    DB_NAME);
        }
        return(b.build());
    }

    synchronized static SongsDB get(Context ctxt) {
        if (INSTANCE == null) {
            INSTANCE = create(ctxt, false);
        }
        return(INSTANCE);
    }
}
