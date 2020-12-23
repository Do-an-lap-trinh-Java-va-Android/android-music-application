package com.music;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.music.databinding.ActivityRegisterSecondStepBinding;

public class RegisterSecondStepActivity extends AppCompatActivity {
    ActivityRegisterSecondStepBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterSecondStepBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnRegister.setOnClickListener(view -> {
            Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
        });
    }
}