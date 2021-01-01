package com.music.repositories;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.music.models.Collection;

public class AlbumRepository {
    private final CollectionReference collection;

    public AlbumRepository() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        collection = database.collection("collections");
    }

    @NonNull
    public Task<Collection> getRecommendAlbums() {
        return collection.whereEqualTo("name", "Album Slider").limit(1).get()
                // Map document sang class Collection
                .continueWith(task -> {
                    QuerySnapshot result = task.getResult();

                    if (!task.isSuccessful() || result.isEmpty()) {
                        return new Collection();
                    }

                    return result.getDocuments().get(0).toObject(Collection.class);
                });
    }
}
