package com.example.tinni.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tinni.databinding.RatingItemBinding;
import com.example.tinni.databinding.RatingsItemBinding;
import com.example.tinni.models.Rating;

import java.util.List;

/**
 * <h1>Ratings Adapter</h1>
 * Adapter for rating items
 *
 * Variables:
 * List<Rating> ratingList: A list of Rating objects to display
 *
 * Source:
 * https://stackoverflow.com/a/48941212/2700965
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   09.07.2020
 */

public class RatingsAdapter extends RecyclerView.Adapter<RatingsAdapter.RatingViewHolder>
{
    public List<Rating> ratingList;

    public RatingsAdapter(List<Rating> ratingList)
    {
        this.ratingList = ratingList;
    }

    /**
     * <h2>On Create View Holder</h2>
     * Overriding and creating the view holder
     */

    @NonNull
    @Override
    public RatingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RatingsItemBinding itemBinding = RatingsItemBinding.inflate(layoutInflater, parent, false);
        return new RatingViewHolder(itemBinding);
    }

    /**
     * <h2>On Bind View Holder</h2>
     * Overriding and binding the view holder
     */

    @Override
    public void onBindViewHolder(@NonNull RatingViewHolder holder, int position)
    {
        Rating category = ratingList.get(position);
        holder.bind(category);
    }

    /**
     * <h2>Get Item Count</h2>
     * Returns the sound_item_horizontal count of the soundList
     */

    @Override
    public int getItemCount()
    {
        return ratingList != null ? ratingList.size() : 0;
    }

    static class RatingViewHolder extends RecyclerView.ViewHolder
    {
        private RatingsItemBinding binding;

        public RatingViewHolder(RatingsItemBinding binding)
        {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Rating rating)
        {
            binding.setModel(rating);
            binding.executePendingBindings();
        }
    }

    /**
     * <h2>Get Item</h2>
     * Get item inside the ratingList by its index
     */

    public Rating getItem(int position)
    {
        if (ratingList != null && getItemCount() > position)
        {
            return ratingList.get(position);
        }
        else
        {
            return null;
        }
    }
}
