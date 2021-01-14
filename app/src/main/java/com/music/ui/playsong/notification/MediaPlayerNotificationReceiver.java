package com.music.ui.playsong.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MediaPlayerNotificationReceiver extends BroadcastReceiver {
    public static final String INTENT_FILTER_NAME = "MEDIA_PLAYER_NOTIFICATION_RECEIVER";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent nextIntent = new Intent(INTENT_FILTER_NAME);
        nextIntent.putExtra("actionName", intent.getAction());

        context.sendBroadcast(nextIntent);
    }
}