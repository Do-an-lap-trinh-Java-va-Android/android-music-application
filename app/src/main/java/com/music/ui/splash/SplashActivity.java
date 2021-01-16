package com.music.ui.splash;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;
import com.music.ui.login.LoginActivity;
import com.music.ui.main.MainActivity;
import com.music.utils.NetworkHelper;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SplashActivity extends AppCompatActivity {
    @Inject
    FirebaseAuth firebaseAuth;

    @Inject
    NetworkHelper networkHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String theme = getSharedPreferences("APP_SETTINGS", MODE_PRIVATE).getString("theme", "auto");

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

        // Kiểm tra kết nối Internet
        if (networkHelper.isNetworkNotAvailable()) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setMessage("Để sử dụng ứng dụng bạn cần kết nối internet");
            alertBuilder.setOnDismissListener(dialog -> finishAndRemoveTask());
            alertBuilder.show();
            return;
        }

        /*
            Kiểm tra đã đăng nhập hay chưa, nếu chưa đăng nhập sẽ dẫn đến trang đăng nhập
            Nếu đăng nhập rồi thì sẽ dẫn đến trang chủ
         */
        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }

        finish();
    }
}