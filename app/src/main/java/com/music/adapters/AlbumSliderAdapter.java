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
    private final List<Album> mAlbums;

    @SuppressWarnings("FieldCanBeLocal")
    private final ViewPager2 mViewPager;

    public AlbumSliderAdapter(List<Album> albums, ViewPager2 viewPager) {
        mAlbums = albums;
        mViewPager = viewPager;
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
        holder.setData(mAlbums.get(position));
    }

    @Override
    public int getItemCount() {
        return mAlbums.size();
    }

    static class ImageSliderViewHolder extends RecyclerView.ViewHolder {
        private final SlideImageContainerBinding mBinding;

        private ImageSliderViewHolder(@NonNull SlideImageContainerBinding binding) {
            super(binding.getRoot());

            mBinding = binding;
        }

        private void setData(Album album) {
            mBinding.tvTitle.setText(album.getName());
            mBinding.tvDescription.setText(album.getDescription());
            Glide.with(itemView).load(album.getCover()).into(mBinding.ivCover);
        }
    }
}
