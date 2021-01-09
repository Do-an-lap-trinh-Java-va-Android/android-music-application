package com.music.activities;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.music.databinding.ActivityRegisterBinding;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    @SuppressWarnings("NotNullFieldNotInitialized")
    @NonNull
    private ActivityRegisterBinding binding;

    @NonNull
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnRegister.setOnClickListener(this::onClickBtnRegister);
    }

    private boolean validateFullName() {
        final String fullName = binding.edtFullName.getText().toString().trim();

        if (fullName.isEmpty()) {
            binding.edtFullName.setError("Không được để trống");
            return false;
        }

        return true;
    }

    private boolean validateEmail() {
        final EditText edtEmail = binding.edtEmail;
        final String email = edtEmail.getText().toString().trim();

        if (email.isEmpty()) {
            edtEmail.setError("Địa chỉ email không được để trống");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Địa chỉ email không hợp lệ");
            return false;
        }

        return true;
    }

    private boolean validatePassword() {
        final EditText edtPassword = binding.edtPassword;
        final EditText edtConfirmPassword = binding.edtConfirmPassword;
        final String password = edtPassword.getText().toString();
        final String confirmPassword = edtConfirmPassword.getText().toString();

        if (password.isEmpty()) {
            edtPassword.setError("Mật khẩu không được để trống");
            return false;
        }

        if (confirmPassword.isEmpty()) {
            edtConfirmPassword.setError("Mật khẩu không được để trống");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            edtConfirmPassword.setError("Mật khẩu nhập lại không khớp");
            return false;
        }

        return true;
    }

    private void onClickBtnRegister(View view) {
        if (!validateFullName() || !validateEmail() || !validatePassword()) {
            return;
        }

        binding.layoutLoading.frmLoading.setVisibility(View.VISIBLE);

        register(binding.edtEmail.getText().toString(), binding.edtPassword.getText().toString())
                .addOnSuccessListener(authResult -> {
                    updateUserProfile(binding.edtFullName.getText().toString())
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                                    alert.setMessage("Đăng ký thành công");
                                    alert.show();
                                } else {
                                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                                    alert.setMessage("Tài khoản hoặc mật khẩu không đúng");
                                    alert.show();
                                }

                                binding.layoutLoading.frmLoading.setVisibility(View.GONE);
                            });
                });

    }

    @NonNull
    private Task<AuthResult> register(@NonNull String email, @NonNull String password) {
        return auth.createUserWithEmailAndPassword(email, password);
    }

    @NonNull
    private Task<Void> updateUserProfile(@Nullable String displayName) {
        UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build();

        return Objects.requireNonNull(auth.getCurrentUser()).updateProfile(profile);
    }
}