package com.example.tinni.models;

import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableInt;
import androidx.databinding.ObservableList;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>Selected Program Model</h1></h1>
 * Model for the currently selected program
 *
 * Fields:
 * Program program: The selected Program object
 * int current: The index of the current Session
 * long start: The start date
 * long end: The end date
 * List<Question> startQuestions: The start questionnaire
 * List<Question> endQuestions: The end questionnaire
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   30.06.2020
 */

public class SelectedProgram
{
    private Program program;
    public ObservableArrayList<Session> sessions = new ObservableArrayList<>();
    private long start;
    private long end;
    private List<Question> startQuestions = new ArrayList<>();
    private List<Question> endQuestions = new ArrayList<>();

    /**
     * <h2>Constructor</h2>
     * Constructor for object
     */

    public SelectedProgram(Program program, long start)
    {
        this.program = program;
        this.start = start;
    }

    public Program getProgram()
    {
        return program;
    }

    public void setProgram(Program program)
    {
        this.program = program;
    }

    public long getStart()
    {
        return start;
    }

    public void setStart(long start)
    {
        this.start = start;
    }

    public long getEnd()
    {
        return end;
    }

    public void setEnd(long end)
    {
        this.end = end;
    }

    public List<Question> getStartQuestions()
    {
        return startQuestions;
    }

    public void setStartQuestions(List<Question> startQuestions)
    {
        this.startQuestions = startQuestions;
    }

    public List<Question> getEndQuestions()
    {
        return endQuestions;
    }

    public void setEndQuestions(List<Question> endQuestions)
    {
        this.endQuestions = endQuestions;
    }
}
