package com.example.tinni.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import com.example.tinni.R;

/**
 * <h1>Custom Placeholder Layout</h1>
 * Custom LinearLayout to simulate a loading effect by blinking
 * AlphaAnimation is used to achieve this
 *
 * Variables:
 * Animation animation: Instance of the Animation
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   18.06.2020
 */

public class Placeholder extends LinearLayout
{
    private static Animation animation;

    public Placeholder(Context context)
    {
        super(context);
    }

    public Placeholder(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    /**
     * <h2>Blinking</h2>
     * Creates a blinking effect via AlphaAnimation
     *
     * @param start Sets up animation if true, stops it if false and hides the view
     */

    public void blinking (boolean start)
    {
        if (start)
        {
            this.setVisibility(VISIBLE);
            if (animation == null)
            {
                Animation anim = new AlphaAnimation(0.3f, 0.8f);
                anim.setDuration(getResources().getInteger(R.integer.anim_length));
                anim.setRepeatMode(Animation.REVERSE);
                anim.setRepeatCount(Animation.INFINITE);
                animation = anim;
            }

            this.startAnimation(animation);
        }
        else
        {
            if (animation != null)
            {
                this.clearAnimation();
            }
            this.setVisibility(GONE);
        }
    }
}
