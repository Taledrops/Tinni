package com.example.tinni.ui.programs;

import android.os.AsyncTask;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tinni.helpers.Constants;
import com.example.tinni.models.Program;
import com.example.tinni.ui.home.HomeFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>Programs ViewModel</h1>
 * ViewModel for the programs ui
 *
 * Variables:
 * ObservableBoolean loading: Loading indicator while programs are loading
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

    /**
     * <h2>Fill</h2>
     * Call populateProgramsAsyncTask to populate the programs on the ProgramsFragment
     *
     * @param delegate Delegate to return a success flag for the refresh layout to remove its icon
     */

    public void fill (HomeFragment.OnFillResult delegate)
    {
        new populateProgramsAsyncTask(programs, loading, delegate).execute();
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
     */

    private static class populateProgramsAsyncTask extends AsyncTask<Void, Void, List<Program>>
    {
        HomeFragment.OnFillResult delegate;
        MutableLiveData<List<Program>> programs;
        ObservableBoolean loading;

        /**
         * <h3>Constructor</h3>
         *
         * @param _programs: The full programs list (mutable)
         * @param _loading: The loading indicator
         */

        private populateProgramsAsyncTask(MutableLiveData<List<Program>> _programs, ObservableBoolean _loading, HomeFragment.OnFillResult _delegate)
        {
            delegate = _delegate;
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
            delegate.result(true);
        }
    }
}
