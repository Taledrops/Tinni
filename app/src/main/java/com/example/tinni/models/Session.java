package com.example.tinni.models;

import android.widget.TextView;

import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableBoolean;

import com.example.tinni.R;
import com.example.tinni.helpers.Functions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * <h1>Session Model</h1></h1>
 * Model for a Session object
 *
 * Fields:
 * int id: Unique identifier for session
 * ObservableBoolean active: Indicator if session is active
 * ObservableBoolean done: Indicator if session is done
 * Sound sound: The sound file
 * int time: The duration of the session
 * int rating: The rating for this session
 * long date: The date of this session
 * Functions func: Static instance of the Functions class
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   28.06.2020
 */

public class Session
{
    private int id;
    public ObservableBoolean active = new ObservableBoolean();
    public ObservableBoolean done = new ObservableBoolean();
    private Sound sound;
    private int time;
    private int rating = 0;
    private long date = System.currentTimeMillis();
    private static final Functions func = new Functions();

    /**
     * <h2>Constructor</h2>
     * Constructor for object
     */

    public Session(int id, boolean active, Sound sound, int time, boolean done)
    {
        this.id = id;
        this.active.set(active);
        this.sound = sound;
        this.time = time;
        this.done.set(done);
    }

    /**
     * <h2>Copy Constructor</h2>
     * Copy Constructor for object
     */

    public Session(Session s)
    {
        this.id = s.getId();
        this.active.set(false);
        this.sound = s.getSound();
        this.time = s.getTime();
        this.done.set(s.done.get());
        this.rating = s.getRating();
        this.date = s.getDate();
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public Sound getSound()
    {
        return sound;
    }

    public void setSound(Sound sound)
    {
        this.sound = sound;
    }

    public int getTime()
    {
        return time;
    }

    public void setTime(int time)
    {
        this.time = time;
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

    /**
     * <h3>Session Text</h3>
     * Setting the sub text with detailed information of session
     *
     * @param id the id of the session
     * @param time the length of the session
     * @param done indicator if session is done
     */

    @BindingAdapter({"android:sessionId", "android:sessionTime", "android:sessionDone"})
    public static void setSessionText(TextView textView, int id, int time, boolean done)
    {
        String sessionCount = String.format(textView.getContext().getString(R.string.session_number), id);
        String timeText = func.getTotalTime(textView.getContext(), time);
        if (!done)
        {
            textView.setText(String.format(textView.getContext().getString(R.string.session_text), sessionCount, timeText));
        }
        else
        {

            textView.setText(String.format(textView.getContext().getString(R.string.session_text_long), sessionCount, timeText));
        }
    }

    /**
     * <h3>Time Full</h3>
     * Converting seconds to a human readable time in minutes
     *
     * @param time the time in seconds
     */

    @BindingAdapter("android:timeFull")
    public static void setTimeFull(TextView textView, int time)
    {
        textView.setText(func.getTotalTime(textView.getContext(), time));
    }

    /**
     * <h3>Session Progress Text</h3>
     * Setting the sub text with detailed information of session inside the progress page
     *
     * @param id the id of the session
     * @param time the length of the session
     * @param date The date of the session
     */

    @BindingAdapter({"android:sessionId", "android:sessionTime", "android:sessionDate"})
    public static void setSessionProgressText(TextView textView, int id, int time, long date)
    {
        String sessionCount = String.format(textView.getContext().getString(R.string.session_number), id);
        String timeText = func.getTotalTime(textView.getContext(), time);
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.YYYY", Locale.getDefault());
        String dateString = formatter.format(new Date(date));

        textView.setText(String.format(textView.getContext().getString(R.string.session_text_progress), sessionCount, timeText, dateString));
    }
}
