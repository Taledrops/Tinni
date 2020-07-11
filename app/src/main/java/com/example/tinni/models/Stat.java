package com.example.tinni.models;

/**
 * <h1>Stat Model</h1></h1>
 * Model for statistics
 *
 * Fields:
 *
 * int number: The stats number
 * String title: The title of the stat
 * String subtitle: The subtitle of the stat
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   10.07.2020
 */

public class Stat
{
    private long number;
    private String title;
    private String subtitle;

    /**
     * <h2>Constructor</h2>
     * Constructor for object
     */

    public Stat(long number, String title, String subtitle)
    {
        this.number = number;
        this.title = title;
        this.subtitle = subtitle;
    }

    public long getNumber()
    {
        return number;
    }

    public void setNumber(long number)
    {
        this.number = number;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getSubtitle()
    {
        return subtitle;
    }

    public void setSubtitle(String subtitle)
    {
        this.subtitle = subtitle;
    }
}
