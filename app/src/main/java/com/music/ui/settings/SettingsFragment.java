package com.music.ui.settings;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.Preference.OnPreferenceClickListener;
import androidx.preference.PreferenceFragmentCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.music.R;

import javax.inject.Inject;

public class SettingsFragment extends PreferenceFragmentCompat implements OnPreferenceChangeListener, OnPreferenceClickListener {
    private static final String TAG = "SettingsFragment";

    @Inject
    FirebaseAuth firebaseAuth;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.setting_preferences, rootKey);

        // Thay đổi giao diện Sáng / Tối / Theo hệ thống
        Preference appThemePreference = findPreference("change_app_theme");
        if (appThemePreference != null) {
            appThemePreference.setOnPreferenceChangeListener(this);
        }

        // Đăng xuất
        Preference logoutPreference = findPreference("logout");
        if (logoutPreference != null) {
            logoutPreference.setOnPreferenceClickListener(this);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if ("change_app_theme".equals(preference.getKey())) {
            handleSwithTheme(preference, newValue);
        }

        return true;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if ("logout".equals(preference.getKey())) {
            handleLogout();
        }

        return true;
    }

    /**
     * Xử lý thao tác chuyển đổi giao diện đen, trắng, mặc định
     *
     * @param preference Preference
     * @param newValue   Giao diện muốn chuyển đến
     */
    private void handleSwithTheme(@NonNull Preference preference, @NonNull Object newValue) {
        String theme = newValue.toString();

        switch (theme) {
            case "dark":
                Log.i(TAG, "handleSwithTheme: Đã chuyển sang giao diện tối");
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case "light":
                Log.i(TAG, "handleSwithTheme: Đã chuyển sang giao diện sáng");
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            default:
                Log.i(TAG, "handleSwithTheme: Đã chuyển sang giao diện theo hệ thống");
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    /**
     * Xử lý thao tác đăng xuất
     */
    private void handleLogout() {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {
            Log.i(TAG, "onPreferenceClick: Đăng xuất tài khoản: " + user.getEmail());
            firebaseAuth.signOut();
        }
    }
}