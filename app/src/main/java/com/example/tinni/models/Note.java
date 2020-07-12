package com.example.tinni.models;

import androidx.databinding.ObservableField;

/**
 * <h1>Note Model</h1></h1>
 * Model for a Note object
 *
 * Fields:
 * int id: Unique identifier for note
 * String text: Text of the note
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   12.07.2020
 */

public class Note
{
    private int id;
    public ObservableField<String> text = new ObservableField<>();

    /**
     * <h2>Constructor</h2>
     * Constructor for object
     */

    public Note(int id, String text)
    {
        this.id = id;
        this.text.set(text);
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }
}
