package com.example.karknify.view;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.karknify.R;
import com.example.karknify.controller.SongController;
import com.example.karknify.model.Song;
import com.example.karknify.service.MusicService;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Song> allSongs;
    private MusicService musicService;   // null until the service is connected
    private boolean bound = false;

    private View playerBar;
    private TextView playerTitle;
    private Button playPauseButton;

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            musicService = ((MusicService.MusicBinder) binder).getService();
            bound = true;
            musicService.setQueue(allSongs);
            musicService.setListener(MainActivity.this::refreshPlayerBar);
            refreshPlayerBar();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicService = null;
            bound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        askNotificationPermission();

        allSongs = new SongController(this).getAllSongs();

        playerBar = findViewById(R.id.playerBar);
        playerTitle = findViewById(R.id.playerTitle);
        playPauseButton = findViewById(R.id.playPauseButton);

        RecyclerView songList = findViewById(R.id.songList);
        songList.setLayoutManager(new LinearLayoutManager(this));
        songList.setAdapter(new SongAdapter(allSongs, this::onSongClicked));

        playPauseButton.setOnClickListener(v -> {
            if (musicService != null) musicService.togglePause();
        });
        findViewById(R.id.nextButton).setOnClickListener(v -> {
            if (musicService != null) musicService.next();
        });
        findViewById(R.id.previousButton).setOnClickListener(v -> {
            if (musicService != null) musicService.previous();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, MusicService.class), connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bound) {
            musicService.setListener(null);
            unbindService(connection);   // the service keeps playing
            bound = false;
        }
    }

    private void onSongClicked(Song song) {
        if (musicService == null) return;

        // "Started" service: stays alive even when the activity is closed
        startService(new Intent(this, MusicService.class));

        if (!musicService.play(song)) {
            Toast.makeText(this, "Audio file not found: " + song.getAudioName(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    // Called by the service every time the song or the state changes
    private void refreshPlayerBar() {
        if (musicService == null || musicService.getCurrentSong() == null) {
            playerBar.setVisibility(View.GONE);
            return;
        }
        Song current = musicService.getCurrentSong();
        playerTitle.setText(current.getTitle() + " - " + current.getArtistName());
        playPauseButton.setText(musicService.isPlaying() ? "Pause" : "Play");
        playerBar.setVisibility(View.VISIBLE);
    }

    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33
                && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        }
    }
}