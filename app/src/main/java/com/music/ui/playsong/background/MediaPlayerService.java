package com.music.ui.playsong.background;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.music.models.Song;

import lombok.SneakyThrows;

public class MediaPlayerService extends Service {
    @NonNull
    private final MediaPlayer mediaPlayer = new MediaPlayer();

    @NonNull
    private final IBinder binder = new LocalBinder();

    public static final String ACTION_PLAY = "ACTION_PLAY";
    public static final String ACTION_PREPARED = "ACTION_PREPARED";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @NonNull
    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    @SneakyThrows
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(ACTION_PLAY)) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.reset();
            }

            Song song = (Song) intent.getExtras().get("song");
            mediaPlayer.setDataSource(this, Uri.parse(song.getMp3()));
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
                Intent onPreparedIntent = new Intent(getApplicationContext(), MediaPlayerReceiver.class);
                onPreparedIntent.setAction(ACTION_PREPARED);
                onPreparedIntent.putExtra("duration", mp.getDuration());
                sendBroadcast(onPreparedIntent);
            });
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }

    public class LocalBinder extends Binder {
        @NonNull
        public MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }
}
