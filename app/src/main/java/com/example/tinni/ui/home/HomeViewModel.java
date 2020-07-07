package com.example.tinni.ui.home;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tinni.helpers.Constants;
import com.example.tinni.models.Program;
import com.example.tinni.models.Sound;
import com.example.tinni.models.SoundStat;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeViewModel extends ViewModel
{
    public ObservableField<Program> currentProgram = new ObservableField<>(null);
    public ObservableBoolean loading = new ObservableBoolean(true);
    public MutableLiveData<List<Sound>> last = new MutableLiveData<>();
    public MutableLiveData<List<Sound>> favorites = new MutableLiveData<>();
    public ObservableInt rating = new ObservableInt();

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
     *
     * Sets up the data for the HomeFragment
     *
     * Source: https://stackoverflow.com/a/29671501/2700965
     */

    public void prepare ()
    {
        if (Constants.getInstance().selectedProgram != null && Constants.getInstance().selectedProgram.getProgram() != null)
        {
            if (currentProgram.get() == null || Objects.requireNonNull(currentProgram.get()).getId() != Constants.getInstance().selectedProgram.getProgram().getId())
            {
                currentProgram.set(Constants.getInstance().selectedProgram.getProgram());
            }
        }

        if ((last.getValue() == null || last.getValue().size() == 0) && Constants.getInstance().listened.size() > 0)
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

        if ((favorites.getValue() == null || favorites.getValue().size() == 0) && Constants.getInstance().favorites.size() > 0)
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

        loading.set(false);
    }

    /**
     * <h2>Set Rating</h2>
     * Clears the given Observable String
     *
     * @param val The rating value

     */

    public void setRating (int val)
    {
        rating.set(val);
    }
}
