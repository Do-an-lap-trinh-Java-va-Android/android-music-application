package com.music.ui.playsong.notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadata;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.music.R;
import com.music.models.Song;

public class MediaPlayerNotification {
    private static final String TAG = "MediaPlayerNotification";

    public static final String CHANNEL_ID = "d6125a05-7632-409c-bf42-a17c18d9e97a";

    private static final int NOTIFICATION_MUSIC_PLAYER_ID = 1;

    public static final String ACTION_PLAY = "ACTION_PLAY";

    public static void createNotification(Context context, @NonNull Song track, int playButton) {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(context, TAG);
        mediaSessionCompat.setMetadata(
                new MediaMetadataCompat.Builder()
                        .putString(MediaMetadata.METADATA_KEY_TITLE, track.getName())
                        .putString(MediaMetadata.METADATA_KEY_ARTIST, track.getArtistsNames())
                        .build()
        );
        mediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                                    MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSessionCompat.setActive(true);

        Intent intentPlaySong = new Intent(context, MediaPlayerNotificationReceiver.class).setAction(ACTION_PLAY);
        PendingIntent pendingIntentPlaySong = PendingIntent.getBroadcast(
                context, 0, intentPlaySong, PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_round_music_note_24)
                .setContentTitle(track.getName())
                .setContentText(track.getArtistsNames())
                .setSubText(track.getFormatListens() + " lượt nghe")
                .setOnlyAlertOnce(false)
                .setShowWhen(false)
                .setColorized(true)
                .addAction(playButton, "Phát nhạc", pendingIntentPlaySong)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setOngoing(true)
                .setStyle(
                        new androidx.media.app.NotificationCompat.MediaStyle()
                                .setMediaSession(mediaSessionCompat.getSessionToken())
                )
                .setAutoCancel(false)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        Glide.with(context.getApplicationContext())
                .asBitmap()
                .override(128)
                .load(track.getThumbnail())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        notification.setLargeIcon(resource);
                        notificationManagerCompat.notify(NOTIFICATION_MUSIC_PLAYER_ID, notification.build());
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) { }
                });

        notificationManagerCompat.notify(NOTIFICATION_MUSIC_PLAYER_ID, notification.build());
    }
}