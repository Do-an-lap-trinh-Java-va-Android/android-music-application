package com.music.views;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.music.databinding.ActivityRegisterSecondStepBinding;

public class RegisterSecondStepActivity extends AppCompatActivity {
    ActivityRegisterSecondStepBinding binding;

    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterSecondStepBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        binding.btnRegister.setOnClickListener(view -> register());
    }

    private void register() {
        final Bundle bundle = getIntent().getExtras();

        final String fullName = bundle.getString("fullName");
        final String email = bundle.getString("email");
        final String password = binding.edtPassword.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();

                    if (firebaseUser == null) {
                        return;
                    }

                    UserProfileChangeRequest profileChangeRequest =
                            new UserProfileChangeRequest.Builder().setDisplayName(fullName).build();

                    firebaseUser.updateProfile(profileChangeRequest)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                    Log.d("Error", "Đăng ký thất bại", e);
                });
    }
}