package com.example.tinni.models;

import android.widget.TextView;

import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableInt;
import androidx.databinding.ObservableList;

import com.example.tinni.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    private int id;
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

    public SelectedProgram(int id, Program program, long start)
    {
        this.id = id;
        this.program = program;
        this.start = start;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
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

    /**
     * <h3>From To</h3>
     * Converts two milliseconds to a from to date
     *
     * @param from The from date in milliseconds
     * @param to The to date in milliseconds
     */

    @BindingAdapter({"android:dateFrom", "android:dateTo"})
    public static void setMonthDateYear(TextView textView, long from, long to)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.YYYY", Locale.getDefault());
        String fromDate = formatter.format(new Date(from));
        String toDate = formatter.format(new Date(to));

        if (!fromDate.equals(toDate))
        {
            textView.setText(String.format(textView.getResources().getString(R.string.from_to), fromDate, toDate));
        }
        else
        {
            textView.setText(toDate);
        }
    }
}
