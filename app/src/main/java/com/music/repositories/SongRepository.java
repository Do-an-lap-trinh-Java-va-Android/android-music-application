package com.music.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.music.models.Song;
import com.music.network.Resource;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SongRepository {
    private static final String TAG = "SongRepository";

    @NonNull
    private final FirebaseFirestore database;

    @Inject
    public SongRepository(@NonNull FirebaseFirestore database) {
        this.database = database;
    }

    @NonNull
    public LiveData<Resource<List<Song>>> getTopSongs() {
        final MutableLiveData<Resource<List<Song>>> resource = new MutableLiveData<>();

        Log.i(TAG, "getTopSongs: Đang tải bảng xếp hạng bài hát");
        resource.postValue(Resource.loading("Đang tải bảng xếp hạng bài hát"));

        database.collection("songs")
                .orderBy("views", Query.Direction.DESCENDING)
                .limit(6)
                .get()
                .addOnCompleteListener(task -> {
                    QuerySnapshot result = task.getResult();

                    if (!task.isSuccessful() || result == null || result.isEmpty()) {
                        Log.e(TAG, "getTopSongs: Tải bảng xếp hạng bài hát thất bại", task.getException());
                        resource.postValue(Resource.error("Không thể tải danh sách bảng xếp hạng", null));
                    } else {
                        Log.i(TAG, "getTopSongs: Tải bảng xếp hạng bài hát thành công");
                        resource.postValue(Resource.success(result.toObjects(Song.class)));
                    }
                });

        return resource;
    }
}
