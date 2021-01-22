package com.music.repositories;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ArtistRepository {
    private static final String TAG = "ArtistRepository";

    @NonNull
    private final FirebaseFirestore database;

    @Inject
    public ArtistRepository(@NonNull FirebaseFirestore database) {
        this.database = database;
    }

    public Task<QuerySnapshot> searchByName(@NonNull String name) {
        return database.collection("artists").whereGreaterThanOrEqualTo("name", name).limit(8).get();
    }
}
