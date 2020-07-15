package com.example.tinni.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tinni.databinding.QuestionnaireItemBinding;
import com.example.tinni.models.Questionnaire;

import java.util.List;

/**
 * <h1>Question Adapter</h1>
 * Adapter for Questionnaire items
 *
 * Variables:
 * List<Questionnaire> questionList: A list of Questionnaire objects to display
 *
 * Source:
 * https://stackoverflow.com/a/48941212/2700965
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   29.06.2020
 */

public class QuestionnaireAdapter extends RecyclerView.Adapter<QuestionnaireAdapter.QuestionViewHolder>
{
    public List<Questionnaire> questionList;

    public QuestionnaireAdapter(List<Questionnaire> questionList)
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
        QuestionnaireItemBinding itemBinding = QuestionnaireItemBinding.inflate(layoutInflater, parent, false);
        return new QuestionViewHolder(itemBinding);
    }

    /**
     * <h2>On Bind View Holder</h2>
     * Overriding and binding the view holder
     */

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position)
    {
        Questionnaire questionnaire = questionList.get(position);
        holder.bind(questionnaire);
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
        private QuestionnaireItemBinding binding;

        public QuestionViewHolder(QuestionnaireItemBinding binding)
        {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Questionnaire questionnaire)
        {
            binding.setModel(questionnaire);
            binding.executePendingBindings();
        }
    }

    /**
     * <h2>Get Item</h2>
     * Get sound_item_horizontal inside the questionList by its index
     */

    public Questionnaire getItem(int position)
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

    public void reloadList(List<Questionnaire> newlist)
    {
        questionList.clear();
        questionList.addAll(newlist);
        this.notifyDataSetChanged();
    }

    /**
     * <h2>Add Item</h2>
     * Add sound_item_horizontal to the questionList with its new index
     */

    public void addItem(Questionnaire questionnaire, int position)
    {
        if (questionList != null)
        {
            questionList.add(position, questionnaire);
            notifyItemInserted(position);
        }
    }
}