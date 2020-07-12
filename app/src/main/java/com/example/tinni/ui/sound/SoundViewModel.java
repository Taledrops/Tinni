package com.example.tinni.ui.sound;

import android.os.AsyncTask;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tinni.helpers.Constants;
import com.example.tinni.models.Note;
import com.example.tinni.models.Program;
import com.example.tinni.models.Session;
import com.example.tinni.models.Sound;

import java.net.URL;
import java.util.HashMap;
import java.util.Objects;

/**
 * <h1>Sound ViewModel</h1>
 * ViewModel for the sound ui
 *
 * Variables:
 * MutableLiveData<Sound> current: The current Sound object
 * ObservableBoolean liked: Whether the current Sound is liked
 * ObservableBoolean repeat: Indicator whether to repeat the current sound
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   25.06.2020
 */

public class SoundViewModel extends ViewModel
{
    public MutableLiveData<Sound> current = new MutableLiveData<>();
    public ObservableBoolean liked = new ObservableBoolean();
    public ObservableBoolean repeat = new ObservableBoolean(false);
    public ObservableField<Session> session = new ObservableField<>(null);
    public ObservableField<Program> program = new ObservableField<>(null);
    public ObservableField<String> note = new ObservableField<>();
    public ObservableInt length = new ObservableInt(0);
    private Note noteObject = null;

    /**
     * <h2>Prepare</h2>
     * Prepare the current sound
     *
     * @param sound current Sound object
     */

    public void prepare (Sound sound, int session)
    {
        sound.playing.set(2);
        current.setValue(sound);

        if (Constants.getInstance().selectedProgram != null && Constants.getInstance().selectedProgram.getProgram() != null)
        {
            Program p = Constants.getInstance().selectedProgram.getProgram();
            this.program.set(p);
            Session s = p.getSessions().stream().filter(x -> x.getId() == session).findFirst().orElse(null);
            if (s != null)
            {
                this.session.set(s);
                this.length.set(s.getTime());
            }
            else
            {
                this.length.set(sound.getLength());
            }
        }
        else
        {
            this.length.set(sound.getLength());
        }
        liked.set(Constants.getInstance().favorites.contains(sound.getId()));

        Note n = Constants.getInstance().notes.stream().filter(x -> x.getId() == sound.getId()).findFirst().orElse(null);
        if (n != null)
        {
            noteObject = n;
            note.set(n.text.get());
        }
    }

    /**
     * <h2>Like Sound Async Task</h2>
     * Async Task to handle likes for sounds
     *
     */

    public static class likeSoundAsyncTask extends AsyncTask<Void, Void, Boolean>
    {
        com.example.tinni.ui.sound.Sound.onLikeSoundResult delegate;
        Sound sound;
        ObservableBoolean liked;
        boolean likedBefore = false;

        public likeSoundAsyncTask(Sound _sound, ObservableBoolean _liked, com.example.tinni.ui.sound.Sound.onLikeSoundResult _onLikeSoundResult)
        {
            delegate = _onLikeSoundResult;
            liked = _liked;
            sound = _sound;
            likedBefore = liked.get();
            liked.set(!liked.get());
        }

        @Override
        protected Boolean doInBackground(Void... voids)
        {
            return Constants.getInstance().addToFavorites(sound);
        }

        protected void onPostExecute(Boolean res)
        {
            delegate.result(res);
        }
    }

    /**
     * <h2>Delete Sound Async Task</h2>
     * Async Task to handle the deletion of sounds
     *
     */

    public static class deleteSoundAsyncTask extends AsyncTask<Void, Void, Boolean>
    {
        com.example.tinni.ui.sound.Sound.onDeleteSoundResult delegate;
        Sound sound;

        public deleteSoundAsyncTask(Sound _sound, com.example.tinni.ui.sound.Sound.onDeleteSoundResult _onDeleteSoundResult)
        {
            delegate = _onDeleteSoundResult;
            sound = _sound;
        }

        @Override
        protected Boolean doInBackground(Void... voids)
        {
            if (sound.isCustom())
            {
                Constants.getInstance().removeCustomSound(sound);
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
     * <h2>Save Note</h2>
     * Saving the note by updating the SharedPreferences
     *
     */

    public void saveNote ()
    {
        if (current.getValue() != null)
        {
            if (noteObject == null)
            {
                Constants.getInstance().addNote(new Note(current.getValue().getId(), note.get()), false);
            }
            else
            {
                noteObject.text.set(note.get());
                Constants.getInstance().addNote(new Note(current.getValue().getId(), note.get()), true);
            }
        }
    }

    /**
     * <h2>Change Repeat</h2>
     * Changes the repeat indicator
     *
     */

    public void changeRepeat ()
    {
        repeat.set(!repeat.get());

        System.out.println("### REPEAT IST: " + repeat.get());
    }
}
