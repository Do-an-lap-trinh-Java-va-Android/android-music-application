package com.music.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.music.databinding.ActivityLoginBinding;
import com.music.ui.main.MainActivity;
import com.music.utils.ToolbarHelper;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ToolbarHelper.hideToolbar(this);

        firebaseAuth = FirebaseAuth.getInstance();

        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.edtFullname.getText().toString().trim();
            String password = binding.edtPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                binding.edtFullname.setError("không được bỏ trống");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                binding.edtPassword.setError("Bỏ trống là không được");
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this,
                        "Địa chỉ Email không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this,
                                    "Đăng nhập thành công",
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            binding.edtFullname.setError("Tài khoản hoặc mật khẩu không đúng");
                        }
                    });
        });
    }
}