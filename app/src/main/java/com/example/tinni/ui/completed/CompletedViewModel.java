package com.example.tinni.ui.completed;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tinni.helpers.Constants;
import com.example.tinni.models.SelectedProgram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <h1>Completed ViewModel</h1>
 * ViewModel for the completed ui
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   09.07.2020
 */

public class CompletedViewModel extends ViewModel
{
    public ObservableBoolean loading = new ObservableBoolean(true);
    public MutableLiveData<List<SelectedProgram>> programs = new MutableLiveData<>();

    public MutableLiveData<List<SelectedProgram>> getPrograms()
    {
        return programs;
    }

    /**
     * <h2>Prepare</h2>
     *
     * Sets up the data for the CompletedFragment
     *
     */

    public void prepare ()
    {
        if ((programs.getValue() == null || programs.getValue().size() == 0) && Constants.getInstance().pastPrograms.size() > 0)
        {
            List<SelectedProgram> newPrograms = new ArrayList<>(Constants.getInstance().pastPrograms);
            Collections.reverse(newPrograms);
            programs.setValue(newPrograms);
        }

        loading.set(false);
    }
}
