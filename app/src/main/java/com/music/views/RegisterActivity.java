package com.music.views;

import android.os.Bundle;
import android.util.Log;
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

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding mBinding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        mBinding.btnRegister.setOnClickListener(this::onClickBtnRegister);
    }

    private boolean validateFullName() {
        final String fullName = mBinding.edtFullName.getText().toString().trim();

        if (fullName.isEmpty()) {
            mBinding.edtFullName.setError("Không được để trống");
            return false;
        }

        return true;
    }

    private boolean validateEmail() {
        final EditText edtEmail = mBinding.edtEmail;
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
        final EditText edtPassword = mBinding.edtPassword;
        final EditText edtConfirmPassword = mBinding.edtConfirmPassword;
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

        register(mBinding.edtEmail.getText().toString(), mBinding.edtPassword.getText().toString())
                .addOnSuccessListener(authResult -> {
                    updateUserProfile(mBinding.edtFullName.getText().toString())
                            .addOnSuccessListener(aVoid -> {
                                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                                alert.setMessage("Đăng ký thành công");
                                alert.show();
                            });

                    Log.d("Info", "Đăng ký thành công");
                });

    }

    private Task<AuthResult> register(@NonNull String email, @NonNull String password) {
        return mAuth.createUserWithEmailAndPassword(email, password);
    }

    private Task<Void> updateUserProfile(@Nullable String displayName) {
        UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build();

        return mAuth.getCurrentUser().updateProfile(profile);
    }
}