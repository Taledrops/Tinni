package com.example.tinni.ui.home;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tinni.helpers.Constants;
import com.example.tinni.models.Program;
import com.example.tinni.models.Rating;
import com.example.tinni.models.Sound;
import com.example.tinni.models.SoundStat;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>Home View Model</h1>
 * The viewModel connected to the HomeFragment
 *
 * Variables:
 * ObservableField<Program> currentProgram: The observed current program
 * ObservableBoolean loading: Indicator whether data is loading
 * MutableLiveData<List<Sound>> last: The observed last played list
 * MutableLiveData<List<Sound>> favorites The observed favorites list
 * ObservableInt rating: The observed current rating between 1 and 5
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   18.06.2020
 */

public class HomeViewModel extends ViewModel
{
    public ObservableField<Program> currentProgram = new ObservableField<>();
    public ObservableBoolean loading = new ObservableBoolean(true);
    public MutableLiveData<List<Sound>> last = new MutableLiveData<>();
    public MutableLiveData<List<Sound>> favorites = new MutableLiveData<>();
    public ObservableInt rating = new ObservableInt(0);

    public MutableLiveData<List<Sound>> getLast()
    {
        return last;
    }

    public MutableLiveData<List<Sound>> getFavorites()
    {
        return favorites;
    }

    /**
     * <h2>Prepare</h2>
     * Sets up the data for the HomeFragment
     * Sets the current program
     * Fills the last played list
     * Fills the favorites list
     * Sets the current rating
     *
     * Source: https://stackoverflow.com/a/29671501/2700965
     */

    public void fill (HomeFragment.OnFillResult delegate)
    {
        loading.set(true);
        if (Constants.getInstance().selectedProgram != null && Constants.getInstance().selectedProgram.getProgram() != null)
        {
            currentProgram.set(Constants.getInstance().selectedProgram.getProgram());
        }
        else
        {
            currentProgram.set(null);
        }

        if (Constants.getInstance().listened.size() > 0)
        {
            List<Sound> lastSounds = new ArrayList<>();

            int lastId = 0;
            int i = 0;
            for (SoundStat soundStat : Constants.getInstance().listened)
            {
                if (soundStat.getId() != lastId)
                {
                    Constants.getInstance().sounds.stream().filter(x -> x.getId() == soundStat.getId()).findFirst().ifPresent(s -> lastSounds.add(lastSounds.size(), s));
                }
                lastId = soundStat.getId();

                i++;
                if (i == Constants.getInstance().limit)
                {
                    break;
                }
            }

            if (lastSounds.size() > 0)
            {
                last.setValue(lastSounds);
            }
        }

        if (Constants.getInstance().favorites.size() > 0)
        {
            List<Sound> favoriteSounds = new ArrayList<>();

            int i = 0;
            for (int id : Constants.getInstance().favorites)
            {
                Constants.getInstance().sounds.stream().filter(x -> x.getId() == id).findFirst().ifPresent(favoriteSounds::add);
                i++;
                if (i == Constants.getInstance().limit)
                {
                    break;
                }
            }

            if (favoriteSounds.size() > 0)
            {
                favorites.setValue(favoriteSounds);
            }
        }

        Rating lastRating = Constants.getInstance().wasLastRatingToday();
        if (lastRating != null)
        {
            rating.set(lastRating.getRating());
        }
        else
        {
            rating.set(0);
        }

        loading.set(false);

        delegate.result(true);
    }

    /**
     * <h2>Set Rating</h2>
     * Sets the current rating text
     *
     * @param val The rating value
     * @param text The text to the rating

     */

    public void setRating (int val, String text)
    {
        rating.set(val);
        Constants.getInstance().addRating(val, text);
    }
}
