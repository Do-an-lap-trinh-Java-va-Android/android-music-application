package com.music.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.music.databinding.ActivityRegisterControlBinding;

public class RegisterControlActivity extends AppCompatActivity {
    private ActivityRegisterControlBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityRegisterControlBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.tvCreateANewAccount.setOnClickListener(v -> {
            Intent registerFirstStepIntent = new Intent(this, RegisterActivity.class);
            startActivity(registerFirstStepIntent);
        });
    }
}