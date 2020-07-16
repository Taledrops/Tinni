package com.example.tinni.models;

/**
 * <h1>Questionnaire Model</h1></h1>
 * Model for a Questionnaire object
 *
 * Fields:
 * int id: Unique identifier for questionnaire
 * String question: Text of the question
 * String before: Answer before the program
 * String after: Answer after the item
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   10.07.2020
 */

public class Questionnaire
{
    private int id;
    private String question;
    private String before;
    private String after;

    /**
     * <h2>Constructor</h2>
     * Constructor for object
     */

    public Questionnaire(int id, String question, String before, String after)
    {
        this.id = id;
        this.question = question;
        this.before = before;
        this.after = after;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getQuestion()
    {
        return question;
    }

    public void setQuestion(String question)
    {
        this.question = question;
    }

    public String getBefore()
    {
        return before;
    }

    public void setBefore(String before)
    {
        this.before = before;
    }

    public String getAfter()
    {
        return after;
    }

    public void setAfter(String after)
    {
        this.after = after;
    }
}
