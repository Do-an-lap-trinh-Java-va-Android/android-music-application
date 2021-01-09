package com.music.ui.home;

import androidx.annotation.NonNull;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.music.models.Album;
import com.music.models.Song;
import com.music.network.Resource;
import com.music.repositories.AlbumRepository;
import com.music.repositories.SongRepository;

import java.util.List;

public class HomeViewModel extends ViewModel {
    @NonNull
    private final MutableLiveData<Resource<List<Album>>> albumSlider;

    @NonNull
    private final MutableLiveData<Resource<List<Song>>> topSongList;

    @ViewModelInject
    public HomeViewModel(@NonNull SongRepository songRepository, @NonNull AlbumRepository albumRepository) {
        // Lấy danh album để làm slider
        albumSlider = (MutableLiveData<Resource<List<Album>>>) albumRepository.getRecommendAlbums();

        // Lấy 6 bài hát có lượt nghe cao nhất
        topSongList = (MutableLiveData<Resource<List<Song>>>) songRepository.getTopSongs(6);
    }

    @NonNull
    public LiveData<Resource<List<Song>>> getTopSongList() {
        return topSongList;
    }

    @NonNull
    public LiveData<Resource<List<Album>>> getAlbumSlider() {
        return albumSlider;
    }
}
