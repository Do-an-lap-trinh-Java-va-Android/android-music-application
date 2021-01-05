package com.music.ui.chart;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.music.databinding.ChartSongContainerBinding;
import com.music.models.Song;

import java.util.List;

public class SongChartHorizontalAdapter extends RecyclerView.Adapter<SongChartHorizontalAdapter.SongChartHorizontalViewHolder> {
    @NonNull
    private final List<Song> songs;

    public SongChartHorizontalAdapter(@NonNull List<Song> songs) {
        this.songs = songs;
    }

    @NonNull
    @Override
    public SongChartHorizontalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SongChartHorizontalViewHolder(
                ChartSongContainerBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull SongChartHorizontalViewHolder holder, int position) {
        holder.bindData(songs.get(position));
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    static class SongChartHorizontalViewHolder extends RecyclerView.ViewHolder {
        @NonNull
        private final ChartSongContainerBinding binding;

        public SongChartHorizontalViewHolder(@NonNull ChartSongContainerBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }

        public void bindData(@NonNull Song song) {
            Glide.with(itemView).load(song.getThumbnail()).into(binding.thumbnail);
            binding.name.setText(song.getName());
            binding.artists.setText(TextUtils.join(", ", song.getArtists()));
        }
    }
}
