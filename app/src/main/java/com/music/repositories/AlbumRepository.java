package com.music.repositories;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;
import com.music.models.Collection;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AlbumRepository extends Repository {
    @Inject
    public AlbumRepository() {

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
