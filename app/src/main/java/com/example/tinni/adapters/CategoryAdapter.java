package com.example.tinni.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tinni.databinding.CategoryItemBinding;
import com.example.tinni.models.Category;

import java.util.List;

/**
 * <h1>Category Adapter</h1>
 * Adapter for category items
 *
 * Variables:
 * List<Category> categoryList: A list of Category objects to display
 *
 * Source:
 * https://stackoverflow.com/a/48941212/2700965
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   20.06.2020
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>
{
    public List<Category> categoryList;

    public CategoryAdapter(List<Category> categoryList)
    {
        this.categoryList = categoryList;
    }

    /**
     * <h2>On Create View Holder</h2>
     * Overriding and creating the view holder
     */

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        CategoryItemBinding itemBinding = CategoryItemBinding.inflate(layoutInflater, parent, false);
        return new CategoryViewHolder(itemBinding);
    }

    /**
     * <h2>On Bind View Holder</h2>
     * Overriding and binding the view holder
     */

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position)
    {
        Category category = categoryList.get(position);
        holder.bind(category);
    }

    /**
     * <h2>Get Item Count</h2>
     * Returns the sound_item_horizontal count of the soundList
     */

    @Override
    public int getItemCount()
    {
        return categoryList != null ? categoryList.size() : 0;
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder
    {
        private CategoryItemBinding binding;

        public CategoryViewHolder(CategoryItemBinding binding)
        {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Category category)
        {
            binding.setModel(category);
            binding.executePendingBindings();
        }
    }

    /**
     * <h2>Get Item</h2>
     * Get sound_item_horizontal inside the categoryList by its index
     */

    public Category getItem(int position)
    {
        if (categoryList != null && getItemCount() > position)
        {
            return categoryList.get(position);
        }
        else
        {
            return null;
        }
    }
}
