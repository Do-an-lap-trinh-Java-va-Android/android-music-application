package com.music.views;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.music.databinding.ActivityRegisterControlBinding;

public class RegisterControlActivity extends AppCompatActivity {
    private ActivityRegisterControlBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterControlBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.tvCreateANewAccount.setOnClickListener(v -> {
            Intent registerFirstStepIntent = new Intent(this, RegisterActivity.class);
            startActivity(registerFirstStepIntent);
        });
    }
}