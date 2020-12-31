package com.music.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.music.R;
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
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.slide_image_container, parent, false)
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
        private final TextView mTitleTextView;
        private final TextView mDescriptionTextView;
        private final ImageView mPosterImageView;

        private ImageSliderViewHolder(@NonNull View itemView) {
            super(itemView);

            mTitleTextView = itemView.findViewById(R.id.tv_title);
            mDescriptionTextView = itemView.findViewById(R.id.tv_description);
            mPosterImageView = itemView.findViewById(R.id.iv_poster);
        }

        private void setData(Album album) {
            mTitleTextView.setText(album.getName());
            mDescriptionTextView.setText(album.getDescription());
            Glide.with(itemView).load(album.getCover()).into(mPosterImageView);
        }
    }
}
