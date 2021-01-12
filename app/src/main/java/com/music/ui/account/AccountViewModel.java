package com.music.ui.account;

import androidx.annotation.NonNull;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class AccountViewModel extends ViewModel {
    @NonNull
    private final FirebaseUser currentUser;

    @ViewModelInject
    public AccountViewModel(@NonNull FirebaseAuth firebaseAuth) {
        currentUser = Objects.requireNonNull(firebaseAuth.getCurrentUser());
    }

    @NonNull
    public FirebaseUser getCurrentUser() {
        return currentUser;
    }
}