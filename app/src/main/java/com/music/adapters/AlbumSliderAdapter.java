package com.music.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.music.databinding.SlideImageContainerBinding;
import com.music.models.Album;

import java.util.List;

public class AlbumSliderAdapter extends RecyclerView.Adapter<AlbumSliderAdapter.ImageSliderViewHolder> {
    private final List<Album> albums;

    @SuppressWarnings("FieldCanBeLocal")
    private final ViewPager2 viewPager;

    public AlbumSliderAdapter(@NonNull List<Album> albums, @NonNull ViewPager2 viewPager) {
        this.albums = albums;
        this.viewPager = viewPager;
    }

    @NonNull
    @Override
    public ImageSliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImageSliderViewHolder(
                SlideImageContainerBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ImageSliderViewHolder holder, int position) {
        holder.setData(albums.get(position));
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    static class ImageSliderViewHolder extends RecyclerView.ViewHolder {
        private final SlideImageContainerBinding binding;

        private ImageSliderViewHolder(@NonNull SlideImageContainerBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }

        private void setData(Album album) {
            binding.tvTitle.setText(album.getName());
            binding.tvDescription.setText(album.getDescription());
            Glide.with(itemView).load(album.getCover()).into(binding.ivCover);
        }
    }
}
