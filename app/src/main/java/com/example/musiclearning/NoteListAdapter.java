package com.example.musiclearning;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.CustomViewHolder> {
    private List<Note> notes = new ArrayList<>();
    LayoutInflater inflater;
    ItemClickListener listener;

    public NoteListAdapter(Context context, ItemClickListener listener) {
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public NoteListAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.note_item, parent, false);
        return new NoteListAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteListAdapter.CustomViewHolder holder, int position) {
        Note note = notes.get(position);

        holder.idTV.setText(Integer.toString(note.getId()));
        holder.noteTV.setText(note.getSongNotes());
        holder.cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(note);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
        notifyDataSetChanged(); // не использовать в RecycleView
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        private TextView idTV, noteTV;
        ConstraintLayout cl;

        public CustomViewHolder(View view) {
            super(view);

            idTV = view.findViewById(R.id._id);
            noteTV = view.findViewById(R.id.note);
            cl = view.findViewById(R.id.recycle_view_layout);
        }
    }
}
