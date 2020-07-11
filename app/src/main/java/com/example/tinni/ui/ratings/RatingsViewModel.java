package com.example.tinni.ui.ratings;

import android.os.AsyncTask;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tinni.helpers.Constants;
import com.example.tinni.models.Program;
import com.example.tinni.models.Rating;
import com.example.tinni.models.SelectedProgram;
import com.example.tinni.models.Session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <h1>Ratings ViewModel</h1>
 * ViewModel for the ratings ui
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   28.06.2020
 */

public class RatingsViewModel extends ViewModel
{
    public ObservableBoolean loading = new ObservableBoolean(true);
    public MutableLiveData<List<Rating>> ratings = new MutableLiveData<>();

    public MutableLiveData<List<Rating>> getRatings()
    {
        return ratings;
    }

    public void prepare ()
    {
        if ((ratings.getValue() == null || ratings.getValue().size() == 0) && Constants.getInstance().ratings.size() > 0)
        {
            List<Rating> newRatings = new ArrayList<>(Constants.getInstance().ratings);
            Collections.reverse(newRatings);
            ratings.setValue(newRatings);
        }
        loading.set(false);
    }
}
