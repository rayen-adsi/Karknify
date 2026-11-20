package com.example.karknify.controller;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.karknify.database.DatabaseHelper;
import com.example.karknify.model.Song;

import java.util.ArrayList;
import java.util.List;

public class SongController {

    private final DatabaseHelper dbHelper;

    public SongController(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // Same idea as your SongDAO.getAllSongs()
    public List<Song> getAllSongs() {
        List<Song> songs = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT s.*, a.name AS artist_name " +
                        "FROM songs s LEFT JOIN artists a ON s.artist_id = a.id " +
                        "ORDER BY s.title", null);
        while (c.moveToNext()) {
            songs.add(cursorToSong(c));
        }
        c.close();
        return songs;
    }

    // Converts one row of the result into a Song object
    private Song cursorToSong(Cursor c) {
        return new Song(
                c.getInt(c.getColumnIndexOrThrow("id")),
                c.getString(c.getColumnIndexOrThrow("title")),
                c.getInt(c.getColumnIndexOrThrow("artist_id")),
                c.getString(c.getColumnIndexOrThrow("artist_name")),
                c.getString(c.getColumnIndexOrThrow("genre")),
                c.getString(c.getColumnIndexOrThrow("duration")),
                c.getString(c.getColumnIndexOrThrow("audio_name")),
                c.getString(c.getColumnIndexOrThrow("cover_name")),
                c.getInt(c.getColumnIndexOrThrow("play_count")),
                c.getLong(c.getColumnIndexOrThrow("last_played")));
    }
    // Called every time a song starts: +1 play count and save the time
    public void markPlayed(int songId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("UPDATE songs SET play_count = play_count + 1, last_played = ? WHERE id = ?",
                new Object[]{System.currentTimeMillis(), songId});
    }
}