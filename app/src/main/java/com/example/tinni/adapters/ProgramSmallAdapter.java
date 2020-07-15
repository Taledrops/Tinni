package com.example.tinni.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tinni.databinding.ProgramSmallItemBinding;
import com.example.tinni.models.SelectedProgram;

import java.util.List;

/**
 * <h1>SelectedProgram Small Adapter</h1>
 * Adapter for small SelectedProgram items
 *
 * Variables:
 * List<SelectedProgram> programList: A list of SelectedProgram objects to display
 *
 * Source:
 * https://stackoverflow.com/a/48941212/2700965
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   27.06.2020
 */

public class ProgramSmallAdapter extends RecyclerView.Adapter<ProgramSmallAdapter.ProgramViewHolder>
{
    public List<SelectedProgram> programList;

    public ProgramSmallAdapter(List<SelectedProgram> programList)
    {
        this.programList = programList;
    }

    /**
     * <h2>On Create View Holder</h2>
     * Overriding and creating the view holder
     */

    @NonNull
    @Override
    public ProgramViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ProgramSmallItemBinding itemBinding = ProgramSmallItemBinding.inflate(layoutInflater, parent, false);
        return new ProgramViewHolder(itemBinding);
    }

    /**
     * <h2>On Bind View Holder</h2>
     * Overriding and binding the view holder
     */

    @Override
    public void onBindViewHolder(@NonNull ProgramViewHolder holder, int position)
    {
        SelectedProgram selectedProgram = programList.get(position);
        holder.bind(selectedProgram);
    }

    /**
     * <h2>Get Item Count</h2>
     * Returns the sound_item_horizontal count of the programList
     */

    @Override
    public int getItemCount()
    {
        return programList != null ? programList.size() : 0;
    }

    static class ProgramViewHolder extends RecyclerView.ViewHolder
    {
        private ProgramSmallItemBinding binding;

        public ProgramViewHolder(ProgramSmallItemBinding binding)
        {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(SelectedProgram program)
        {
            binding.setModel(program);
            binding.executePendingBindings();
        }
    }

    /**
     * <h2>Get Item</h2>
     * Get sound_item_horizontal inside the programList by its index
     */

    public SelectedProgram getItem(int position)
    {
        if (programList != null && getItemCount() > position)
        {
            return programList.get(position);
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

    public void reloadList(List<SelectedProgram> newlist)
    {
        programList.clear();
        programList.addAll(newlist);
        this.notifyDataSetChanged();
    }

    /**
     * <h2>Add Item</h2>
     * Add sound_item_horizontal to the programList with its new index
     */

    public void addItem(SelectedProgram selectedProgram)
    {
        if (programList != null)
        {
            programList.add(0, selectedProgram);
            notifyItemInserted(0);
        }
    }

    /**
     * <h2>Remove Item</h2>
     * Remove sound_item_horizontal from the programList
     */

    public void removeItem(SelectedProgram program)
    {
        if (programList != null)
        {
            int position = programList.indexOf(program);
            programList.remove(program);
            notifyItemRemoved(position);
        }
    }
}