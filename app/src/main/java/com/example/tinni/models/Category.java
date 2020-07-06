package com.example.tinni.models;

import androidx.databinding.ObservableBoolean;

/**
 * <h1>Category Model</h1></h1>
 * Model for a category object
 *
 * Fields:
 * int id: Unique identifier for category
 * String title: Title of category
 * boolean fav: Indicator if category is "My favorites"
 * boolean reset: Indicator if category is "All"
 * ObservableBoolean active: Indicator if category is selected
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   18.06.2020
 */

public class Category
{
    private int id;
    private String title;
    private boolean fav;
    private boolean reset;
    private boolean mine;
    public ObservableBoolean active = new ObservableBoolean(false);

    /**
     * <h2>Constructors</h2>
     * Constructors for object
     */

    public Category(int id, String title, boolean fav, boolean reset, boolean mine, boolean active)
    {
        this.id = id;
        this.title = title;
        this.fav = fav;
        this.reset = reset;
        this.mine = mine;
        this.active.set(active);
    }

    public Category(Category c)
    {
        this.id = c.id;
        this.title = c.title;
        this.fav = c.fav;
        this.reset = c.reset;
        this.mine = c.mine;
        this.active.set(false);
    }

    public int getId()
    {
        return id;
    }

    public String getTitle()
    {
        return title;
    }

    public boolean isFav()
    {
        return fav;
    }

    public boolean isReset()
    {
        return reset;
    }

    public boolean isMine()
    {
        return mine;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public void setFav(boolean fav)
    {
        this.fav = fav;
    }

    public void setReset(boolean reset)
    {
        this.reset = reset;
    }

    public void setMine(boolean mine)
    {
        this.mine = mine;
    }
}
