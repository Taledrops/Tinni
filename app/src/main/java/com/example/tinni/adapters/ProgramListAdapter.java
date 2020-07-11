package com.example.tinni.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tinni.databinding.SelectedProgramItemBinding;
import com.example.tinni.models.SelectedProgram;

import java.util.List;

/**
 * <h1>SelectedProgram Adapter</h1>
 * Adapter for SelectedProgram items
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

public class ProgramListAdapter extends RecyclerView.Adapter<ProgramListAdapter.ProgramViewHolder>
{
    public List<SelectedProgram> programList;

    public ProgramListAdapter(List<SelectedProgram> programList)
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
        SelectedProgramItemBinding itemBinding = SelectedProgramItemBinding.inflate(layoutInflater, parent, false);
        return new ProgramViewHolder(itemBinding);
    }

    /**
     * <h2>On Bind View Holder</h2>
     * Overriding and binding the view holder
     */

    @Override
    public void onBindViewHolder(@NonNull ProgramViewHolder holder, int position)
    {
        SelectedProgram category = programList.get(position);
        holder.bind(category);
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
        private SelectedProgramItemBinding binding;

        public ProgramViewHolder(SelectedProgramItemBinding binding)
        {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(SelectedProgram SelectedProgram)
        {
            binding.setModel(SelectedProgram);
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
     * <h2>Update List</h2>
     * Updates the ui
     */

    public void updateList()
    {
        this.notifyDataSetChanged();
    }

    /**
     * <h2>Add Item</h2>
     * Add sound_item_horizontal to the programList with its new index
     */

    public void addItem(SelectedProgram sound)
    {
        if (programList != null)
        {
            programList.add(0, sound);
            notifyItemInserted(0);
        }
    }

    /**
     * <h2>Remove Item</h2>
     * Remove sound_item_horizontal from the programList
     */

    public void removeItem(SelectedProgram SelectedProgram)
    {
        if (programList != null)
        {
            int position = programList.indexOf(SelectedProgram);
            programList.remove(SelectedProgram);
            notifyItemRemoved(position);
        }
    }
}