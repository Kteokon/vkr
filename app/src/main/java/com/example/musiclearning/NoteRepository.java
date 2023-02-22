package com.example.musiclearning;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class NoteRepository {
    NoteDAO noteDAO;
    LiveData<List<Note>> notes;
//    LiveData<List<SongAndNote>> notesWithSong;

    public NoteRepository(Application application) {
        MyRoomDB myroomDB = MyRoomDB.get(application);
        noteDAO = myroomDB.noteDAO();
        notes = noteDAO.selectAll();
//        notesWithSong = noteDAO.selectAllNotesWithSong();
    }

    public void insert(Note note) {
        new InsertNoteTask(noteDAO).execute(note);
    }

    public void update(Note note) {
        new UpdateNoteTask(noteDAO).execute(note);
    }

    public void delete(Note note) {
        new DeleteNoteTask(noteDAO).execute(note);
    }

    public LiveData<Note> getById(Integer noteId) throws ExecutionException, InterruptedException {
        GetNoteByIdTask task = new GetNoteByIdTask(noteDAO);
        LiveData<Note> note = task.execute(noteId).get();
        return note;
    }

//    public void deleteAllNotes(Note note) {
//
//    }

    public static class InsertNoteTask extends AsyncTask<Note, Void, Void> {
        private NoteDAO noteDAO;

        private InsertNoteTask(NoteDAO noteDAO) {
            this.noteDAO = noteDAO;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDAO.insert(notes[0]);
            return null;
        }
    }

    public static class UpdateNoteTask extends AsyncTask<Note, Void, Void> {
        private NoteDAO noteDAO;

        private UpdateNoteTask(NoteDAO noteDAO) {
            this.noteDAO = noteDAO;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDAO.update(notes[0]);
            return null;
        }
    }

    public static class DeleteNoteTask extends AsyncTask<Note, Void, Void> {
        private NoteDAO noteDAO;

        private DeleteNoteTask(NoteDAO noteDAO) {
            this.noteDAO = noteDAO;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDAO.delete(notes[0]);
            return null;
        }
    }

    public static class GetNoteByIdTask extends AsyncTask<Integer, Void, LiveData<Note>> {
        private NoteDAO noteDAO;

        private GetNoteByIdTask(NoteDAO noteDAO) {
            this.noteDAO = noteDAO;
        }

        @Override
        protected LiveData<Note> doInBackground(Integer... integers) {
            LiveData<Note> note = noteDAO.findById(integers[0]);
            return note;
        }
    }

    public LiveData<List<Note>> getNotes() {
        return notes;
    }

//    public LiveData<List<SongAndNote>> getNotesWithSong() {
//        return notesWithSong;
//    }
}
