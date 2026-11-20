package com.example.karknify.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.karknify.R;
import com.example.karknify.model.Song;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    // Lets MainActivity know which song was tapped
    public interface OnSongClickListener {
        void onSongClick(Song song);
    }

    private final List<Song> songs;
    private final OnSongClickListener listener;

    public SongAdapter(List<Song> songs, OnSongClickListener listener) {
        this.songs = songs;
        this.listener = listener;
    }

    // Creates the view of one row
    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_song, parent, false);
        return new SongViewHolder(v);
    }

    // Fills one row with the data of one song
    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songs.get(position);
        holder.title.setText(song.getTitle());
        holder.artist.setText(song.getArtistName());
        holder.meta.setText(song.getGenre() + " • " + song.getDuration());

        // Tapping the row sends the song to the listener
        holder.itemView.setOnClickListener(v -> listener.onSongClick(song));
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    // Holds the views of a row so they are not searched again and again
    static class SongViewHolder extends RecyclerView.ViewHolder {
        TextView title, artist, meta;

        SongViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.songTitle);
            artist = itemView.findViewById(R.id.songArtist);
            meta = itemView.findViewById(R.id.songMeta);
        }
    }
}