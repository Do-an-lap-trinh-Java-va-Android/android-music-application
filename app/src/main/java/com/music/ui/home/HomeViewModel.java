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
    private final SongRepository songRepository;

    @NonNull
    private final AlbumRepository albumRepository;

    @NonNull
    private MutableLiveData<Resource<List<Song>>> topSongList = new MutableLiveData<>();

    @NonNull
    private MutableLiveData<Resource<List<Album>>> albumSlider = new MutableLiveData<>();

    @ViewModelInject
    public HomeViewModel(@NonNull SongRepository songRepository, @NonNull AlbumRepository albumRepository) {
        this.songRepository = songRepository;
        this.albumRepository = albumRepository;
    }

    @NonNull
    public LiveData<Resource<List<Song>>> getTopSongList() {
        return topSongList;
    }

    @NonNull
    public LiveData<Resource<List<Album>>> getAlbumSlider() {
        return albumSlider;
    }

    public void fetchTopSongs() {
        topSongList = (MutableLiveData<Resource<List<Song>>>) songRepository.getTopSongs();
    }

    public void fetchCollectionAlbumSlider() {
        albumSlider = (MutableLiveData<Resource<List<Album>>>) albumRepository.getRecommendAlbums();
    }
}
