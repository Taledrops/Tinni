package com.example.tinni.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tinni.databinding.AnswerItemBinding;
import com.example.tinni.models.Answer;

import java.util.List;

/**
 * <h1>Answer Adapter</h1>
 * Adapter for Answer items
 *
 * Variables:
 * List<Answer> answerList: A list of Answer objects to display
 *
 * Source:
 * https://stackoverflow.com/a/48941212/2700965
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   29.06.2020
 */

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.AnswerViewHolder>
{
    public List<Answer> answerList;

    public AnswerAdapter(List<Answer> answerList)
    {
        this.answerList = answerList;
    }

    /**
     * <h2>On Create View Holder</h2>
     * Overriding and creating the view holder
     */

    @NonNull
    @Override
    public AnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        AnswerItemBinding itemBinding = AnswerItemBinding.inflate(layoutInflater, parent, false);
        return new AnswerViewHolder(itemBinding);
    }

    /**
     * <h2>On Bind View Holder</h2>
     * Overriding and binding the view holder
     */

    @Override
    public void onBindViewHolder(@NonNull AnswerViewHolder holder, int position)
    {
        Answer category = answerList.get(position);
        holder.bind(category);
    }

    /**
     * <h2>Get Item Count</h2>
     * Returns the sound_item_horizontal count of the answerList
     */

    @Override
    public int getItemCount()
    {
        return answerList != null ? answerList.size() : 0;
    }

    static class AnswerViewHolder extends RecyclerView.ViewHolder
    {
        private AnswerItemBinding binding;

        public AnswerViewHolder(AnswerItemBinding binding)
        {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Answer answer)
        {
            binding.setModel(answer);
            binding.executePendingBindings();
        }
    }

    /**
     * <h2>Get Item</h2>
     * Get sound_item_horizontal inside the answerList by its index
     */

    public Answer getItem(int position)
    {
        if (answerList != null && getItemCount() > position)
        {
            return answerList.get(position);
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

    public void reloadList(List<Answer> newlist)
    {
        answerList.clear();
        answerList.addAll(newlist);
        this.notifyDataSetChanged();
    }

    /**
     * <h2>Add Item</h2>
     * Add sound_item_horizontal to the answerList with its new index
     */

    public void addItem(Answer sound, int position)
    {
        if (answerList != null)
        {
            answerList.add(position, sound);
            notifyItemInserted(position);
        }
    }
}