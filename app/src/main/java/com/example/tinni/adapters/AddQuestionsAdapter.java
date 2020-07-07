package com.example.tinni.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tinni.databinding.AddQuestionItemBinding;
import com.example.tinni.helpers.ItemMoveCallback;
import com.example.tinni.models.Question;
import com.example.tinni.models.Session;

import java.util.Collections;
import java.util.List;

/**
 * <h1>Add Questions Adapter</h1>
 * Adapter for Question items in the add form
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

public class AddQuestionsAdapter extends RecyclerView.Adapter<AddQuestionsAdapter.QuestionViewHolder> implements ItemMoveCallback.ItemTouchHelperContract
{
    public List<Question> questionList;

    public AddQuestionsAdapter(List<Question> questionList)
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
        AddQuestionItemBinding itemBinding = AddQuestionItemBinding.inflate(layoutInflater, parent, false);
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

    public static class QuestionViewHolder extends RecyclerView.ViewHolder
    {
        private AddQuestionItemBinding binding;

        public QuestionViewHolder(AddQuestionItemBinding binding)
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

    public void reloadList()
    {
        this.notifyDataSetChanged();
    }

    /**
     * <h2>Add Item</h2>
     * Add sound_item_horizontal to the questionList with its new index
     */

    public void addItem(Question question)
    {
        if (questionList != null)
        {
            questionList.add(question);
            notifyItemInserted(questionList.size() - 1);
        }
    }

    /**
     * <h2>Edit Item</h2>
     * Edit sound_item_horizontal in the questionList
     */

    public void editItem(Question question)
    {
        if (questionList != null && questionList.contains(question))
        {
            notifyItemChanged(questionList.indexOf(question));
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
    public void onBindViewHolderQuestions(QuestionViewHolder holder, int position)
    {

    }

    @Override
    public void onRowMovedQuestions(int fromPosition, int toPosition)
    {
        if (fromPosition < toPosition)
        {
            for (int i = fromPosition; i < toPosition; i++)
            {
                Collections.swap(questionList, i, i + 1);
            }
        }
        else
        {
            for (int i = fromPosition; i > toPosition; i--)
            {
                Collections.swap(questionList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onRowSelectedQuestions(QuestionViewHolder myViewHolder)
    {

    }

    @Override
    public void onRowClearQuestions(QuestionViewHolder myViewHolder)
    {

    }

    @Override
    public void onBindViewHolderAnswers(AddAnswersAdapter.AnswerViewHolder holder, int position)
    {

    }

    @Override
    public void onRowMovedAnswers(int fromPosition, int toPosition)
    {

    }

    @Override
    public void onRowSelectedAnswers(AddAnswersAdapter.AnswerViewHolder myViewHolder)
    {

    }

    @Override
    public void onRowClearAnswers(AddAnswersAdapter.AnswerViewHolder myViewHolder)
    {

    }
}