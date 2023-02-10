package com.example.musiclearning;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.CustomViewHolder>{
    private List<Song> songs = new ArrayList<>();
    LayoutInflater inflater;
    ItemClickListener listener;

    public SongListAdapter(Context context, ItemClickListener listener) {
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.song_item, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        Song song = songs.get(position);

        holder.idTV.setText(Integer.toString(song.getId()));
        holder.songTV.setText(song.getSong());
        holder.artistTV.setText(song.getArtist());
        holder.sourceTV.setText(song.getSource());
        holder.cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(song);
            }
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
        notifyDataSetChanged(); // не использовать в RecycleView
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        private TextView idTV, songTV, artistTV, sourceTV;
        ConstraintLayout cl;

        public CustomViewHolder(View view) {
            super(view);

            idTV = view.findViewById(R.id._id);
            songTV = view.findViewById(R.id.song);
            artistTV = view.findViewById(R.id.artist);
            sourceTV = view.findViewById(R.id.source);
            cl = view.findViewById(R.id.recycle_view_layout);
        }
    }
}
