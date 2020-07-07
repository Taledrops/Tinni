package com.example.tinni.models;

/**
 * <h1>SoundStat Model</h1></h1>
 * Model for Sound statistics
 *
 * Fields:
 * int id: Sound id
 * int time: Number of seconds listened
 * int date: Listening date
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   07.06.2020
 */

public class SoundStat
{
    private int id;
    private int time;
    private long date;

    /**
     * <h2>Constructor</h2>
     * Constructor for object
     */

    public SoundStat(int id, int time, long date)
    {
        this.id = id;
        this.time = time;
        this.date = date;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getTime()
    {
        return time;
    }

    public void setTime(int time)
    {
        this.time = time;
    }

    public long getDate()
    {
        return date;
    }

    public void setDate(long date)
    {
        this.date = date;
    }
}
