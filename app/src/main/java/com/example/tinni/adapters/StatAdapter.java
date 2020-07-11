package com.example.tinni.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tinni.databinding.StatItemBinding;
import com.example.tinni.models.Stat;

import java.util.List;

/**
 * <h1>Stat Adapter</h1>
 * Adapter for stat items
 *
 * Variables:
 * List<Stat> statList: A list of Stat objects to display
 *
 * Source:
 * https://stackoverflow.com/a/48941212/2700965
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   09.07.2020
 */

public class StatAdapter extends RecyclerView.Adapter<StatAdapter.StatViewHolder>
{
    public List<Stat> statList;

    public StatAdapter(List<Stat> statList)
    {
        this.statList = statList;
    }

    /**
     * <h2>On Create View Holder</h2>
     * Overriding and creating the view holder
     */

    @NonNull
    @Override
    public StatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        StatItemBinding itemBinding = StatItemBinding.inflate(layoutInflater, parent, false);
        return new StatViewHolder(itemBinding);
    }

    /**
     * <h2>On Bind View Holder</h2>
     * Overriding and binding the view holder
     */

    @Override
    public void onBindViewHolder(@NonNull StatViewHolder holder, int position)
    {
        Stat category = statList.get(position);
        holder.bind(category);
    }

    /**
     * <h2>Get Item Count</h2>
     * Returns the count of the statList
     */

    @Override
    public int getItemCount()
    {
        return statList != null ? statList.size() : 0;
    }

    static class StatViewHolder extends RecyclerView.ViewHolder
    {
        private StatItemBinding binding;

        public StatViewHolder(StatItemBinding binding)
        {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Stat stat)
        {
            binding.setModel(stat);
            binding.executePendingBindings();
        }
    }

    /**
     * <h2>Get Item</h2>
     * Get item inside the statList by its index
     */

    public Stat getItem(int position)
    {
        if (statList != null && getItemCount() > position)
        {
            return statList.get(position);
        }
        else
        {
            return null;
        }
    }
}
