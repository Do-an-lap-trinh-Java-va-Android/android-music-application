package com.music.ui.playsong.playback;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.MediaSessionCompat.Callback;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.session.MediaButtonReceiver;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.music.R;
import com.music.models.Song;

import java.util.List;

import lombok.SneakyThrows;

public class MediaPlayBackService extends MediaBrowserServiceCompat {
    private static final String TAG = "MediaPlayBackService";

    public static final String CHANNEL_ID = "d6125a05-7632-409c-bf42-a17c18d9e97a";
    private static final int NOTIFICATION_MUSIC_PLAYER_ID = 1;

    @Nullable
    private MediaPlayer mediaPlayer;

    @Nullable
    private MediaSessionCompat mediaSession;

    @Nullable
    private Song song;

    @NonNull
    private final IBinder binder = new LocalBinder();

    @NonNull
    private final MediaSessionCompat.Callback callback = new Callback() {
        @Override
        public void onPlay() {
            Log.i(TAG, "onPlay: ");

            mediaSession.setActive(true);
            setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING);
            mediaPlayer.start();
            showPauseNotification();
        }

        @Override
        public void onPause() {
            Log.i(TAG, "onPause: ");
            mediaPlayer.pause();
            setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED);
            showPlayingNotification();
            stopForeground(false);
        }

        @Override
        public void onStop() {
            Log.i(TAG, "onStop: onStop");
            mediaSession.setActive(false);
            mediaPlayer.stop();
            stopForeground(false);
        }

        @Override
        public void onSeekTo(long pos) {
            mediaPlayer.seekTo((int) pos);

            if (mediaPlayer.isPlaying()) {
                setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING);
            } else {
                setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED);
            }
        }

        @SneakyThrows
        @Override
        public void onPlayFromUri(Uri uri, Bundle extras) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.reset();
            }

            try {
                mediaPlayer.setDataSource(getBaseContext(), uri);
            } catch (Exception e) {
                mediaPlayer.release();
                initMediaPlayer();
                mediaPlayer.setDataSource(getBaseContext(), uri);
            }

            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
                initMediaSessionMetadata();
            });
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        initMediaPlayer();
        initMediaSession();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MediaButtonReceiver.handleIntent(mediaSession, intent);

        song = (Song) intent.getExtras().get("song");

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: ");
        super.onDestroy();

        if (mediaSession != null) {
            mediaSession.release();
        }

        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        if (TextUtils.equals(clientPackageName, getPackageName())) {
            return new BrowserRoot(getString(R.string.app_name), null);
        }

        return null;
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        result.sendResult(null);
    }

    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
        mediaPlayer.setVolume(1.0f, 1.0f);
        mediaPlayer.setOnCompletionListener(mp -> {
            if (mediaSession.getController().getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING) {
                mediaSession.getController().getTransportControls().seekTo(0);
                mediaSession.getController().getTransportControls().pause();
            }
        });
    }

    private void initMediaSession() {
        mediaSession = new MediaSessionCompat(getBaseContext(), TAG);

        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY |
                            PlaybackStateCompat.ACTION_PLAY_PAUSE);
        mediaSession.setPlaybackState(stateBuilder.build());

        mediaSession.setCallback(callback);

        setSessionToken(mediaSession.getSessionToken());
    }

    private void initMediaSessionMetadata() {
        MediaMetadataCompat.Builder metadataBuilder = new MediaMetadataCompat.Builder();

        if (song == null) return;
        if (mediaSession == null) return;

        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.getName());
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.getArtistsNames());
        metadataBuilder.putString(
                MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION,
                song.getFormatListens() + " lượt nghe"
        );
        metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, song.getDuration());
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, song.getThumbnail());

        mediaSession.setMetadata(metadataBuilder.build());
    }

    private void showPauseNotification() {
        if (mediaSession == null) return;

        MediaControllerCompat controller = mediaSession.getController();
        MediaMetadataCompat mediaMetadata = controller.getMetadata();
        MediaDescriptionCompat description = mediaMetadata.getDescription();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mediaPlaybackChannel =
                    new NotificationChannel(CHANNEL_ID, "Media Playback", NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(mediaPlaybackChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext(), CHANNEL_ID);

        builder.setContentTitle(description.getTitle())
                .setContentText(description.getSubtitle())
                .setSubText(description.getDescription())
                .setLargeIcon(description.getIconBitmap())
                .setContentIntent(controller.getSessionActivity())
                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(getBaseContext(), PlaybackStateCompat.ACTION_STOP))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.ic_round_music_note_24)
                .setShowWhen(false)
                .addAction(new NotificationCompat.Action(
                        R.drawable.ic_outline_pause_circle_light_64, "Pause",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(getBaseContext(), PlaybackStateCompat.ACTION_PLAY_PAUSE))
                )
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.getSessionToken())
                        .setShowActionsInCompactView(0)
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(getBaseContext(), PlaybackStateCompat.ACTION_STOP))
                )
                .setPriority(NotificationCompat.PRIORITY_LOW);

        Glide.with(getApplicationContext()).asBitmap().load(description.getIconUri()).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                builder.setLargeIcon(resource);
                startForeground(NOTIFICATION_MUSIC_PLAYER_ID, builder.build());
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });
    }

    private void showPlayingNotification() {
        if (mediaSession == null) return;

        MediaControllerCompat controller = mediaSession.getController();
        MediaMetadataCompat mediaMetadata = controller.getMetadata();
        MediaDescriptionCompat description = mediaMetadata.getDescription();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext(), CHANNEL_ID);

        builder.setContentTitle(description.getTitle())
                .setContentText(description.getSubtitle())
                .setSubText(description.getDescription())
                .setLargeIcon(description.getIconBitmap())
                .setContentIntent(controller.getSessionActivity())
                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(getBaseContext(), PlaybackStateCompat.ACTION_STOP))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.ic_round_music_note_24)
                .setShowWhen(false)
                .addAction(new NotificationCompat.Action(
                        R.drawable.ic_launcher_background, "Close",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(getBaseContext(), PlaybackStateCompat.ACTION_STOP)
                ))
                .addAction(new NotificationCompat.Action(
                        R.drawable.ic_outline_play_circle_light_64, "Play",
                        MediaButtonReceiver.buildMediaButtonPendingIntent(getBaseContext(), PlaybackStateCompat.ACTION_PLAY_PAUSE))
                )
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.getSessionToken())
                        .setShowActionsInCompactView(0, 1)
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(getBaseContext(), PlaybackStateCompat.ACTION_STOP))
                )
                .setPriority(NotificationCompat.PRIORITY_LOW);

        Glide.with(getApplicationContext()).asBitmap().load(description.getIconUri()).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                builder.setLargeIcon(resource);
                startForeground(NOTIFICATION_MUSIC_PLAYER_ID, builder.build());
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });
    }

    private void setMediaPlaybackState(int state) {
        if (mediaSession == null) return;

        PlaybackStateCompat.Builder playBackStateBuilder = new PlaybackStateCompat.Builder();

        if (state == PlaybackStateCompat.STATE_PLAYING) {
            playBackStateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE |
                                            PlaybackStateCompat.ACTION_PAUSE |
                                            PlaybackStateCompat.ACTION_SEEK_TO);
            playBackStateBuilder.setState(state, mediaPlayer.getCurrentPosition(), 1.0f);
        } else {
            playBackStateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE |
                                            PlaybackStateCompat.ACTION_PLAY |
                                            PlaybackStateCompat.ACTION_SEEK_TO);
            playBackStateBuilder.setState(state, mediaPlayer.getCurrentPosition(), 0f);
        }

        mediaSession.setPlaybackState(playBackStateBuilder.build());
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (SERVICE_INTERFACE.equals(intent.getAction())) {
            return super.onBind(intent);
        }

        return binder;
    }

    public class LocalBinder extends Binder {
        @Nullable
        public MediaPlayer getMediaPlayer() {
            return mediaPlayer;
        }
    }
}
