package com.example.musiclearning;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Song.class, Note.class}, version = 1)
public abstract class MyRoomDB extends RoomDatabase {
    abstract SongDAO songDAO();
    abstract NoteDAO noteDAO();

    private static final String DB_NAME = "songs.db";
    private static volatile MyRoomDB INSTANCE = null;

    static MyRoomDB create(Context ctxt, boolean memoryOnly) {
        RoomDatabase.Builder<MyRoomDB> b;
        if (memoryOnly) {
            b = Room.inMemoryDatabaseBuilder(ctxt.getApplicationContext(),
                    MyRoomDB.class);
        }
        else {
            b = Room.databaseBuilder(ctxt.getApplicationContext(), MyRoomDB.class,
                    DB_NAME);
        }
        return(b.build());
    }

    synchronized static MyRoomDB get(Context ctxt) {
        if (INSTANCE == null) {
            INSTANCE = create(ctxt, false);
        }
        return(INSTANCE);
    }
}
