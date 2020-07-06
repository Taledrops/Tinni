package com.example.tinni.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tinni.databinding.ProgramItemBinding;
import com.example.tinni.models.Program;
import com.example.tinni.models.Sound;

import java.util.List;

/**
 * <h1>Program Adapter</h1>
 * Adapter for Program items
 *
 * Variables:
 * List<Program> programList: A list of Program objects to display
 *
 * Source:
 * https://stackoverflow.com/a/48941212/2700965
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   27.06.2020
 */

public class ProgramAdapter extends RecyclerView.Adapter<ProgramAdapter.ProgramViewHolder>
{
    public List<Program> programList;

    public ProgramAdapter(List<Program> programList)
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
        ProgramItemBinding itemBinding = ProgramItemBinding.inflate(layoutInflater, parent, false);
        return new ProgramViewHolder(itemBinding);
    }

    /**
     * <h2>On Bind View Holder</h2>
     * Overriding and binding the view holder
     */

    @Override
    public void onBindViewHolder(@NonNull ProgramViewHolder holder, int position)
    {
        Program category = programList.get(position);
        holder.bind(category);
    }

    /**
     * <h2>Get Item Count</h2>
     * Returns the item count of the programList
     */

    @Override
    public int getItemCount()
    {
        return programList != null ? programList.size() : 0;
    }

    static class ProgramViewHolder extends RecyclerView.ViewHolder
    {
        private ProgramItemBinding binding;

        public ProgramViewHolder(ProgramItemBinding binding)
        {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Program program)
        {
            binding.setModel(program);
            binding.executePendingBindings();
        }
    }

    /**
     * <h2>Get Item</h2>
     * Get item inside the programList by its index
     */

    public Program getItem(int position)
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

    public void reloadList(List<Program> newlist)
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
     * Add item to the programList with its new index
     */

    public void addItem(Program sound)
    {
        if (programList != null)
        {
            programList.add(0, sound);
            notifyItemInserted(0);
        }
    }

    /**
     * <h2>Remove Item</h2>
     * Remove item from the programList
     */

    public void removeItem(Program program)
    {
        if (programList != null)
        {
            int position = programList.indexOf(program);
            programList.remove(program);
            notifyItemRemoved(position);
        }
    }
}