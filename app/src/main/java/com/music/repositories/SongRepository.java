package com.music.repositories;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.music.models.Song;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SongRepository {
    @NonNull
    private final FirebaseFirestore database;

    @Inject
    public SongRepository(@NonNull FirebaseFirestore database) {
        this.database = database;
    }

    @NonNull
    public Task<List<Song>> getTopSongs() {
        return database.collection("songs")
                .orderBy("views", Query.Direction.DESCENDING)
                .limit(6)
                .get()
                .continueWith(task -> {
                    return Objects.requireNonNull(task.getResult()).toObjects(Song.class);
                });
    }
}
