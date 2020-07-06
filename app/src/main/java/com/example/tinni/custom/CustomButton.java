package com.example.tinni.custom;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.example.tinni.R;

/**
 * <h1>Custom Button</h1>
 * Custom button with animations
 * Can show a checkmark for success, a loading indicator and a cross for error
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   22.06.2020
 */

public class CustomButton extends FrameLayout
{
    public CustomButton(Context context)
    {
        super(context);
    }
    private ImageView check;
    private ImageView error;
    private TextView textView;
    private FrameLayout bg;
    private ProgressBar progress;
    private static int bgWidth;
    public boolean expand = false;
    private int width = 0;

    /**
     * <h2>Finished</h2>
     * An interface to indicate that the current animation is done
     *
     */

    public interface Finished
    {
        void onAnimationFinished(boolean done);
    }

    /**
     * <h2>Custom Button</h2>
     * The default constructor
     *
     */

    public CustomButton(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
    }

    /**
     * <h2>Change Text</h2>
     * Changes the current button text
     *
     * Arguments:
     * String text: The new text
     *
     */

    public void ChangeText (String text)
    {
        if (textView != null)
        {
            textView.setText(text);
        }
    }

    /**
     * <h2>Init</h2>
     * Initialization of the button
     *
     * Arguments:
     * String text: The text of the button
     *
     */

    public void Init (String text)
    {
        if (text != null)
        {
            // PARAMS
            LinearLayout.LayoutParams topFrameLp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            topFrameLp.gravity = Gravity.CENTER;
            topFrameLp.topMargin = (int)getResources().getDimension(R.dimen.bigMargin);
            LayoutParams layoutParams=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams imgLp = new LinearLayout.LayoutParams((int)getResources().getDimension(R.dimen.buttonSize) / 2, (int)getResources().getDimension(R.dimen.buttonSize));
            LinearLayout.LayoutParams frameLp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, (int)getResources().getDimension(R.dimen.buttonSize));
            LayoutParams iconLp = new LayoutParams((int)getResources().getDimension(R.dimen.buttonIcon), (int)getResources().getDimension(R.dimen.buttonIcon));
            iconLp.gravity = Gravity.CENTER;
            LayoutParams textLp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            textLp.gravity = Gravity.CENTER;
            // END PARAMS

            // TOP FRAME
            this.setLayoutParams(topFrameLp);
            // END TOP FRAME

            // LINEAR
            LinearLayout linearLayout = new LinearLayout(getContext());
            layoutParams.gravity = Gravity.CENTER_VERTICAL;
            linearLayout.setLayoutParams(layoutParams);
            // END LINEAR

            // LEFT CIRCLE
            ImageView leftCircle = new ImageView(new ContextThemeWrapper(getContext(), R.style.HalfCircleLeft));
            leftCircle.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.circle_l));
            leftCircle.setLayoutParams(imgLp);
            // END LEFT CIRCLE

            // RIGHT CIRCLE
            ImageView rightCircle = new ImageView(new ContextThemeWrapper(getContext(), R.style.HalfCircleRight));
            rightCircle.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.circle_r));
            rightCircle.setLayoutParams(imgLp);
            // END RIGHT CIRCLE

            // BG FRAME
            FrameLayout frameLayout = new FrameLayout(new ContextThemeWrapper(getContext(), R.style.CustomButtonBg));
            frameLayout.setLayoutParams(frameLp);
            bg = frameLayout;
            // END BG FRAME

            // TEXT
            TextView txt = new TextView(new ContextThemeWrapper(getContext(), R.style.CustomButtonText));
            txt.setLayoutParams(textLp);
            txt.setText(text);
            txt.setEllipsize(null);
            txt.setSingleLine(true);
            textView = txt;
            // END TEXT

            // CHECK
            ImageView checkImage = new ImageView(new ContextThemeWrapper(getContext(), R.style.CustomButtonImg));
            checkImage.setLayoutParams(iconLp);
            checkImage.setImageResource(R.drawable.check);
            ImageViewCompat.setImageTintList(checkImage, ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorLight)));
            check = checkImage;
            // END CHECK

            // ERROR
            ImageView errorImage = new ImageView(new ContextThemeWrapper(getContext(), R.style.CustomButtonImg));
            errorImage.setLayoutParams(iconLp);
            errorImage.setImageResource(R.drawable.close);
            ImageViewCompat.setImageTintList(errorImage, ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorLight)));
            error = errorImage;
            // END ERROR

            // PROGRESS
            ProgressBar progressBar = new ProgressBar(new ContextThemeWrapper(getContext(), R.style.CustomButtonProgress));
            progressBar.setLayoutParams(iconLp);
            progressBar.setIndeterminate(true);
            Drawable wrapDrawable = DrawableCompat.wrap(progressBar.getIndeterminateDrawable());
            DrawableCompat.setTint(wrapDrawable, ContextCompat.getColor(getContext(), R.color.colorLight));
            progressBar.setIndeterminateDrawable(DrawableCompat.unwrap(wrapDrawable));
            progress = progressBar;
            // END PROGRESS

            // ADDEN
            frameLayout.addView(textView);
            linearLayout.addView(leftCircle);
            linearLayout.addView(frameLayout);
            linearLayout.addView(rightCircle);

            this.addView(linearLayout);
            this.addView(check);
            this.addView(error);
            this.addView(progress);


            check.setImageAlpha(0);
            error.setImageAlpha(0);
            progress.setAlpha(0);

            bg.post(() -> bgWidth = bg.getMeasuredWidth());
        }
    }

    /**
     * <h2>Animate</h2>
     * Animate the button
     *
     * Arguments:
     * int type:
     *      1: Loading
     *      2: Success
     *      3: Error
     * int duration: The duration of the animation
     * int delay: The delay of the animation
     * Finished callback: The callback once the animation is done
     *
     */

    public void Animate (int type, int duration, int delay, final Finished callback)
    {
        switch (type)
        {
            case 2:
                // SUCCESS
                progress.animate().alpha(0f).setDuration(duration / 2).start();
                ValueAnimator checkAnimator = ValueAnimator.ofInt(0,255);
                checkAnimator.setDuration(duration);
                checkAnimator.addUpdateListener(valueAnimator -> check.setImageAlpha((Integer) valueAnimator.getAnimatedValue()));
                checkAnimator.addListener(new AnimatorListenerAdapter()
                {
                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        if (!expand)
                        {
                            callback.onAnimationFinished(true);
                        }
                        else
                        {
                            ValueAnimator checkAnimator = ValueAnimator.ofInt(255, 0);
                            checkAnimator.setStartDelay(delay);
                            checkAnimator.setDuration(duration);
                            checkAnimator.addUpdateListener(valueAnimator -> check.setImageAlpha((Integer) valueAnimator.getAnimatedValue()));
                            checkAnimator.addListener(new AnimatorListenerAdapter()
                            {
                                @Override
                                public void onAnimationEnd(Animator animation)
                                {
                                    ValueAnimator anim = ValueAnimator.ofInt(0, bgWidth);
                                    anim.addUpdateListener(valueAnimator ->
                                    {
                                        int val = (Integer) valueAnimator.getAnimatedValue();
                                        ViewGroup.LayoutParams layoutParams = bg.getLayoutParams();
                                        layoutParams.width = val;
                                        bg.setLayoutParams(layoutParams);
                                    });
                                    anim.setDuration(duration);
                                    anim.setInterpolator(new FastOutSlowInInterpolator());

                                    anim.addListener(new AnimatorListenerAdapter()
                                    {
                                        @Override
                                        public void onAnimationEnd(Animator animation)
                                        {
                                            textView.animate().alpha(1f).setDuration(duration).start();
                                            bg.getLayoutParams().width = width;
                                            callback.onAnimationFinished(true);
                                        }
                                    });

                                    anim.start();
                                }
                            });
                            checkAnimator.start();
                        }
                    }
                });
                checkAnimator.start();
                break;
            case 3:
                // ERROR
                progress.animate().alpha(0f).setDuration(duration / 2).start();
                ValueAnimator errorAnimator = ValueAnimator.ofInt(0,255);
                errorAnimator.setDuration(duration);
                errorAnimator.addUpdateListener(valueAnimator -> error.setImageAlpha((Integer) valueAnimator.getAnimatedValue()));
                errorAnimator.addListener(new AnimatorListenerAdapter()
                {
                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        ValueAnimator errorAnimator = ValueAnimator.ofInt(255,0);
                        errorAnimator.setStartDelay(delay);
                        errorAnimator.setDuration(duration);
                        errorAnimator.addUpdateListener(valueAnimator -> error.setImageAlpha((Integer) valueAnimator.getAnimatedValue()));
                        errorAnimator.addListener(new AnimatorListenerAdapter()
                        {
                            @Override
                            public void onAnimationEnd(Animator animation)
                            {
                                ValueAnimator anim = ValueAnimator.ofInt(0, bgWidth);
                                anim.addUpdateListener(valueAnimator ->
                                {
                                    int val = (Integer) valueAnimator.getAnimatedValue();
                                    ViewGroup.LayoutParams layoutParams = bg.getLayoutParams();
                                    layoutParams.width = val;
                                    bg.setLayoutParams(layoutParams);
                                });
                                anim.setDuration(duration);
                                anim.setInterpolator(new FastOutSlowInInterpolator());

                                anim.addListener(new AnimatorListenerAdapter()
                                {
                                    @Override
                                    public void onAnimationEnd(Animator animation)
                                    {
                                        bg.getLayoutParams().width = width;
                                        textView.animate().alpha(1f).setDuration(duration).start();
                                        callback.onAnimationFinished(true);
                                    }
                                });

                                anim.start();
                            }
                        });
                        errorAnimator.start();
                    }
                });
                errorAnimator.start();
                break;
            default:
                // START
                textView.animate().alpha(0f).setDuration(duration / 2).start();
                ValueAnimator anim = ValueAnimator.ofInt(bg.getMeasuredWidth(), 0);
                width = bg.getLayoutParams().width;
                bgWidth = bg.getWidth();
                anim.addUpdateListener(valueAnimator ->
                {
                    int val = (Integer) valueAnimator.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = bg.getLayoutParams();
                    layoutParams.width = val;
                    bg.setLayoutParams(layoutParams);
                });
                anim.setDuration(duration);
                anim.setInterpolator(new FastOutSlowInInterpolator());

                anim.addListener(new AnimatorListenerAdapter()
                {
                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        progress.animate().alpha(1f).setDuration(duration).start();
                        callback.onAnimationFinished(true);
                    }
                });

                anim.start();
                break;
        }
    }
}
