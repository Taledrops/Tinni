package com.example.tinni.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tinni.databinding.QuestionItemBinding;
import com.example.tinni.models.Question;

import java.util.List;

/**
 * <h1>Question Adapter</h1>
 * Adapter for Question items
 *
 * Variables:
 * List<Question> questionList: A list of Question objects to display
 *
 * Source:
 * https://stackoverflow.com/a/48941212/2700965
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   29.06.2020
 */

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder>
{
    public List<Question> questionList;

    public QuestionAdapter(List<Question> questionList)
    {
        this.questionList = questionList;
    }

    /**
     * <h2>On Create View Holder</h2>
     * Overriding and creating the view holder
     */

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        QuestionItemBinding itemBinding = QuestionItemBinding.inflate(layoutInflater, parent, false);
        return new QuestionViewHolder(itemBinding);
    }

    /**
     * <h2>On Bind View Holder</h2>
     * Overriding and binding the view holder
     */

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position)
    {
        Question category = questionList.get(position);
        holder.bind(category);
    }

    /**
     * <h2>Get Item Count</h2>
     * Returns the sound_item_horizontal count of the questionList
     */

    @Override
    public int getItemCount()
    {
        return questionList != null ? questionList.size() : 0;
    }

    static class QuestionViewHolder extends RecyclerView.ViewHolder
    {
        private QuestionItemBinding binding;

        public QuestionViewHolder(QuestionItemBinding binding)
        {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Question program)
        {
            binding.setModel(program);
            binding.executePendingBindings();
        }
    }

    /**
     * <h2>Get Item</h2>
     * Get sound_item_horizontal inside the questionList by its index
     */

    public Question getItem(int position)
    {
        if (questionList != null && getItemCount() > position)
        {
            return questionList.get(position);
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

    public void reloadList(List<Question> newlist)
    {
        questionList.clear();
        questionList.addAll(newlist);
        this.notifyDataSetChanged();
    }

    /**
     * <h2>Add Item</h2>
     * Add sound_item_horizontal to the questionList with its new index
     */

    public void addItem(Question sound, int position)
    {
        if (questionList != null)
        {
            questionList.add(position, sound);
            notifyItemInserted(position);
        }
    }
}