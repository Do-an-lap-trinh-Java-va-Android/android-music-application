package com.music.adapters.song;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.music.databinding.CardImageContainerBinding;
import com.music.models.Song;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    @NonNull
    private final List<Song> songs;

    public SongAdapter(@NonNull List<Song> songs) {
        this.songs = songs;
    }

    @NonNull
    @Override
    public SongAdapter.SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SongAdapter.SongViewHolder(
                CardImageContainerBinding.inflate(
                        LayoutInflater.from(parent.getContext()), parent, false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull SongAdapter.SongViewHolder holder, int position) {
        holder.setSong(songs.get(position));
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    static class SongViewHolder extends RecyclerView.ViewHolder {
        @NonNull
        private final CardImageContainerBinding binding;

        public SongViewHolder(@NonNull CardImageContainerBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }

        public void setSong(@NonNull Song song) {
            binding.tvTitle.setText(song.getName());
            binding.tvDescription.setText(StringUtils.join(song.getArtists(), ", "));

            Glide.with(itemView).load(song.getThumbnail()).into(binding.ivImage);
        }
    }
}
