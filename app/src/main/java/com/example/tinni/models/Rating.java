package com.example.tinni.models;

import android.graphics.Color;
import android.media.Image;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tinni.R;
import com.example.tinni.adapters.AnswerAdapter;
import com.example.tinni.helpers.ItemClickSupport;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

    /**
     * <h3>Month Date</h3>
     * Converts milliseconds to a human readable date (without the year)
     *
     * Source: https://stackoverflow.com/a/7953839/2700965
     *
     * @param date The date in milliseconds
     */

    @BindingAdapter("android:monthDate")
    public static void setMonthDate(TextView textView, long date)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM", Locale.getDefault());
        textView.setText(formatter.format(new Date(date)));
    }

    /**
     * <h3>Month Date Year</h3>
     * Converts milliseconds to a human readable date
     *
     * Source: https://stackoverflow.com/a/7953839/2700965
     *
     * @param date The date in milliseconds
     */

    @BindingAdapter("android:monthDateYear")
    public static void setMonthDateYear(TextView textView, long date)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.YYYY", Locale.getDefault());
        textView.setText(formatter.format(new Date(date)));
    }

    /**
     * <h3>Text From Rating</h3>
     * Shows a different text depending on the rating from 1 to 5
     *
     * @param rating The rating integer from 1 to 5
     */

    @BindingAdapter("android:textFromRating")
    public static void setTextFromRatong(TextView textView, int rating)
    {
        switch (rating)
        {
            case 5:
                textView.setText(textView.getContext().getResources().getString(R.string.very_good));
                textView.setTextColor(Color.GREEN);
                break;
            case 4:
                textView.setText(textView.getContext().getResources().getString(R.string.good));
                textView.setTextColor(Color.GREEN);
                break;
            case 3:
                textView.setText(textView.getContext().getResources().getString(R.string.neutral));
                break;
            case 2:
                textView.setText(textView.getContext().getResources().getString(R.string.bad));
                textView.setTextColor(Color.RED);
                break;
            case 1:
                textView.setText(textView.getContext().getResources().getString(R.string.miserable));
                textView.setTextColor(Color.RED);
                break;
            default:
                textView.setText(textView.getContext().getResources().getString(R.string.neutral));
                break;
        }
    }

    /**
     * <h3>Custom Weight</h3>
     * Sets a custom layout_weight
     *
     * Source: https://stackoverflow.com/a/11611566/2700965
     *
     * @param weight the weight of the view
     */

    @BindingAdapter("android:customWeight")
    public static void setCustomWeight(ImageView imageView, int weight)
    {
        imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, Float.parseFloat(String.valueOf(weight))));
    }

}
