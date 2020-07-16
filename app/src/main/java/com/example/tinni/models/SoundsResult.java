package com.example.tinni.models;

import java.util.List;

/**
 * <h1>Sounds Result</h1></h1>
 * Model for a Sounds Result object
 * Used on the SoundsViewModel to return from the AsyncTask to update categories and sounds
 *
 * Fields:
 * List<Sound> sounds: List of Sound objects
 * List<Category> categories: List of Category Objects
 * boolean fav: Indicator if category is "My favorites"
 * boolean reset: Indicator if category is "All"
 * ObservableBoolean active: Indicator if category is selected
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   18.06.2020
 */

public class SoundsResult
{
    public List<Sound> sounds;
    public List<Category> categories;

    /**
     * <h2>Constructor</h2>
     * Constructor for object
     */

    public SoundsResult(List<Sound> sounds, List<Category> categories)
    {
        this.sounds = sounds;
        this.categories = categories;
    }

    public List<Sound> getSounds()
    {
        return sounds;
    }

    public List<Category> getCategories()
    {
        return categories;
    }

    public void setSounds(List<Sound> sounds)
    {
        this.sounds = sounds;
    }

    public void setCategories(List<Category> categories)
    {
        this.categories = categories;
    }
}
