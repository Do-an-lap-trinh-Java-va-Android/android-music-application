package com.music.ui.search;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.music.databinding.SearchItemLayoutBinding;
import com.music.models.Artist;

import java.util.List;

public class ArtistVerticalRecycleView extends RecyclerView.Adapter<ArtistVerticalRecycleView.ArtistVerticalViewHolder> {
    @NonNull
    private List<Artist> artists;

    public ArtistVerticalRecycleView(@NonNull List<Artist> artists) {
        this.artists = artists;
    }

    @NonNull
    @Override
    public ArtistVerticalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ArtistVerticalViewHolder(
                SearchItemLayoutBinding.inflate(
                        LayoutInflater.from(parent.getContext()), parent, false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistVerticalViewHolder holder, int position) {
        holder.setArtist(artists.get(position));
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    static class ArtistVerticalViewHolder extends RecyclerView.ViewHolder {
        @NonNull
        private final SearchItemLayoutBinding binding;

        public ArtistVerticalViewHolder(@NonNull SearchItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setArtist(@NonNull Artist artist) {
            Glide.with(binding.getRoot()).load(artist.getThumbnail()).into(binding.thumbnail);
            binding.name.setText(artist.getName());
        }
    }
}
