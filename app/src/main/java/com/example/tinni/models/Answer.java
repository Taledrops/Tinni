package com.example.tinni.models;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

/**
 * <h1>Answer Model</h1></h1>
 * Model for an Answer object
 *
 * Fields:
 * int id: Unique identifier for answer
 * String text: Text of the answer
 * boolean active: Indicator if answer was selected
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   30.06.2020
 */

public class Answer
{
    private int id;
    public ObservableField<String> text = new ObservableField<>();
    public ObservableBoolean active = new ObservableBoolean(false);

    /**
     * <h2>Constructor</h2>
     * Constructor for object
     */

    public Answer(int id, String text, boolean active)
    {
        this.id = id;
        this.text.set(text);
        this.active.set(active);
    }

    public Answer(Answer a)
    {
        this.id = a.getId();
        this.text.set(a.text.get());
        this.active.set(a.active.get());
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
