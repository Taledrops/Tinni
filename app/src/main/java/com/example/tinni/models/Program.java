package com.example.tinni.models;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableInt;

import com.example.tinni.R;
import com.example.tinni.custom.AnimatedImageView;
import com.example.tinni.custom.ExpandableTextView;
import com.example.tinni.custom.Placeholder;
import com.example.tinni.helpers.Constants;
import com.example.tinni.helpers.Functions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.tankery.lib.circularseekbar.CircularSeekBar;

/**
 * <h1>Program Model</h1></h1>
 * Model for a Program object
 *
 * Fields:
 * int id: Unique identifier for program
 * boolean custom: Indicator if program is uploaded by user
 * String title: Title of sound file
 * String description: Description of sound file
 * boolean done: Indicator if program has already been completed
 * int img: Resource ID of image of sound file
 * String imgUri: The uri of the image file
 * int interval: The interval of days between each session
 * List<Session> session: List of sessions of program
 * List<Question> questions: List of questions of program
 * boolean custom: Indicator if sound file is uploaded by user
 * Bitmap bitmap: The bitmap of the courrent sound file
 * ObservableBoolean active: Indicator if program is currently active
 * Functions func: Static instance of the Functions class
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   27.06.2020
 */

public class Program
{
    private int id;
    private boolean custom;
    private String title;
    private String description;
    private int img;
    private List<Session> sessions = new ArrayList<>();
    private List<Question> questions = new ArrayList<>();
    private int interval;
    private boolean done;
    private String imgUri;
    private Bitmap bitmap = null;
    public ObservableBoolean active = new ObservableBoolean(false);
    private static final Functions func = new Functions();

    /**
     * <h2>Constructor</h2>
     * Constructor for object
     */

    public Program (int id, boolean custom, String title, String description, int img, List<Session> sessions, List<Question> questions, int interval, boolean done, String imgUri)
    {
        this.id = id;
        this.custom = custom;
        this.title = title;
        this.description = description;
        this.img = img;
        this.interval = interval;
        this.sessions = sessions;
        this.questions = questions;
        this.done = done;
        this.imgUri = imgUri;
    }

    public Program (Program p)
    {
        this.id = p.getId();
        this.custom = p.isCustom();
        this.title = p.getTitle();
        this.description = p.getDescription();
        this.img = p.getImg();
        this.interval = p.getInterval();
        for (Session s: p.getSessions())
        {
            this.sessions.add(new Session(s));
        }
        if (this.sessions.size() > 0)
        {
            this.sessions.get(0).active.set(true);
        }
        for (Question q : p.getQuestions())
        {
            this.questions.add(new Question(q));
        }
        this.done = p.isDone();
        this.imgUri = p.getImgUri();
        this.bitmap = p.getBitmap();
        this.active = p.active;
    }

    public int getId()
    {
        return id;
    }

    public boolean isCustom()
    {
        return custom;
    }

    public String getTitle()
    {
        return title;
    }

    public String getDescription()
    {
        return description;
    }

    public boolean isDone()
    {
        return done;
    }

    public int getImg()
    {
        return img;
    }

    public int getInterval()
    {
        return interval;
    }

    public List<Session> getSessions()
    {
        return sessions;
    }

    public List<Question> getQuestions()
    {
        return questions;
    }

    public String getImgUri()
    {
        return imgUri;
    }

    public Bitmap getBitmap()
    {
        return bitmap;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public void setCustom(boolean custom)
    {
        this.custom = custom;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public void setDone(boolean done)
    {
        this.done = done;
    }

    public void setImg(int img)
    {
        this.img = img;
    }

    public void setInterval(int interval)
    {
        this.interval = interval;
    }

    public void setSessions(List<Session> sessions)
    {
        this.sessions = sessions;
    }

    public void setQuestions(List<Question> questions)
    {
        this.questions = questions;
    }

    public void setImgUri(String imgUri)
    {
        this.imgUri = imgUri;
    }

    public void setBitmap(Bitmap bitmap)
    {
        this.bitmap = bitmap;
    }



    /**
     * <h3>Program Img</h3>
     * Displaying program images via picasso
     *
     * @param program current Program object
     */

    @BindingAdapter("android:programImg")
    public static void setProgramImg(ImageView iv, Program program)
    {
        if (program != null)
        {
            if (Constants.getInstance().picasso != null && program.getImg() > 0)
            {
                Constants.getInstance().picasso.load(program.getImg()).fit().centerCrop().into(iv);
            }
            else if (program.getImgUri() != null && program.getImgUri().length() > 0)
            {
                Constants.getInstance().picasso.load(program.getImgUri()).fit().centerCrop().into(iv);
            }
        }
    }

    /**
     * <h3>Program Text</h3>
     * Setting the sub heading with detailed information of program
     *
     * @param program current Program object
     */

    @BindingAdapter("android:programText")
    public static void setProgramText(TextView tv, Program program)
    {
        if (program != null)
        {
            System.out.println("###### PROGRAM BINDADAPTER START : " + program.getSessions().size());
            String sessionCount = String.format(tv.getContext().getString(R.string.sessions_count_no_brackets), program.sessions.size());
            String daily = tv.getContext().getString(R.string.daily);
            if (program.getInterval() > 1)
            {
                daily = String.format(tv.getContext().getString(R.string.more_days), program.getInterval());
            }

            long timeSum = program.getSessions().stream().mapToLong(Session::getTime).sum();
            String totalTime = String.format(tv.getContext().getString(R.string.total_time), func.getTotalTime(tv.getContext(), timeSum));
            tv.setText(String.format(tv.getContext().getString(R.string.program_text), sessionCount, daily, totalTime));
            System.out.println("###### PROGRAM BINDADAPTER END : " + program.getSessions().size());
        }
    }

    /**
     * <h3>Program Text</h3>
     * Setting the sub heading with detailed information of program
     *
     * @param selectedProgram current SelectedProgram object
     */

    @BindingAdapter("android:programTextCompleted")
    public static void setProgramTextCompleted(TextView textView, SelectedProgram selectedProgram)
    {
        if (selectedProgram != null)
        {
            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.YYYY", Locale.getDefault());
            String fromDate = formatter.format(new Date(selectedProgram.getStart()));
            String toDate = formatter.format(new Date(selectedProgram.getEnd()));
            String date;
            if (!fromDate.equals(toDate))
            {
                date = String.format(textView.getResources().getString(R.string.from_to), fromDate, toDate);
            }
            else
            {
                date = toDate;
            }

            long total = Constants.getInstance().pastPrograms.stream().filter(s -> s.getProgram().getId() == selectedProgram.getProgram().getId()).count();
            String totalText = String.format(textView.getResources().getString(R.string.total_completed), total);

            textView.setText(String.format(textView.getResources().getString(R.string.past_program_text), date, totalText));
        }
    }

    /**
     * <h3>Current Program</h3>
     * Checks if program is current program
     *
     * @param id ID of the program
     */

    @BindingAdapter("android:currentProgram")
    public static void setCurrentProgram(TextView textView, int id)
    {
        if (Constants.getInstance().selectedProgram != null && Constants.getInstance().selectedProgram.getProgram() != null)
        {
            if (id == Constants.getInstance().selectedProgram.getProgram().getId())
            {
                textView.setVisibility(View.VISIBLE);
            }
            else
            {
                textView.setVisibility(View.GONE);
            }
        }
    }

    /**
     * <h3>Program Bitmap</h3>
     * Sets the bitmap for the Program object
     *
     * @param program the current Program object
     */


    @BindingAdapter("android:programBitmap")
    public static void setProgramBitmap(ImageView imageView, Program program)
    {
        if (program != null)
        {
            Target target = new Target()
            {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
                {
                    program.setBitmap(bitmap);
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable)
                {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable)
                {

                }
            };

            if (Constants.getInstance().picasso != null)
            {
                if (program.getImg() > 0)
                {
                    Constants.getInstance().picasso.load(program.getImg()).into(target);

                    imageView.setTag(target);
                }
                else if (program.getImgUri() != null && program.getImgUri().length() > 0)
                {
                    Constants.getInstance().picasso.load(program.getImgUri()).into(target);

                    imageView.setTag(target);
                }
            }
        }
    }
}
