package com.music;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.music.databinding.ActivityMainRegisterBinding;

public class RegisterMainActivity extends AppCompatActivity {
    private ActivityMainRegisterBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.tvCreateANewAccount.setOnClickListener(v -> {
            Intent registerFirstStepIntent = new Intent(this, RegisterFirstStepActivity.class);
            startActivity(registerFirstStepIntent);
        });
    }
}