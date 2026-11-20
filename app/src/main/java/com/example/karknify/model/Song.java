package com.example.karknify.model;

public class Song {
    private int id;
    private String title;
    private int artistId;
    private String artistName;   // filled by the database join
    private String genre;
    private String duration;     // e.g. "3:45"
    private String audioName;    // file name in res/raw, without extension
    private String coverName;    // image name in res/drawable
    private int playCount;
    private long lastPlayed;     // time in milliseconds, 0 = never played

    public Song(int id, String title, int artistId, String artistName, String genre,
                String duration, String audioName, String coverName,
                int playCount, long lastPlayed) {
        this.id = id;
        this.title = title;
        this.artistId = artistId;
        this.artistName = artistName;
        this.genre = genre;
        this.duration = duration;
        this.audioName = audioName;
        this.coverName = coverName;
        this.playCount = playCount;
        this.lastPlayed = lastPlayed;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public int getArtistId() { return artistId; }
    public String getArtistName() { return artistName; }
    public String getGenre() { return genre; }
    public String getDuration() { return duration; }
    public String getAudioName() { return audioName; }
    public String getCoverName() { return coverName; }
    public int getPlayCount() { return playCount; }
    public long getLastPlayed() { return lastPlayed; }
}