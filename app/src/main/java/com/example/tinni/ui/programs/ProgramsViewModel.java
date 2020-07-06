package com.example.tinni.ui.programs;

import android.os.AsyncTask;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tinni.helpers.Constants;
import com.example.tinni.helpers.Functions;
import com.example.tinni.models.Program;
import com.example.tinni.models.Sound;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>Programs ViewModel</h1>
 * ViewModel for the programs ui
 *
 * Variables:
 * ObservableBoolean loading: Loading indicator while sounds are loading
 * MutableLiveData<List<Program>> programs: A mutable list (observed) with all programs
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   27.06.2020
 */

public class ProgramsViewModel extends ViewModel
{
    public ObservableBoolean loading = new ObservableBoolean(true);
    private MutableLiveData<List<Program>> programs = new MutableLiveData<>();
    private static final Functions func = new Functions();

    /**
     * <h2>Fill</h2>
     * Call populateProgramsAsyncTask to populate the categories and sounds triggered on SoundsFragment
     */

    public void fill ()
    {
        new populateProgramsAsyncTask(programs, loading).execute();
    }

    /**
     * <h2>Get Programs</h2>
     * Get the Program List
     */

    public MutableLiveData<List<Program>> getPrograms()
    {
        return programs;
    }

    /**
     * <h2>Populate Programs Async Task</h2>
     * Async Task to populate the programs
     * Programs might be loaded from a server in the future
     *
     */

    private static class populateProgramsAsyncTask extends AsyncTask<Void, Void, List<Program>>
    {
        MutableLiveData<List<Program>> programs;
        ObservableBoolean loading;

        /**
         * <h3>Constructor</h3>
         *
         * Arguments:
         * @param _programs: The full programs list (mutable)
         * @param _loading: The loading indicator
         *
         */

        private populateProgramsAsyncTask(MutableLiveData<List<Program>> _programs, ObservableBoolean _loading)
        {
            programs = _programs;
            loading = _loading;
        }
        @Override
        protected List<Program> doInBackground(Void... voids)
        {
            return new ArrayList<>(Constants.getInstance().programs);
        }

        protected void onPostExecute(List<Program> list)
        {
            if (list != null && list.size() > 0)
            {
                programs.setValue(list);
            }
            else
            {
                programs.setValue(null);
            }

            loading.set(false);
        }
    }

    /**
     * <h2>Manual Add</h2>
     * Manually adding a program to the list
     *
     */

    public void manualAdd (Program p)
    {
        if (programs.getValue() != null)
        {
            boolean reset = programs.getValue().size() == 0;
            programs.getValue().add(0, p);

            if (reset)
            {
                programs.setValue(programs.getValue());
            }
        }
    }

    /**
     * <h2>Manual Remove</h2>
     * Manually removing a program from the list
     *
     */

    public void manualRemove (Program p)
    {
        if (programs.getValue() != null)
        {
            programs.getValue().stream().filter(x -> x.getId() == p.getId()).findFirst().ifPresent(ss -> programs.getValue().remove(ss));

            if (programs.getValue().size() == 0)
            {
                programs.setValue(programs.getValue());
            }
        }
    }
}
