package com.example.tinni.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tinni.databinding.AddAnswerItemBinding;
import com.example.tinni.helpers.ItemMoveCallback;
import com.example.tinni.models.Answer;
import com.example.tinni.models.Session;

import java.util.Collections;
import java.util.List;

/**
 * <h1>Add Answers Adapter</h1>
 * Adapter for Answer items in the add form
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

public class AddAnswersAdapter extends RecyclerView.Adapter<AddAnswersAdapter.AnswerViewHolder> implements ItemMoveCallback.ItemTouchHelperContract
{
    public List<Answer> answerList;

    public AddAnswersAdapter(List<Answer> answerList)
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
        AddAnswerItemBinding itemBinding = AddAnswerItemBinding.inflate(layoutInflater, parent, false);
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

    public static class AnswerViewHolder extends RecyclerView.ViewHolder
    {
        private AddAnswerItemBinding binding;

        public AnswerViewHolder(AddAnswerItemBinding binding)
        {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Answer program)
        {
            binding.setModel(program);
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

    public void reloadList()
    {
        this.notifyDataSetChanged();
    }

    /**
     * <h2>Remove Item</h2>
     * Remove sound_item_horizontal from the answerList
     */

    public void removeItem(Answer answer)
    {
        if (answerList != null)
        {
            int position = answerList.indexOf(answer);
            answerList.remove(answer);
            notifyItemRemoved(position);
        }
    }

    /**
     * <h2>Add Item</h2>
     * Add sound_item_horizontal to the answerList with its new index
     */

    public void addItem(Answer answer)
    {
        if (answerList != null)
        {
            answerList.add(answer);
            notifyItemInserted(answerList.size() - 1);
        }
    }

    @Override
    public void onBindViewHolderSession(SelectedSessionAdapter.SessionViewHolder holder, int position)
    {

    }

    @Override
    public void onRowMovedSession(int fromPosition, int toPosition)
    {

    }

    @Override
    public void onRowSelectedSession(SelectedSessionAdapter.SessionViewHolder myViewHolder)
    {

    }

    @Override
    public void onRowClearSession(SelectedSessionAdapter.SessionViewHolder myViewHolder)
    {

    }

    @Override
    public void onBindViewHolderQuestions(AddQuestionsAdapter.QuestionViewHolder holder, int position)
    {

    }

    @Override
    public void onRowMovedQuestions(int fromPosition, int toPosition)
    {

    }

    @Override
    public void onRowSelectedQuestions(AddQuestionsAdapter.QuestionViewHolder myViewHolder)
    {

    }

    @Override
    public void onRowClearQuestions(AddQuestionsAdapter.QuestionViewHolder myViewHolder)
    {

    }

    @Override
    public void onBindViewHolderAnswers(AnswerViewHolder holder, int position)
    {

    }

    @Override
    public void onRowMovedAnswers(int fromPosition, int toPosition)
    {
        if (fromPosition < toPosition)
        {
            for (int i = fromPosition; i < toPosition; i++)
            {
                Collections.swap(answerList, i, i + 1);
            }
        }
        else
        {
            for (int i = fromPosition; i > toPosition; i--)
            {
                Collections.swap(answerList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onRowSelectedAnswers(AnswerViewHolder myViewHolder)
    {

    }

    @Override
    public void onRowClearAnswers(AnswerViewHolder myViewHolder)
    {

    }
}