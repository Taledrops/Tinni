package com.example.tinni.helpers;


import android.graphics.RectF;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.example.tinni.custom.AnimatedImageView;

import java.util.Random;

/**
 * <h1>Image Transition</h1>
 * Handles the image transition by generating a random transition
 *
 * Copyright 2014 Flavio Faria
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Source: https://github.com/flavioarfaria/KenBurnsView/blob/master/library/src/main/java/com/flaviofaria/kenburnsview/RandomTransitionGenerator.java
 *
 * @author Nassim Amar
 * @version 1.0
 * @since 25.06.2020
 */

public class ImageTransition implements TransitionGenerator
{
    private static final int DEFAULT_TRANSITION_DURATION = 10000;
    private static final android.view.animation.Interpolator DEFAULT_TRANSITION_INTERPOLATOR = new AccelerateDecelerateInterpolator();
    private static final Functions func = new Functions();
    private final Random mRandom = new Random(System.currentTimeMillis());
    private long mTransitionDuration;
    private android.view.animation.Interpolator mTransitionInterpolator;
    private Transition mLastGenTrans;
    private RectF mLastDrawableBounds;
    private RectF init;

    public ImageTransition(AnimatedImageView animatedImageView)
    {
        this(DEFAULT_TRANSITION_DURATION, new AccelerateDecelerateInterpolator());
    }


    public ImageTransition(long transitionDuration, android.view.animation.Interpolator transitionInterpolator)
    {
        setTransitionDuration(transitionDuration);
        setTransitionInterpolator(transitionInterpolator);
    }

    @Override
    public Transition generateNextTransition(RectF drawableBounds, RectF viewport, boolean reset, RectF current)
    {
        boolean firstTransition = mLastGenTrans == null;
        boolean drawableBoundsChanged = true;
        boolean viewportRatioChanged = true;

        RectF srcRect;
        RectF dstRect = null;

        if (!firstTransition)
        {
            dstRect = mLastGenTrans.getDestinyRect();
            drawableBoundsChanged = !drawableBounds.equals(mLastDrawableBounds);
            viewportRatioChanged = !func.haveSameAspectRatio(dstRect, viewport);
        }

        if (dstRect == null || drawableBoundsChanged || viewportRatioChanged)
        {
            srcRect = generateRandomRect(drawableBounds, viewport, true);
        }
        else
        {
            srcRect = dstRect;
        }
        if (!reset || init == null || current == null)
        {
            dstRect = generateRandomRect(drawableBounds, viewport, false);
        }
        else
        {
            mTransitionDuration = 200;
            srcRect = current;
            dstRect = init;
        }

        mLastGenTrans = new Transition(srcRect, dstRect, mTransitionDuration, mTransitionInterpolator);

        mLastDrawableBounds = new RectF(drawableBounds);

        return mLastGenTrans;
    }

    /**
     * <h2>Generate Random Rect</h2>
     * Generates a random rect that can be fully contained within {@code drawableBounds} and
     * has the same aspect ratio of {@code viewportRect}. The dimensions of this random rect
     * won't be higher than the largest rect with the same aspect ratio of {@code viewportRect}
     * that {@code drawableBounds} can contain. They also won't be lower than the dimensions
     * of this upper rect limit weighted by {@code MIN_RECT_FACTOR}.
     * @param drawableBounds the bounds of the drawable that will be zoomed and panned.
     * @param viewportRect the bounds of the view that the drawable will be shown.
     * @return an arbitrary generated rect with the same aspect ratio of {@code viewportRect}
     * that will be contained within {@code drawableBounds}.
     */

    private RectF generateRandomRect(RectF drawableBounds, RectF viewportRect, boolean first) {
        float drawableRatio = func.getRectRatio(drawableBounds);
        float viewportRectRatio = func.getRectRatio(viewportRect);
        RectF maxCrop;

        if (drawableRatio > viewportRectRatio)
        {
            float r = (drawableBounds.height() / viewportRect.height()) * viewportRect.width();
            float b = drawableBounds.height();
            maxCrop = new RectF(0, 0, r, b);
        }
        else
        {
            float r = drawableBounds.width();
            float b = (drawableBounds.width() / viewportRect.width()) * viewportRect.height();
            maxCrop = new RectF(0, 0, r, b);
        }

        if (!first)
        {
            float randomFloat = func.truncate(mRandom.nextFloat(), 2);
            float MIN_RECT_FACTOR = 0.75f;
            float factor = MIN_RECT_FACTOR + ((1 - MIN_RECT_FACTOR) * randomFloat);
            float width = factor * maxCrop.width();
            float height = factor * maxCrop.height();
            int widthDiff = (int) (drawableBounds.width() - width);
            int heightDiff = (int) (drawableBounds.height() - height);
            int left = widthDiff > 0 ? mRandom.nextInt(widthDiff) : 0;
            int top = heightDiff > 0 ? mRandom.nextInt(heightDiff) : 0;
            return new RectF(left, top, left + width, top + height);
        }
        else
        {
            float factor = 1.0f;
            float width = factor * maxCrop.width();
            float height = factor * maxCrop.height();
            int widthDiff = (int) (drawableBounds.width() - width);
            int left = widthDiff / 2;
            int top = 0;
            init = new RectF(left, top, left + width, top + height);
            return init;
        }
    }

    public void setTransitionDuration(long transitionDuration) {
        mTransitionDuration = transitionDuration;
    }

    public void setTransitionInterpolator(android.view.animation.Interpolator interpolator)
    {
        mTransitionInterpolator = interpolator;
    }
}
