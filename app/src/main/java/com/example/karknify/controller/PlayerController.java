package com.example.karknify.controller;

import android.content.Context;
import android.media.MediaPlayer;

import com.example.karknify.model.Song;

public class PlayerController {

    private final Context context;
    private MediaPlayer player;
    private Song currentSong;
    private Runnable onCompletion;   // called when a song ends

    public PlayerController(Context context) {
        this.context = context.getApplicationContext();
    }

    public void setOnCompletionListener(Runnable onCompletion) {
        this.onCompletion = onCompletion;
    }

    // Plays a song from res/raw. Returns false if the audio file is missing.
    public boolean play(Song song) {
        int resId = context.getResources()
                .getIdentifier(song.getAudioName(), "raw", context.getPackageName());
        if (resId == 0) return false;

        release();
        player = MediaPlayer.create(context, resId);
        player.setOnCompletionListener(mp -> {
            if (onCompletion != null) onCompletion.run();
        });
        player.start();
        currentSong = song;
        return true;
    }

    public void togglePause() {
        if (player == null) return;
        if (player.isPlaying()) player.pause();
        else player.start();
    }

    public boolean isPlaying() {
        return player != null && player.isPlaying();
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public void release() {
        if (player != null) {
            player.release();
            player = null;
        }
    }
}