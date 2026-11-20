package com.example.karknify.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "karknify.db";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // Called once, the first time the database is created
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT UNIQUE NOT NULL, " +
                "email TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL, " +
                "full_name TEXT, " +
                "role TEXT DEFAULT 'user')");

        db.execSQL("CREATE TABLE artists (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "bio TEXT, " +
                "photo_name TEXT, " +
                "map_x REAL, " +
                "map_y REAL)");

        db.execSQL("CREATE TABLE songs (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, " +
                "artist_id INTEGER, " +
                "genre TEXT, " +
                "duration TEXT, " +
                "audio_name TEXT, " +
                "cover_name TEXT, " +
                "play_count INTEGER DEFAULT 0, " +
                "last_played INTEGER DEFAULT 0, " +
                "FOREIGN KEY (artist_id) REFERENCES artists(id))");

        seedData(db);
    }

    // Called when DB_VERSION increases: simplest approach is to rebuild
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS songs");
        db.execSQL("DROP TABLE IF EXISTS artists");
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    // Sample data. Replace with your real artists and songs.
    private void seedData(SQLiteDatabase db) {
        insertArtist(db, "Artist One", "Sample biography.", "artist1", 0.30f, 0.40f);
        insertArtist(db, "Artist Two", "Sample biography.", "artist2", 0.60f, 0.55f);

        insertSong(db, "Song One", 1, "Traditional", "3:45", "song1", "cover1");
        insertSong(db, "Song Two", 1, "Traditional", "4:10", "song2", "cover2");
        insertSong(db, "Song Three", 2, "Folk", "2:58", "song3", "cover3");
    }

    private void insertArtist(SQLiteDatabase db, String name, String bio,
                              String photo, float x, float y) {
        ContentValues v = new ContentValues();
        v.put("name", name);
        v.put("bio", bio);
        v.put("photo_name", photo);
        v.put("map_x", x);
        v.put("map_y", y);
        db.insert("artists", null, v);
    }

    private void insertSong(SQLiteDatabase db, String title, int artistId, String genre,
                            String duration, String audio, String cover) {
        ContentValues v = new ContentValues();
        v.put("title", title);
        v.put("artist_id", artistId);
        v.put("genre", genre);
        v.put("duration", duration);
        v.put("audio_name", audio);
        v.put("cover_name", cover);
        db.insert("songs", null, v);
    }
}