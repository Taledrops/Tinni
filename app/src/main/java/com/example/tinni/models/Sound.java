package com.example.tinni.models;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import androidx.databinding.ObservableInt;

import com.example.tinni.R;
import com.example.tinni.custom.AnimatedImageView;
import com.example.tinni.custom.ExpandableTextView;
import com.example.tinni.custom.Placeholder;
import com.example.tinni.helpers.CircleTransform;
import com.example.tinni.helpers.Constants;
import com.example.tinni.helpers.Functions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import me.tankery.lib.circularseekbar.CircularSeekBar;

/**
 * <h1>Sound Model</h1></h1>
 * Model for a Program object
 *
 * Fields:
 * int id: Unique identifier for sound file
 * boolean custom: Indicator if sound is uploaded by user
 * String title: Title of sound file
 * String description: Description of sound file
 * int length: Length of sound file in seconds
 * int img: Resource ID of image of sound file
 * List<Category> categories: List of categories of sound file
 * boolean custom: Indicator if sound file is uploaded by user
 * ObservableInt listened: Times sound file was listened
 * String uri: The uri of the sound file
 * String imgUri: The uri of the image file
 * Bitmap bitmap: The bitmap of the courrent sound file
 * ObservableInt time: The current playback time in seconds
 * ObservableInt playing: The current play state of the Sound object
 * Functions func: A static instance of the helper class "Functions"
 * ValueAnimator va: A static instance of a ValueAnimator
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   18.06.2020
 */

public class Sound
{
    private int id;
    private boolean custom;
    private String title;
    private String description;
    private int length;
    private int img;
    private List<Category> categories;
    private String imgUri;
    private String uri;
    private Bitmap bitmap = null;
    public ObservableInt time = new ObservableInt(0);
    public ObservableInt playing = new ObservableInt(0);
    private static final Functions func = new Functions();
    private static ValueAnimator va = new ValueAnimator();

    /**
     * <h2>Constructor</h2>
     * Constructor for object
     */

    public Sound(int id, boolean custom, String title, String description, int length, int img, List<Category> categories, String uri, String imgUri)
    {
        this.id = id;
        this.custom = custom;
        this.title = title;
        this.description = description;
        this.length = length;
        this.img = img;
        this.categories = categories;
        this.uri = uri;
        this.imgUri = imgUri;
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

    public int getLength()
    {
        return length;
    }

    public int getImg()
    {
        return img;
    }

    public List<Category> getCategories()
    {
        return categories;
    }

    public String getUri()
    {
        return uri;
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

    public void setLength(int length)
    {
        this.length = length;
    }

    public void setImg(int img)
    {
        this.img = img;
    }

    public void setCategories(List<Category> categories)
    {
        this.categories = categories;
    }

    public void setUri(String uri)
    {
        this.uri = uri;
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
     * <h3>Placerholder visibility</h3>
     * Responsible for hiding/showing placeholders
     *
     * @param placeholder the custom placeholder layout
     * @param visible the boolean determining whether view is visible or not
     */

    @BindingAdapter("android:placeholderVisibility")
    public static void setPlaceholderVisibility(Placeholder placeholder, boolean visible)
    {
        if (visible)
        {
            placeholder.setVisibility(View.VISIBLE);
            placeholder.blinking(true);
        }
        else
        {
            placeholder.blinking(false);
        }
    }

    /**
     * <h3>Seconds to time</h3>
     * Formatting seconds to MM:SS Format
     *
     * @param seconds the number of seconds
     */

    @BindingAdapter("android:secondsToTime")
    public static void setSecondsToTime(TextView textView, int seconds)
    {
        textView.setText(String.format(Locale.getDefault(), "%d:%02d", seconds/60, seconds%60));
    }

    /**
     * <h3>Sound Img</h3>
     * Displaying sound images via picasso
     *
     * @param sound current Sound object
     */

    @BindingAdapter("android:soundImg")
    public static void setSoundImg(ImageView iv, Sound sound)
    {
        if (sound != null)
        {
            if (Constants.getInstance().picasso != null && sound.getImg() > 0)
            {
                Constants.getInstance().picasso.load(sound.getImg()).fit().centerCrop().into(iv);
            }
            else if (sound.getImgUri() != null && sound.getImgUri().length() > 0)
            {
                Constants.getInstance().picasso.load(sound.getImgUri()).fit().centerCrop().into(iv);
            }
        }
    }

    /**
     * <h3>Sound Img Rounded</h3>
     * Displaying rounded sound images via picasso
     *
     * @param sound current Sound object
     */

    @BindingAdapter("android:soundImgRounded")
    public static void setSoundImgRounded(ImageView iv, Sound sound)
    {
        if (sound != null)
        {
            if (Constants.getInstance().picasso != null && sound.getImg() > 0)
            {
                Constants.getInstance().picasso.load(sound.getImg()).fit().transform(new CircleTransform()).centerCrop().into(iv);
            }
            else if (sound.getImgUri() != null && sound.getImgUri().length() > 0)
            {
                Constants.getInstance().picasso.load(sound.getImgUri()).fit().transform(new CircleTransform()).centerCrop().into(iv);
            }
        }
    }

    /**
     * <h3>Sound Bitmap</h3>
     * Sets the bitmap for the Sound object
     *
     * @param sound the current Sound object
     */

    @BindingAdapter("android:soundBitmap")
    public static void setSoundBitmap(ImageView imageView, Sound sound)
    {
        if (sound != null)
        {
            Target target = new Target()
            {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
                {
                    sound.setBitmap(bitmap);
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
                if (sound.getImg() > 0)
                {
                    Constants.getInstance().picasso.load(sound.getImg()).into(target);

                    imageView.setTag(target);
                }
                else if (sound.getImgUri() != null && sound.getImgUri().length() > 0)
                {
                    Constants.getInstance().picasso.load(sound.getImgUri()).into(target);

                    imageView.setTag(target);
                }
            }
        }
    }

    /**
     * <h3>Image Bitmap</h3>
     * Sets the bitmap of an ImageView with the uri
     *
     * @param uri Uri of the image
     */

    @BindingAdapter("android:imageBitmap")
    public static void setImageBitmap(ImageView iv, String uri)
    {
        try
        {
            if (uri != null && uri.length() > 0)
            {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(iv.getContext().getContentResolver(), Uri.parse(uri));
                iv.setImageBitmap(func.rotateBitmap(iv.getContext(), Uri.parse(uri), bitmap));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * <h3>Ken Burns</h3>
     * Sets the Image for the ImageView of the Animated Image View
     *
     * @param img resource id of img
     * @param uri of img
     */

    @BindingAdapter({"android:kenBurnsSoundResourceId", "android:kenBurnsSoundImageUri"})
    public static void setKenBurns(AnimatedImageView imageView, int img, String uri)
    {
        Target target = new Target()
        {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from)
            {
                imageView.setImageBitmap(bitmap);
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
            if (img > 0)
            {
                Constants.getInstance().picasso.load(img).into(target);

                imageView.setTag(target);
            }
            else if (uri != null && uri.length() > 0)
            {
                Constants.getInstance().picasso.load(uri).into(target);

                imageView.setTag(target);
            }
        }
    }

    /**
     * <h3>Total Time</h3>
     * Formats the length to a human readable format (Min)
     *
     * @param length the length of the Sound object
     */

    @BindingAdapter("android:totalTime")
    public static void setTotalTime(TextView textView, int length)
    {
        textView.setText(String.format(textView.getContext().getResources().getString(R.string.minutes_short), String.format(Locale.getDefault(),"%d:%02d", length / 60, length % 60)));
    }

    /**
     * <h3>Current Time</h3>
     * Formats the current time to a human readable format
     *
     * @param time the current time in seconds
     */

    @BindingAdapter("android:currentTime")
    public static void setCurrentTime(TextView textView, long time)
    {
        textView.setText(func.millisecondsToTimer(time));
    }

    /**
     * <h3>Listened</h3>
     * The number of times the sound has been listened to
     *
     * @param id the id of the sound
     */

    @BindingAdapter("android:listened")
    public static void setListened(TextView textView, int id)
    {
        long number = Constants.getInstance().listened.stream().filter(s -> s.getId() == id).count();
        textView.setText(String.format(textView.getContext().getResources().getString(R.string.times_listened), number));
    }

    /**
     * <h3>Current Progress</h3>
     * Animates the current progress for the regular progress bar
     *
     * @param time the current time
     * @param max the maximum time
     */

    @BindingAdapter({"android:soundProgress", "android:soundMaxProgress"})
    public static void setCurrentProgress(SeekBar seekBar, int time, int max)
    {
        if (max != seekBar.getMax())
        {
            seekBar.setMax(max);
        }
        seekBar.setProgress(time / 1000);
    }

    /**
     * <h3>Current Progress Circular</h3>
     * Animates the current progress for the cicular progress bar
     *
     * @param time the current time
     * @param max the maximum time
     */

    @BindingAdapter({"android:soundProgressCircular", "android:soundMaxProgressCircular"})
    public static void setCurrentProgressCircular(CircularSeekBar seekBar, int time, int max)
    {
        if (max != seekBar.getMax())
        {
            seekBar.setMax(max);
        }

        float progress = seekBar.getProgress() * 1000;
        int diff = Math.round(time - progress);
        if (va != null)
        {
            va.cancel();
        }

        if (diff >= 0 && diff < (seekBar.getResources().getInteger(R.integer.player_repeat) + 100))
        {
            va = ValueAnimator.ofFloat(progress / 1000f, time / 1000f);
            va.setDuration(diff);
            va.addUpdateListener(animation -> seekBar.setProgress((float) animation.getAnimatedValue()));
            va.start();
        }
        else
        {
            seekBar.setProgress(time / 1000f);
        }
    }

    /**
     * <h3>Play Handle</h3>
     * Indicator of current play state
     *
     * @param playing the current state
     * 0: Idle
     * 1: Playing
     * 2: Loading
     * 3: Error
     */

    @BindingAdapter("android:playHandle")
    public static void setPlayHandle(FrameLayout frameLayout, int playing)
    {
        ImageView button = frameLayout.findViewById(R.id.play);
        ProgressBar loading = frameLayout.findViewById(R.id.loading);

        if (button == null || loading == null)
        {
            button = frameLayout.findViewById(R.id.bottomPlay);
            loading = frameLayout.findViewById(R.id.bottomLoading);
        }
        if (button != null && loading != null)
        {
            switch (playing)
            {
                    // Idle
                case 0:
                    loading.setVisibility(View.GONE);
                    button.setVisibility(View.VISIBLE);
                    button.setImageDrawable(ContextCompat.getDrawable(frameLayout.getContext(), R.drawable.play));
                    break;
                    // Playing
                case 1:
                    loading.setVisibility(View.GONE);
                    button.setVisibility(View.VISIBLE);
                    button.setImageDrawable(ContextCompat.getDrawable(frameLayout.getContext(), R.drawable.pause));
                    break;
                    // Loading
                case 2:
                    button.setVisibility(View.GONE);
                    loading.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    // Retry
                    loading.setVisibility(View.GONE);
                    button.setVisibility(View.VISIBLE);
                    button.setImageDrawable(ContextCompat.getDrawable(frameLayout.getContext(), R.drawable.forward));
                    break;
            }
        }
    }

    /**
     * <h3>Liked</h3>
     * Colors drawable by checking the liked state
     *
     * @param liked Indicator if sound was likes
     */

    @BindingAdapter("android:liked")
    public static void setLiked(ImageView imageView, boolean liked)
    {
        if (!liked)
        {
            func.paintDrawable(imageView, R.drawable.heart, R.color.colorLight);
        }
        else
        {
            func.paintDrawable(imageView, R.drawable.heart, R.color.colorAccent);
        }
    }

    /**
     * <h3>Repeat</h3>
     * Colors drawable by checking the repeat state
     *
     * @param repeat Indicator if sound should be repeated
     */

    @BindingAdapter("android:repeat")
    public static void setRepeat(ImageView imageView, boolean repeat)
    {
        if (!repeat)
        {
            func.paintDrawable(imageView, R.drawable.repeat, R.color.colorLight);
        }
        else
        {
            func.paintDrawable(imageView, R.drawable.repeat, R.color.colorAccent);
        }
    }

    /**
     * <h3>Description</h3>
     * Instantiating the ExpandableTextView
     *
     * @param text the text to show
     */

    @BindingAdapter("android:description")
    public static void setDescription(ExpandableTextView textView, String text)
    {
        textView.Init(text);
    }
}
