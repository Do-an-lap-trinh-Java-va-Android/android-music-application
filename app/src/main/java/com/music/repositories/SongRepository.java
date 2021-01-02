package com.music.repositories;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.music.models.Song;

import java.util.List;
import java.util.Objects;

public class SongRepository extends Repository {
    private static class SingletonHelper {
        @NonNull
        private static final SongRepository INSTANCE = new SongRepository();
    }

    private SongRepository() {

    }

    @NonNull
    public static SongRepository getInstance() {
        return SingletonHelper.INSTANCE;
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
