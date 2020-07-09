package com.example.tinni.models;

/**
 * <h1>Rating Model</h1></h1>
 * Model for daily ratings
 *
 * Fields:
 * int rating: The rating from 1 (miserable) to 5 (very good)
 * int date: The date of the rating
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   08.06.2020
 */

public class Rating
{
    private int rating;
    private long date;
    private String text;

    /**
     * <h2>Constructor</h2>
     * Constructor for object
     */

    public Rating(int rating, long date, String text)
    {
        this.rating = rating;
        this.date = date;
        this.text = text;
    }

    public int getRating()
    {
        return rating;
    }

    public void setRating(int rating)
    {
        this.rating = rating;
    }

    public long getDate()
    {
        return date;
    }

    public void setDate(long date)
    {
        this.date = date;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }
}
