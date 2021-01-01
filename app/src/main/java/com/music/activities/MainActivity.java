package com.music.activities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.music.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    @SuppressWarnings({"FieldCanBeLocal", "NotNullFieldNotInitialized"})
    @NonNull
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}