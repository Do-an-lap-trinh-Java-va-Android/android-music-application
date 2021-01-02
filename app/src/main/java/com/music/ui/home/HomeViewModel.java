package com.music.ui.home;

import androidx.annotation.NonNull;
import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.music.models.Album;
import com.music.models.Song;
import com.music.repositories.AlbumRepository;
import com.music.repositories.SongRepository;

import java.util.Collections;
import java.util.List;

public class HomeViewModel extends ViewModel {
    @NonNull
    private final SongRepository songRepository;

    @NonNull
    private final AlbumRepository albumRepository;

    @NonNull
    private final MutableLiveData<List<Song>> topSongList = new MutableLiveData<>(Collections.emptyList());

    @NonNull
    private final MutableLiveData<List<Album>> albumSlider = new MutableLiveData<>(Collections.emptyList());

    @ViewModelInject
    public HomeViewModel(@NonNull SongRepository songRepository, @NonNull AlbumRepository albumRepository) {
        this.songRepository = songRepository;
        this.albumRepository = albumRepository;
    }

    @NonNull
    public LiveData<List<Song>> getTopSongList() {
        return topSongList;
    }

    @NonNull
    public MutableLiveData<List<Album>> getAlbumSlider() {
        return albumSlider;
    }

    public void fetchTopSongs() {
        songRepository.getTopSongs().addOnSuccessListener(topSongList::postValue);
    }

    public void fetchCollectionAlbumSlider() {
        albumRepository.getRecommendAlbums().addOnSuccessListener(collection -> {
            albumSlider.postValue(collection.getAlbums());
        });
    }
}
