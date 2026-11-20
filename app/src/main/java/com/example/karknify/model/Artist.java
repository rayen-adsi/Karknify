package com.example.karknify.model;

public class Artist {
    private int id;
    private String name;
    private String bio;
    private String photoName;   // image file name in res/drawable
    private float mapX;         // position of the pin on the Kerkennah map (0 to 1)
    private float mapY;

    public Artist(int id, String name, String bio, String photoName, float mapX, float mapY) {
        this.id = id;
        this.name = name;
        this.bio = bio;
        this.photoName = photoName;
        this.mapX = mapX;
        this.mapY = mapY;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getBio() { return bio; }
    public String getPhotoName() { return photoName; }
    public float getMapX() { return mapX; }
    public float getMapY() { return mapY; }
}