package com.example.tinni.ui.program;

import android.os.AsyncTask;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tinni.helpers.Constants;
import com.example.tinni.models.Program;
import com.example.tinni.models.SelectedProgram;
import com.example.tinni.models.Session;

import java.util.Objects;

/**
 * <h1>Program ViewModel</h1>
 * ViewModel for the program ui
 *
 * Variables:
 * MutableLiveData<Sound> current: The current Program object
 * ObservableField<SelectedProgram> active: Set when program is the currently selected one
 * Session nextSession: The next session which has not been completed yet
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   28.06.2020
 */

public class ProgramViewModel extends ViewModel
{
    public MutableLiveData<Program> current = new MutableLiveData<>();
    public ObservableField<SelectedProgram> active = new ObservableField<>();
    public Session nextSession = null;
    public SelectedProgram finished = null;

    /**
     * <h2>Get Current</h2>
     * Get the current Program
     */

    public MutableLiveData<Program> getCurrent()
    {
        return current;
    }

    /**
     * <h2>Prepare</h2>
     * Prepare the current program
     *
     * @param program current Program object
     */

    public void prepare (com.example.tinni.models.Program program)
    {

        if (Constants.getInstance().selectedProgram != null && program.getId() == Constants.getInstance().selectedProgram.getProgram().getId())
        {
            active.set(Constants.getInstance().selectedProgram);
            if (active.get() != null && Objects.requireNonNull(active.get()).getProgram() != null)
            {
                current.setValue(Objects.requireNonNull(active.get()).getProgram());
            }
            else
            {
                current.setValue(program);
            }
        }
        else
        {
            current.setValue(program);
        }
    }

    /**
     * <h2>Update Session</h2>
     * Updates the current session and marks it as done
     *
     */

    public boolean updateProgram ()
    {
        Session session =  Constants.getInstance().updateSession;
        boolean success = false;
        if (session != null)
        {
            session.done.set(true);
            if (current.getValue() != null)
            {
                Session s = current.getValue().getSessions().stream().filter(x -> x.getId() == session.getId()).findFirst().orElse(null);
                if (s != null)
                {
                    int index = current.getValue().getSessions().indexOf(s);
                    int next = index + 1;
                    if (current.getValue().getSessions().size() > next)
                    {
                        nextSession = current.getValue().getSessions().get(next);
                        nextSession.active.set(true);
                        if (active.get() != null)
                        {
                            Objects.requireNonNull(active.get()).sessions.add(s);
                            Constants.getInstance().updateSelectedProgram(active.get());
                        }
                    }
                    else
                    {
                        success = true;
                    }
                }
            }
            Constants.getInstance().updateSession = null;
        }
        return success;
    }

    /**
     * <h2>Delete Program Async Task</h2>
     * Async Task to handle the deletion of programs
     *
     */

    public static class deleteProgramAsyncTask extends AsyncTask<Void, Void, Boolean>
    {
        com.example.tinni.ui.program.Program.onDeleteProgramResult delegate;
        Program program;

        public deleteProgramAsyncTask(Program _program, com.example.tinni.ui.program.Program.onDeleteProgramResult _onDeleteProgramResult)
        {
            delegate = _onDeleteProgramResult;
            program = _program;
        }

        @Override
        protected Boolean doInBackground(Void... voids)
        {
            if (program.isCustom())
            {
                Constants.getInstance().removeCustomProgram(program);
                return true;
            }
            else
            {
                return false;
            }
        }

        protected void onPostExecute(Boolean res)
        {
            delegate.result(res);
        }
    }

    /**
     * <h2>Activate</h2>
     * Makes the program the currently selected one
     *
     */

    public void activate (boolean start)
    {
        if (active.get() == null && start)
        {
            active.set(Constants.getInstance().handleSelectedProgram(current.getValue()));
            if (active.get() != null && Objects.requireNonNull(active.get()).getProgram() != null)
            {
                Objects.requireNonNull(active.get()).getStartQuestions().addAll(Objects.requireNonNull(current.getValue()).getQuestions());
                current.setValue(Objects.requireNonNull(active.get()).getProgram());
            }
        }
        else if (active.get() != null && !start)
        {
            Objects.requireNonNull(active.get()).getEndQuestions().addAll(Objects.requireNonNull(current.getValue()).getQuestions());
            Objects.requireNonNull(active.get()).setEnd(System.currentTimeMillis());
            Constants.getInstance().handleSelectedProgram(current.getValue());
            finished = active.get();
            active.set(null);
            current.getValue().getSessions().forEach(x -> x.done.set(false));
            current.getValue().getSessions().forEach(x -> x.active.set(true));
        }
    }
}
