package com.example.tinni.helpers;

import com.google.android.material.appbar.AppBarLayout;

/**
 * <h1>AppBar State Change Listener</h1>
 * Listens to state changes of the AppBar
 *
 * Variables:
 * enum State: The current state of the AppBar
 *
 * Source: https://stackoverflow.com/a/33891727/2700965
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   25.06.2020
 */

public abstract class AppBarStateChangeListener implements AppBarLayout.OnOffsetChangedListener
{
    public enum State {
        EXPANDED,
        COLLAPSED,
        IDLE
    }

    private State mCurrentState = State.IDLE;

    @Override
    public final void onOffsetChanged(AppBarLayout appBarLayout, int i)
    {
        if (i == 0)
        {
            if (mCurrentState != State.EXPANDED)
            {
                onStateChanged(appBarLayout, State.EXPANDED);
            }
            mCurrentState = State.EXPANDED;
        }
        else if (Math.abs(i) >= appBarLayout.getTotalScrollRange())
        {
            if (mCurrentState != State.COLLAPSED)
            {
                onStateChanged(appBarLayout, State.COLLAPSED);
            }
            mCurrentState = State.COLLAPSED;
        }
        else
        {
            if (mCurrentState != State.IDLE)
            {
                onStateChanged(appBarLayout, State.IDLE);
            }
            mCurrentState = State.IDLE;
        }
    }

    public abstract void onStateChanged(AppBarLayout appBarLayout, State state);
}
