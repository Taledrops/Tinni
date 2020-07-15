package com.example.tinni.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tinni.databinding.SelectedSessionItemBinding;
import com.example.tinni.helpers.ItemMoveCallback;
import com.example.tinni.models.Session;

import java.util.Collections;
import java.util.List;

/**
 * <h1>Selected Session Adapter</h1>
 * Adapter for Selected Session items
 *
 * Variables:
 * List<Session> sessionList: A list of Session objects to display
 *
 * Source:
 * https://stackoverflow.com/a/48941212/2700965
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   29.06.2020
 */

public class SelectedSessionAdapter extends RecyclerView.Adapter<SelectedSessionAdapter.SessionViewHolder> implements ItemMoveCallback.ItemTouchHelperContract
{
    public List<Session> sessionList;

    public SelectedSessionAdapter(List<Session> sessionList)
    {
        this.sessionList = sessionList;
    }

    /**
     * <h2>On Create View Holder</h2>
     * Overriding and creating the view holder
     */

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        SelectedSessionItemBinding itemBinding = SelectedSessionItemBinding.inflate(layoutInflater, parent, false);
        return new SessionViewHolder(itemBinding);
    }

    /**
     * <h2>On Bind View Holder</h2>
     * Overriding and binding the view holder
     */

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position)
    {
        Session session = sessionList.get(position);
        holder.bind(session);
    }

    /**
     * <h2>Get Item Count</h2>
     * Returns the session count of the sessionList
     */

    @Override
    public int getItemCount()
    {
        return sessionList != null ? sessionList.size() : 0;
    }

    public static class SessionViewHolder extends RecyclerView.ViewHolder
    {
        private SelectedSessionItemBinding binding;

        public SessionViewHolder(SelectedSessionItemBinding binding)
        {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Session program)
        {
            binding.setModel(program);
            binding.executePendingBindings();
        }
    }

    /**
     * <h2>Get Item</h2>
     * Get session inside the sessionList by its index
     */

    public Session getItem(int position)
    {
        if (sessionList != null && getItemCount() > position)
        {
            return sessionList.get(position);
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
     * Add session to the sessionList with its new index
     */

    public void addItem(Session sound)
    {
        if (sessionList != null)
        {
            sessionList.add(sound);
            notifyItemInserted(sessionList.size() - 1);
        }
    }

    /**
     * <h2>Remove Item</h2>
     * Remove session from the sessionList
     */

    public void removeItem(Session session)
    {
        if (sessionList != null)
        {
            int position = sessionList.indexOf(session);
            sessionList.remove(session);
            notifyItemRemoved(position);
        }
    }

    @Override
    public void onBindViewHolderSession(SessionViewHolder holder, int position)
    {

    }

    @Override
    public void onRowMovedSession(int fromPosition, int toPosition)
    {
        if (fromPosition < toPosition)
        {
            for (int i = fromPosition; i < toPosition; i++)
            {
                Collections.swap(sessionList, i, i + 1);
            }
        }
        else
        {
            for (int i = fromPosition; i > toPosition; i--)
            {
                Collections.swap(sessionList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onRowSelectedSession(SessionViewHolder myViewHolder)
    {
        myViewHolder.binding.container.setBackgroundColor(Color.BLACK);
    }

    @Override
    public void onRowClearSession(SessionViewHolder myViewHolder)
    {
        myViewHolder.binding.container.setBackgroundColor(Color.TRANSPARENT);
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