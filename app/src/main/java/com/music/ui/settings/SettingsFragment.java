package com.music.ui.settings;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.music.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.setting_preferences, rootKey);

        // Thay đổi giao diện Sáng / Tối / Theo hệ thống
        Preference appThemePreference = findPreference("change_app_theme");
        if (appThemePreference != null) {
            appThemePreference.setOnPreferenceChangeListener(this::switchThemePreferenceChangeListener);
        }

        // Đăng xuất
        Preference logoutPreference = findPreference("logout");
        if (logoutPreference != null) {
            logoutPreference.setOnPreferenceClickListener(preference -> {
                FirebaseAuth.getInstance().signOut();
                return false;
            });
        }
    }

    private boolean switchThemePreferenceChangeListener(@NonNull Preference preference,
                                                        @NonNull Object newValue) {
        String theme = newValue.toString();

        switch (theme) {
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case "light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }

        return true;
    }
}