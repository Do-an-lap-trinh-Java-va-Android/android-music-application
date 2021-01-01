package com.music.repositories;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.FirebaseFirestore;

public abstract class Repository {
    @NonNull
    protected FirebaseFirestore database;

    public Repository() {
        database = FirebaseFirestore.getInstance();
    }
}
