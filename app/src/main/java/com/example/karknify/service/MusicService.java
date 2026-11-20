package com.example.karknify.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;
import androidx.core.app.ServiceCompat;

import com.example.karknify.controller.PlayerController;
import com.example.karknify.controller.SongController;
import com.example.karknify.model.Song;
import com.example.karknify.view.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class MusicService extends Service {

    // Actions sent by the notification buttons
    public static final String ACTION_PLAY_PAUSE = "com.example.karknify.PLAY_PAUSE";
    public static final String ACTION_NEXT = "com.example.karknify.NEXT";
    public static final String ACTION_PREVIOUS = "com.example.karknify.PREVIOUS";

    private static final String CHANNEL_ID = "karknify_playback";
    private static final int NOTIFICATION_ID = 1;

    private final IBinder binder = new MusicBinder();
    private PlayerController playerController;
    private SongController songController;

    private List<Song> queue = new ArrayList<>();   // the songs to play, in order
    private int currentIndex = -1;                  // position in the queue
    private Runnable listener;                      // tells the activity to refresh

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        playerController = new PlayerController(this);
        songController = new SongController(this);
        playerController.setOnCompletionListener(this::next);   // auto-play next song
        createNotificationChannel();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    // Called when a notification button is tapped
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case ACTION_PLAY_PAUSE: togglePause(); break;
                case ACTION_NEXT: next(); break;
                case ACTION_PREVIOUS: previous(); break;
            }
        }
        return START_NOT_STICKY;
    }

    // ----- Methods called by the activity -----

    public void setQueue(List<Song> songs) {
        Song current = getCurrentSong();
        queue = new ArrayList<>(songs);
        currentIndex = -1;
        if (current != null) {   // keep our position if a song is already playing
            for (int i = 0; i < queue.size(); i++) {
                if (queue.get(i).getId() == current.getId()) currentIndex = i;
            }
        }
    }

    public void setListener(Runnable listener) {
        this.listener = listener;
    }

    public boolean play(Song song) {
        currentIndex = -1;
        for (int i = 0; i < queue.size(); i++) {
            if (queue.get(i).getId() == song.getId()) currentIndex = i;
        }
        if (currentIndex == -1) {
            queue.add(song);
            currentIndex = queue.size() - 1;
        }
        return playCurrent();
    }

    public void next() {
        if (queue.isEmpty()) return;
        currentIndex = (currentIndex + 1) % queue.size();
        playCurrent();
    }

    public void previous() {
        if (queue.isEmpty()) return;
        currentIndex = currentIndex <= 0 ? queue.size() - 1 : currentIndex - 1;
        playCurrent();
    }

    public void togglePause() {
        if (playerController.getCurrentSong() == null) return;
        playerController.togglePause();
        if (playerController.isPlaying()) {
            showForegroundNotification();
        } else {
            // Paused: the notification stays but can be swiped away
            stopForeground(STOP_FOREGROUND_DETACH);
            getSystemService(NotificationManager.class)
                    .notify(NOTIFICATION_ID, buildNotification());
        }
        notifyListener();
    }

    public boolean isPlaying() {
        return playerController.isPlaying();
    }

    public Song getCurrentSong() {
        return playerController.getCurrentSong();
    }

    // ----- Internal -----

    private boolean playCurrent() {
        if (currentIndex < 0 || currentIndex >= queue.size()) return false;
        Song song = queue.get(currentIndex);
        if (!playerController.play(song)) return false;
        songController.markPlayed(song.getId());
        showForegroundNotification();
        notifyListener();
        return true;
    }

    private void notifyListener() {
        if (listener != null) listener.run();
    }

    private void showForegroundNotification() {
        ServiceCompat.startForeground(this, NOTIFICATION_ID, buildNotification(),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK);
    }

    private Notification buildNotification() {
        Song song = playerController.getCurrentSong();
        boolean playing = playerController.isPlaying();

        // Tapping the notification reopens the app
        PendingIntent openApp = PendingIntent.getActivity(
                this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setContentTitle(song.getTitle())
                .setContentText(song.getArtistName())
                .setContentIntent(openApp)
                .setOnlyAlertOnce(true)
                .addAction(android.R.drawable.ic_media_previous, "Previous",
                        actionIntent(ACTION_PREVIOUS, 1))
                .addAction(playing ? android.R.drawable.ic_media_pause
                                : android.R.drawable.ic_media_play,
                        playing ? "Pause" : "Play", actionIntent(ACTION_PLAY_PAUSE, 2))
                .addAction(android.R.drawable.ic_media_next, "Next",
                        actionIntent(ACTION_NEXT, 3))
                .build();
    }

    // A button that sends an action back to this service
    private PendingIntent actionIntent(String action, int requestCode) {
        Intent intent = new Intent(this, MusicService.class).setAction(action);
        return PendingIntent.getService(this, requestCode, intent, PendingIntent.FLAG_IMMUTABLE);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Music playback", NotificationManager.IMPORTANCE_LOW);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
    }

    @Override
    public void onDestroy() {
        playerController.release();
        super.onDestroy();
    }
}