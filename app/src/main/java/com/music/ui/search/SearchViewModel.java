package com.music.ui.search;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.QuerySnapshot;
import com.music.models.Song;
import com.music.network.Resource;
import com.music.repositories.SongRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SearchViewModel extends ViewModel {
    @NonNull
    private final SongRepository songRepository;

    private MutableLiveData<Resource<List<Song>>> songs;

    @Inject
    public SearchViewModel(@NonNull SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    public void searchSongByName(@NonNull String name) {
        songs.postValue(Resource.loading("Đang tìm kiếm bài hát có từ khóa: " + name));

        songRepository.searchSongByName(name).addOnCompleteListener(task -> {
            QuerySnapshot result = task.getResult();

            if (!task.isSuccessful() || result == null || result.isEmpty()) {
                songs.postValue(Resource.error("Tìm kiếm bài hát có từ khóa: " + name + " thất bại", null));
            } else {
                songs.postValue(Resource.success(result.toObjects(Song.class)));
            }
        });
    }

    @NonNull
    public LiveData<Resource<List<Song>>> getSongs() {
        if (songs == null) {
            songs = new MutableLiveData<>();
        }

        return songs;
    }
}