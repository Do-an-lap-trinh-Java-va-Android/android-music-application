package com.music.utils;

import android.content.Context;
import android.content.res.Configuration;

import androidx.annotation.NonNull;

public final class UiModeUtils {
    private static int getCurrentUiMode(@NonNull Context context) {
        return context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
    }

    /**
     * @param context Context
     * @return Trả về true nếu là giao diện tối và ngược lại
     */
    public static boolean isDarkMode(@NonNull Context context) {
        return getCurrentUiMode(context) == Configuration.UI_MODE_NIGHT_YES;
    }

    /**
     * @param context Context
     * @return Trả về true nếu là giao diện sáng và ngược lại
     */
    public static boolean isLightMode(@NonNull Context context) {
        return getCurrentUiMode(context) == Configuration.UI_MODE_NIGHT_NO;
    }
}
