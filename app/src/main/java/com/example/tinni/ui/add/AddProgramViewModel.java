package com.example.tinni.ui.add;

import android.os.AsyncTask;

import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tinni.helpers.Constants;
import com.example.tinni.models.Answer;
import com.example.tinni.models.Category;
import com.example.tinni.models.Program;
import com.example.tinni.models.Question;
import com.example.tinni.models.Session;
import com.example.tinni.models.Sound;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <h1>AddProgram ViewModel</h1>
 * ViewModel for the addProgram ui
 *
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   04.07.2020
 */

public class AddProgramViewModel extends ViewModel
{
    public ObservableField<String> title = new ObservableField<>();
    public ObservableField<String> description = new ObservableField<>();
    public ObservableField<String> image = new ObservableField<>();
    public ObservableField<Question> currentQuestion = new ObservableField<>();
    public ObservableField<Answer> currentAnswer = new ObservableField<>();
    public List<Session> sessions = new ArrayList<>();
    public ObservableInt interval = new ObservableInt(1);
    public ObservableArrayList<Session> selectedSessions = new ObservableArrayList<>();
    public ObservableArrayList<Question> questions = new ObservableArrayList<>();
    public ObservableBoolean loading = new ObservableBoolean();

    /**
     * <h2>Prepare</h2>
     * Fill the sessions
     *
     */

    public void prepare ()
    {
        if (Constants.getInstance().sounds != null && Constants.getInstance().sounds.size() > 0)
        {
            int i = 1;
            for (Sound s : Constants.getInstance().sounds)
            {
                sessions.add(new Session(i, true, s, 60, false));
                i++;
            }
        }
    }

    /**
     * <h2>Save Async Task</h2>
     * Async Task to save new sound file and store them in the SharedPreferences
     * Sound might be saved to a server in the future hence the Async Task
     *
     * Arguments:
     * List<Category> categories: The list of all categories
     * ObservableField<String> title: The title of the uploaded sound file
     * ObservableField<String> description: The description of the uploaded sound file
     * ObservableField<Uri> uri: The uri of the uploaded sound file
     * ObservableField<Bitmap> bitmap: The bitmap of the uploaded sound file
     * Add.onSubmitResult delegate: The delegate catching the result
     *
     */

    public static class saveAsyncTask extends AsyncTask<Void, Void, Boolean>
    {
        ObservableField<String> title;
        ObservableField<String> description;
        ObservableField<String> image;
        ObservableArrayList<Session> selectedSessions;
        ObservableArrayList<Question> questions;
        ObservableInt interval;
        Add.onSubmitResult delegate;

        public saveAsyncTask(ObservableField<String> _title, ObservableField<String> _description, ObservableField<String> _image, ObservableArrayList<Session> _selectedSessions, ObservableArrayList<Question> _questions, ObservableInt _interval, Add.onSubmitResult _onSubmitResult)
        {
            title = _title;
            description = _description;
            image = _image;
            selectedSessions = _selectedSessions;
            questions = _questions;
            interval = _interval;
            delegate = _onSubmitResult;
        }
        @Override
        protected Boolean doInBackground(Void... voids)
        {
            int i = 1;
            for (Question q : questions)
            {
                int j = 1;
                for (Answer a : q.answers)
                {
                    a.setId(j);
                    j++;
                }
                q.setId(i);
                i++;
            }

            i = 1;
            for (Session s : selectedSessions)
            {
                s.setId(i);
                i++;
            }
            Program newProgram = new Program((int)(System.currentTimeMillis()/1000), true, title.get(), description.get(), 0, selectedSessions, questions, interval.get(), false, image.get());
            Constants.getInstance().addCustomProgram(newProgram, true);
            return true;
        }

        protected void onPostExecute(Boolean res)
        {
            delegate.result(res);
        }
    }

    /**
     * <h2>Clear Text</h2>
     * Clears the given Observable String
     *
     * @param txt The text to clear

     */

    public void clearText (ObservableField<String> txt)
    {
        txt.set("");
    }

    /**
     * <h2>Toggle Bool</h2>
     * Clears the given Observable String
     *
     * @param bool The boolean to toggle

     */

    public void toggleBool (ObservableBoolean bool)
    {
        bool.set(!bool.get());
    }
}
