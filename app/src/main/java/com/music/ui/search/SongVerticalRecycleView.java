package com.music.ui.search;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.music.databinding.SearchItemLayoutBinding;
import com.music.models.Song;

import java.util.List;

public class SongVerticalRecycleView extends RecyclerView.Adapter<SongVerticalRecycleView.SongVerticalViewHolder> {
    @NonNull
    private final List<Song> songs;

    public SongVerticalRecycleView(@NonNull List<Song> songs) {
        this.songs = songs;
    }

    @NonNull
    @Override
    public SongVerticalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SongVerticalViewHolder(
                SearchItemLayoutBinding.inflate(
                        LayoutInflater.from(parent.getContext()), parent, false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull SongVerticalViewHolder holder, int position) {
        holder.setSong(songs.get(position));
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    static class SongVerticalViewHolder extends RecyclerView.ViewHolder {
        @NonNull
        private final SearchItemLayoutBinding binding;

        public SongVerticalViewHolder(@NonNull SearchItemLayoutBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }

        public void setSong(@NonNull Song song) {
            Glide.with(binding.getRoot()).load(song.getThumbnail()).into(binding.thumbnail);
            binding.name.setText(song.getName());
            binding.artists.setText(song.getArtistsNames());
        }
    }
}
