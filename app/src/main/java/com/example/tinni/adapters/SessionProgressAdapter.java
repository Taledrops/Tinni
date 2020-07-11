package com.example.tinni.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tinni.databinding.SessionItemBinding;
import com.example.tinni.databinding.SessionProgressItemBinding;
import com.example.tinni.models.Session;

import java.util.List;

/**
 * <h1>Session Progress Adapter</h1>
 * Adapter for Session progress items
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

public class SessionProgressAdapter extends RecyclerView.Adapter<SessionProgressAdapter.SessionViewHolder>
{
    public List<Session> sessionList;

    public SessionProgressAdapter(List<Session> sessionList)
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
        SessionProgressItemBinding itemBinding = SessionProgressItemBinding.inflate(layoutInflater, parent, false);
        return new SessionViewHolder(itemBinding);
    }

    /**
     * <h2>On Bind View Holder</h2>
     * Overriding and binding the view holder
     */

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position)
    {
        Session category = sessionList.get(position);
        holder.bind(category);
    }

    /**
     * <h2>Get Item Count</h2>
     * Returns the sound_item_horizontal count of the sessionList
     */

    @Override
    public int getItemCount()
    {
        return sessionList != null ? sessionList.size() : 0;
    }

    static class SessionViewHolder extends RecyclerView.ViewHolder
    {
        private SessionProgressItemBinding binding;

        public SessionViewHolder(SessionProgressItemBinding binding)
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
     * Get sound_item_horizontal inside the sessionList by its index
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

    public void reloadList(List<Session> newlist)
    {
        sessionList.clear();
        sessionList.addAll(newlist);
        this.notifyDataSetChanged();
    }

    /**
     * <h2>Add Item</h2>
     * Add sound_item_horizontal to the sessionList with its new index
     */

    public void addItem(Session sound, int position)
    {
        if (sessionList != null)
        {
            sessionList.add(position, sound);
            notifyItemInserted(position);
        }
    }
}