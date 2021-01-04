package com.music.ui.chart;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.music.databinding.ChartSongContainerBinding;
import com.music.models.Song;

import java.util.List;

public class ChartAdapter extends RecyclerView.Adapter<ChartAdapter.ChartViewHolder> {
    @NonNull
    private List<Song> songs;

    public ChartAdapter(@NonNull List<Song> songs) {
        this.songs = songs;
    }

    @NonNull
    @Override
    public ChartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChartViewHolder(
                ChartSongContainerBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ChartViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    static class ChartViewHolder extends RecyclerView.ViewHolder {
        public ChartViewHolder(@NonNull ChartSongContainerBinding binding) {
            super(binding.getRoot());
        }
    }
}
