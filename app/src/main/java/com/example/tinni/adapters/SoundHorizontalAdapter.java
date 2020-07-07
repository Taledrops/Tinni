package com.example.tinni.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tinni.databinding.SoundItemBinding;
import com.example.tinni.databinding.SoundItemHorizontalBinding;
import com.example.tinni.models.Sound;

import java.util.List;

/**
 * <h1>Sound Horizontal Adapter</h1>
 * Adapter for horizontal Sound items
 *
 * Variables:
 * List<Sound> soundList: A list of Sound objects to display
 *
 * Source:
 * https://stackoverflow.com/a/48941212/2700965
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   07.07.2020
 */

public class SoundHorizontalAdapter extends RecyclerView.Adapter<SoundHorizontalAdapter.SoundViewHolder>
{
    public List<Sound> soundList;

    public SoundHorizontalAdapter(List<Sound> soundList)
    {
        this.soundList = soundList;
    }

    /**
     * <h2>On Create View Holder</h2>
     * Overriding and creating the view holder
     */

    @NonNull
    @Override
    public SoundViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        SoundItemHorizontalBinding itemBinding = SoundItemHorizontalBinding.inflate(layoutInflater, parent, false);
        return new SoundViewHolder(itemBinding);
    }

    /**
     * <h2>On Bind View Holder</h2>
     * Overriding and binding the view holder
     */

    @Override
    public void onBindViewHolder(@NonNull SoundViewHolder holder, int position)
    {
        Sound sound = soundList.get(position);
        holder.bind(sound);
    }

    /**
     * <h2>Get Item Count</h2>
     * Returns the sound_item_horizontal count of the soundList
     */

    @Override
    public int getItemCount()
    {
        return soundList != null ? soundList.size() : 0;
    }

    static class SoundViewHolder extends RecyclerView.ViewHolder
    {
        private SoundItemHorizontalBinding binding;

        public SoundViewHolder(SoundItemHorizontalBinding binding)
        {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Sound Sound)
        {
            binding.setModel(Sound);
            binding.executePendingBindings();
        }
    }

    /**
     * <h2>Get Item</h2>
     * Get sound_item_horizontal inside the soundList by its index
     */

    public Sound getItem(int position)
    {
        if (soundList != null && getItemCount() > position)
        {
            return soundList.get(position);
        }
        else
        {
            return null;
        }
    }

    /**
     * <h2>Reload List</h2>
     * Reload the list and update the ui
     */

    public void reloadList(List<Sound> newlist)
    {
        soundList.clear();
        soundList.addAll(newlist);
        this.notifyDataSetChanged();
    }

    /**
     * <h2>Add Item</h2>
     * Add sound_item_horizontal to the soundList with its new index
     */

    public void addItem(Sound sound)
    {
        if (soundList != null)
        {
            soundList.add(0, sound);
            notifyItemInserted(0);
        }
    }

    /**
     * <h2>Remove Item</h2>
     * Remove sound_item_horizontal from the soundList
     */

    public void removeItem(Sound sound)
    {
        if (soundList != null)
        {
            int position = soundList.indexOf(sound);
            soundList.remove(sound);
            notifyItemRemoved(position);
        }
    }
}