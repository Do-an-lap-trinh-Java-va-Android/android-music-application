package com.music.ui.home;

import androidx.annotation.NonNull;
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
    private final MutableLiveData<List<Song>> topSongList;

    @NonNull
    private final MutableLiveData<List<Album>> albumSlider;


    public HomeViewModel() {
        songRepository = SongRepository.getInstance();
        albumRepository = AlbumRepository.getInstance();
        topSongList = new MutableLiveData<>(Collections.emptyList());
        albumSlider = new MutableLiveData<>(Collections.emptyList());
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
