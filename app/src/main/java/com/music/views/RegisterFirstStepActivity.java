package com.music.views;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.music.databinding.ActivityRegisterFirstStepBinding;

public class RegisterFirstStepActivity extends AppCompatActivity {
    ActivityRegisterFirstStepBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterFirstStepBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnNext.setOnClickListener(view -> {
            Intent registerSecondStepIntent = new Intent(this, RegisterSecondStepActivity.class);

            Bundle bundle = new Bundle();
            bundle.putString("fullName", binding.edtFullName.getText().toString());
            bundle.putString("email", binding.edtEmail.getText().toString());
            registerSecondStepIntent.putExtras(bundle);

            startActivity(registerSecondStepIntent);
        });
    }
}