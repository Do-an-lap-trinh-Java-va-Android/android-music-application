package com.music.repositories;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;
import com.music.models.Collection;

public class AlbumRepository extends Repository {
    private static class SingletonHelper {
        private final static AlbumRepository INSTANCE = new AlbumRepository();
    }

    private AlbumRepository() { }

    public static AlbumRepository getInstance() {
        return SingletonHelper.INSTANCE;
    }

    @NonNull
    public Task<Collection> getRecommendAlbums() {
        return database.collection("collections")
                .whereEqualTo("name", "Album Slider").limit(1).get()
                // Map document sang class Collection
                .continueWith(task -> {
                    QuerySnapshot result = task.getResult();

                    if (!task.isSuccessful() || result == null || result.isEmpty()) {
                        return new Collection();
                    }

                    return result.toObjects(Collection.class).get(0);
                });
    }
}
