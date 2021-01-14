package com.music.ui.playsong;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationActionService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent("TRACKS_TRACKS");
        intent1.putExtra("actionName", intent.getAction());
        context.sendBroadcast(intent1);
    }
}
